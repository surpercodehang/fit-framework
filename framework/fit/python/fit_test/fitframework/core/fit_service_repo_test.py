# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：fit_service_repo.py单元测试
"""
import unittest
from unittest.mock import patch

import fitframework.core.repo.fitable_register
from fitframework.core.repo import fitable_register, service_repo
from fitframework.core.repo.value_register import register_value_ref

TEST_GEN_ID = 'gen1'
TEST_GEN_FITABLE_ID = 'fitable1_1'
TEST_GEN_WITH_FITABLE_ID = (TEST_GEN_ID, TEST_GEN_FITABLE_ID)
TEST_FITABLE_FUNC = lambda _: _
TEST_FIT_FUNC = lambda: None
TEST_PLUGIN_NAME = 'plugin1'
TEST_KEY = 'key1'
TEST_VAL = 'val1'


class FitServiceRepoTest(unittest.TestCase):
    def set_up(self, is_bootstrap=True, register_fitable=True):
        if is_bootstrap:
            fitframework.core.repo.fitable_register.loading_context.is_bootstrap = True
        if register_fitable:
            fitable_register.register_fitable(*TEST_GEN_WITH_FITABLE_ID, is_private=False, aliases=[],
                                              fitable_ref=TEST_FITABLE_FUNC)

    def tear_down(self):
        if hasattr(fitframework.core.repo.fitable_register.loading_context, 'is_bootstrap'):
            del fitframework.core.repo.fitable_register.loading_context.is_bootstrap
        service_repo._glued_fitables.clear()
        service_repo._fitable_ref_repo.clear()
        service_repo._value_repo.clear()

    @patch.object(fitable_register, '_register_to_plugin')
    def test_register_fitable_is_boostrap_success(self, mock_register_to_plugin):
        self.set_up(register_fitable=False)
        fitable_register.register_fitable(*TEST_GEN_WITH_FITABLE_ID, is_private=False, aliases=[],
                                          fitable_ref=TEST_FITABLE_FUNC)
        self.assertIn(TEST_GEN_ID, service_repo._glued_fitables)
        self.assertListEqual(service_repo._glued_fitables[TEST_GEN_ID], [TEST_GEN_FITABLE_ID])
        mock_register_to_plugin.assert_not_called()
        self.assertIn(TEST_GEN_WITH_FITABLE_ID, service_repo._fitable_ref_repo)
        self.assertEqual(service_repo._fitable_ref_repo.get(TEST_GEN_WITH_FITABLE_ID),
                         TEST_FITABLE_FUNC)
        self.tear_down()

    @patch.object(fitable_register, '_register_to_plugin')
    def test_register_fitable_non_bootstrap_success(self, mock_register_to_plugin):
        fitable_register.register_fitable(*TEST_GEN_WITH_FITABLE_ID, is_private=False, aliases=[],
                                          fitable_ref=TEST_FITABLE_FUNC)
        self.assertFalse(service_repo._glued_fitables)
        mock_register_to_plugin.assert_called_once()
        self.assertEqual(service_repo._fitable_ref_repo.get(TEST_GEN_WITH_FITABLE_ID),
                         TEST_FITABLE_FUNC)
        self.tear_down()

    def test_register_fitable_param_error_fail(self):
        with self.assertRaises(Exception):
            fitable_register.register_fitable(TEST_GEN_ID, None, is_private=False, aliases=[],
                                              fitable_ref=TEST_FITABLE_FUNC)
        with self.assertRaises(Exception):
            fitable_register.register_fitable('', TEST_GEN_FITABLE_ID, is_private=False, aliases=[],
                                              fitable_ref=TEST_FITABLE_FUNC)

    def test_get_fitable_ref(self):
        self.set_up()
        address = service_repo.get_fitable_ref(*TEST_GEN_WITH_FITABLE_ID)
        self.assertEqual(address.__name__, TEST_FITABLE_FUNC.__name__)
        self.tear_down()

    def test_get_bootstrap_fitable_ids(self):
        self.set_up()
        fitable_id_s = service_repo.get_glued_fitable_ids(TEST_GEN_ID)
        self.assertListEqual(fitable_id_s, [TEST_GEN_FITABLE_ID])
        self.tear_down()

    @patch.object(fitframework.core.repo.value_register, "inspect_plugin_name_by_func", return_value=TEST_PLUGIN_NAME)
    def test_register_value_info(self, _):
        register_value_ref(TEST_FITABLE_FUNC, TEST_KEY)
        self.assertIn(TEST_FITABLE_FUNC, service_repo._value_repo)
        self.assertTupleEqual(service_repo._value_repo.get(TEST_FITABLE_FUNC), (TEST_PLUGIN_NAME, TEST_KEY))

    # @patch.object(service_repo_utils, '_found_plugin_by_path', return_value=None)
    def test_register_value_info_fail(self):
        """ 覆盖全局的`mock_found_plugin_by_path`，而使用局部版本 """
        with self.assertLogs() as cm:
            register_value_ref(TEST_FITABLE_FUNC, TEST_KEY)
            self.assertTrue(hasattr(cm, 'output'))
            self.assertIsNotNone(cm.output)
            self.assertNotEqual(str(cm.output), '')

    @patch.object(service_repo, 'get_configuration_item', return_value=TEST_VAL)
    def test_get_value_info(self, _):
        register_value_ref(TEST_FITABLE_FUNC, TEST_KEY)
        val = service_repo.get_value(TEST_FITABLE_FUNC)
        self.assertEqual(val, TEST_VAL)
        self.tear_down()

    @patch.object(service_repo, 'get_configuration_item', return_value=TEST_VAL)
    def test_get_value_info_fail(self, mock_get_configuration_item):
        with self.assertRaises(Exception):
            _ = service_repo.get_value(TEST_FITABLE_FUNC)
        mock_get_configuration_item.assert_not_called()
        self.tear_down()


if __name__ == '__main__':
    unittest.main()
