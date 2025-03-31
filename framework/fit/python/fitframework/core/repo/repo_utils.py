# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：插件仓库工具类
"""
from typing import Optional


def inspect_plugin_name_by_func(func) -> Optional[str]:
    """
    根据方法指针获得方法所在的插件名称
    原为：service_db公共服务

    Args:
        func (function): 方法指针

    Returns:
        str: 所在的插件名
    """
    return func.__module__.partition('.')[0]
