# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：插件仓库
"""
from __future__ import annotations

from typing import TYPE_CHECKING, Optional
from collections import defaultdict
from typing import List, Iterable

from fitframework.core.repo.repo_utils import inspect_plugin_name_by_func
from fitframework.api.enums import PluginState

if TYPE_CHECKING:  # 表示以下依赖仅作为annotation使用
    from fitframework.domain.plugin import Plugin

_all_plugins = {}
_loaded_plugins = {}
# key: plugin name, value: list of start functions
_stop_funcs_by_plugin = defaultdict(list)
_start_funcs_by_plugin = defaultdict(list)


def get_plugin_state(plugin_name: str) -> Optional[PluginState]:
    return _loaded_plugins.get(plugin_name)


def get_plugin_start_funcs(plugin_name: str):
    return _start_funcs_by_plugin[plugin_name]


def get_plugin_stop_funcs(plugin_name: str):
    return _stop_funcs_by_plugin[plugin_name]


def get_all_plugins() -> Iterable[Plugin]:
    return _loaded_plugins.values()


def get_all_plugin_locations() -> List[str]:
    return list(map(lambda _: _.full_dir_location, _loaded_plugins.values()))


def add_plugin(plugin: Plugin):
    _loaded_plugins[repr(plugin)] = plugin


def add_plugin_start_func(func):
    _start_funcs_by_plugin[inspect_plugin_name_by_func(func)].append(func)


def add_plugin_stop_func(func):
    _stop_funcs_by_plugin[inspect_plugin_name_by_func(func)].append(func)
