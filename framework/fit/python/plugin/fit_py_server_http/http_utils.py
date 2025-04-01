# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit底座Http Server服务接口依赖的工具模块
"""
import base64
import ssl
import threading
from collections import defaultdict
from queue import Queue
from typing import Optional, List, Dict
from urllib.parse import quote

from fit_common_struct.core import Address
from fitframework import const
from fitframework.api.decorators import fit, value, run_once
from fitframework.core.network.enums import ProtocolEnum, SerializingStructureEnum
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.http_header import HttpHeader
from fitframework.core.network.metadata.metadata_utils import TagLengthValuesUtil
from fitframework.core.network.metadata.request_metadata import RequestMetadata
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.utils.tools import to_list, to_bool, get_free_tcp_port

_TLS_PROTOCOLS = {
    "TLS_V1_3": ssl.PROTOCOL_TLS,
    "TLS_V1_2": ssl.PROTOCOL_TLSv1_2
}

_TRUSTED_CIPHERS = ["DHE-RSA-AES128-GCM-SHA256",
                    "DHE-RSA-AES256-GCM-SHA384",
                    "DHE-DSS-AES128-GCM-SHA256",
                    "DHE-DSS-AES256-GCM-SHA384",
                    "ECDHE-ECDSA-AES128-GCM-SHA256",
                    "ECDHE-ECDSA-AES256-GCM-SHA384",
                    "ECDHE-RSA-AES128-SHA256",
                    "ECDHE-RSA-AES256-SHA384",
                    "ECDHE-RSA-CHACHA20-POLY1305-SHA256",
                    "ECDHE-PSK-CHACHA20-POLY1305-SHA256",
                    "ECDHE-PSK-AES128-GCM-SHA256",
                    "ECDHE-PSK-AES256-GCM-SHA384",
                    "ECDHE-PSK-AES128-CCM-SHA256",
                    "DHE-RSA-AES128-CCM",
                    "DHE-RSA-AES256-CCM",
                    "DHE-RSA-CHACHA20-POLY1305-SHA256",
                    "PSK-AES256-CCM",
                    "DHE-PSK-AES128-CCM",
                    "DHE-PSK-AES256-CCM",
                    "ECDHE-ECDSA-AES128-CCM",
                    "ECDHE-ECDSA-AES256-CCM",
                    "ECDHE-ECDSA-CHACHA20-POLY1305-SHA256",
                    "AES128-GCM-SHA256",
                    "AES256-GCM-SHA384",
                    "CHACHA20-POLY1305-SHA256",
                    "AES128-CCM-SHA256"]


class WorkerInfo:
    def __init__(self, instance_id: str, result_queue: Queue):
        self.instance_id = instance_id
        self.result_queue = result_queue


class AsyncExecuteResult:
    def __init__(self, task_id: str, meta: ResponseMetadata, data: bytes, finished_time):
        self.task_id = task_id
        self.meta = meta
        self.data = data
        self.finished_time = finished_time


@value('local_ip')
def get_host():
    pass


@value(key='worker-environment.env')
def get_worker_env():
    pass


@value(key='context-path', default_value="")
def get_context_path():
    pass


@value('server-thread-count', 8, converter=int)
def get_server_thread_count():
    pass


@value('http.server.enabled', False, converter=to_bool)
def get_http_enabled():
    pass


@value('http.server.address.protocol', default_value=ProtocolEnum.HTTP.value, converter=int)
def get_http_protocol():
    pass


@value('http.server.address.formats', default_value=[SerializingStructureEnum.JSON.value], converter=to_list)
def get_http_formats():
    pass


@value('http.server.address.use-random-port', converter=to_bool)
def _get_http_use_random_port():
    pass


@value('http.server.address.port', converter=int)
def _get_http_server_port():
    pass


@value('http.server.address.port-to-register', converter=int, default_value=_get_http_server_port())
def get_http_server_port_to_register():
    pass


@value('https.server.enabled', default_value=False, converter=to_bool)
def get_https_enabled():
    pass


@value('https.server.ssl_enabled', default_value=True, converter=to_bool)
def get_ssl_enabled():
    pass


@value('https.server.crt_path')
def get_server_crt_path():
    pass


@value('https.server.key_path')
def get_server_key_path():
    pass


@value('https.server.key_file_encrypted')
def get_server_key_file_encrypted():
    pass


@value('https.server.key_file_password')
def get_server_key_file_password():
    pass


@value('HTTPS__SERVER__KEY__FILE__PASSWORD')
def get_server_key_file_password_env():
    pass


@value('https.server.key_file_password_encrypted')
def get_server_key_file_password_encrypted():
    pass


@value('https.server.tls_protocol')
def _get_server_tls_protocol():
    pass


@value('https.server.ciphers', converter=to_list, default_value=[])
def _get_server_ciphers() -> List[str]:
    pass


@value('https.server.verify_enabled', converter=to_bool)
def get_server_verify_enabled():
    pass


@value('https.server.ca_path')
def get_server_ca_path():
    pass


@value('https.server.assert_host_name')
def get_server_assert_host_name():
    pass


@value('https.server.address.protocol', default_value=ProtocolEnum.HTTPS.value, converter=int)
def get_https_protocol():
    pass


@value('https.server.address.formats', default_value=[SerializingStructureEnum.JSON.value], converter=to_list)
def get_https_formats():
    pass


@value('https.server.address.use-random-port', converter=to_bool)
def _get_https_use_random_port():
    pass


@value('https.server.address.port', converter=int)
def _get_https_server_port():
    pass


@value('https.server.address.port-to-register', converter=int, default_value=_get_https_server_port())
def get_https_server_port_to_register():
    pass


@value('async.task-count-limit', converter=int)
def get_task_count_limit() -> int:
    pass


@value('async.polling-wait-time', converter=int)
def get_polling_wait_time():
    pass


@fit(generic_id=const.SERVER_RESPONSE_GEN_ID, alias='python_default_server_response')
def server_response(metadata: RequestMetadata, data: bytes) -> FitResponse:
    pass


@fit("modelengine.fit.security.decrypt")
def decrypt(cipher: str) -> str:
    """
    对于加密后内容进行解密。
    特别注意：
    1. 该接口的 fitable 实现需要通过本地静态插件的方式给出；
    2. 必须在 fit.yml 中指定要调用的 fitable id。

    :param cipher: 待解密的内容。
    :return: 解密后的内容。
    """
    pass


@fit(const.RUNTIME_GET_WORKER_ID_GEN_ID)
def get_runtime_worker_id() -> str:
    pass


@fit(const.RUNTIME_GET_WORKER_INSTANCE_ID_GEN_ID)
def get_runtime_instance_id() -> str:
    pass


@run_once
def get_http_server_port() -> int:
    if _get_http_use_random_port():
        return get_free_tcp_port()
    else:
        return _get_http_server_port()


@run_once
def get_https_server_port() -> int:
    if _get_https_use_random_port():
        return get_free_tcp_port()
    else:
        return _get_https_server_port()


def get_server_tls_protocol():
    return _TLS_PROTOCOLS.get(_get_server_tls_protocol(), ssl.PROTOCOL_TLS)


def get_server_ciphers() -> str:
    """
    获取加密套件列表。

    @return: 表示所获取到的加密列表的字符串，如果有多个加密套件，则通过冒号连接。
    """
    if len(_get_server_ciphers()) == 0:
        selected_ciphers = _TRUSTED_CIPHERS
    else:
        selected_ciphers = [each for each in _get_server_ciphers() if each in _TRUSTED_CIPHERS]
    if len(selected_ciphers) == 0:
        raise ValueError("No ciphers suites available.")
    concatenated_ciphers = ""
    for cipher in selected_ciphers:
        concatenated_ciphers += cipher + ":"
    return concatenated_ciphers[:-1]


def _get_sever_password():
    if get_server_key_file_password_env() is not None:
        return get_server_key_file_password_env()
    else:
        return get_server_key_file_password()


def get_decrypted_key_file_password():
    # 对于 https 服务端，不存在不需要客户端验证自身的情况。
    if not get_server_key_file_encrypted():  # 如果私钥未被加密
        return None
    if not get_server_key_file_password_encrypted():  # 如果私钥密码没有被加密
        return _get_sever_password()

    return decrypt(_get_sever_password())


def http_server_start(address: Address, event: threading.Event = None):
    if event:
        event.set()


def build_response_headers_by_response_metadata(metadata: ResponseMetadata, task_id: Optional[str]) -> Dict[str, str]:
    """
    构造提交任务请求以及长轮询请求时响应结果的响应头，其中 FIT-Async-Task-Id 为可选字段。

    @param metadata: 表示响应元数据。
    @param task_id: 表示任务标识，可以为 None。
    @return: 所创建的 HTTP 响应头集合。
    """
    tlvs = {
        TagLengthValuesUtil.INSTANCE_ID: get_runtime_instance_id(),
        TagLengthValuesUtil.WORKER_ID: get_runtime_worker_id()
    }
    if task_id is not None:
        tlvs[TagLengthValuesUtil.TASK_ID] = task_id
    return {
        HttpHeader.FORMAT.value: f"{metadata.data_format}",
        HttpHeader.DEGRADABLE.value: f"{metadata.degradable}",
        HttpHeader.CODE.value: f"{metadata.code}",
        HttpHeader.MESSAGE.value: quote(metadata.msg),
        HttpHeader.TLV.value: base64.b64encode(TagLengthValuesUtil.serialize(tlvs))
    }


class StartServeTimeManager:
    """
    开始服务时间管理器。
    """

    def __init__(self):
        self._start_serve_times_lock: threading.Lock = threading.Lock()
        self._start_serve_times: defaultdict = defaultdict(float)

    def add_start_serve_time(self, start_server_time: float):
        with self._start_serve_times_lock:
            self._start_serve_times[start_server_time] += 1

    def remove_start_serve_time(self, start_server_time: float):
        with self._start_serve_times_lock:
            self._start_serve_times[start_server_time] -= 1
            if self._start_serve_times[start_server_time] <= 0:
                del self._start_serve_times[start_server_time]

    def get_earliest_start_time(self) -> Optional[float]:
        with self._start_serve_times_lock:
            start_times = list(self._start_serve_times.keys())
            start_times.sort()
            if len(start_times) == 0:
                return None
            return start_times[0]


if __name__ == '__main__':
    http_server_start(Address('127.0.0.1', 8000, None, None, None, 'test'))
