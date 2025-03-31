# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：http 通信服务端测试
"""
import base64
import threading
import time
import unittest
from http import HTTPStatus
from multiprocessing import Queue
from time import sleep
from unittest.mock import patch

import requests
from requests import Response

from fitframework.core.exception.fit_exception import InternalErrorCode
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.http_header import HttpHeader
from fitframework.core.network.metadata.metadata_utils import TagLengthValuesUtil
from fitframework.core.network.metadata.request_metadata import RequestMetadata
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.testing.test_support import FitTestSupport, decorate_func_with_counter
from plugin.fit_py_server_http.http_async_server_utils import parse_unique_task_id

_TEST_HOST = '127.0.0.1'
_TEST_PORT = 9666
_TEST_CONTEXT_PATH = '/test_context_path'


class TestSubmitSyncClient:
    def __init__(self, context_path: str = "", genericable_id: str = "gid_test", fitable_id: str = "fid_test",
                 payload: object = 0):
        self.context_path: str = context_path
        self.genericable_id: str = genericable_id
        self.fitable_id: str = fitable_id
        self.payload: object = payload

    def request(self) -> Response:
        headers = {
            "FIT-Version": "2",
            "FIT-Data-Format": "1",
            "FIT-Genericable-Version": "1.0.0",
        }
        url = f"http://{_TEST_HOST}:{_TEST_PORT}/{self.context_path}/fit/{self.genericable_id}/{self.fitable_id}"
        return requests.request("POST", url, json=self.payload, headers=headers)


class TestSubmitAsyncClient:
    def __init__(self, context_path: str = "", genericable_id: str = "gid_test", fitable_id: str = "fid_test",
                 payload: object = 0, worker_id: str = "wid-0", instance_id: str = "iid-0", task_id: str = "tid-0"):
        self.context_path: str = context_path
        self.genericable_id: str = genericable_id
        self.fitable_id: str = fitable_id
        self.payload: object = payload
        self.instance_id: str = instance_id
        self.worker_id: str = worker_id
        self.task_id: str = task_id

    def request(self) -> Response:
        tlv_data = {
            TagLengthValuesUtil.WORKER_ID: self.worker_id,
            TagLengthValuesUtil.INSTANCE_ID: self.instance_id,
            TagLengthValuesUtil.TASK_ID: self.task_id,
        }
        headers = {
            "FIT-TLV": base64.b64encode(TagLengthValuesUtil.serialize(tlv_data=tlv_data)),
            "FIT-Version": "2",
            "FIT-Data-Format": "1",
            "FIT-Genericable-Version": "1.0.0",
        }
        url = f"http://{_TEST_HOST}:{_TEST_PORT}/{self.context_path}/fit/{self.genericable_id}/{self.fitable_id}"
        return requests.request("POST", url, json=self.payload, headers=headers)


class TestAsyncAwaitClient:
    def __init__(self, context_path: str = "", worker_id: str = "wid-0", instance_id: str = "iid-0"):
        self.context_path: str = context_path
        self.instance_id: str = instance_id
        self.worker_id: str = worker_id

    def request(self) -> Response:
        tlv_data = {
            TagLengthValuesUtil.WORKER_ID: self.worker_id,
            TagLengthValuesUtil.INSTANCE_ID: self.instance_id,
        }
        headers = {
            "FIT-TLV": base64.b64encode(TagLengthValuesUtil.serialize(tlv_data=tlv_data)),
            "FIT-Version": "2",
            "FIT-Data-Format": "1",
            "FIT-Genericable-Version": "1.0.0",
        }
        url = f"http://{_TEST_HOST}:{_TEST_PORT}/{self.context_path}/fit/async/await-response"
        return requests.request("GET", url, headers=headers)


def mock_server_response_side_effect(request_meta: RequestMetadata, payload: bytes):
    number = int(payload.decode("utf-8"))

    if number <= -10000:
        return FitResponse(
            ResponseMetadata(2, False, 0xDEADBEEF,
                             "line\nnew line!@#$%^&*()_+{}|:<>?,./;'[]-=\"~`让我们说中文\n", {}), b"")
    if number >= 0:
        sleep(number)
    return FitResponse(ResponseMetadata(1, False, 0, "OK", {}), str(number).encode("utf-8"))


class HttpServerTest(FitTestSupport):
    @classmethod
    def setUpClass(cls):
        super(HttpServerTest, cls).setUpClass()
        from plugin.fit_py_server_http import http_server
        from plugin.fit_py_server_http import fit_http_server
        from plugin.fit_py_server_http import http_async_server_utils

        global http_server
        global fit_http_server
        global http_async_server_utils

        cls.patchers = [
            patch.object(http_server, 'register_exposed_server'),
            patch.object(http_server, 'get_http_enabled', return_value=True),
            patch.object(http_server, 'get_runtime_worker_id', return_value="test_worker_id"),
            patch.object(http_server, 'get_host', return_value=_TEST_HOST),
            patch.object(http_server, 'get_http_server_port', return_value=_TEST_PORT),
            patch.object(fit_http_server, 'get_context_path', return_value=""),
            patch.object(fit_http_server, 'get_task_count_limit', return_value=1000),
            patch.object(fit_http_server, 'server_response', side_effect=mock_server_response_side_effect),
            patch.object(fit_http_server, 'get_context_path', return_value=_TEST_CONTEXT_PATH),
            patch.object(fit_http_server, 'get_polling_wait_time', return_value=2),
            patch.object(http_async_server_utils, 'get_result_save_duration', return_value=1),
        ]
        mocks = [_.start() for _ in cls.patchers]
        cls.mock_register_server = mocks[0]
        cls.mock_server_response = mocks[1]
        http_server.server_start()

    @classmethod
    def tearDownClass(cls):
        http_server.server_stop()
        [_.stop() for _ in cls.patchers]

    def test_should_return_ok_when_call_health_check(self, *_):
        url = f"http://{_TEST_HOST}:{_TEST_PORT}/fit/health"
        response = requests.request("GET", url)

        self.assertTrue(self.mock_server_response.called)
        self.assertTrue(self.mock_server_response.called)
        self.assertEqual(response.status_code, HTTPStatus.OK)
        self.assertEqual(response.content, b"OK")

    def test_should_return_quoted_message_when_sync_response_error_message(self, *_):
        response: Response = TestSubmitSyncClient(payload=-10000).request()
        self.assertEqual(response.headers.get(HttpHeader.MESSAGE.value),
                         'line%0Anew%20line%21%40%23%24%25%5E%26%2A%28%29_%2B%7B%7D%7C%3A%3C%3E%3F%2C./%3B%27%5B'
                         '%5D-%3D%22~%60%E8%AE%A9%E6%88%91%E4%BB%AC%E8%AF%B4%E4%B8%AD%E6%96%87%0A')

    def test_should_return_same_result_when_call_with_context_path(self):
        response = TestSubmitSyncClient(context_path=_TEST_CONTEXT_PATH).request()
        self.assertTrue(self.mock_server_response.called)
        self.assertEqual(response.status_code, HTTPStatus.OK)
        self.assertEqual(response.content, b"0")

    def test_should_return_error_when_call_with_incorrect_context_path(self):
        response = TestSubmitSyncClient(context_path="/incorrect_context_path").request()
        self.assertTrue(self.mock_server_response.called)
        self.assertNotEqual(response.status_code, HTTPStatus.OK)

    def test_should_return_status_accepted_when_send_async_submit_request(self):
        response = TestSubmitAsyncClient().request()
        self.assertTrue(self.mock_server_response.called)
        self.assertEqual(response.status_code, HTTPStatus.ACCEPTED)

    def test_should_polling_return_immediately_when_calculated_immediately(self):
        TestSubmitAsyncClient().request()
        response = TestAsyncAwaitClient().request()
        self.assertEqual(int(response.headers.get(HttpHeader.CODE.value)), 0)
        self.assertEqual(int(response.headers.get(HttpHeader.FORMAT.value)), 1)
        self.assertEqual(response.content, b"0")

    def test_should_reject_task_if_too_many_task_is_running(self):
        with patch.object(fit_http_server, 'get_task_count_limit', return_value=1):
            TestSubmitAsyncClient(payload=1).request()
            response = TestSubmitAsyncClient(task_id="tid-1").request()
            self.assertEqual(int(response.headers.get(HttpHeader.CODE.value)),
                             InternalErrorCode.ASYNC_TASK_NOT_ACCEPTED.value)
            TestAsyncAwaitClient().request()

    def test_should_polling_return_by_order_immediately_when_send_many(self):
        for i in range(0, -100, -1):
            TestSubmitAsyncClient(task_id=f"tid-{abs(i)}", payload=i).request()
            response = TestAsyncAwaitClient().request()
            self.assertEqual(response.headers.get(HttpHeader.CODE.value), "0")
            unique_task_id = parse_unique_task_id(response.headers)
            self.assertEqual(unique_task_id.task_id, f"tid-{abs(i)}")
            self.assertEqual(response.content, f"{i}".encode("utf-8"))

    def test_should_return_correctly_when_submit_and_polling_parallely(self):
        client_count = 5
        task_count = 50
        queues = [Queue() for _ in range(client_count)]
        finish_queues = [Queue() for _ in range(client_count)]

        def task_submit(worker_index: int, count: int):
            for i in range(0, -count - 1, -1):
                TestSubmitAsyncClient(worker_id=f"wid-{worker_index}", instance_id=f"iid-{worker_index}",
                                      task_id=f"tid-{abs(i)}", payload=i).request()
                if i == 0:
                    queues[worker_index].put(True)

        def task_polling(worker_index: int, count: int):
            queues[worker_index].get()
            for i in range(0, -count - 1, -1):
                response = TestAsyncAwaitClient(worker_id=f"wid-{worker_index}",
                                                instance_id=f"iid-{worker_index}").request()
                self.assertEqual(response.headers.get(HttpHeader.CODE.value), "0")
                unique_task_id = parse_unique_task_id(response.headers)
                self.assertEqual(unique_task_id.task_id, f"tid-{abs(i)}")
                self.assertEqual(response.content, f"{i}".encode("utf-8"))

            finish_queues[worker_index].put(True)

        for index in range(client_count):
            threading.Thread(target=task_submit, args=(index, task_count)).start()
        for index in range(client_count):
            threading.Thread(target=task_polling, args=(index, task_count)).start()

        for index in range(client_count):
            finish_queues[index].get()

    def test_should_polling_twice_when_calculated_for_three_second(self):
        TestSubmitAsyncClient(payload=3).request()
        response = TestAsyncAwaitClient().request()
        self.assertEqual(int(response.headers.get(HttpHeader.CODE.value)),
                         InternalErrorCode.ASYNC_TASK_NOT_COMPLETED.value)
        response = TestAsyncAwaitClient().request()
        self.assertEqual(int(response.headers.get(HttpHeader.CODE.value)), 0)
        self.assertEqual(int(response.headers.get(HttpHeader.FORMAT.value)), 1)
        self.assertEqual(response.content, b"3")

    def test_should_return_task_not_found_when_given_incorrect_worker_id(self):
        TestSubmitAsyncClient().request()
        response = TestAsyncAwaitClient(worker_id="incorrect_worker_id").request()
        self.assertEqual(int(response.headers.get(HttpHeader.CODE.value)),
                         InternalErrorCode.ASYNC_TASK_NOT_FOUND.value)

    def test_should_discard_expired_results_when_next_submit(self):
        cached_remove_expired_worker_info = fit_http_server._worker_info_manager._remove_expired_worker_info
        fit_http_server._worker_info_manager._remove_expired_worker_info, counter = decorate_func_with_counter(
            fit_http_server._worker_info_manager._remove_expired_worker_info)
        TestSubmitAsyncClient().request()
        time.sleep(1.5)
        TestSubmitAsyncClient(instance_id="iid-1", task_id="tid-1").request()
        self.assertEqual(counter.count, 1)
        response = TestAsyncAwaitClient(instance_id="iid-1").request()
        self.assertEqual(response.headers.get(HttpHeader.CODE.value), "0")
        unique_task_id = parse_unique_task_id(response.headers)
        self.assertEqual(unique_task_id.task_id, "tid-1")
        fit_http_server._worker_info_manager._remove_expired_worker_info = cached_remove_expired_worker_info

    def test_should_discard_expired_instance_when_new_instance_create(self):
        cached_create_new_worker_info = fit_http_server._worker_info_manager._create_new_worker_info
        fit_http_server._worker_info_manager._create_new_worker_info, counter = decorate_func_with_counter(
            fit_http_server._worker_info_manager._create_new_worker_info)
        TestSubmitAsyncClient().request()
        TestSubmitAsyncClient(instance_id="iid-1", task_id="tid-1").request()
        self.assertEqual(counter.get_count(), 2)
        response = TestAsyncAwaitClient(instance_id="iid-1").request()
        self.assertEqual(response.headers.get(HttpHeader.CODE.value), "0")
        unique_task_id = parse_unique_task_id(response.headers)
        self.assertEqual(unique_task_id.task_id, "tid-1")
        fit_http_server._worker_info_manager._create_new_worker_info = cached_create_new_worker_info


if __name__ == '__main__':
    unittest.main()
