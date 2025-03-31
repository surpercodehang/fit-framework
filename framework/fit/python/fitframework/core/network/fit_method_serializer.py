# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit网络通讯序列化工具
"""
import inspect

from fitframework.core.network.cbor_serializer import _CborSerializer
from fitframework.core.network.enums import SerializingStructureEnum
from fitframework.core.network.json_serializer import _JsonSerializer
from fitframework.utils.serialize_utils import get_parameter_types, get_return_type


class _FlyweightMixin(type):
    """
    metaclass for converter, creates flyweight instances by function instance and mixin actual
    converter functions in _JsonConverter and _ProtobufConverter
    """
    _instances = {}

    def __init__(cls, name, bases, dic):
        super(_FlyweightMixin, cls).__init__(name, bases, dic)
        _member_list = (_JsonSerializer, _CborSerializer)

        for impl_member in _member_list:
            if not impl_member:
                continue

            for method_name, func in inspect.getmembers(impl_member, inspect.isfunction):
                setattr(cls, method_name, func)

    def __call__(cls, *args, **kwargs):
        if args[0] not in cls._instances:
            cls._instances[args[0]] = super(_FlyweightMixin, cls).__call__(*args, **kwargs)
        return cls._instances[args[0]]


class FitMethodSerializer(metaclass=_FlyweightMixin):
    """
    请求：fitable -> client --args--> from_request -> bytes ---远程通讯--- server -> to_request -> args -> fitable

    返回：server -> from_return_value ->bytes ---远程通讯--- client -> to_return_value  -> ret_val -> fitable
    """

    def __init__(self, function):
        self._inspect_function(function)

    def from_request(self, fmt: int, args: list) -> bytes:
        """ 客户端根据function定义和输入参数转FitRequest，用于发起远程通讯请求 """
        return getattr(FitMethodSerializer,
                       f"from_request_{SerializingStructureEnum(fmt).name.lower()}")(self, self._in_types, args)

    def to_request(self, fmt: int, data: bytes) -> list:
        """ 服务端根据function定义和FitRequest转输入参数，用于接收远程通讯请求 """
        return getattr(FitMethodSerializer,
                       f"to_request_{SerializingStructureEnum(fmt).name.lower()}")(self, self._in_types, data)

    def from_return_value(self, fmt: int, return_value) -> bytes:
        """ 服务端根据function定义和方法结果序列化，用于响应远程通讯请求 """
        return getattr(FitMethodSerializer, f"from_return_value_{SerializingStructureEnum(fmt).name.lower()}")(
            self, self._return_type, return_value)

    def to_return_value(self, fmt: int, response_data: bytes):
        """
        客户端根据function定义和FitResponse数据反序列化，用于获得远程通讯请求结果。
        函数签名的返回值类型可能被覆盖。
        """
        return getattr(FitMethodSerializer, f"to_return_value_{SerializingStructureEnum(fmt).name.lower()}")(
            self, self._return_type, response_data)

    def _inspect_function(self, function):
        self._in_types = get_parameter_types(function)
        self._return_type = get_return_type(function)
