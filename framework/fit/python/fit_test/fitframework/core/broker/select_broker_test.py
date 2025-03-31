# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：select_broker单元测试
"""
import unittest
from unittest.mock import patch

from fitframework.core.broker import select_broker
from fitframework.core.broker.configure_based_brokerimpl import ConfigureBasedBroker
from fitframework.core.broker.simple_brokerimpl import SimpleBroker
from fitframework.core.repo import service_repo


class SelectBrokerTest(unittest.TestCase):
    @patch.object(service_repo, 'is_glued', return_value=True)
    def test_select_broker_when_is_glued(self, _):
        self.assertIsInstance(select_broker.select('gen1').broker, SimpleBroker)

    @patch.object(service_repo, 'is_glued', return_value=False)
    def test_select_broker_when_is_normal(self, _):
        self.assertIsInstance(select_broker.select('gen1').broker, ConfigureBasedBroker)


if __name__ == '__main__':
    unittest.main()
