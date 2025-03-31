# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：心跳服务本地代理测试。
"""
import time
import unittest
from unittest.mock import patch

from fitframework.testing.test_support import FitTestSupport


class HeartBeatTest(FitTestSupport):

    @classmethod
    def setUpClass(cls):
        super(HeartBeatTest, cls).setUpClass()
        from plugin.fit_py_heart_beat_agent import heart_beat_agent
        global heart_beat_agent

    def setUp(self):
        self.patchers = [
            patch.object(heart_beat_agent, "_interval", return_value=200),
            patch.object(heart_beat_agent, "heartbeat"),
        ]
        self.mocks = [patcher.start() for patcher in self.patchers]

    def test_heart_beat_at_least_once(self):
        heart_beat_agent.online()
        time.sleep(1)
        self.assertTrue(self.mocks[-1].called)

        heart_beat_agent.offline()

    def test_heart_beat_periodicity(self):
        heart_beat_agent.online()
        time.sleep(1)
        begin_call_count = self.mocks[-1].call_count
        time.sleep(1)
        end_call_count = self.mocks[-1].call_count

        self.assertGreaterEqual(end_call_count - begin_call_count, 1)
        heart_beat_agent.offline()


if __name__ == '__main__':
    unittest.main()
