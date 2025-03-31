# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：模块加载功能单元测试
"""
import os
import unittest
from unittest import skip
from unittest.mock import patch

from fitframework import PluginType
from fitframework.testing.test_support import FitTestSupport
from fitframework.utils import load_module


def get_abs_path(path):
    _dir = os.path.dirname(__file__)
    return os.path.abspath(os.path.join(_dir, path))


@skip("由于插件加载相关逻辑变化过大，且此部分测试在 FIT for Python 项目重启就无法运行，此部分测试暂不恢复。")
class LoadModuleTest(FitTestSupport):
    @patch('fitframework.utils.load_module.importlib.import_module')
    def test_load_sys_plugin(self, mock_method):
        load_module.load_plugins_from_roots([get_abs_path('_load_plugin_test/_test_system_plugin')],
                                            PluginType.BOOTSTRAP)
        mock_method.assert_called_with("system_plugin.sys_module")

    @patch('fitframework.utils.load_module.register_plugin')
    @patch('fitframework.utils.load_module.importlib.import_module')
    def test_load_plugin(self, mock_method, mock_register_plugin):
        load_module.load_plugins_from_roots([get_abs_path('_load_plugin_test/_test_plugin')], PluginType.SYSTEM)
        mock_method.assert_called_with("simple_plugin.test_module")
        # assert __file__
        mock_register_plugin.assert_called()

    @patch('fitframework.utils.load_module.register_plugin')
    @patch('fitframework.utils.load_module._import_package')
    @patch('os.path.isdir')
    def test_not_load_not_dir(self, mock_isdir, mock_import_package, _):
        mock_isdir.return_value = False
        load_module.load_plugins_from_roots([get_abs_path('_load_plugin_test/_test_not_load_plugin')],
                                            PluginType.SYSTEM)
        assert not mock_import_package.called

    @patch('fitframework.utils.load_module.register_plugin')
    @patch('fitframework.utils.load_module.pkgutil.iter_modules')
    def test_not_load_not_test_module(self, mock_iter_modules, _):
        load_module.load_plugins_from_roots([get_abs_path('_load_plugin_test/_test_load_test_module')],
                                            PluginType.SYSTEM)
        assert mock_iter_modules.call_args is None


if __name__ == '__main__':
    unittest.main()
