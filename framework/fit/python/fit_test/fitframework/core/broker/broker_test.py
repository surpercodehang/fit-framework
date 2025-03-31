# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：BrokerTemplate单元测试
"""
import unittest
from functools import wraps
from unittest.mock import patch

from fitframework.core.broker.broker import BrokerTemplate
from fitframework.core.broker.broker_utils import FitableIdentifier, IdType

_TEST_IDENTIFIER = FitableIdentifier('fitable1', IdType.id)


def _mock_normal_process(function):
    @wraps(function)
    def wrapped(*args):
        with patch.object(BrokerTemplate, 'fit_execute', return_value='ok'):
            with patch.object(BrokerTemplate, 'load_balancing', return_value='address'):
                with patch.object(BrokerTemplate, 'routing', return_value='fitable1'):
                    return function(*args)

    return wrapped


class BrokerTemplateTest(unittest.TestCase):
    @_mock_normal_process
    def test_invoke_happy_path_with_fitable(self):
        result = BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None, timeout=None,
                                                 is_async=None, retry=None, route_filter=None, address_filter=None)
        self.assertEqual(result, 'ok')

    @_mock_normal_process
    def test_invoke_when_before_raise_exception(self):
        with patch.object(BrokerTemplate, 'on_before', side_effect=Exception()):
            result = BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None, timeout=None, is_async=None,
                                                     retry=None, route_filter=None, address_filter=None)
            self.assertEqual(result, 'ok')

    @_mock_normal_process
    def test_invoke_when_after_raise_exception(self):
        with patch.object(BrokerTemplate, 'on_after', side_effect=Exception()):
            result = BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None, timeout=None,
                                                     is_async=None, retry=None, route_filter=None,
                                                     address_filter=None)
            self.assertEqual(result, 'ok')

    @_mock_normal_process
    def test_invoke_when_validate_raise_exception(self):
        with self.assertRaises(Exception):
            with patch.object(BrokerTemplate, 'on_validate', side_effect=Exception()):
                BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None, timeout=None,
                                                is_async=None, retry=None, route_filter=None, address_filter=None)

    @_mock_normal_process
    def test_invoke_when_route_return_none(self):
        with self.assertRaises(Exception):
            with patch.object(BrokerTemplate, 'route', return_value=None):
                BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None)

    @_mock_normal_process
    def test_invoke_when_load_balancing_return_none(self):
        with self.assertRaises(Exception):
            with patch.object(BrokerTemplate, 'load_balancing', return_value=None):
                BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None)

    @_mock_normal_process
    def test_invoke_when_trigger_degradation(self):
        def execute_side_effect(address, generic_id, fitable_id, *args, timeout, is_async):
            if fitable_id == 'fitable1':
                raise Exception()
            return 'ok'

        with patch.object(BrokerTemplate, 'fit_execute', side_effect=execute_side_effect):
            with patch.object(BrokerTemplate, 'get_degradation', return_value='degradation_id'):
                result = BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None, timeout=1,
                                                         is_async=False, retry=False, route_filter=None,
                                                         address_filter=None)
                self.assertEqual(result, 'ok')

                if __name__ == '__main__':
                    unittest.main()
