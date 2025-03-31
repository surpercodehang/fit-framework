# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：json 序列化和反序列化方法
"""
import json
from typing import _GenericAlias, Any, Union, Iterable
from fitframework.utils.tools import b64encode_to_str
from fitframework.utils.serialize_utils import common_data_model_deserialize
from fitframework.const import DEFAULT_CODECS

_Type = Union[type, _GenericAlias]
_Stream = Union[str, bytes]


def json_serialize(obj: Any, to_bytes: bool = False) -> _Stream:
    """
    将一个对象或结构体序列化为一个字符串或字节流
    :param obj: 任意的对象或结构体，可以是公共结构体，可以是列表或字典，也可以是字符串、字节等基本类型
    :param to_bytes: 是否序列化到字节流的程度
    :return: 序列化后的结果
    """

    def _degrade_dumps(obj):
        return b64encode_to_str(obj) if isinstance(obj, bytes) else obj.__dict__

    obj_string = json.dumps(obj, default=_degrade_dumps)
    if to_bytes:
        return bytes(obj_string, DEFAULT_CODECS)
    return obj_string


def json_deserialize(obj_types: Union[_Type, Iterable[_Type]],
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
        if isinstance(obj_stream, bytes):
            obj_stream = obj_stream.decode(DEFAULT_CODECS)
        # json.loads返回的结果只反序列化到dict结构
        obj_in_json = json.loads(obj_stream)
        result_s.append(common_data_model_deserialize(obj_type, obj_in_json))
    return result_s[0] if len(result_s) == 1 else result_s
