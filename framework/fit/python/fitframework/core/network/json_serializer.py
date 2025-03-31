# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit网络通讯JSON序列化工具
"""
from typing import List, Tuple

from fitframework.utils.json_serialize_utils import json_serialize, json_deserialize


class _JsonSerializer:
    def from_request_json(self, _, args: list) -> bytes:
        """ 客户端输入参数转JSON，用于发起远程通讯请求 """
        return json_serialize(args, to_bytes=True)

    def to_request_json(self, in_types: List[type], args_bytes: bytes):
        """ 服务端根据function定义和JSON转输入参数，用于接收远程通讯请求
        """
        args_type = Tuple[tuple(in_types)]
        return json_deserialize(args_type, args_bytes)

    def from_return_value_json(self, _, return_value) -> bytes:
        """ 服务端根据function定义和返回结果转JSON，用于响应远程通讯请求 """
        return json_serialize(return_value, to_bytes=True)

    def to_return_value_json(self, return_type: type, ret_bytes: bytes):
        """ 客户端根据function定义和FitResponse JSON转返回结果，用于获得远程通讯请求结果 """
        return json_deserialize(return_type, ret_bytes) if return_type else None
