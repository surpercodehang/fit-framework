# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：开始服务时间管理器测试。
"""
import time

from fitframework.testing.test_support import FitTestSupport


class StartServeManagerTest(FitTestSupport):
    @classmethod
    def setUpClass(cls):
        super(StartServeManagerTest, cls).setUpClass()
        from plugin.fit_py_server_http import http_utils

        global http_utils

    def test_should_return_time_add_when_get_it(self):
        manager = http_utils.StartServeTimeManager()
        start_serve_time = time.time()
        manager.add_start_serve_time(start_serve_time)
        self.assertEqual(manager.get_earliest_start_time(), start_serve_time)

    def test_should_return_none_when_all_removed(self):
        manager = http_utils.StartServeTimeManager()
        start_serve_time = time.time()
        manager.add_start_serve_time(start_serve_time)
        manager.remove_start_serve_time(start_serve_time)
        self.assertEqual(manager.get_earliest_start_time(), None)

    def test_should_return_correctly_when_add_same(self):
        manager = http_utils.StartServeTimeManager()
        start_serve_time = time.time()
        manager.add_start_serve_time(start_serve_time)
        manager.add_start_serve_time(start_serve_time)
        self.assertEqual(manager.get_earliest_start_time(), start_serve_time)
        manager.remove_start_serve_time(start_serve_time)
        self.assertEqual(manager.get_earliest_start_time(), start_serve_time)
        manager.remove_start_serve_time(start_serve_time)
        self.assertEqual(manager.get_earliest_start_time(), None)

    def test_should_return_earlier_when_add_three_different(self):
        manager = http_utils.StartServeTimeManager()
        manager.add_start_serve_time(0.1)
        manager.add_start_serve_time(0.2)
        manager.add_start_serve_time(0.3)
        manager.remove_start_serve_time(0.2)
        self.assertEqual(manager.get_earliest_start_time(), 0.1)
        manager.remove_start_serve_time(0.1)
        self.assertEqual(manager.get_earliest_start_time(), 0.3)
        manager.remove_start_serve_time(0.3)
        self.assertEqual(manager.get_earliest_start_time(), None)
