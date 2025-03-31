# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：FitableTrace结构体所依赖的相关枚举，用于Trace日志输出。
"""
from enum import IntEnum


class TraceType(IntEnum):
    RPC = 0
    CACHE = 1
    DB = 2
    QUEUE = 3


class FlowType(IntEnum):
    NORMAL = 0
    TEST = 1
    MOCK = 2


class Stage(IntEnum):
    IN = 0
    OUT = 1


class CallType(IntEnum):
    LOCAL_TO_LOCAL = 1
    LOCAL_TO_REMOTE = 2
    REMOTE_TO_LOCAL = 3


class TrustStage(IntEnum):
    VALIDATION = 0
    BEFORE = 1
    PROCESS = 2
    DEGRADATION = 3
    AFTER = 4
    ERROR = 5
