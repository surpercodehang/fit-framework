# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：运行时状态枚举
"""
from collections import namedtuple
from enum import Enum, auto, unique
from typing import Union


@unique
class FrameworkEvent(Enum):
    """ 框架支持的事件枚举 """
    FRAMEWORK_STARTED = auto()
    APPLICATION_STARTED = auto()
    FRAMEWORK_STOPPING = auto()
    CONFIGURATION_CHANGED = auto()


class FrameworkState(Enum):
    UNKNOWN = auto()
    BOOTING = auto()
    RUNNING = auto()
    SHUTDOWN = auto()


@unique
class FrameworkSubState(Enum):
    UNKNOWN = "unknown"
    BOOTING_INIT = "booting_init"
    ALL_PLUGIN_LOADED = "all_plugin_loaded"
    COMPONENT_LOADED = "component_loaded"
    FIT_METADATA_LOADED = "fit_metadata_loaded"
    SERVER_STARTED = "server_started"
    CONFIGURATION_SUBSCRIBED = "configuration_subscribed"
    FITABLE_REGISTERED = "fitable_registered"
    HEART_BEAT_STARTED = "heart_beat_started"
    RUNNING = "running"
    SHUTDOWN = "shutdown"


@unique
class PluginEvent(Enum):
    # event_data signature:
    """ 事件可以由状态的变化触发，也可以由其他系统关键行为触发
    action -> events; current actions in FIT include：
        - state changes
        - interruption
        - ...
    def handler(starting_plugin: Plugin)
        pass
    """
    STARTING = auto()
    STARTED = auto()
    STOPPING = auto()
    STOPPED = auto()


@unique
class PluginState(Enum):
    """
    插件状态：表达了一个持续时间段内的比较稳定的状态的概念；状态的改变
        通常用“事件来表示”@PluginEvent。一般而言的逻辑：
            - 某动作完成后 -> 修改状态 -> 触发事件
            - action -> state -> event
    目前的消费场景：暂时只在插件启动调用对应的启动函数时的筛选过程中
        有用到，未来场景可按需使用
    UNINSTALLED目前暂不支持

    其他，见：@broadcast_action
    """
    UNKNOWN = "unknown"
    INSTALLED = "installed"
    RESOLVED = "resolved"
    STARTING = "starting"
    ACTIVE = "active"
    STOPPING = "stopping"
    UNINSTALLED = "uninstalled"


class PluginType(Enum):
    """ 模块类型
    系统插件和用户插件的加载等级取值均为：[1, 7]
    系统插件加载永远先于用户插件加载
    同类型加载顺序为从小到大依次加载，故数字越小代表优先级越高
    加载，英文暂定词为startup
        - 包括安装（install）：找到插件对应的实际Python包，将其作为一个module类型来导入
        - 解析（resolve）
        - 启动（start）

    注意这里枚举值需和`fit_startup.yml`的plugins下面的key保持一致（详见`fit_startup.yml`）
    注：BOOTSTRAP作为特殊插件，不进行插件类型管理，故此处不设置
        BOOTSTRAP枚举值
    """
    __LoadLevel = namedtuple('LoadLevel', 'min, max, default')

    SYSTEM = __LoadLevel(-7, -1, -4)
    USER = __LoadLevel(1, 7, 4)


# 状态与事件的映射关系
STATE_TO_EVENT = {
    FrameworkSubState.ALL_PLUGIN_LOADED: FrameworkEvent.APPLICATION_STARTED,
    FrameworkSubState.HEART_BEAT_STARTED: FrameworkEvent.FRAMEWORK_STARTED,
    PluginState.STARTING: PluginEvent.STARTING,
    PluginState.ACTIVE: PluginEvent.STARTED,
    PluginState.STOPPING: PluginEvent.STOPPING,
    PluginState.UNINSTALLED: PluginEvent.STOPPED
}

FitState = Union[FrameworkState, FrameworkSubState, PluginState]
FitEvent = Union[FrameworkEvent, PluginEvent]
