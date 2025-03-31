# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：提供插件工具
"""
import os.path
from typing import Dict, List
import yaml

from fitframework.api.logging import fit_logger

_STATIC_PLUGINS_PATH = "plugin"
_PLUGIN_CONF_DIR = "conf"
_PLUGIN_CONF_FILE_NAME = "info.yml"
_PLUGIN_NAME_KEY = "name"
_PLUGIN_LOCATION_KEY = "location"
_PLUGIN_CATEGORY_KEY = "category"
_PLUGIN_LEVEL_KEY = "level"

# 用户可指定插件级别的约定，对于系统插件和用户插件优先级取值均为 [1, 7]，有别于框架内部对于优先级取值的约定
_MAX_PLUGIN_LEVEL = 1
_MIN_PLUGIN_LEVEL = 7
_DEFAULT_PLUGIN_LEVEL = 4

_all_plugins_info: Dict = None


def load_plugin_info(plugin_path: str, plugin_name: str) -> dict:
    """
    加载插件通过内置配置文件提供的插件信息，校验和补全其中的配置，并对于优先级信息进行转义，即将系统插件优先级调整为负数方便后续处理。
    :param plugin_path: 插件的相对路径
    :param plugin_name: 插件的名称
    :return: 通过字典形式表示的插件信息
    """
    if plugin_path is None:
        raise Exception("load plugin failed, plugin path is None.")
    if plugin_name is None:
        raise Exception("load plugin failed, plugin name is None.")
    plugin_abs_path = os.path.join(os.getcwd(), plugin_path, plugin_name)
    info_file_path = os.path.join(plugin_abs_path, _PLUGIN_CONF_DIR, _PLUGIN_CONF_FILE_NAME)
    plugin_info = {_PLUGIN_NAME_KEY: plugin_name, _PLUGIN_LOCATION_KEY: os.path.join(plugin_path, plugin_name)}
    if not os.path.isfile(info_file_path):
        fit_logger.warning(f"the plugin information file info.yml is missing. [plugin_name={plugin_name}]")
        plugin_info.update({_PLUGIN_CATEGORY_KEY: "user", _PLUGIN_LEVEL_KEY: _DEFAULT_PLUGIN_LEVEL})
        return plugin_info
    with open(info_file_path, encoding='utf-8') as file:
        plugin_info.update(yaml.safe_load(file))
    if plugin_info.get(_PLUGIN_CATEGORY_KEY) is None or plugin_info.get(_PLUGIN_CATEGORY_KEY) not in ["system", "user"]:
        fit_logger.warning(f"the plugin category info not set correctly. [plugin_name={plugin_name}]")
        plugin_info[_PLUGIN_CATEGORY_KEY] = "user"
    if (plugin_info.get(_PLUGIN_LEVEL_KEY) is None or not isinstance(plugin_info.get(_PLUGIN_LEVEL_KEY), int) or
            not _MAX_PLUGIN_LEVEL <= plugin_info.get(_PLUGIN_LEVEL_KEY) <= _MIN_PLUGIN_LEVEL):
        fit_logger.warning(f"the plugin level info not set correctly. [plugin_name={plugin_name}]")
        plugin_info[_PLUGIN_LEVEL_KEY] = _DEFAULT_PLUGIN_LEVEL
    if plugin_info[_PLUGIN_CATEGORY_KEY] == "system":
        # 将系统插件的级别调整为负数，方便后续使用
        plugin_info[_PLUGIN_LEVEL_KEY] -= (_MIN_PLUGIN_LEVEL + _MAX_PLUGIN_LEVEL)
    return plugin_info


def _load_all_plugins_info() -> List[dict]:
    """
    加载一组插件的信息
    :return: 插件信息列表
    """
    plugin_infos = []
    if not os.path.isdir(_STATIC_PLUGINS_PATH):
        fit_logger.warning(f"path of static plugins is not exist.")
        return []
    for dir_name in os.listdir(_STATIC_PLUGINS_PATH):
        if not os.path.isdir(os.path.join(_STATIC_PLUGINS_PATH, dir_name)) or dir_name == "__pycache__":
            continue
        plugin_infos.append(load_plugin_info(_STATIC_PLUGINS_PATH, dir_name))
    return plugin_infos


def get_all_plugins_info() -> List:
    """
    获取所有系统插件和静态用户插件的信息
    :return: 插件信息列表
    """
    global _all_plugins_info
    if _all_plugins_info is None:
        _all_plugins_info = _load_all_plugins_info()
    return _all_plugins_info
