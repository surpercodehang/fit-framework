# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：动态插件加载。
"""
import os
import re
import shutil
import subprocess
import sys
import tarfile
import threading
import time
import traceback
import zipfile
from collections import defaultdict
from typing import List

from fitframework import const
from fitframework.api.decorators import fit, fitable, value
from fitframework.api.enums import PluginType
from fitframework.api.logging import sys_plugin_logger
from fitframework.domain.plugin import Plugin
from fitframework.utils.plugin_utils import load_plugin_info
from fitframework.utils.tools import get_memory_usage
from .utils import validate_zipped_file_and_dir_names

_versions = defaultdict(lambda: 0)
_invalid_pattern = re.compile(r'(\.\.[\\/])|(^[\\/])|(^[a-zA-Z]:)')


@value('user_plugins_path')
def _user_plugins_path() -> str:
    pass


@value('cache_path')
def _cache_path() -> str:
    pass


@value("third_party_cache_path", default_value="third_party_cache")
def _third_party_cache_path():
    pass


@value('scan_period', default_value=3, converter=int)
def _scan_period() -> int:
    pass


@value('python_interpreter_alias')
def _python_interpreter_alias() -> str:
    pass


if os.path.exists(os.path.join(os.getcwd(), _third_party_cache_path())):
    shutil.rmtree(os.path.join(os.getcwd(), _third_party_cache_path()))
os.mkdir(os.path.join(os.getcwd(), _third_party_cache_path()))
sys.path.append(_third_party_cache_path())


def _check_command_availability(command) -> bool:
    return subprocess.run([command, "--version"], capture_output=True, text=True).stdout.strip().startswith("Python")


@fit(const.SERVICE_DB_REGISTER_ALL_FIT_SERVICE_GEN_ID)
def register_all_fit_services() -> None:
    pass


def _get_decompressed_plugin_name(plugin_name):
    if _versions[plugin_name] == 1:
        return plugin_name
    return f"{plugin_name}_{_versions[plugin_name]}"


def _decompress_plugin_and_get_level(plugin_path: str) -> (str, str):
    file_name = os.path.basename(plugin_path)
    plugin_name = file_name.split('.')[0]
    if _invalid_pattern.search(file_name):
        raise IOError(f"Invalid filename: {file_name}")
    _versions[plugin_name] += 1
    decompressed_plugin_name = _get_decompressed_plugin_name(plugin_name)
    decompressed_plugin_path = os.path.join(os.getcwd(), _cache_path(), decompressed_plugin_name)
    sys.path.append(decompressed_plugin_path)
    if file_name.endswith('.tar'):
        with tarfile.open(plugin_path, "r") as tar:
            tar.extractall(decompressed_plugin_path)
    elif file_name.endswith('.zip'):
        with zipfile.ZipFile(plugin_path, 'r') as zip_ref:
            zip_ref.extractall(decompressed_plugin_path)
    plugin_level = load_plugin_info(os.path.join(os.getcwd(), decompressed_plugin_path), decompressed_plugin_name).get(
        "level", None)
    if plugin_level is None:
        plugin_level = getattr(PluginType.USER.value, 'default')
        sys_plugin_logger.info(f"level of plugin is set to default. [plugin_name={plugin_name}]")
    return decompressed_plugin_name, plugin_level


def _remove_plugin(plugin_path: str):
    file_name = os.path.basename(plugin_path)
    plugin_name = file_name.split('.')[0]
    decompressed_plugin_name = _get_decompressed_plugin_name(plugin_name)
    decompressed_plugin_path = os.path.join(_cache_path(), decompressed_plugin_name)
    plugin = Plugin(name=decompressed_plugin_name, location=decompressed_plugin_path, level=None, type_=PluginType.USER)
    if not os.path.exists(decompressed_plugin_path):
        sys_plugin_logger.warning(f"cannot find dynamic plugin to be remove. [plugin_name={decompressed_plugin_name}")
    shutil.rmtree(decompressed_plugin_path)
    plugin.stop()


def _run_path(path: str):
    file_list = os.listdir(path)
    for file_name in file_list:
        if file_name == "requirements.txt":
            file_path = os.path.join(path, file_name)
            _run_python_command(path, file_path)


def _run_python_command(path, file_path):
    with open(file_path, 'r') as f:
        for line in f:
            whl_file = line.strip()
            if whl_file:
                whl_path = os.path.join(path, whl_file)
                _install_package(whl_path)


def _install_package(whl_path):
    if _python_interpreter_alias() and _check_command_availability(_python_interpreter_alias()):
        subprocess.call([_python_interpreter_alias(), "-m", "pip", "install", whl_path])
    elif _check_command_availability("python3"):
        subprocess.run(['python3', "-m", "pip", "install", whl_path])
    elif _check_command_availability("python"):
        subprocess.run(['python', "-m", "pip", "install", whl_path])
    else:
        sys_plugin_logger.error("failed to install pypi plugins: cannot find any available python version.")


def _load_plugin(decompressed_plugin_name: str):
    decompressed_plugin_path = os.path.join(os.getcwd(), _cache_path(), decompressed_plugin_name)
    third_party_path = os.path.join(decompressed_plugin_path, "custom_third_party")
    if os.path.exists(third_party_path):
        for item in os.listdir(third_party_path):
            target_path = os.path.join(_third_party_cache_path(), item)
            if os.path.exists(target_path):
                sys_plugin_logger.warning(f"third party package already exists. [path={target_path}]")
            else:
                shutil.copytree(os.path.join(third_party_path, item), target_path)
                sys_plugin_logger.info(f"third party package loaded. [path={target_path}]")
            shutil.rmtree(os.path.join(third_party_path, item))
    custom_resources_path = os.path.join(decompressed_plugin_path, "custom_plugins_resources")
    if os.path.exists(custom_resources_path):
        sys_plugin_logger.info(f"start install pypi plugins...")
        _run_path(os.path.abspath(custom_resources_path))
        sys_plugin_logger.info(f"install pypi plugins successfully.")
        shutil.rmtree(custom_resources_path)
    plugin = Plugin(name=decompressed_plugin_name, location=decompressed_plugin_path, level=None, type_=PluginType.USER)
    plugin.install()


def _remove_plugins(plugins: List):
    for deleted_plugin in plugins:
        plugin_path = os.path.join(_user_plugins_path(), deleted_plugin)
        try:
            _remove_plugin(plugin_path)
            sys_plugin_logger.info(
                f"plugin removed. [plugin_path={plugin_path}], memory_usage={'{:,}'.format(get_memory_usage())}")
        except:
            sys_plugin_logger.warning(f"remove plugin failed. [plugin_path={plugin_path}]")
            except_type, except_value, except_traceback = sys.exc_info()
            sys_plugin_logger.warning(f"remove plugin error type: {except_type}")
            sys_plugin_logger.warning(f"remove plugin error value: {except_value}")
            sys_plugin_logger.warning(
                f"remove plugin error trace back:\n{''.join(traceback.format_tb(except_traceback))}")


def _load_plugins(plugins: List):
    plugin_paths = [os.path.join(_user_plugins_path(), plugin) for plugin in plugins]
    plugin_levels = {}
    for plugin_path in plugin_paths:
        try:
            validate_zipped_file_and_dir_names(plugin_path)
            _decompressed_plugin_name, plugin_level = _decompress_plugin_and_get_level(plugin_path)
        except:
            except_type, except_value, except_traceback = sys.exc_info()
            sys_plugin_logger.error(f"plugin level info load failed. [plugin_path={plugin_path}, "
                                    f"except_type={except_type}, except_value={except_value}, "
                                    f"except_traceback={except_traceback}]")
            continue
        plugin_levels[_decompressed_plugin_name] = plugin_level
    _decompressed_plugin_names = list(plugin_levels.keys())
    _decompressed_plugin_names.sort()
    _decompressed_plugin_names.sort(key=lambda name: plugin_levels.get(name))
    for _decompressed_plugin_name in _decompressed_plugin_names:
        try:
            _load_plugin(_decompressed_plugin_name)
            sys_plugin_logger.info(f"plugin loaded. [plugin_name={_decompressed_plugin_name}]")
        except:
            sys_plugin_logger.warning(
                f"load plugin failed. [plugin_name={_decompressed_plugin_name}, "
                f"memory_usage={'{:,}'.format(get_memory_usage())}]")
            except_type, except_value, except_traceback = sys.exc_info()
            sys_plugin_logger.warning(f"load plugin error type: {except_type}")
            sys_plugin_logger.warning(f"load plugin error value: {except_value}")
            sys_plugin_logger.warning(
                f"load plugin error trace back:\n{''.join(traceback.format_tb(except_traceback))}")


def _is_any_plugin_changed(new_plugins: List, deleted_plugins: List, modified_plugins: List) -> bool:
    return len(new_plugins) > 0 or len(deleted_plugins) > 0 or len(modified_plugins) > 0


def _scan_plugin_files():
    last_plugins_modify_time = {}
    if not os.path.exists(_user_plugins_path()):
        sys_plugin_logger.warning(f"path {_user_plugins_path()} not exist.")
    while not os.path.exists(_user_plugins_path()):
        time.sleep(1)
    sys_plugin_logger.info(f"start scan {_user_plugins_path()}.")

    while True:
        plugins_modify_time = {}
        try:
            dynamic_plugins = os.listdir(_user_plugins_path())
        except:
            except_type, except_value, _ = sys.exc_info()
            sys_plugin_logger.warning(
                f"cannot get dynamic plugins. [error_type: {except_type}, error_value: {except_value}]")
            time.sleep(_scan_period())
            continue
        for filename in dynamic_plugins:
            if filename.endswith('.tar') or filename.endswith('.zip'):
                plugins_modify_time[filename] = os.stat(os.path.join(_user_plugins_path(), filename)).st_mtime

        new_plugins = list(plugins_modify_time.keys() - last_plugins_modify_time.keys())
        deleted_plugins = list(last_plugins_modify_time.keys() - plugins_modify_time.keys())
        intersection_plugins = set(plugins_modify_time.keys()) & set(last_plugins_modify_time.keys())
        modified_plugins = []
        for file in intersection_plugins:
            if plugins_modify_time.get(file) != last_plugins_modify_time.get(file):
                modified_plugins.append(file)

        _remove_plugins(deleted_plugins + modified_plugins)
        _load_plugins(new_plugins + modified_plugins)
        if _is_any_plugin_changed(new_plugins, deleted_plugins, modified_plugins):
            register_all_fit_services()

        last_plugins_modify_time = plugins_modify_time
        time.sleep(_scan_period())


@fitable(const.DYNAMIC_LOADING_START_GEN_ID, const.DYNAMIC_LOADING_START_FIT_ID)
def dynamic_loading_start():
    scan_thread = threading.Thread(target=_scan_plugin_files)
    scan_thread.daemon = True
    scan_thread.start()
