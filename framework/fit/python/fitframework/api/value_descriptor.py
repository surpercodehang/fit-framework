# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
from fitframework.api.decorators import private_fit
from fitframework.api.logging import fit_logger
from fitframework.core.exception.fit_exception import FitException, InternalErrorCode
from fitframework.core.repo.repo_utils import inspect_plugin_name_by_func
from fitframework.utils.context import runtime_context


@private_fit
def get_configuration_item(plugin_name: str, key: str):
    pass


class Value(object):
    """用于类变量属性注入"""

    def __init__(self, key: str, default_value=None):
        self.key = key
        self.default_value = default_value

    def __get__(self, instance, owner):
        val = runtime_context.get_item(self.key)
        if val is None:
            val = get_configuration_item(inspect_plugin_name_by_func(instance.__class__), self.key)
        val = self.default_value if not val else val
        return val

    def __set__(self, instance, value):
        fit_logger.warning(f'value inject not support set, the class is {instance.__class__}')
        raise FitException(InternalErrorCode.INVALID_ARGUMENTS, 'value injection not support set')
