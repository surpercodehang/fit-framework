# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：数据实体定义
"""
from typing import List


class RequestContext:
    def __init__(self, timeout: int, is_async: bool):
        self.timeout = timeout
        self.is_async = is_async


class Pair:
    def __init__(self, key: str, value: str):
        self.key = key
        self.value = value


class GlobalContext:
    def __init__(self, pair: List[Pair]):
        self.pair = pair
