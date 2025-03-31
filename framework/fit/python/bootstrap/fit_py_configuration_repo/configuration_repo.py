# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：FIT全局配置和插件配置的维护管理模块，
主要负责配置的加载/更新/推送、存储、读取
一个插件或一个genericable即对应了一个配置
"""
import os
import yaml

from fitframework import const
from fitframework.api.decorators import fit, fitable, private_fitable
from fitframework.api.logging import bootstrap_logger

_KEY_SEPARATOR = '.'

fit_config_repo = {}

""" ------------------------------------- 服务实现 ------------------------------------- """


@fitable(const.FIT_METADATA_LOAD_GEN_ID, const.FIT_METADATA_LOAD_FIT_ID)
def load_fit_metadata(file_path: str) -> None:
    """
    读取fit.yml加载Fit元数据信息，（yml格式）转换成dict
    :param file_path: fit.yml元数据配置的文件路径
    :return: None
    """
    # [TOOD] log;
    gen_s_config = _load_yaml(file_path)
    if gen_s_config is not None:
        fit_config_repo.update(gen_s_config)


@fitable(const.FIT_PLUGIN_CONFIGURATION_LOADER_GEN_ID, const.FIT_PLUGIN_CONFIGURATION_LOADER_FIT_ID)
def load_plugin_configuration(file_path: str, plugin_name: str):
    """
    读取application.yml加载插件配置信息，转换成dict
    每个插件支持自定义各自的application.yml，默认在plugin/${plugin_name}/config目录下
    :param file_path: 插件元数据配置文件application.yml的路径
    :param plugin_name: 插件名
    :return:
    """
    plugin_config = _load_yaml(file_path)
    if plugin_config is not None:
        fit_config_repo[plugin_name] = plugin_config


@fitable(const.FIT_PLUGIN_CONFIGURATION_UNLOAD_GEN_ID, const.FIT_PLUGIN_CONFIGURATION_UNLOAD_FIT_ID)
def unload_plugin_configuration(file_path: str, plugin_name: str):
    del fit_config_repo[plugin_name]


@fitable(const.FIT_PLUGIN_CONFIGURATION_UPDATE_GEN_ID, const.FIT_PLUGIN_CONFIGURATION_UPDATE_FIT_ID)
def update_configuration_item(conf_node_key: str, conf_child_node_key: str, new_val: str) -> None:
    """
    从配置中心推送的配置项更新。如果value为None或空串，代表删除对应配置项(del)
    :param conf_node_key: 待更新配置节点的key
        （即对应本地repo存储的哈希键，可能是插件名，也可能是`fit.public.genericables.xxx`的形式）
    :param conf_child_node_key: 待更新配置节点下某一子节点的key（去掉了父节点key前缀）
    :param new_val: 待更新配置项的新的value
    :return: None
    """
    # 以下代码中的key不是指python dict的key，而是fit元数据或插件的配置节点的key
    if not conf_child_node_key:
        bootstrap_logger.warning(f'empty `key_path` is not allowed when updating configuration item '
                                 f'from configuration center; current plugin: [{conf_node_key}].')
        return
    full_key = _concatenate(conf_node_key, conf_child_node_key)
    if new_val:
        _do_new_or_update(fit_config_repo, full_key, new_val)
    else:
        _do_delete(fit_config_repo, full_key)


@private_fitable
def get_configuration_item(plugin_name: str, key_path: str = None):
    """ 约定：`key_path`不能为空；如果`key_path`为None，则返回整个插件配置 """
    if plugin_name not in fit_config_repo:
        bootstrap_logger.debug(f'plugin: {plugin_name} has no configuration.')
        return None

    cur_val = fit_config_repo[plugin_name]
    if key_path is None:
        return cur_val
    for key in key_path.split(_KEY_SEPARATOR):
        if not isinstance(cur_val, dict) or key not in cur_val or key == '':
            bootstrap_logger.debug(f'config item not found. key: {key_path}')
            return None
        cur_val = cur_val[key]
    return cur_val


@private_fitable
def clear_configurations(conf_node_key: str):
    if conf_node_key in fit_config_repo:
        del fit_config_repo[conf_node_key]


def _load_yaml(file_path: str):
    if not os.path.isfile(file_path):
        bootstrap_logger.warning(f"file not found: {file_path}")
        return {}

    try:
        with open(file_path, encoding='utf-8') as file:
            return yaml.safe_load(file)
    except Exception:
        bootstrap_logger.exception(f'error occurred when parsing yaml file.')
        raise


def _concatenate(conf_node_key, conf_child_node_key):
    conf_child_node_key_split = conf_child_node_key.split(_KEY_SEPARATOR)
    if conf_node_key.startswith(const.GEN_CONF_NODE_KEY_PREFIX):
        conf_child_node_key_split = _un_escape(conf_child_node_key_split)
    return tuple([conf_node_key, *conf_child_node_key_split])


def _un_escape(conf_child_node_key_split):
    # 如果需要更新的配置为genericable配置，将key中的'-'替换成'.'
    if conf_child_node_key_split[0] == "fitables":
        return [key_part.replace('-', '.') for key_part in conf_child_node_key_split]
    return conf_child_node_key_split


def _do_new_or_update(cur_val, key_part_s, new_val):
    for i, key in enumerate(key_part_s):
        if i < len(key_part_s) - 1:
            if key not in cur_val or not isinstance(cur_val[key], dict):
                cur_val[key] = {}
            cur_val = cur_val[key]
        else:
            cur_val.update({key: new_val})


def _do_delete(cur_val, key_part_s):
    for i, key in enumerate(key_part_s):
        if not isinstance(cur_val, dict) or key not in cur_val:
            bootstrap_logger.warning(f'config item not found; current key [{key}]')
            return
        if i < len(key_part_s) - 1:
            cur_val = cur_val.get(key)
        else:
            del cur_val[key]
