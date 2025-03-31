# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：用于判断是否需要终结自身服务的插件。
"""
import time
from typing import Optional

from fitframework.api.decorators import fit, fitable, value
from fitframework.api.logging import sys_plugin_logger
from fitframework.utils.tools import get_memory_usage

_LAST_RUNNING_TIME = time.time()


@value("terminate-main.protected-limit", converter=int)
def get_protected_limit():
    pass


@value("terminate-main.force-terminate-limit", converter=int)
def get_force_terminate_limit():
    pass


@value("terminate-main.max-rest-time-when-empty", converter=int)
def get_max_rest_time_when_empty():
    pass


@value("terminate-main.max-rest-time-when-not-empty", converter=int)
def get_max_rest_time_when_not_empty():
    pass


@value("terminate-main.max-not-success-time", converter=int)
def get_max_not_success_time():
    pass


@fit("modelengine.fit.get.running.async.task.count")
def get_running_async_task_count() -> int:
    pass


@fit("modelengine.fit.bigdata.cache.get.info")
def get_info_from_cache() -> (int, int):
    pass


@fit("modelengine.fit.get.last.success.time")
def get_last_success_time() -> float:
    pass


@fit("modelengine.fit.get.earliest.start.time")
def get_earliest_start_time() -> Optional[float]:
    pass


@fitable("modelengine.fit.get.should.terminate.main", "local-worker")
def get_should_terminate_main():
    memory_usage = get_memory_usage()
    running_task_count = get_running_async_task_count()
    global _LAST_RUNNING_TIME
    if memory_usage < get_protected_limit():
        return False
    if running_task_count > 0:
        _LAST_RUNNING_TIME = time.time()
    rest_time = time.time() - _LAST_RUNNING_TIME
    not_success_time = time.time() - get_last_success_time()
    earliest_start_time = get_earliest_start_time()
    if rest_time > get_max_rest_time_when_empty() and get_info_from_cache()[0] == 0:
        sys_plugin_logger.info(
            f"main process will exited due to empty cache and long rest time. [rest_time={rest_time}]")
        return True
    if rest_time > get_max_rest_time_when_not_empty():
        sys_plugin_logger.info(
            f"main process will exited due to long rest time. [rest_time={rest_time}]")
        return True
    if not_success_time > get_max_not_success_time() and (earliest_start_time is None or (
            time.time() - earliest_start_time) < get_max_not_success_time()):
        sys_plugin_logger.info(
            f"main process will exited due to long not success time. [not_success_time={not_success_time}]")
        return True
    if get_memory_usage() > get_force_terminate_limit():
        sys_plugin_logger.info(
            f"main process will exited due to high memory usage. [memory_usage={memory_usage}]")
        return True
    return False
