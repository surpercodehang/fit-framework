# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：FitResponse结构体，远程通讯返回对象
"""
from fitframework.api.exception import FitBaseException
from fitframework.core.network.metadata.response_metadata import ResponseMetadata

CODE_SUCCESS = 0
# fit调用产生成功消息。
MSG_SUCCESS = "success"
# fit调用产生未知错误码
CODE_UNKNOWN = -1
# fit调用产生未知错误码消息
MSG_UNKNOWN = "unknown error"


class FitResponse:
    def __init__(self, metadata: ResponseMetadata, data: bytes = None):
        # 调用失败时，可能没有返回结果，故为`data`设置一个默认值
        self.metadata: ResponseMetadata = metadata
        self.data: bytes = data


def get_error_code_by_exc_val(exc_val):
    if not exc_val:
        return CODE_SUCCESS
    if isinstance(exc_val, FitBaseException):
        return exc_val.error_code

    return CODE_UNKNOWN
