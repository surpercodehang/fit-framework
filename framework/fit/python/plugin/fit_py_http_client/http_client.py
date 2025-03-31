# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：HTTP客户端
"""
import base64
import uuid
from http import HTTPStatus
from queue import Queue
from threading import Lock, Thread
from typing import List, Dict
from urllib.parse import unquote

import requests
import urllib3
from requests import HTTPError, Response

from fit_common_struct.core import Address
from fitframework import const
from fitframework.api.decorators import fitable, fit
from fitframework.api.exception import FIT_OK
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.exception.fit_exception import InternalErrorCode, ClientException
from fitframework.core.network.enums import ProtocolEnum, SerializingStructureEnum
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.http_header import HttpHeader
from fitframework.core.network.metadata.metadata_utils import TagLengthValuesUtil
from fitframework.core.network.metadata.request_metadata import RequestMetadata
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.core.network.temp_entity import RequestContext
from fitframework.utils.tools import b64decode_from_str
from .http_client_utils import TaskStorage, get_polling_timeout, PollingMetadata, get_cert, get_verify

# task_id 到任务结果队列的映射
_result_queues: Dict[str, Queue] = {}
_result_queues_lock = Lock()

# 存放 task_id 的数据结构
_task_storage = TaskStorage()
_task_storage_lock = Lock()

_POLLING_FAIL_THRESHOLD = 3

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


@fit(const.SERVICE_DB_REGISTER_CLIENT_GEN_ID)
def register_client(client_protocol_id: int) -> None:
    pass


@fit(const.RUNTIME_GET_WORKER_ID_GEN_ID)
def get_runtime_worker_id() -> str:
    pass


@fit(const.RUNTIME_GET_WORKER_INSTANCE_ID_GEN_ID)
def get_runtime_instance_id() -> str:
    pass


def _delete_result_queue(task_id: str):
    with _result_queues_lock:
        if task_id in _result_queues:
            del _result_queues[task_id]


def _broadcast_response(tasks: List, responses: List):
    result_queues = []
    with _result_queues_lock:
        for task in tasks:
            result_queues.append(_result_queues.get(task))
    for i in range(len(tasks)):
        if result_queues[i] is None:
            sys_plugin_logger.warning(f"result queue cannot found. [task_id={task}]")
            continue
        result_queues[i].put(responses[i])


def _broadcast_fail_for_instance(worker_id: str, instance_id: str, code: int, message: str):
    with _task_storage_lock:
        task_ids = _task_storage.remove_instance(worker_id, instance_id)
    fit_responses = [FitResponse(ResponseMetadata(SerializingStructureEnum.UNKNOWN.value, False,
                                                  code, message, {}), bytes()) for _ in range(len(task_ids))]
    _broadcast_response(task_ids, fit_responses)


def _broadcast_fail_for_worker_except_instance(worker_id: str, instance_id: str, code: int, message: str):
    with _task_storage_lock:
        task_ids = _task_storage.remove_tasks_except_instance(worker_id, instance_id)
    fit_responses = [FitResponse(ResponseMetadata(SerializingStructureEnum.UNKNOWN.value, False,
                                                  code, message, {}), bytes()) for _ in range(len(task_ids))]
    _broadcast_response(task_ids, fit_responses)


def _build_polling_headers(data_format):
    tlvs = {
        TagLengthValuesUtil.WORKER_ID: get_runtime_worker_id(),
        TagLengthValuesUtil.INSTANCE_ID: get_runtime_instance_id()
    }
    return {
        HttpHeader.TLV.value: base64.b64encode(TagLengthValuesUtil.serialize(tlvs)),
        HttpHeader.FORMAT.value: f"{data_format}"
    }


def _convert_polling_response_to_fit_response(response: Response) -> FitResponse:
    meta = ResponseMetadata(int(response.headers.get(HttpHeader.FORMAT.value)),
                            bool(response.headers.get(HttpHeader.DEGRADABLE.value)),
                            int(response.headers.get(HttpHeader.CODE.value)),
                            unquote(response.headers.get(HttpHeader.MESSAGE.value)),
                            TagLengthValuesUtil.deserialize(
                                base64.b64decode(response.headers.get(HttpHeader.TLV.value))))
    return FitResponse(meta, response.content)


def _polling_task(use_https: bool, polling_meta: PollingMetadata, remote_address: Address,
                  data_format: int) -> None:
    polling_fail_count = 0
    while True:
        headers = _build_polling_headers(data_format)
        url = f"{'https' if use_https else 'http'}://{remote_address.host}:{remote_address.port}" + \
              f"{remote_address.context_path}/fit/async/await-response"
        try:
            response = requests.get(url, headers=headers, timeout=get_polling_timeout(),
                                    cert=get_cert() if use_https else None,
                                    verify=get_verify() if use_https else None)
        except requests.RequestException as err:
            polling_fail_count += 1
            if polling_fail_count < _POLLING_FAIL_THRESHOLD:
                continue
            sys_plugin_logger.exception(err)
            _broadcast_fail_for_instance(polling_meta.worker_id, polling_meta.instance_id,
                                         InternalErrorCode.NETWORK_ERROR.value, "network error.")
            return
        polling_fail_count = 0
        if int(response.headers.get(HttpHeader.CODE.value)) == InternalErrorCode.ASYNC_TASK_NOT_COMPLETED.value:
            continue
        if int(response.headers.get(HttpHeader.CODE.value)) == InternalErrorCode.ASYNC_TASK_NOT_FOUND.value:
            _broadcast_fail_for_instance(polling_meta.worker_id, polling_meta.instance_id,
                                         InternalErrorCode.ASYNC_TASK_NOT_FOUND.value, "async task not found")
            return
        fit_responses = [_convert_polling_response_to_fit_response(response)]
        task_id = fit_responses[0].metadata.tlv_data.get(TagLengthValuesUtil.TASK_ID)
        with _task_storage_lock:
            _task_storage.remove_task(polling_meta.worker_id, polling_meta.instance_id, task_id)
        _broadcast_response([task_id], fit_responses)
        with _task_storage_lock:
            if _task_storage.get_task_count_of_instance(polling_meta.worker_id, polling_meta.instance_id) == 0:
                return


def _convert_sync_response_to_fit_response(response: Response) -> FitResponse:
    if response.headers.get("FIT-Metadata") is not None:
        response_metadata = ResponseMetadata.deserialize(b64decode_from_str(response.headers.get("FIT-Metadata")))
    else:
        data_format = int(response.headers.get(HttpHeader.FORMAT.value))
        degradable = bool(response.headers.get(HttpHeader.DEGRADABLE.value))
        code = int(response.headers.get(HttpHeader.CODE.value))
        message = ""
        if not response.headers.get(HttpHeader.MESSAGE.value) is None:
            message = unquote(response.headers.get(HttpHeader.MESSAGE.value))
        tlvs = TagLengthValuesUtil.deserialize(base64.b64decode(response.headers.get(HttpHeader.TLV.value)))
        response_metadata = ResponseMetadata(data_format, degradable, code, message, tlvs)
    return FitResponse(response_metadata, response.content)


def _try_to_start_polling(use_https: bool, polling_meta: PollingMetadata, remote_address: Address,
                          data_format: int):
    with _task_storage_lock:
        _task_storage.add_task(polling_meta.worker_id, polling_meta.instance_id, polling_meta.task_id)
        if _task_storage.get_task_count_of_instance(polling_meta.worker_id, polling_meta.instance_id) > 1:
            return
    thread = Thread(target=_polling_task, args=(use_https, polling_meta, remote_address, data_format))
    thread.start()


def _build_submit_task_headers(metadata: RequestMetadata, task_id: str, is_async: bool) -> Dict:
    headers = {
        HttpHeader.FORMAT.value: str(metadata.data_format),
        HttpHeader.GENERICABLE_VERSION.value: f"{metadata.generic_version.major}.{metadata.generic_version.minor}."
                                              f"{metadata.generic_version.revision}",
    }
    if is_async is None or not is_async:
        return headers
    tlvs = {
        TagLengthValuesUtil.TASK_ID: task_id, TagLengthValuesUtil.INSTANCE_ID: get_runtime_instance_id(),
        TagLengthValuesUtil.WORKER_ID: get_runtime_worker_id()
    }
    headers[HttpHeader.TLV.value] = base64.b64encode(TagLengthValuesUtil.serialize(tlvs))
    return headers


def _fit_request_response_universal(use_https: bool, remote_address: Address, metadata: RequestMetadata,
                                    data_bytes: bytes, context: RequestContext) -> FitResponse:
    task_id = str(uuid.uuid4())
    result_queue = Queue()
    with _result_queues_lock:
        _result_queues[task_id] = result_queue
    headers = _build_submit_task_headers(metadata, task_id, context.is_async)
    url = f"{'https' if use_https else 'http'}://{remote_address.host}:{remote_address.port}" + \
          f"{remote_address.context_path}/fit/{metadata.generic_id}/{metadata.fitable_id}"
    try:
        try:
            response: Response = requests.post(url, headers=headers, data=data_bytes, timeout=context.timeout,
                                               cert=get_cert() if use_https else None,
                                               verify=get_verify() if use_https else None)
        except requests.RequestException as err:
            sys_plugin_logger.exception(err)
            raise ClientException(err) from None
    except:
        _delete_result_queue(task_id)
        raise
    if response.status_code != HTTPStatus.OK and response.status_code != HTTPStatus.ACCEPTED:
        raise HTTPError(response=response)
    if (response.status_code == HTTPStatus.ACCEPTED and int(response.headers.get(HttpHeader.CODE.value)) != FIT_OK) or \
            response.status_code == HTTPStatus.OK:
        _delete_result_queue(task_id)
        return _convert_sync_response_to_fit_response(response)
    tlvs = TagLengthValuesUtil.deserialize(base64.b64decode(response.headers.get(HttpHeader.TLV.value)))
    worker_id = tlvs.get(TagLengthValuesUtil.WORKER_ID)
    instance_id = tlvs.get(TagLengthValuesUtil.INSTANCE_ID)
    _broadcast_fail_for_worker_except_instance(worker_id, instance_id, InternalErrorCode.ASYNC_TASK_NOT_FOUND.value,
                                               "async task not found")
    polling_meta = PollingMetadata(worker_id, instance_id, task_id)
    _try_to_start_polling(use_https, polling_meta, remote_address, metadata.data_format)
    result = result_queue.get()
    _delete_result_queue(task_id)
    return result


@fitable(const.REQUEST_RESPONSE_GEN_ID, const.HTTP_REQUEST_RESPONSE_FITABLE_ID)
def fit_request_response_http(remote_address: Address, metadata: RequestMetadata, data_bytes: bytes,
                              context: RequestContext) -> FitResponse:
    return _fit_request_response_universal(False, remote_address, metadata, data_bytes, context)


@fitable(const.REQUEST_RESPONSE_GEN_ID, const.HTTPS_REQUEST_RESPONSE_FITABLE_ID)
def fit_request_response_https(remote_address: Address, metadata: RequestMetadata, data_bytes: bytes,
                               context: RequestContext) -> FitResponse:
    return _fit_request_response_universal(True, remote_address, metadata, data_bytes, context)


register_client(ProtocolEnum.HTTP.value)
register_client(ProtocolEnum.HTTPS.value)
