# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit底座Server服务接口实现：HTTP，目前HTTP服务绑定JSON协议。
"""
from fit_common_struct.core import Address
from fitframework import const
from fitframework.api.decorators import fit, fitable, register_event
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.network.enums import ProtocolEnum
from fitframework.api.enums import FrameworkEvent

from .http_utils import get_http_formats, get_http_protocol, get_host, get_http_server_port, \
    get_http_server_port_to_register, get_http_enabled, get_https_enabled, get_worker_env, get_https_server_port, \
    get_https_server_port_to_register, get_https_protocol, get_https_formats, get_context_path, get_runtime_worker_id
from .fit_http_server import init_fit_https_server, init_fit_http_server, start_all_server, \
    stop_all_server

_WILDCARD_ADDRESS: str = "0.0.0.0"


@fit(const.SERVICE_DB_REGISTER_EXPOSED_SERVER_GEN_ID)
def register_exposed_server(server_id: int, local_address: Address) -> None:
    pass


@fitable(const.FIT_SERVER_START_GEN_ID, const.FIT_SERVER_START_HTTP_FITABLE_ID)
def server_start():
    """
    接口实现应包含以下步骤：
    1. 启动一个新线程
    2. 线程中启动HTTP监听服务，服务绑定在配置IP/Port，监听配置URI上的Requests
    3. 主流程等待服务启动完成
    4. service_db注册本服务器
    """
    if get_http_enabled():
        addr = get_exposed_http_server_address()
        port_to_listen = get_http_server_port()
        register_exposed_server(ProtocolEnum.HTTP.value, addr)
        init_fit_http_server(port_to_listen)
        sys_plugin_logger.info(
            f"http server initialized. [port_to_listen={port_to_listen}, port_to_register={addr.port}, "
            f"context_path={get_context_path()}].")

    if get_https_enabled():
        addr = get_exposed_https_server_address()
        port_to_listen = get_https_server_port()
        register_exposed_server(ProtocolEnum.HTTPS.value, addr)
        init_fit_https_server(port_to_listen)
        sys_plugin_logger.info(
            f"https server initialized. [port_to_listen={port_to_listen}, port_to_register={addr.port}, "
            f"context_path={get_context_path()}].")

    if get_http_enabled() or get_https_enabled():
        start_all_server()
        sys_plugin_logger.info("all http and https server started.")


@register_event(FrameworkEvent.FRAMEWORK_STOPPING)
@fitable(const.FIT_SERVER_STOP_GEN_ID, const.FIT_SERVER_STOP_HTTP_FITABLE_ID)
def server_stop():
    if get_http_enabled() or get_https_enabled():
        sys_plugin_logger.info("prepare to stop all http and https server.")
        stop_all_server()
        sys_plugin_logger.info("all http and https server stopped.")


def get_exposed_http_server_address() -> Address:
    """
    获取通过注册中心对外暴露的本机 http server 地址
    address.host: 优先读取插件配置：local.address。如果配置不存在，主动获得本机地址：utils.get_local_address()
    address.port: 读取插件配置：local.port
    address.id: <host>:<port>
    address.protocol: HTTP, 参考utils.ProtocolEnum
    address.formats: PROTOBUF, 参考utils.FormatEnum
    """
    return Address(get_host(), get_http_server_port_to_register(), get_runtime_worker_id(),
                   get_http_protocol(), get_http_formats(), get_worker_env(), get_context_path())


def get_exposed_https_server_address() -> Address:
    """
    获取通过注册中心对外暴露的本机https server 地址
    address.host: 优先读取插件配置：local.address。如果配置不存在，主动获得本机地址：utils.get_local_address()
    address.port: 读取插件配置：local.port
    address.id: <host>:<port>
    address.protocol: HTTP, 参考utils.ProtocolEnum
    address.formats: PROTOBUF, 参考utils.FormatEnum
    """
    return Address(get_host(), get_https_server_port_to_register(), get_runtime_worker_id(),
                   get_https_protocol(), get_https_formats(), get_worker_env(), get_context_path())
