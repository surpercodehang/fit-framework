# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：@value单元测试
"""
import unittest
from unittest.mock import patch

from fitframework.api.decorators import value
from fitframework.api.value_descriptor import Value
from fitframework.core.exception.fit_exception import FitException
from fitframework.core.repo import service_repo
from fitframework.testing.test_support import FitTestSupport
from fitframework.utils.context import runtime_context


def get_value_info(_, key):
    if key == 'a.b.c':
        return '_test-value'
    else:
        return None


class ValueDecoratorTest(FitTestSupport):

    def setUp(self) -> None:
        pass
        from fitframework.core.repo.fitable_register import loading_context
        loading_context.plugin_info = 'testpath', 'test_plugin_name'

    def tearDown(self) -> None:
        pass

    @patch('fitframework.core.repo.value_register.register_value_ref')
    @patch.object(service_repo, 'get_configuration_item', side_effect=get_value_info)
    @patch.object(service_repo, 'get_value', return_value=None)
    def test_inject_function(self, *_):
        @value(key='a.b.c')
        def getabc():
            pass

        @value(key='a.b.c.d')
        def getabcd():
            pass

        self.assertEqual('_test-value', getabc())
        self.assertEqual(None, getabcd())

    @patch('fitframework.core.repo.value_register.register_value_ref')
    @patch.object(service_repo, 'get_configuration_item', side_effect=get_value_info)
    @patch.object(service_repo, 'get_value', return_value=None)
    def test_inject_method(self, *_):
        class ClassForTest:
            @value('a.b.c')
            def attr_a(self):
                pass

            @value('a.b.c.b')
            def attr_b(self):
                pass

        self.assertEqual('_test-value', ClassForTest().attr_a())
        self.assertEqual(None, ClassForTest().attr_b())

    @patch('fitframework.core.repo.value_register.register_value_ref')
    @patch.object(runtime_context, 'get_item', return_value='value from runtime context')
    @patch.object(service_repo, 'get_configuration_item', side_effect=get_value_info)
    def test_value_from_context(self, *_):
        @value(key='a.b.c')
        def getabc():
            pass

        self.assertEqual('value from runtime context', getabc())

    @patch.object(runtime_context, 'get_item', return_value='value from runtime context')
    def test_value_from_class(self, *_):
        class ATest:
            test_value = Value(key='any key', default_value='none')

        a_test = ATest()
        assert a_test.test_value == 'value from runtime context'

        test_value = Value(key='any key', default_value='none')

    @patch.object(runtime_context, 'get_item', return_value='value from runtime context')
    def test_value_from_class_and_set_value_fail(self, *_):
        class ATest:
            test_value = Value(key='any key', default_value='none')

        a_test = ATest()
        with self.assertRaises(FitException):
            a_test.test_value = 'x'


if __name__ == '__main__':
    unittest.main()
