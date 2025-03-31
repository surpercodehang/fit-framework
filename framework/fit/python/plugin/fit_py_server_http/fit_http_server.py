# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：http 通信服务端
"""
import base64
import ssl
import threading
import time
from concurrent.futures import ThreadPoolExecutor
from http import HTTPStatus
from queue import Queue, Empty
from typing import Optional
from urllib.parse import quote

import tornado.ioloop
import tornado.web
import tornado.websocket

from fitframework.api.decorators import fitable, fit
from fitframework.api.exception import FIT_OK
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.exception.fit_exception import InternalErrorCode, FitException
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.http_header import HttpHeader
from fitframework.core.network.metadata.metadata_utils import TagLengthValuesUtil
from fitframework.core.network.metadata.request_metadata import RequestMetadata, GenericVersion
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.utils.tools import AtomicInt
from .http_async_server_utils import WorkerInfoManager, parse_unique_task_id, UniqueTaskId, is_sync_request
from .http_utils import get_context_path, get_server_thread_count, get_server_crt_path, get_server_key_path, \
    get_server_ca_path, get_server_verify_enabled, get_server_assert_host_name, get_task_count_limit, \
    get_polling_wait_time, server_response, get_decrypted_key_file_password, StartServeTimeManager, get_ssl_enabled, \
    get_server_tls_protocol, get_server_ciphers, build_response_headers_by_response_metadata

_http_server: Optional[tornado.httpserver.HTTPServer] = None
_https_server: Optional[tornado.httpserver.HTTPServer] = None
_server_thread: Optional[threading.Thread] = None

_last_success_time = time.time()

_start_serve_time_manager = StartServeTimeManager()

_worker_info_manager = WorkerInfoManager()

# 在执行的异步任务数量
_running_task_count: AtomicInt = AtomicInt(0)


@fitable(generic_id="modelengine.fit.get.running.async.task.count", fitable_id='local-worker')
def get_running_task_count() -> int:
    return _running_task_count.get_value()


@fitable(generic_id="modelengine.fit.get.last.success.time", fitable_id='local-worker')
def get_last_success_time() -> float:
    return _last_success_time


@fitable(generic_id="modelengine.fit.get.earliest.start.time", fitable_id='local-worker')
def get_earliest_start_time() -> Optional[float]:
    return _start_serve_time_manager.get_earliest_start_time()


@fit("modelengine.fit.get.all.plugins.ready")
def get_all_plugins_ready() -> str:
    pass


class HealthCheckHandler(tornado.web.RequestHandler):
    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    async def get(self):
        self.set_status(HTTPStatus.OK)
        self.write("OK")


class PluginsReadyCheckHandler(tornado.web.RequestHandler):
    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    @tornado.concurrent.run_on_executor
    def process_plugins_ready_task(self):
        try:
            return get_all_plugins_ready(), HTTPStatus.OK, {}
        except FitException:
            return "NOT OK", HTTPStatus.OK, {}

    async def get(self):
        body, status, headers = await self.process_plugins_ready_task()
        self.set_status(status)
        for key in headers:
            self.set_header(key, headers[key])
        self.write(body)


class PollingTaskHandler(tornado.web.RequestHandler):
    @classmethod
    def _build_polling_failed_response(cls, code: int, message: str) -> tuple[str, int, dict]:
        return "", HTTPStatus.OK, {HttpHeader.CODE.value: f"{code}", HttpHeader.MESSAGE.value: quote(f"{message}")}

    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    async def get(self):
        body, status, headers = await self._process_polling_request()
        self.set_status(status)
        for key in headers:
            self.set_header(key, headers[key])
        self.write(body)

    @tornado.concurrent.run_on_executor
    def _process_polling_request(self):
        unique_task_id = parse_unique_task_id(self.request.headers)
        sys_plugin_logger.info(f"{'HTTPS' if self.request.protocol == 'https' else 'HTTP'} GET {get_context_path()}"
                               f"/fit/async/await-response, worker_id={unique_task_id.worker_id}, "
                               f"instance_id={unique_task_id.instance_id}")
        finished_task_queue = _worker_info_manager.get_result_queue(unique_task_id.worker_id,
                                                                    unique_task_id.instance_id)
        if finished_task_queue is None:
            return self._build_polling_failed_response(InternalErrorCode.ASYNC_TASK_NOT_FOUND.value,
                                                       "async task not found.")
        try:
            finished_task_id: Optional[str] = finished_task_queue.get(block=True, timeout=get_polling_wait_time())
        except Empty:
            return self._build_polling_failed_response(InternalErrorCode.ASYNC_TASK_NOT_COMPLETED.value,
                                                       "async task not completed.")
        result: FitResponse = _worker_info_manager.get_execute_result(unique_task_id.worker_id,
                                                                      unique_task_id.instance_id, finished_task_id)
        response_headers = build_response_headers_by_response_metadata(result.metadata, finished_task_id)
        if result.metadata.code == FIT_OK:
            global _last_success_time
            _last_success_time = time.time()
        sys_plugin_logger.info(f"Async task result returned. [worker_id={unique_task_id.worker_id}, "
                               f"instance_id={unique_task_id.instance_id}, task_id={finished_task_id}]")
        return result.data, HTTPStatus.OK, response_headers


class FitHandler(tornado.web.RequestHandler):
    @classmethod
    def _async_serve_response(cls, metadata: RequestMetadata, data: bytes, unique_task_id: UniqueTaskId,
                              finished_task_queue: Queue):
        response: FitResponse = server_response(metadata, data)
        _worker_info_manager.put_execute_result(unique_task_id.worker_id, unique_task_id.instance_id,
                                                unique_task_id.task_id, response)
        finished_task_queue.put(unique_task_id.task_id)
        _running_task_count.decrease()

    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    async def post(self, genericable_id: str, fitable_id: str):
        body, status, headers = await self._process_task_submit_request(genericable_id, fitable_id)
        self.set_status(status)
        for key in headers:
            self.set_header(key, headers[key])
        self.write(body)

    @tornado.concurrent.run_on_executor
    def _process_task_submit_request(self, genericable_id: str, fitable_id: str):
        start_serve_time = time.time()
        _start_serve_time_manager.add_start_serve_time(start_serve_time)
        payload = self.request.body
        request_meta = self._convert_request_to_metadata(genericable_id, fitable_id)
        unique_task_id = parse_unique_task_id(self.request.headers)
        sys_plugin_logger.info(f"{'HTTPS' if self.request.protocol == 'https' else 'HTTP'} POST {get_context_path()}"
                               f"/fit/{genericable_id}/{fitable_id} , worker_id={unique_task_id.worker_id}, "
                               f"instance_id={unique_task_id.instance_id}, task_id={unique_task_id.task_id}")
        if is_sync_request(unique_task_id):
            result = server_response(request_meta, payload)
            response_headers = build_response_headers_by_response_metadata(result.metadata, None)
            if result.metadata.code == FIT_OK:
                global _last_success_time
                _last_success_time = time.time()
            _start_serve_time_manager.remove_start_serve_time(start_serve_time)
            return result.data, HTTPStatus.OK, response_headers
        if _running_task_count.get_value() >= get_task_count_limit():
            code = InternalErrorCode.ASYNC_TASK_NOT_ACCEPTED.value
            message = f"async task not accepted. [task_count_limit={get_task_count_limit()}]"
            response_meta = ResponseMetadata(request_meta.data_format, True, code, message, {})
            response_headers = build_response_headers_by_response_metadata(response_meta, unique_task_id.task_id)
            _start_serve_time_manager.remove_start_serve_time(start_serve_time)
            return "", HTTPStatus.ACCEPTED, response_headers
        _worker_info_manager.clear_expired_worker_info()
        finished_task_queue = _worker_info_manager.create_and_get_result_queue(unique_task_id.worker_id,
                                                                               unique_task_id.instance_id)
        _running_task_count.increase()
        _executor.submit(self._async_serve_response, request_meta, payload, unique_task_id, finished_task_queue)
        response_meta = ResponseMetadata(request_meta.data_format, False, FIT_OK, "", {})
        response_headers = build_response_headers_by_response_metadata(response_meta, unique_task_id.task_id)
        _start_serve_time_manager.remove_start_serve_time(start_serve_time)
        return "", HTTPStatus.ACCEPTED, response_headers

    def _convert_request_to_metadata(self, genericable_id: str, fitable_id: str):
        headers = self.request.headers
        try:
            data_format = int(headers.get(HttpHeader.FORMAT.value))
        except KeyError as cause:
            raise Exception("cannot find the data format field in headers.") from cause
        try:
            genericable_version = GenericVersion.from_string(headers[HttpHeader.GENERICABLE_VERSION.value])
        except KeyError as cause:
            raise Exception("cannot find the genericable version field in headers.") from cause
        tlvs = TagLengthValuesUtil.deserialize(base64.b64decode(headers.get(HttpHeader.TLV.value, b"")))
        return RequestMetadata(data_format, genericable_version, genericable_id, fitable_id, tlvs)


class WebSocketHandler(tornado.websocket.WebSocketHandler):
    # 仅预留待完善的 websocket 事件处理接口，后续再进行完善。
    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    def open(self, genericable_id: str, fitable_id: str):
        sys_plugin_logger.info(f"[websocket] connected. [genericable_id={genericable_id}, "
                               f"fitable_id={fitable_id}]")

    async def on_message(self, message):
        sys_plugin_logger.info(f"[websocket] received message. [message={message}]")

        def process_message(message):
            return message

        result = await tornado.ioloop.IOLoop.current().run_in_executor(_executor, process_message, message)
        await self.write_message(result)

    def on_close(self):
        sys_plugin_logger.info(f"[websocket] closed [close_code={self.close_code}, close_reason={self.close_reason}]")


_base_patterns = [
    (r"/fit/health", HealthCheckHandler),
    (r"/fit/pluginsReady", PluginsReadyCheckHandler),
    (r"/fit/async/await-response", PollingTaskHandler),
    (r"/fit/([^/]+)/([^/]+)", FitHandler),
    (r"/fit/websocket/([^/]+)/([^/]+)", WebSocketHandler)
]

_executor: ThreadPoolExecutor = None
_executor_lock: threading.Lock = threading.Lock()

_app: tornado.web.Application = None
_app_lock: threading.Lock = threading.Lock()


def _get_executor():
    global _executor
    if _executor is not None:
        return _executor
    with _executor_lock:
        if _executor is not None:
            return _executor
        _executor = ThreadPoolExecutor(get_server_thread_count())
        return _executor


def _get_all_patterns():
    return [(get_context_path() + pattern[0], pattern[1], dict(executor=_get_executor())) for pattern in
            _base_patterns] + [(pattern[0], pattern[1], dict(executor=_get_executor())) for pattern in _base_patterns]


def _get_app():
    global _app
    if _app is not None:
        return _app
    with _app_lock:
        if _app is not None:
            return _app
        _app = tornado.web.Application(_get_all_patterns())
        return _app


def init_fit_http_server(port):
    global _http_server
    _http_server = tornado.httpserver.HTTPServer(_get_app())
    _http_server.listen(port)


def init_fit_https_server(port):
    global _https_server
    if get_ssl_enabled():
        try:
            ssl_context = ssl.SSLContext(protocol=get_server_tls_protocol())
            ssl_context.set_ciphers(get_server_ciphers())
            if get_server_verify_enabled():
                ssl_context.verify_mode = ssl.CERT_REQUIRED
                ssl_context.check_hostname = get_server_assert_host_name()
                ssl_context.load_verify_locations(cafile=get_server_ca_path())
            ssl_context.load_cert_chain(certfile=get_server_crt_path(), keyfile=get_server_key_path(),
                                        password=get_decrypted_key_file_password())
        except FileNotFoundError:
            sys_plugin_logger.error("file for https server is missing.")
            raise
    else:
        ssl_context = None
    _https_server = tornado.httpserver.HTTPServer(_get_app(), ssl_options=ssl_context)
    _https_server.listen(port)


def start_all_server():
    global _server_thread
    _server_thread = threading.Thread(target=tornado.ioloop.IOLoop.instance().start, name="HttpServerThread",
                                      daemon=True)
    _server_thread.start()


def stop_all_server():
    global _http_server
    global _https_server
    global _server_thread
    if _https_server is not None:
        _https_server.stop()
        sys_plugin_logger.info("https server stopped.")
    if _http_server is not None:
        _http_server.stop()
        sys_plugin_logger.info("http server stopped.")
    tornado.ioloop.IOLoop.instance().add_callback(tornado.ioloop.IOLoop.instance().stop)
    sys_plugin_logger.info("IOLoop stopped.")
    _executor.shutdown()
    sys_plugin_logger.info("_executor shut down.")
