# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：fitable服务注册。
"""
import threading
from typing import List

from fitframework import const
from fitframework.api.decorators import fit
from fitframework.api.enums import PluginEvent
# 当前线程上下文，用于加载插件时获得当前插件信息
from fitframework.core.repo.service_repo import _set_fitable_ref, _add_glued_fitable_id
from fitframework.utils.eventing import notify

loading_context = threading.local()

# 用于启动插件加载上下文
_CONTEXT_IS_BOOTSTRAP = 'is_bootstrap'


@fit(const.SERVICE_DB_REGISTER_TO_PLUGIN_GEN_ID)
def _register_to_plugin(plugin_name: str, generic_id: str, fitable_id: str, aliases: List[str]) -> None:
    pass


def register_fitable(generic_id: str, fitable_id: str, is_private: bool, aliases: List[str], fitable_ref) -> None:
    """
    fitable方法注册

    Args:
        generic_id (str): genericable id
        fitable_id (str): fitable id
        is_private (bool): 是否是私有服务
        fitable_ref (function): 服务方法指针
    """
    if generic_id is None or fitable_id is None:
        raise Exception(f"generic_id and fitable_id cannot be none. [generic_id={generic_id}, fitable_id={fitable_id}]")
    # 注册Fitable方法索引
    _set_fitable_ref(generic_id, fitable_id, fitable_ref)

    if _is_loading_bootstrap() or is_private:
        _add_glued_fitable_id(generic_id, fitable_id)
        return

    # 注册到service_db
    plugin_name = fitable_ref.__module__.partition('.')[0]
    _register_to_plugin(plugin_name, generic_id, fitable_id, aliases)
    if plugin_name == '_fit_py_fake_user_plugin':
        notify(PluginEvent.STARTED, plugin_name=plugin_name)


def _is_loading_bootstrap() -> bool:
    return hasattr(loading_context, _CONTEXT_IS_BOOTSTRAP) and getattr(loading_context,
                                                                       _CONTEXT_IS_BOOTSTRAP)
