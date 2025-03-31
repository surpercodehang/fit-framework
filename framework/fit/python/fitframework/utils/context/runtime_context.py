# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：底座环境上下文。包括输入参数和环境变量Map
"""
from collections import ChainMap

_runtime_context = ChainMap()
_KEY_SEPARATOR = '.'


def add_context(context: dict):
    global _runtime_context
    _runtime_context = _runtime_context.new_child(context)


def get_item(item: str):
    result = _runtime_context.get(item)
    if result is not None:
        return result
    keys = item.split(_KEY_SEPARATOR)
    result = _runtime_context
    for key in keys:
        result = result.get(key)
        if result is None:
            return
    return result
