# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit内部异常类
错误码：
缺省错误码为0x7F000000
小于缺省错误码为插件用户自定义错误码
大于缺省错误码为FIT系统内部使用
"""
from enum import IntEnum
from typing import Union

import requests
from requests import RequestException, codes

from fitframework.api.exception import FitBaseException

# 内部错误消息
GENERAL_ERROR_MESSAGE = 'Internal Error'


class InternalErrorCode(IntEnum):
    FIT_EXCEPTION = 0x7F000000
    DEGRADABLE_EXCEPTION = 0x7F000001
    RETRYABLE_EXCEPTION = 0x7F000002

    # 1、 一般异常
    ROUTER_RETRIEVAL_FAILURE = 0x7F010000
    GENERICABLE_NOT_FOUND = 0x7F010001
    CLIENT_LOCAL_EXECUTOR_NOT_FOUND = 0x7F010002
    SERVER_LOCAL_EXECUTOR_NOT_FOUND = 0x7F010003
    ASYNC_TASK_NOT_ACCEPTED = 0x7F010007
    ASYNC_TASK_NOT_FOUND = 0x7F010008
    ASYNC_TASK_NOT_COMPLETED = 0x7F010009
    ASYNC_TASK_EXECUTION_ERROR = 0x7F010010

    GENERAL_ERROR = 0x7F000100  # java 不存在
    INVALID_ARGUMENTS = 0x7F000102  # java 不存在
    FITABLE_ID_DUPLICATED = 0x7F000103  # java 不存在
    SYSTEM_NOT_READY = 0x7F000104  # java 不存在
    EXCEPTION_FROM_USER_CODE_OCCURRED = 0x7F000105  # java 不存在
    TIME_OUT_EXCEPTION_FROM_USER_CODE_OCCURRED = 0x7F000106  # java 不存在
    INVALID_USER_ERROR_CODE_DEFINITION = 0x7F000107  # java 不存在

    # 2、 路由异常
    ROUTING_ERROR = 0x7F000200  # java 不存在
    FITABLE_NOT_FOUND_ERROR = 0x7F020001
    ROUTING_RULE_NOT_FOUND = 0x7F000202  # java 不存在
    AVAILABLE_FITABLE_NOT_FOUND_BY_DEFAULT_OR_REGISTRY = 0x7F000203  # java 不存在
    ROUTING_ALIAS_NOT_FOUND = 0x7F000204  # java 不存在
    ADDRESS_FILTER_ERROR = 0x7F000205  # java 不存在
    AVAILABLE_FITABLE_NOT_FOUND_BY_RULE = 0x7F000205  # java 不存在
    AVAILABLE_FITABLE_NOT_FOUND_BY_DYNAMIC_ROUTING = 0x7F000206  # java 不存在

    # 3、 负载均衡异常
    TARGET_NOT_FOUND_ERROR = 0x7F030000

    # 4、远程调用客户端的异常
    CLIENT_ERROR = 0x7F040000

    # 5、序列化异常
    SERIALIZATION_ERROR = 0x7F050000

    # 6、网络异常（均不可降级）
    NETWORK_ERROR = 0x7F000500  # java 不存在
    TIMEOUT_ERROR = 0x7F000501  # java 不存在
    CONNECT_FAILED = 0x7F000502  # java 不存在
    FAILED_TO_SEND_DATA = 0x7F000503  # java 不存在

    TOO_MANY_FITABLES_ERROR = 0x7F020000  # 表示服务实现过多的异常码，对应 java 暂未使用

    # 6、响应式异常
    FLOWABLE_ERROR = 0x7F060000  # 表示响应式编程框架的根异常码。

    # 7、缓存组件异常
    CAPACITY_OVERFLOW = 0x7FF00000  # 表示容量溢出发生的异常

    __HTTP_EXCEPTION_MAPPING__ = {
        requests.ConnectionError: CONNECT_FAILED,
        requests.Timeout: TIMEOUT_ERROR,
        requests.HTTPError: (EXCEPTION_FROM_USER_CODE_OCCURRED, FAILED_TO_SEND_DATA),
    }

    @classmethod
    def from_network_exception(cls, err: RequestException):
        return cls._from_http_exception(err)

    @classmethod
    def _from_http_exception(cls, err):
        fit_code = cls.__HTTP_EXCEPTION_MAPPING__.get(err, cls.NETWORK_ERROR)
        if not isinstance(fit_code, int):
            http_code = err.response.status_code
            if http_code >= codes.internal_server_error:
                return fit_code[0]
            elif http_code >= codes.bad_request:
                return fit_code[1]
        return fit_code


class FitException(FitBaseException):
    """ fit 框架底座抛出的异常,框架内部发生了其他的异常应该捕捉，然后抛出此异常 """

    def __init__(self, error_code, message, degradable: bool = True):
        super().__init__(error_code, message, degradable)


class FitableNotFoundException(FitException):
    """ 当无法找到指定的Fitable时引发的异常 """

    def __init__(self, generic_id, fit_id):
        message = f"fitable not found by generic id={generic_id}, fit id={fit_id}"
        super().__init__(InternalErrorCode.FITABLE_NOT_FOUND_ERROR.value, message)


class TargetNotFoundException(FitException):
    """ 当找不到服务地址时发生的异常 """

    def __init__(self, generic_id, fit_id):
        message = f"load balancing failed by generic id={generic_id}, fit id={fit_id}"
        super().__init__(InternalErrorCode.TARGET_NOT_FOUND_ERROR.value, message)


class NoAvailableFormatException(FitException):
    """ 当没有可用的消息格式时引发的异常 """

    def __init__(self, generic_id, fit_id, format_id):
        message = f"structured data format id {format_id} is not supported" \
                  f", called by generic id={generic_id}, fit id={fit_id}"
        super().__init__(InternalErrorCode.SERIALIZATION_ERROR.value, message)


class NetworkException(FitException):
    """
    网络相关异常，错误码区间段：[0x7F000500, 0x7F000600)
    网络异常的message为统一格式；均不可降级，但支持超时重试的配置
    详见 :func:`fitframework.core.broker.broker_utils.retryable`
    """

    def __init__(self, err: RequestException):
        raiser = 'http client'
        super().__init__(InternalErrorCode.from_network_exception(err), f"{type(err)} from {raiser}", degradable=False)


class ClientException(FitException):
    """
    表示远程调用发生在客户端的异常。
    """

    def __init__(self, err: RequestException):
        raiser = 'http client'
        super().__init__(InternalErrorCode.CLIENT_ERROR,
                         f"{type(err)} from {raiser}: {err.code()}", degradable=False)


class CapacityOverflowException(FitException):
    """缓存容量不足异常"""

    def __init__(self, message: str, config_max_memory: int, used_memory: int, to_allocate_memory: int):
        self.config_max_memory = config_max_memory
        self.used_memory = used_memory
        self.to_allocate_memory = to_allocate_memory
        super().__init__(InternalErrorCode.CAPACITY_OVERFLOW.value, message)


class FlowableException(FitException):
    """响应式框架运行错误时抛出的异常"""

    def __init__(self, message: str):
        """
        初始化 FlowableException 异常。
        Args:
            message: 错误消息。
        """
        super().__init__(InternalErrorCode.FLOWABLE_ERROR, message)


def from_error_code(code, message):
    return FitException(code, message) if code > InternalErrorCode.GENERAL_ERROR.value \
        else FitBaseException(code, message, True)
