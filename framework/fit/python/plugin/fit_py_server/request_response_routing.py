# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：const.REQUEST_RESPONSE_GEN_ID 服务的路由，根据协议转到对应的实现
"""
from typing import Optional

from fit_common_struct.core import Address
from fitframework import const
from fitframework.api.decorators import fitable
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.network.enums import ProtocolEnum
from fitframework.core.network.temp_entity import RequestContext
from fitframework.utils.context import call_context
from fitframework.utils.context.call_context import ROUTING_PROTOCOL


@fitable(generic_id=const.REQUEST_RESPONSE_GEN_ID,
         fitable_id=const.REQUEST_RESPONSE_ROUTE_FITABLE_ID)
def request_response_route(remote_address: Address, metadata: bytes, data: bytes,
                           req_params: RequestContext) -> Optional[str]:
    """
    const.REQUEST_RESPONSE_GEN_ID 服务的路由，根据协议转到对应的实现
    Args:
        remote_address:
        metadata:
        data:
        req_params:

    Returns:

    """
    protocol = call_context.get_context_value(ROUTING_PROTOCOL)
    if protocol == ProtocolEnum.GRPC.value:
        return const.GRPC_REQUEST_RESPONSE_FITABLE_ID
    elif protocol == ProtocolEnum.HTTP.value:
        return const.HTTP_REQUEST_RESPONSE_FITABLE_ID
    elif protocol == ProtocolEnum.UC.value:
        return const.UC_REQUEST_RESPONSE_FITABLE_ID
    elif protocol == ProtocolEnum.HTTPS.value:
        return const.HTTPS_REQUEST_RESPONSE_FITABLE_ID
    sys_plugin_logger.warning(f"request_response_route: protocol {protocol} is not supported")
    return None
