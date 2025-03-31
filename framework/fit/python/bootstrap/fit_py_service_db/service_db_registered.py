# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit运行时可选插件服务服务数据库
"""
from typing import List

from fit_common_struct.core import Address
from fitframework.api.logging import bootstrap_logger
from fitframework import const
from fitframework.api.decorators import fitable
from fitframework.core.network.enums import ProtocolEnum, SerializingStructureEnum

# 注册服务端支持的通讯规约 - key: protocol id, value: Address 本地地址
_registered_server_dict = {}

# 注册客户端支持的通讯规约
_registered_client_ids = []

# 注册客户端支持的通讯协议
_registered_format_ids = []


@fitable(const.SERVICE_DB_REGISTER_EXPOSED_SERVER_GEN_ID, const.SERVICE_DB_REGISTER_EXPOSED_SERVER_FIT_ID)
def register_exposed_server(server_id: int, local_address: Address) -> None:
    """
    服务器通讯规约ID注册。
    同时注册服务器地址，通讯规约和格式。服务端通讯规约和格式绑定
    """
    try:
        _registered_server_dict[ProtocolEnum(server_id)] = local_address
        bootstrap_logger.info(f"register {ProtocolEnum(server_id).name} server")
    except ValueError:
        bootstrap_logger.error(f"invalid server id to be registered: {server_id}")


@fitable(const.SERVICE_DB_UNREGISTER_SERVER_GEN_ID, const.SERVICE_DB_UNREGISTER_SERVER_FIT_ID)
def unregister_server(server_id: str) -> None:
    """ 服务器注销，用于插件注销情景 """
    try:
        del _registered_server_dict[ProtocolEnum(server_id)]
        bootstrap_logger.info(f"unregister {ProtocolEnum(server_id).name} server")
    except ValueError:
        bootstrap_logger.error(f"invalid server id to be unregistered: {server_id}")


@fitable(const.SERVICE_DB_GET_REGISTER_SERVER_GEN_ID, const.SERVICE_DB_GET_REGISTER_SERVER_FIT_ID)
def get_server_address_by_server_id(server_id: int) -> Address:
    try:
        return _registered_server_dict[ProtocolEnum(server_id)]
    except ValueError:
        bootstrap_logger.error(f"invalid server id when get_server_address_by_server_id: {server_id}")
        return None


@fitable(const.SERVICE_DB_GET_ALL_ADDRESS_GEN_ID, const.SERVICE_DB_GET_ALL_ADDRESS_FIT_ID)
def get_server_addresses() -> List[Address]:
    return list(_registered_server_dict.values())


@fitable(const.SERVICE_DB_REGISTER_CLIENT_GEN_ID, const.SERVICE_DB_REGISTER_CLIENT_FIT_ID)
def register_client_protocol(client_protocol_id: int) -> None:
    """ 客户端通讯规约ID注册 """
    try:
        _registered_client_ids.append(ProtocolEnum(client_protocol_id))
        bootstrap_logger.info(f"register client side protocol {ProtocolEnum(client_protocol_id).name}")
    except ValueError:
        bootstrap_logger.error(f"invalid client id to be registered: {client_protocol_id}")


@fitable(const.SERVICE_DB_UNREGISTER_CLIENT_GEN_ID, const.SERVICE_DB_UNREGISTER_CLIENT_FIT_ID)
def unregister_client(client_protocol_id: str) -> None:
    """ 客户端注销，用于插件注销情景 """
    try:
        _registered_client_ids.remove(ProtocolEnum(client_protocol_id))
        bootstrap_logger.info(f"unregister client side protocol {ProtocolEnum(client_protocol_id).name}")
    except ValueError:
        bootstrap_logger.error(f"invalid client id to be unregistered: {client_protocol_id}")


@fitable(const.SERVICE_DB_GET_REGISTER_CLIENTS_GEN_ID, const.SERVICE_DB_GET_REGISTER_CLIENTS_FIT_ID)
def get_registered_clients() -> List[int]:
    return _registered_client_ids


@fitable(const.SERVICE_DB_REGISTER_FORMAT_GEN_ID, const.SERVICE_DB_REGISTER_FORMAT_FIT_ID)
def register_format(format_id: str) -> None:
    """ 通讯协议ID注册 """
    try:
        _registered_format_ids.append(SerializingStructureEnum(format_id))
        bootstrap_logger.info(f"register format: {SerializingStructureEnum(format_id).name}")
    except ValueError:
        bootstrap_logger.error(f"invalid format id to be registered: {format_id}")


@fitable(const.SERVICE_DB_GET_REGISTER_FORMATS_GEN_ID, const.SERVICE_DB_GET_REGISTER_FORMATS_FIT_ID)
def get_registered_formats() -> List[int]:
    return _registered_format_ids
