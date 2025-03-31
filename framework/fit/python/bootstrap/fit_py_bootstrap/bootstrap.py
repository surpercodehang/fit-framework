# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit底座启动引导接口实现。引导入口为start()
启动引导总流程包括以下步骤：
1. 加载引导区插件。此时无法获得插件配置，远程调用服务。需要限制插件的调用。
包括本地调用，插件无配置。
2. 调用引导服务。包括
   * 加载全局fit_startup.yml
   * 加载业务插件
   * 加载公共结构体
   * 加载Fit元数据信息，归入全局目录
   * 注册底座支持format
   * 启动服务器
   * 调用用户 注册的回调服务
   * 下载并注册配置中心配置
   * 服务注册，启动心跳
   * 启动完成，删除启动临时数据
"""
import os
import sys
from typing import List

from fitframework import const
from fitframework.api.decorators import fit, fitable, local_context, state_broadcast
from fitframework.api.enums import FrameworkState, FrameworkSubState
from fitframework.api.logging import bootstrap_logger
from fitframework.core.broker.select_broker import select
from fitframework.core.network.enums import SerializingStructureEnum
from fitframework.core.repo import service_repo
from fitframework.utils import load_module
from fitframework.utils.tools import string_normalize

_FIT_YAML = "fit.yml"
_GLOBAL_APPLICATION_YML = 'application.yml'
_GET_ONLINE_FITABLE_ALIAS = {
    'fit': 'heart_beat_online_fit_python',
    'om': 'heart_beat_online_om_python',
    'default': 'heart_beat_online_fit_python',
}


@local_context("share_paths")
def share_paths():
    pass


def add_share_paths():
    for path in share_paths():
        sys.path.append(path)


@fit(const.RUNTIME_STATE_UPDATE_GEN_ID)
def update_state(new_state: str) -> None:
    pass


@state_broadcast(update_state, None, FrameworkSubState.CONFIGURATION_SUBSCRIBED)
@fit(const.SERVICE_DB_DOWNLOAD_AND_SUBSCRIBE_ALL_CONF_GEN_ID)
def download_and_subscribe_all_configuration() -> None:
    pass


@state_broadcast(update_state, None, FrameworkSubState.FITABLE_REGISTERED)
@fit(const.SERVICE_DB_REGISTER_ALL_FIT_SERVICE_GEN_ID)
def register_all_fit_services() -> None:
    pass


@fit(const.SERVICE_DB_REGISTER_FORMAT_GEN_ID)
def register_format(format_id: str) -> None:
    pass


@fit(const.FIT_METADATA_LOAD_GEN_ID)
def load_fit_metadata(file_path: str) -> None:
    pass


@fit(const.FIT_PLUGIN_CONFIGURATION_LOADER_GEN_ID)
def load_plugin_configuration(file_path: str, plugin_name: str) -> None:
    pass


@fit(const.DYNAMIC_LOADING_START_GEN_ID)
def dynamic_loading_start() -> None:
    pass


@fitable(const.BOOTSTRAP_START_GEN_ID, const.BOOTSTRAP_START_FIT_ID)
def start(plugins_info: List, config_folder: str) -> bool:
    """ 底座启动引导，返回值可用于标志启动成功或失败，目前暂未使用
    :param config_folder:配置文件路径
    :param plugins_info:所有静态插件信息
    """
    add_share_paths()
    _load_global_application_yml(config_folder)
    load_module.load_plugins(plugins_info)  # 加载业务目录下插件，插件注册service_db
    _load_fit_yml(config_folder)
    _start_all_servers()
    bootstrap_logger.info('_' * 5 + 'READY TO RECEIVE EXTERNAL REQUESTS')
    dynamic_loading_start()
    register_all_fit_services()
    _trigger_heartbeat()
    bootstrap_logger.info('_' * 5 + 'READY TO PROVIDE FITABLE SERVICE OUTSIDE')
    cleanup()


@state_broadcast(update_state, None, FrameworkSubState.FIT_METADATA_LOADED)
def _load_fit_yml(config_folder: str):
    # 加载Fit元数据信息，归入全局目录
    fit_yaml_path = os.path.join(config_folder, _FIT_YAML)
    load_fit_metadata(fit_yaml_path)


@state_broadcast(update_state, FrameworkState.BOOTING, FrameworkSubState.BOOTING_INIT)
def _load_global_application_yml(config_folder: str):
    bootstrap_logger.debug("load application yml")
    global_application_yml_path = os.path.join(config_folder, _GLOBAL_APPLICATION_YML)
    load_plugin_configuration(global_application_yml_path, const.GLOBAL_PLUGIN_NAME)


def _register_support_formats():
    register_format(SerializingStructureEnum.JSON.value)
    register_format(SerializingStructureEnum.CBOR.value)


@fitable(const.BOOTSTRAP_CLEANUP_GEN_ID, const.BOOTSTRAP_CLEANUP_FIT_ID)
def cleanup():
    pass


@state_broadcast(update_state, None, FrameworkSubState.RUNNING)
@state_broadcast(update_state, None, FrameworkSubState.HEART_BEAT_STARTED)
def _trigger_heartbeat():
    @local_context('use-heart-beat-center', 'fit')
    def use_heart_beat_center() -> str:
        pass

    def _alias_of_online_fitable() -> str:
        use_heart_beat_center_ = string_normalize(use_heart_beat_center())
        if use_heart_beat_center_ in _GET_ONLINE_FITABLE_ALIAS:
            return _GET_ONLINE_FITABLE_ALIAS[use_heart_beat_center_]
        else:
            return _GET_ONLINE_FITABLE_ALIAS['default']

    @fit(const.ONLINE_HEART_BEAT_GEN_ID, _alias_of_online_fitable())
    def trigger_heartbeat() -> None:
        pass

    trigger_heartbeat()


@state_broadcast(update_state, None, FrameworkSubState.SERVER_STARTED)
def _start_all_servers():
    _register_support_formats()
    select(const.FIT_SERVER_START_GEN_ID).route_filter(_start_server_fitable_exists).fit_selector_invoke()


def _start_server_fitable_exists(fitable_id: str) -> bool:
    return service_repo.get_fitable_ref(const.FIT_SERVER_START_GEN_ID, fitable_id) is not None


def _on_application_started():
    select(const.FIT_ON_APPLICATION_STARTED_GEN_ID).route_filter(_on_application_started_exists).fit_selector_invoke()


def _on_application_started_exists(fitable_id: str) -> bool:
    return service_repo.get_fitable_ref(const.FIT_ON_APPLICATION_STARTED_GEN_ID, fitable_id) is not None
