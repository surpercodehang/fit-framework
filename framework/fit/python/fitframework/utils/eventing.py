# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit server事件服务
"""
from collections import defaultdict
from inspect import signature
from typing import Union

from fitframework.api.enums import FrameworkEvent, PluginEvent, PluginState, FitEvent
from fitframework.api.logging import fit_logger
from fitframework.core.repo.repo_utils import inspect_plugin_name_by_func

# key: an Event object
# value: list of tuple: (subscribe_function, filter_function)
_registered_events = defaultdict(list)


class ConfigureChangedEventData(object):
    def __init__(self, key: str, old_value: str, new_value: str, conf_path: str):
        self.key = key
        self.old_value = old_value
        self.new_value = new_value
        self.conf_path = conf_path


def register(event: Union[FrameworkEvent, PluginEvent], event_handle, data_filter):
    _registered_events[event].append((event_handle, data_filter))


def notify(event: FitEvent, **event_data) -> None:
    """
    事件触发后，将其通知给订阅了该事件的对象。
    对象有可能是引擎本身，也有可能是插件。

    :param event: 事件
    :param event_data: 事件数据
    :return: None
    """
    for event_handler, event_filter in _registered_events[event]:
        try:
            if event_filter is None or event_filter(**event_data):
                _trigger_handling(event_handler, **event_data)
        except Exception as err:
            fit_logger.warning(f"try to {event_handler.__module__}.{event_handler.__name__} failed, {type(err)}"
                               f"possibly caused by an interruption. skipped.")


def _handler_is_active(handler):
    plugin_containing_handler = inspect_plugin_name_by_func(handler)
    if plugin_containing_handler:
        from fitframework.core.repo.plugin_repo import get_plugin_state
        return get_plugin_state(plugin_containing_handler) == PluginState.ACTIVE
    else:
        return True  # 如果handler持有对象为引擎或BOOTSTRAP，则无需判断状态


def _trigger_handling(event_handler, **event_data):
    param_len = len(signature(event_handler).parameters)
    event_handler(**event_data) if param_len else event_handler()
