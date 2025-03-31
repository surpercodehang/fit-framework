# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：远程调用模块
"""
from pprint import pformat
from typing import List

from fit_common_struct.core import Address
from fitframework import const
from fitframework.api.decorators import fit, local_context
from fitframework.api.exception import FIT_OK
from fitframework.api.logging import fit_logger, FileHandler
from fitframework.core.broker.broker_utils import get_priority_index
from fitframework.core.exception.fit_exception import FitException, NetworkException
from fitframework.core.network.enums import ProtocolEnum, SerializingStructureEnum
from fitframework.core.network.fit_method_serializer import FitMethodSerializer
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.metadata.request_metadata import RequestMetadata
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.core.network.temp_entity import RequestContext
from fitframework.utils.context import call_context
from fitframework.utils.context.call_context import ROUTING_PROTOCOL
from fitframework.utils.tools import to_list

default_priority = [
    f'{ProtocolEnum.GRPC.name}:{SerializingStructureEnum.PROTOBUF.name}',
    f'{ProtocolEnum.HTTPS.name}:{SerializingStructureEnum.PROTOBUF.name}',
    f'{ProtocolEnum.HTTP.name}:{SerializingStructureEnum.PROTOBUF.name}',
    f'{ProtocolEnum.GRPC.name}:{SerializingStructureEnum.CBOR.name}',
    f'{ProtocolEnum.HTTPS.name}:{SerializingStructureEnum.CBOR.name}',
    f'{ProtocolEnum.HTTP.name}:{SerializingStructureEnum.CBOR.name}',
    f'{ProtocolEnum.GRPC.name}:{SerializingStructureEnum.JSON.name}',
    f'{ProtocolEnum.HTTPS.name}:{SerializingStructureEnum.JSON.name}',
    f'{ProtocolEnum.HTTP.name}:{SerializingStructureEnum.JSON.name}'
]


@local_context(key='worker.protocol-priorities', default_value=default_priority, converter=to_list)
def protocol_priors():
    pass


@fit(const.REQUEST_RESPONSE_GEN_ID)
def request_response(address: Address, metadata: RequestMetadata, data: bytes,
                     request_context: RequestContext) -> FitResponse:
    pass


@fit(const.SERVICE_DB_GET_REGISTER_FORMATS_GEN_ID)
def get_registered_formats() -> List[int]:
    pass


@fit(const.MARK_ADDRESS_STATUS_GEN_ID, alias='set_fitable_status_python')
def set_fitable_instance_status(_address: Address, _valid: bool) -> None:
    pass


def get_supported_formats(formats: list) -> list:
    return list(set(get_registered_formats()).intersection(set(formats)))


def call(address: Address, fit_invoke_info: tuple, args, timeout: int, is_async: bool):
    generic_id, fitable_id, fit_ref = fit_invoke_info
    supported_format = _get_supported_format(address)
    try:
        params = fit_invoke_info, args, address, supported_format, timeout, is_async
        fit_response = request_response(*_assemble_parameters(*params))
        _validate_result(fit_response.metadata)
        return _process_result(fit_ref, fit_response.data, supported_format)
    except NetworkException:
        info = f"error occurred in remote invoke towards: {address} with generic id: {generic_id}"
        fit_logger.error(info + f" and args: {pformat(args, depth=1)}")
        fit_logger.error(
            info + f"\n\tfit_ref: {fit_ref}\n\tfitable_id: {fitable_id}\n\tformat: {supported_format}"
                   f"\n\ttimeout: {timeout}\n\targs: {pformat(args)}", dests=FileHandler)
        set_fitable_instance_status(address, False)
        raise


def _get_supported_format(remote_endpoint: Address) -> int:
    supported_formats = get_supported_formats(remote_endpoint.formats)
    if len(supported_formats) == 1:
        return supported_formats[0]

    def sort_rule(_format):
        return get_priority_index(remote_endpoint.protocol, [_format], protocol_priors())

    supported_formats.sort(key=sort_rule)
    return supported_formats[0]


def _assemble_parameters(fit_invoke_info, args, remote_address: Address, supported_format: int,
                         timeout: int, is_async: bool):
    generic_id, fitable_id, fit_ref = fit_invoke_info
    call_context.put_context_value(ROUTING_PROTOCOL, remote_address.protocol)
    address = remote_address
    metadata = RequestMetadata.default(generic_id, fitable_id, supported_format)
    data_bytes = FitMethodSerializer(fit_ref).from_request(supported_format, list(args))
    request_context = RequestContext(timeout, is_async)
    return address, metadata, data_bytes, request_context


def _validate_result(resp_metadata: ResponseMetadata):
    if resp_metadata.code != FIT_OK:
        raise FitException(resp_metadata.code, resp_metadata.msg,
                           resp_metadata.is_err_degradable)


def _process_result(fit_ref, return_data, supported_format):
    return FitMethodSerializer(fit_ref).to_return_value(supported_format, return_data)
