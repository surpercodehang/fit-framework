# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit网络通讯CBOR序列化工具
"""
from typing import List, Tuple

from fitframework.utils.cbor_serialize_utils import cbor_serialize, cbor_deserialize


class _CborSerializer:
    def from_request_cbor(self, _, args: list) -> bytes:
        """ 客户端输入参数转 CBOR，用于发起远程通讯请求 """
        return cbor_serialize(args)

    def to_request_cbor(self, in_types: List[type], args_bytes: bytes):
        """ 服务端根据 function 定义和 CBOR 转输入参数，用于接收远程通讯请求 """
        args_type = Tuple[tuple(in_types)]
        return cbor_deserialize(args_type, args_bytes)

    def from_return_value_cbor(self, _, return_value) -> bytes:
        """ 服务端根据function定义和返回结果转 CBOR，用于响应远程通讯请求 """
        return cbor_serialize(return_value)

    def to_return_value_cbor(self, return_type: type, ret_bytes: bytes):
        """ 客户端根据function定义和FitResponse CBOR 转返回结果，用于获得远程通讯请求结果 """
        return cbor_deserialize(return_type, ret_bytes) if return_type else None
