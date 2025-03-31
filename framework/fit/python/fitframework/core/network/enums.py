# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：通讯协议和规约类型枚举
"""
from enum import IntEnum


class ProtocolEnum(IntEnum):
    UNKNOWN = -1
    RSOCKET = 0,
    SOCKET = 1,
    HTTP = 2,
    GRPC = 3,
    HTTPS = 4,
    SHARE_MEMORY = 11
    UC = 12,


class SerializingStructureEnum(IntEnum):
    UNKNOWN = -1
    PROTOBUF = 0
    JSON = 1
    CBOR = 2


