# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：cbor 序列化和反序列化方法
"""
from typing import _GenericAlias, Any, Union, Iterable
from fitframework.utils.serialize_utils import common_data_model_deserialize
from fitframework.utils.cbor.cbor import encodes, decodes

_Type = Union[type, _GenericAlias]
_Stream = Union[str, bytes]


def cbor_serialize(obj: Any) -> _Stream:
    """
    将一个对象或结构体序列化为一个字符串或字节流
    :param obj: 任意的对象或结构体，可以是公共结构体，可以是列表或字典，也可以是字符串、字节等基本类型
    :return: 序列化后的结果
    """

    def _degrade_dumps(obj_):
        return obj_.__dict__

    return encodes(obj, default=_degrade_dumps)


def cbor_deserialize(obj_types: Union[_Type, Iterable[_Type]],
                     obj_streams: Union[_Stream, Iterable[_Stream]]) -> Any:
    """
    将一个/多个字符串或字节流反序列化为对应的对象/公共结构体
    :param obj_types: 对应待反序列化到的目标类型（一个/多个）
    :param obj_streams: 待反序列化的字符串/字节流（一个/多个）
    :return: 反序列化后的结果
    """
    # 防止某些自定义类复写了__iter__方法，故两个同时判断
    if not (isinstance(obj_types, Iterable) and (
            isinstance(obj_streams, Iterable) and not isinstance(obj_streams, bytes))):
        obj_types, obj_streams = (obj_types,), (obj_streams,)

    result_s = []
    for obj_type, obj_stream in zip(obj_types, obj_streams):
        common_data_model = decodes(obj_stream)
        result_s.append(common_data_model_deserialize(obj_type, common_data_model))
    return result_s[0] if len(result_s) == 1 else result_s
