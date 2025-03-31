# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit通用异常基类
"""

FIT_OK = 0x0
DEFAULT_ERROR_CODE = 0x7F000000


class FitBaseException(Exception):
    """
    Fit通用异常基类
        错误码：
        缺省错误码为0x7F000000（最大允许的用户错误码不能大于等于该值）
        小于缺省错误码为插件用户自定义错误码
        大于缺省错误码为FIT系统内部使用
    """

    def __init__(self, error_code: int, message: str, degradable: bool = True):
        if self.is_user_defined_exception():
            FitBaseException._validate_user_error_code(error_code)
        super().__init__(message)
        self.degradable = degradable
        self.error_code = error_code
        self.message = message

    @classmethod
    def is_raw_exception(cls, e: Exception):
        return not isinstance(e, FitBaseException)

    @classmethod
    def _validate_user_error_code(cls, error_code):
        if not (FIT_OK < error_code < DEFAULT_ERROR_CODE):
            from fitframework.core.exception.fit_exception import InternalErrorCode, FitException
            raise FitException(InternalErrorCode.INVALID_USER_ERROR_CODE_DEFINITION,
                               f"invalid fit exception code definition: code should be in range: "
                               f"[{FIT_OK}, {DEFAULT_ERROR_CODE})")

    def is_user_defined_exception(self):
        from fitframework.core.exception.fit_exception import FitException
        return not isinstance(self, FitException)
