# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：simple_brokerimpl.py单元测试
"""
import unittest
from unittest.mock import patch

from fitframework.core.broker.simple_brokerimpl import SimpleBroker
from fitframework.core.repo import service_repo


def _mock_function(in_param: str):
    return in_param


class SimpleBrokerImplTest(unittest.TestCase):
    @patch.object(service_repo, 'get_glued_fitable_ids', return_value=['1'])
    def test_routing_good(self, *_):
        fit_ids = SimpleBroker().routing('gen1', None)
        self.assertListEqual(['1'], fit_ids)

    @patch.object(service_repo, 'get_glued_fitable_ids', return_value=[])
    def test_routing_return_empty(self, *_):
        fit_ids = SimpleBroker().routing('gen1', None)
        self.assertFalse(fit_ids)

    @patch.object(service_repo, 'get_glued_fitable_ids', return_value=['1', '2'])
    def test_routing_return_multiple(self, *_):
        fit_ids = SimpleBroker().routing('gen1', None)
        self.assertFalse(fit_ids)

    @patch.object(service_repo, 'get_fitable_ref', return_value=_mock_function)
    def test_load_balancing(self, *_):
        address = SimpleBroker().load_balancing('gen1', 'fitable1')
        self.assertEqual('_mock_function', address.__name__)

    def test_execute(self):
        self.assertEqual('ok',
                         SimpleBroker().fit_execute(_mock_function, 'gen1', 'fitable1', None, 'ok'))


if __name__ == '__main__':
    unittest.main()
