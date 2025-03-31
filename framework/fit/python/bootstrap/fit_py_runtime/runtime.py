# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit底座运行时关闭接口
运行时状态包括status和sub_status：
unknown: 未知状态
booting: 正在启动
  booting_init
  plugin_loaded
  component_loaded
  fit_metadata_loaded
  server_started
  configuration_registered
  fitable_registered
  heartbeat_started
  
running: 启动完成，系统运行
shutdown: 正在关闭
"""
import threading
import uuid

from numpy import int32

from fitframework import const
from fitframework.api.decorators import fitable, local_context
from fitframework.api.logging import bootstrap_logger, sys_plugin_logger
from fitframework.api.enums import FrameworkState, FrameworkSubState, FrameworkEvent
from fitframework.utils.eventing import notify

_random_worker_id = str(uuid.uuid4())  # 在未通过配置指定 worker_id 时才会使用的随即生成的 worker_id
_worker_instance_id = str(uuid.uuid4())

_runtime_state = FrameworkState.UNKNOWN
_runtime_sub_state = FrameworkSubState.UNKNOWN


@local_context('worker.id')
def get_worker_id_by_config():
    pass


@fitable(const.RUNTIME_SHUTDOWN_GEN_ID, const.RUNTIME_SHUTDOWN_FIT_ID)
def shutdown() -> None:
    bootstrap_logger.info('runtime plugin shutdown')
    try:
        import pydevd
        pydevd.stoptrace()
    except ImportError:
        pass
    notify(FrameworkEvent.FRAMEWORK_STOPPING)
    for thread in threading.enumerate():
        if not thread.isDaemon() and thread is not threading.main_thread():
            sys_plugin_logger.info(f"waiting for {thread} to stop... [target={thread._target}]")
            thread.join()
            sys_plugin_logger.info(f"{thread} has ended...")
    bootstrap_logger.info("all services have been off. main thread exiting...")
    exit()


@fitable(const.RUNTIME_STATE_UPDATE_GEN_ID, const.RUNTIME_STATUS_UPDATE_FIT_ID)
def update_runtime_state(new_state: str) -> None:
    """ 根据当前状态和新状态，更新fit运行时服务器状态 """
    global _runtime_state, _runtime_sub_state
    if new_state in set(_.name for _ in FrameworkState):
        _runtime_state = FrameworkState[new_state]
    elif new_state in set(_.name for _ in FrameworkSubState):
        _runtime_sub_state = FrameworkSubState[new_state]


@fitable(const.RUNTIME_GET_STATUS_GEN_ID, const.RUNTIME_GET_STATUS_FIT_ID)
def get_runtime_state() -> int32:
    """ fit运行时服务器状态"""
    return _runtime_state


@fitable(const.RUNTIME_GET_SUB_STATUS_GEN_ID, const.RUNTIME_GET_SUB_STATUS_FIT_ID)
def get_runtime_sub_state() -> int32:
    """ fit运行时服务器状态"""
    return _runtime_sub_state


@fitable(const.RUNTIME_GET_WORKER_ID_GEN_ID, const.RUNTIME_GET_WORKER_ID_FIT_ID)
def get_runtime_worker_id() -> str:
    worker_id = get_worker_id_by_config()
    if worker_id is None:
        worker_id = _random_worker_id
    return worker_id


@fitable(const.RUNTIME_GET_WORKER_INSTANCE_ID_GEN_ID, const.RUNTIME_GET_WORKER_INSTANCE_ID_FIT_ID)
def get_runtime_instance_worker_id() -> str:
    return _worker_instance_id
