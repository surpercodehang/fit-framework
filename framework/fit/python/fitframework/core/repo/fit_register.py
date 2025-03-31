# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
@fit服务依赖指针记录。仅用于debug场景
"""
from fitframework.core.repo.service_repo import _set_at_fit_ref


def register_at_fit_function(generic_id: str, func):
    _set_at_fit_ref(generic_id, func)
