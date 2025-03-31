# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit运行时配置数据库管理模块，包括插件配置和本地服务元数据配置
"""
from typing import List, Optional, Dict, Union

from fit_py_service_db import service_db
from fitframework import const
from fitframework.api.decorators import fit, fitable, private_fitable, private_fit, register_event, local_context
from fitframework.api.enums import FrameworkEvent
from fitframework.api.logging import bootstrap_logger
from fitframework.api.scan_genericables import get_all_gen_id
from fitframework.core.exception.fit_exception import FitException, InternalErrorCode
from fitframework.utils.context import runtime_context
from fitframework.utils.plugin_utils import get_all_plugins_info

_FIT_META_GEN_RULE_KEY = 'route.rule'
_FIT_META_GEN_PARAMS_KEY = 'params'


@local_context('app.name')
def app_name():
    pass


@fit(const.CONFIGURATION_AGENT_DOWNLOAD_GEN_ID, 'py_impl')
def download_configuration(path: str) -> Dict[str, str]:
    pass


@private_fit
def get_configuration_item(plugin_name: str, key: str):
    pass


@private_fit
def subscribe_configurations(conf_node_key_s: List[str], subscription_name) -> None:
    pass


@private_fit
def unsubscribe_configurations(conf_node_key_s: List[str], subscription_name: str) -> None:
    pass


@fitable(const.SERVICE_DB_DOWNLOAD_AND_SUBSCRIBE_ALL_CONF_GEN_ID,
         const.SERVICE_DB_DOWNLOAD_AND_SUBSCRIBE_ALL_CONF_FIT_ID)
def download_and_subscribe_all_configuration():
    """ 下载和注册插件以及@fit的genericableId配置"""
    plugin_config_paths = service_db.get_plugin_config_paths()
    if plugin_config_paths is not None:
        bootstrap_logger.info("downloading all plugin configurations")
        for config_path in plugin_config_paths:
            download_configuration(config_path)
        bootstrap_logger.info("subscribing all plugin configurations")
        subscribe_configurations(plugin_config_paths, app_name())

    bootstrap_logger.info("begin to download all genericable configurations, this may take a few minutes...")
    gen_conf_key_s = list(map(_build_genericable_conf_node_key, _all_generic_ids))
    for key in gen_conf_key_s:
        download_configuration(key)
    bootstrap_logger.info("begin to subscribe all genericable configurations")
    subscribe_configurations(gen_conf_key_s, app_name())

    @register_event(FrameworkEvent.FRAMEWORK_STOPPING)
    def unsubscribe_all_configurations():
        if plugin_config_paths:
            unsubscribe_configurations(plugin_config_paths, app_name())
        unsubscribe_configurations(gen_conf_key_s, app_name())


@fitable(const.SERVICE_DB_GET_FIT_FFP_ALIAS_GEN_ID, const.SERVICE_DB_GET_FIT_FFP_ALIAS_FIT_ID)
def get_fit_ffp_fitable_id(generic_id: str, func_sub_path: str) -> str:
    return get_fit_meta_configuration(generic_id, func_sub_path)


@fitable(const.SERVICE_DB_GET_GENERICABLE_TAGS_GEN_ID, const.SERVICE_DB_GET_GENERICABLE_TAGS_FIT_ID)
def get_genericable_tags(generic_id: str) -> List[str]:
    tags = get_fit_meta_configuration(generic_id, 'tags')
    if tags is None:
        return []
    return _to_array(tags)


@fitable(const.SERVICE_DB_GET_FITABLE_UUID_BY_ALIAS_GEN_ID,
         const.SERVICE_DB_GET_FITABLE_UUID_BY_ALIAS_FIT_ID)
def get_fitable_id_by_alias(generic_id: str, fitable_alias: str) -> Optional[str]:
    fitables = get_fit_meta_configuration(generic_id, 'fitables')
    if not fitables:
        return None
    targets = {}
    try:
        for fit_id, contents in fitables.items():
            if contents and "aliases" in contents and fitable_alias in _to_array(contents["aliases"]):
                targets[fit_id] = contents
        if not targets or len(targets) > 1:
            raise FitException(InternalErrorCode.ROUTING_ALIAS_NOT_FOUND,
                               "Routing alias not found - it's not found or multiple found. "
                               "generic id = %s, alias = %s" % (generic_id, fitable_alias))
        return list(targets.keys())[0]
    except FitException as e:
        bootstrap_logger.error(e.message)
        raise


@fitable(const.GET_ALL_FITABLES_FROM_CONFIG_GEN_ID, const.GET_ALL_FITABLES_FROM_CONFIG_FIT_ID)
def get_all_fitables_from_config(generic_id: str) -> List[str]:
    fitables = get_fit_meta_configuration(generic_id, "fitables")
    return fitables.keys() if fitables else []


@private_fitable
def get_fit_meta_configuration(generic_id: str, key: str) -> Union[str, dict]:
    """
    获得genericable元数据Dict
    Args:
        generic_id (str): genericable id
        key (str): Dict中根节点到目标节点的路径。以'.'将节点名顺序连接

    Returns:
        dict: genericable元数据Dict
    """
    return get_configuration_item(_build_genericable_conf_node_key(generic_id), key)


@fitable(const.SERVICE_DB_GET_GENERICABLE_NAME_GEN_ID, const.SERVICE_DB_GET_GENERICABLE_NAME_FIT_ID)
def get_genericable_name(generic_id: str) -> Optional[str]:
    return get_fit_meta_configuration(generic_id, 'name')


@fitable(const.SERVICE_DB_GET_GENERICABLE_RULE_GEN_ID, const.SERVICE_DB_GET_GENERICABLE_RULE_FIT_ID)
def get_genericable_rule(generic_id: str) -> Dict[str, str]:
    return get_fit_meta_configuration(generic_id, _FIT_META_GEN_RULE_KEY)


@private_fitable
def get_genericable_params(generic_id: str) -> Dict[str, Dict[str, str]]:
    return get_fit_meta_configuration(generic_id, _FIT_META_GEN_PARAMS_KEY)


@private_fitable
def get_degradation(generic_id: str, fitable_id: str) -> Optional[str]:
    fitables = get_fit_meta_configuration(generic_id, "fitables")
    if fitables is None or fitable_id not in fitables:
        return None
    fitable_meta_info = fitables[fitable_id]
    return fitable_meta_info.get('degradation')


@private_fitable
def get_all_generic_ids() -> List[str]:
    return _all_generic_ids


def _build_genericable_conf_node_key(generic_id):
    return const.GEN_CONF_NODE_KEY_PREFIX + generic_id


def _to_array(list_or_str):
    return list_or_str.split(",") if isinstance(list_or_str, str) else list_or_str


def _assemble_framework_and_plugin_locations():  # 获取静态的所有插件的路径信息
    return [runtime_context.get_item(const.STARTUP_FRAMEWORK_FOLDER_KEY),
            *(info[const.STARTUP_PLUGINS_LOCATION_KEY] for info in get_all_plugins_info())]


_all_generic_ids = list(get_all_gen_id(scan_dir_s=_assemble_framework_and_plugin_locations()))
