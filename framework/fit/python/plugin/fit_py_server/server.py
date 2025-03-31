# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：对远程的来的http或grpc请求进行处理
"""

from fitframework.api.logging import sys_plugin_logger

from fitframework import const
from fitframework.api.decorators import fitable
from fitframework.api.exception import FitBaseException
from fitframework.core.broker import select_broker
from fitframework.core.broker.broker_utils import FitableIdentifier, IdType
from fitframework.core.exception.fit_exception import InternalErrorCode
from fitframework.core.network import fit_method_serializer
from fitframework.core.network.fit_response import FitResponse, CODE_UNKNOWN, \
    MSG_UNKNOWN
from fitframework.core.network.metadata.request_metadata import RequestMetadata
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.core.repo import service_repo as service_repo


@fitable(const.SERVER_RESPONSE_GEN_ID, const.SERVER_RESPONSE_FIT_ID)
def server_response(metadata: RequestMetadata, data: bytes) -> FitResponse:
    """
    http/grpc 服务端接受字节流请求后处理并返回FitResponse字节流
    :param metadata: 元数据
    :param data: fitable 参数
    :return:
    """
    generic_id, fitable_id, data_format = metadata.generic_id, metadata.fitable_id, metadata.data_format
    try:
        fitable_ref = service_repo.get_fitable_ref(generic_id, fitable_id)
        if fitable_ref is None:
            return FitResponse(
                ResponseMetadata.error_message(data_format, InternalErrorCode.SERVER_LOCAL_EXECUTOR_NOT_FOUND.value,
                                               "No local executor."), b'')
        _converter = fit_method_serializer.FitMethodSerializer(fitable_ref)
        args = _converter.to_request(data_format, data)
        identifier = FitableIdentifier(fitable_id, IdType.id)
        return_ = select_broker.select(generic_id).from_remote() \
            .fitable_identifier(identifier).fit_ref(fitable_ref).fit_selector_invoke(*tuple(args))
        resp_metadata = ResponseMetadata.success(data_format=data_format)
        return_bytes = _converter.from_return_value(data_format, return_)
        return FitResponse(resp_metadata, return_bytes)
    except Exception as error:
        sys_plugin_logger.exception(
            f"Fail to execute fitable. [genericableId=%s, fitableId=%s]" % (
                generic_id, fitable_id))
        if not isinstance(error, FitBaseException):
            error = FitBaseException(CODE_UNKNOWN, MSG_UNKNOWN)
        resp_metadata = ResponseMetadata.failure(data_format, error)
        return FitResponse(resp_metadata, b'')
