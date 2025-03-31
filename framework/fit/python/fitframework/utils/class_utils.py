# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：fit基本工具类
"""


class CountedFlyweight(type):
    """
    metaclass implements flyweight pattern; keyed by first arg; counting the reference number.
    Not thread-safe
    """
    _instances = {}

    def __call__(cls, *args, **kwargs):
        key = args[0]
        if key not in cls._instances:
            cls._instances[key] = super(CountedFlyweight, cls).__call__(*args, **kwargs)
        else:
            cls._instances[key]._init_arguments(*args[1:])
        instance = cls._instances[key]
        instance._add_ref()
        return instance


