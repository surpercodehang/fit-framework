# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：value方法指针注册。
"""
from fitframework.api.logging import fit_logger

from fitframework.core.repo.repo_utils import inspect_plugin_name_by_func
from fitframework.core.repo.service_repo import _set_value_ref


def register_value_ref(func, key: str):
    plugin_name = inspect_plugin_name_by_func(func)
    if plugin_name is None:
        fit_logger.error(
                    f"not able to find registered plugin for @value: {func.__name__}")
        return

    fit_logger.info(f'register @value {plugin_name}:{key}')
    _set_value_ref(plugin_name, key, func)
