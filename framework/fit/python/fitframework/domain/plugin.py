# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：插件定义
"""
from fitframework import const
from fitframework.api.decorators import state_broadcast, fit
from fitframework.api.enums import PluginState, PluginType
from fitframework.core.repo import repo_utils
from fitframework.core.repo.plugin_repo import get_plugin_start_funcs, get_plugin_stop_funcs
from fitframework.core.repo.service_repo import remove_plugin_refs
from fitframework.utils.pkg_utils import import_package


@fit(const.SERVICE_DB_REGISTER_PLUGIN_GEN_ID)
def register_plugin(plugin_name: str, plugin_local_path: str) -> None:
    pass


class Plugin:
    def __init__(self, location: str, type_: PluginType, name: str = None,
                 level: int = None, state: PluginState = None, version: str = None, **kw):
        """
        如果name为空，location不为空，则默认使用location的最后一个路径组成部分作为插件名
        如果name和location都为空，则表示是一个虚拟插件（实现所在位置为用户独立应用）

        Args:
            location: 目前为必选
            type_: 见PluginType
            name: 必须全局唯一
            level: 见PluginType
            **kw:
        """
        self.location = location  # 当前将路径格式统一采用相对路径方式
        self.full_dir_location = self.location
        self.type = type_
        self.name = name if name else repo_utils.resolve_plugin_name(self.location)
        self.level = level if level is not None else getattr(type_.value, 'default')
        self.state = state if state else PluginState.UNKNOWN
        self.version = version if version else '1.0.0'

    def __repr__(self) -> str:  # 方便调试
        return self.name

    __str__ = __repr__

    def level_getter(self) -> int:
        return self.level

    def state_setter(self, new_state_name: str):
        self.state = PluginState[new_state_name]

    @state_broadcast(state_setter, None, PluginState.INSTALLED)
    def install(self):
        """
        将插件自己的handler()注册到eventing：自己要处理的其他插件的事情
        将插件自己的starter()注册到service_repo：自己要处理的自己插件的事情（初始化相关）
        """

        # 先本地注册插件，再本地注册服务
        register_plugin(self.name, self.full_dir_location)
        import_package(self.location, recursive=True)

    @state_broadcast(state_setter, None, PluginState.RESOLVED)
    def resolve(self):
        pass

    @state_broadcast(state_setter, PluginState.STARTING, PluginState.ACTIVE)
    def start(self):
        """
        从eventing中取出别的插件的handler()去触发执行
        从service_repo中取出插件自己的starter()去触发执行
        """
        _ = [f() for f in get_plugin_start_funcs(self.name)]
        return self  # 方便进行迭代操作

    @state_broadcast(state_setter, PluginState.STOPPING, PluginState.UNINSTALLED)
    def stop(self):
        @fit(const.SERVICE_DB_UNREGISTER_PLUGIN_GEN_ID)
        def unregister_plugin(plugin_name: str, plugin_local_path: str) -> None:
            pass

        @fit(const.SERVICE_DB_REMOVE_PLUGIN_FITABLES_GEN_ID)
        def remove_plugin_fitables(plugin_name: str) -> None:
            pass

        # 先本地注册插件，再本地注册服务
        remove_plugin_fitables(self.name)
        remove_plugin_refs(self.name)
        unregister_plugin(self.name, self.full_dir_location)
        _ = [f() for f in get_plugin_stop_funcs(self.name)]
