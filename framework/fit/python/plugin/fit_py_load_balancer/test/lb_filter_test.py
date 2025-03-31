# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：load balance环境标filter服务测试
"""
import unittest
from unittest.mock import patch

from fit_common_struct.core import Address
from fitframework.testing.test_support import FitTestSupport


class LoadBalanceFilterTest(FitTestSupport):
    @classmethod
    def setUpClass(cls):
        super(LoadBalanceFilterTest, cls).setUpClass()
        from plugin.fit_py_load_balancer import lb_filter
        global lb_filter

    @patch('plugin.fit_py_load_balancer.lb_filter.get_env_seq', return_value='a,b,c')
    @patch('plugin.fit_py_load_balancer.lb_filter.get_worker_env', return_value='a')
    def test_do_filter(self, *_):
        in_addresses = [
            Address('1.1.1.1', 80, '1.1.1.1:80', 1, [1], 'b', ""),
            Address('2.2.2.2', 80, '2.2.2.2:80', 1, [1], 'a', ""),
            Address('3.3.3.3', 80, '3.3.3.3:80', 1, [1], 'c', ""),
            Address('4.4.4.4', 80, '4.4.4.4:80', 1, [1], 'a', ""),
        ]
        result = lb_filter.do_filter(in_addresses)
        expect_addresses = [
            Address('2.2.2.2', 80, '2.2.2.2:80', 1, [1], 'a', ""),
            Address('4.4.4.4', 80, '4.4.4.4:80', 1, [1], 'a', ""),
        ]
        self.assertListEqual(expect_addresses, result)

    @patch('plugin.fit_py_load_balancer.lb_filter.get_env_seq', return_value='a,b,c')
    @patch('plugin.fit_py_load_balancer.lb_filter.get_worker_env', return_value='a')
    def test_do_filter_next_environment(self, *_):
        in_addresses = [
            Address('1.1.1.1', 80, '1.1.1.1:80', 1, [1], 'b', ""),
            Address('2.2.2.2', 80, '2.2.2.2:80', 1, [1], 'd', ""),
            Address('3.3.3.3', 80, '3.3.3.3:80', 1, [1], 'c', ""),
            Address('4.4.4.4', 80, '4.4.4.4:80', 1, [1], 'b', ""),
        ]
        result = lb_filter.do_filter(in_addresses)
        expect_addresses = [
            Address('1.1.1.1', 80, '1.1.1.1:80', 1, [1], 'b', ""),
            Address('4.4.4.4', 80, '4.4.4.4:80', 1, [1], 'b', ""),
        ]
        self.assertListEqual(expect_addresses, result)

    @patch('plugin.fit_py_load_balancer.lb_filter.get_env_seq', return_value=None)
    @patch('plugin.fit_py_load_balancer.lb_filter.get_worker_env', return_value='a')
    def test_do_filter_chain_not_defined(self, *_):
        in_addresses = [
            Address('1.1.1.1', 80, '1.1.1.1:80', 1, [1], 'b', ""),
            Address('2.2.2.2', 80, '2.2.2.2:80', 1, [1], 'd', ""),
            Address('3.3.3.3', 80, '3.3.3.3:80', 1, [1], 'c', ""),
            Address('4.4.4.4', 80, '4.4.4.4:80', 1, [1], 'b', ""),
        ]
        result = lb_filter.do_filter(in_addresses)
        self.assertListEqual(in_addresses, result)


if __name__ == '__main__':
    unittest.main()
