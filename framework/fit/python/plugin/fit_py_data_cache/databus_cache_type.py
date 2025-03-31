# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：提供DataBus缓存的可选择类型。
"""
from enum import Enum


class DataBusCacheContentType(Enum):
    STRING = (b"\x01", "str")
    BYTES = (b"\x02", "bytes")
    UNKNOWN = (b"\xff", "unknown")

    @classmethod
    def from_bytecode(cls, byte_code: bytes):
        """
        返回本枚举值对应的bytecode

        :param byte_code: ContentType对应的bytecode, 例如b"1"
        :return: bytecode对应的枚举值, 如果没有则返回UNKNOWN
        """
        ret = list(filter(lambda item: item[1].value[0] == byte_code, cls.__members__.items()))
        return ret[0][1] if ret and len(ret[0]) > 1 else cls.UNKNOWN

    def to_bytecode(self) -> bytes:
        """
        返回本枚举值对应的bytecode

        :return: ContentType对应的bytecode, 例如b"1"
        """
        return self.value[0]

    def to_name(self) -> str:
        """
        返回本枚举值对应的名称

        :return: ContentType对应的名称, 例如"str"
        """
        return self.value[1]
