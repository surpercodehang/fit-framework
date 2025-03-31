# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：从package中加载所有Fitable方法
"""
import sys
import traceback
from typing import List

from fitframework import const
from fitframework.api.decorators import fit, state_broadcast
from fitframework.api.enums import FrameworkEvent, PluginType, FrameworkSubState
from fitframework.api.logging import fit_logger
from fitframework.core.repo import plugin_repo
from fitframework.utils.eventing import notify
from fitframework.utils.pkg_utils import import_package
from fitframework.utils.tools import sort_and_groupby
from fitframework.domain.plugin import Plugin


@fit(const.RUNTIME_SHUTDOWN_GEN_ID)
def shutdown() -> None:
    pass


@fit(const.RUNTIME_STATE_UPDATE_GEN_ID)
def update_framework_state(new_state: str) -> None:
    pass


def load_bootstraps(bootstrap_locations: List[str]):
    """
    :param bootstrap_locations: 一种特殊的插件，不对其进行插件管理，也
        不存在插件状态等一系列生命周期特征，仅仅用作引擎最开始的初始化
        加载、元数据存储，也不对外进行服务注册，所以此处仅仅传入路径字
        符串即可
    """
    from fitframework.core.repo.fitable_register import loading_context as ctx
    ctx.is_bootstrap = True
    for location in bootstrap_locations:
        import_package(location, recursive=True)
    del ctx.is_bootstrap


@state_broadcast(update_framework_state, None, FrameworkSubState.ALL_PLUGIN_LOADED)
def load_plugins(plugins_info: List):
    """
    从roots中搜索全局配置文件（fit_startup.yml）中的插件列表，并加载。
        共包括四个步骤：install, resolve, start, install fitables；其中非用户插件
        只包含第一个步骤
        :param plugins_info:所有静态插件的信息
    """
    plugins = [Plugin(**info,
                      type_=PluginType.SYSTEM if info["category"] == "system" else PluginType.USER)
               for info in plugins_info]
    for cur_level, cur_plugins in sort_and_groupby(plugins, Plugin.level_getter):
        fit_logger.info(f"plugins of level {cur_level} begin to load")
        if cur_level >= getattr(PluginType.USER.value, 'min'):  # user plugins
            notify(FrameworkEvent.FRAMEWORK_STARTED)
        cur_plugins = list(cur_plugins)
        if not cur_plugins:
            continue
        for plugin in cur_plugins:
            try:
                Plugin.install(plugin)
            except:
                fit_logger.error(f"load plugin failed. [plugin_name={plugin}]")
                except_type, except_value, except_traceback = sys.exc_info()
                fit_logger.error(f"load plugin error type: {except_type}")
                fit_logger.error(f"load plugin error value: {except_value}")
                fit_logger.error(f"load plugin error trace back:\n{''.join(traceback.format_tb(except_traceback))}")
                _ = list(map(Plugin.stop, reversed(plugin_repo.get_all_plugins())))
                shutdown()
        fit_logger.info(f"plugins of level {cur_level} loaded successfully")
