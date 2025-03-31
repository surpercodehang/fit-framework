# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
无法通过service_db获得函数指针。所以函数指针由本模块直接保存。

系统初始化引导相关的Fitable由本模块单独保存（因为此时service_db还没有加载），
并支持他们的服务发现。其他Fitable注册进service_db。
"""
from collections import defaultdict

from typing import List

from fitframework.api.decorators import private_fit
from fitframework.api.logging import fit_logger

# 加载时的插件信息上下文
_CONTEXT_PLUGIN_INFO = 'plugin_info'

# @fitable方法指针表 - key: (generic_id, fitable_id), value: FitFunctionRef
_fitable_ref_repo = {}

# @fit方法指针表 - key: generic_id, value: FitFunctionRef
_fit_ref_repo = {}

# 某个插件所拥有的 fitable 列表 - key: plugin_name, value: List[(generic_id, fitable_id)]
_plugin_fitable_repo = defaultdict(list)

# 用于识别micro、从/bootstrap目录加载的所有plugin中的fitable - key: generic_id, value: List[fitable_id]
_glued_fitables = defaultdict(list)

# 记录value的信息 - key: function, value:(plugin_name , key)
_value_repo = {}


@private_fit
def get_configuration_item(plugin_name: str, key: str):
    pass


def get_fitable_ref(generic_id, fitable_id):
    """
    获得fitable方法指针

    Args:
        generic_id (str): genericable id
        fitable_id (str): fitable id

    Returns:
        function: 服务方法指针
    """
    return _fitable_ref_repo.get((generic_id, fitable_id))


def get_plugin_name_with_func_ref(ref) -> str:
    return ref.__module__.partition('.')[0]


def _set_fitable_ref(generic_id, fitable_id, func_ref) -> None:
    """
    设置fitable方法指针

    Args:
        generic_id (str): genericable id
        fitable_id (str): fitable id
        func_ref (function): 方法指针
    """
    _fitable_ref_repo[(generic_id, fitable_id)] = func_ref
    plugin_name = get_plugin_name_with_func_ref(func_ref)
    from fitframework.utils.pkg_utils import loading_so_plugin_name
    if loading_so_plugin_name is not None:
        fit_logger.info(f"replace plugin name with so module name. [so module name={loading_so_plugin_name}]")
        plugin_name = loading_so_plugin_name
    _plugin_fitable_repo[plugin_name].append((generic_id, fitable_id))


def _set_at_fit_ref(generic_id, func_ref) -> None:
    """
    设置@fit方法指针
    Args:
        generic_id (str):
        func_ref (function):
    """
    _fit_ref_repo[generic_id] = func_ref


def remove_plugin_refs(plugin_name: str):
    if plugin_name in _plugin_fitable_repo:
        for (generic_id, fitable_id) in _plugin_fitable_repo[plugin_name]:
            del _fitable_ref_repo[(generic_id, fitable_id)]


def get_at_fit_ref(genericable_id: str):
    return _fit_ref_repo.get(genericable_id)


def get_one_fitable_ref(generic_id: str):
    """
    根据genericable id获得任意一个fitable方法指针

    Args:
        generic_id (str): genericable id

    Returns:
        function: 服务方法指针
    """
    return next((ref for fitable, ref in _fitable_ref_repo.items() if fitable[0] == generic_id),
                None)


def query_fit_or_fitable_ref(generic_id: str):
    fit_ref = get_at_fit_ref(generic_id)
    if not fit_ref:
        fit_ref = get_one_fitable_ref(generic_id)
    return fit_ref


def get_glued_fitable_ids(generic_id: str) -> list:
    """
    获得Fit框架专用的本地接口对应的fitable id列表

    Args:
        generic_id (str): genericable id

    Returns:
        List[str]: genericable id对应的fitable id列表
    """
    return _glued_fitables[generic_id]


def _add_glued_fitable_id(generic_id, fitable_id) -> None:
    """
    增加Fit框架专用的本地接口对应的fitable id
    Args:
        generic_id (str): generic_id
        fitable_id (str): fitable_id
    """
    _glued_fitables[generic_id].append(fitable_id)


def is_glued(generic_id: str) -> bool:
    """
    是否Fit框架专用的本地接口

    Args:
        generic_id (str): genericable id

    Returns:
        bool: True/False
    """
    return generic_id in _glued_fitables


def get_value(func):
    info_tuple = _value_repo.get(func)
    if not info_tuple:
        raise Exception(f"not plugin info found for {func.__name__}")
    _plugin_name, _key = info_tuple
    return get_configuration_item(_plugin_name, _key)


def _set_value_ref(plugin_name, key, func):
    _value_repo[func] = (plugin_name, key)
