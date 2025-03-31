# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit插件单元测试用基类。插件的单元测试注意事项：
测试的Module需要在测试方法中实时import，不能在Module头import
"""

import unittest
from typing import Callable, Any
from unittest.mock import Mock

from fitframework.api import decorators
from fitframework.utils import eventing


class FitTestSupport(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.cached_fit_ref = decorators.fit
        cls.cached_fitable_ref = decorators.fitable
        cls.cached_value_ref = decorators.value
        cls.cached_private_fitable = decorators.private_fitable
        cls.cached_private_fit = decorators.private_fit
        cls.cached_local_context = decorators.local_context
        cls.cached_register_event = eventing.register

        decorators.fit = Mock(return_value=lambda func: func)
        decorators.fitable = Mock(return_value=lambda func: func)
        decorators.value = Mock(return_value=lambda func: func)
        decorators.local_context = Mock(return_value=lambda func: func)
        decorators.private_fitable = Mock(side_effect=lambda func: func)
        decorators.private_fit = Mock(side_effect=lambda func: func)
        eventing.register_event = Mock(return_value=lambda func: func)

    @classmethod
    def tearDownClass(cls):
        decorators.fit = cls.cached_fit_ref
        decorators.fitable = cls.cached_fitable_ref
        decorators.value = cls.cached_value_ref
        decorators.private_fitable = cls.cached_private_fitable
        decorators.private_fit = cls.cached_private_fit
        eventing.register = cls.cached_register_event


class Counter:
    def __init__(self):
        self.count = 0

    def get_count(self) -> int:
        return self.count


def decorate_func_with_counter(func: Callable[[tuple[Any, ...], dict[str, Any]], Any]) -> \
        tuple[Callable[[tuple[Any, ...], dict[str, Any]], Any], Counter]:
    counter = Counter()

    def decorator(func_):
        def wrapper(*args, **kwargs):
            counter.count += 1
            return func_(*args, **kwargs)

        return wrapper

    return decorator(func), counter
