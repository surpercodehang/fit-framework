# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：注册中心地址相关功能测试。
"""
import unittest
from unittest.mock import patch

from fit_common_struct.core import Address
from fitframework.testing.test_support import FitTestSupport


class RegistryAddressServiceTest(FitTestSupport):
    @classmethod
    def setUpClass(cls):
        super(RegistryAddressServiceTest, cls).setUpClass()

        from plugin.fit_py_registry_client import registry_address_service
        global registry_address_service
        to_return = [Address("testhost", "8080", "test_id", 2, [1], "unittest", "")]
        cls.patchers = [
            patch.object(registry_address_service, "_get_registry_addresses_from_configuration",
                         return_value=to_return)]

        for patcher in cls.patchers:
            patcher.start()

    @classmethod
    def tearDownClass(cls):
        for patcher in cls.patchers:
            patcher.stop()

    def test_should_return_registry_addresses_when_get(self):
        addresses = registry_address_service.get_cache_aware_registry_address()
        self.assertEqual(addresses, [Address("testhost", "8080", "test_id", 2, [1], "unittest", "")])


if __name__ == "__main__":
    unittest.main()
