# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：包加载工具类
load a `python package` or `python module` (language design concept)
--- into -->
`system mod object`/`mod_obj`/`ModuleType object`/`module type object` (code object concept)
"""

import glob
import os
import sys
from importlib import import_module as import_as_mod_obj
from importlib.util import spec_from_file_location, module_from_spec as mod_obj_from_spec
from types import ModuleType

_CPYTHON_MODULE_VERSION_SYMBOL = ".cpython-39"

_pkg_python_paths_cache = set()

loading_so_plugin_name = None  # 用于解决 so 中的函数指针无法正确解析插件名称的问题


def import_package(pkg_uri: str, pkg_depth=1, *, recursive=False) -> ModuleType:
    """
    Import a python package. Note that package is also imported as an object of
        type `module` class (type `ModuleType`).

    :param pkg_uri: can be a directory path, or a dotted-style package name.
    :param pkg_depth:
        Specify which level the package begins at. this param would be ignored
            when `pkg_uri` is a package name.
        For example, if `pkg_depth` is 3 and the `pkg_uri` is `/opt/a/b/c/d`, then
            the package will begin at /opt/a and the package name will be set to
            `b.c.d` (default value: 1).
        The level where package begins could be understood as a package root,
            i.e. a place which the package is located at. This is what we usually called
            "PYTHONPATH".
    :param recursive:
    :return: a mod object.
    """
    if _is_valid_path(pkg_uri):
        pkg_python_path, pkg_name = _resolve_package_info(pkg_uri, pkg_depth)
        _update_pkg_python_paths(pkg_python_path)
        pkg_python_path, _ = _resolve_package_info_related_to_current_file(pkg_uri, pkg_depth)
        _update_pkg_python_paths(pkg_python_path)
    else:
        pkg_name = pkg_uri
        pkg_depth = len(pkg_name.split('.'))
    pkg_mod_obj = import_as_mod_obj(pkg_name)
    # optional, which would be done automatically by `importlib.import_module`
    sys.modules[pkg_name] = pkg_mod_obj
    if recursive:
        # module and package name could not be same
        if not hasattr(pkg_mod_obj, '__path__'):
            raise Exception("the module name cannot be the same with the package.")
        for full_path in sorted(glob.iglob(f"{pkg_mod_obj.__path__[0]}//*"), reverse=True):
            if _is_python_module(full_path):
                sub_mod_obj = load_module(full_path, pkg_depth)
                setattr(pkg_mod_obj, sub_mod_obj.__name__.split('.')[-1], sub_mod_obj)
            elif _is_python_so_module(full_path):
                sub_mod_obj = load_so_module(full_path, pkg_depth)
                setattr(pkg_mod_obj, sub_mod_obj.__name__.split('.')[-1], sub_mod_obj)
            elif _is_python_package(full_path):
                sub_pkg_mod_obj = import_package(full_path, pkg_depth + 1, recursive=True)
                # optional, which would be done automatically by `importlib.import_module`
                setattr(pkg_mod_obj, sub_pkg_mod_obj.__name__.split('.')[-1], sub_pkg_mod_obj)
            else:
                pass
    return pkg_mod_obj


def get_module_name_by_uri(module_uri: str, pkg_depth: int) -> str:
    if len(module_uri) == 0:
        raise Exception("module uri cannot be empty.")
    return ".".join(os.path.splitext(module_uri)[0].split(os.sep)[-1 - pkg_depth:])


def load_module(module_uri: str, pkg_depth=0) -> ModuleType:
    """
    Import a python module.

    :param module_uri: can be a file path, or a full dotted-style module name
        (= package name + module name)
    :param pkg_depth: optional specified package, comprised of `a.b.c...`.
        this param would be ignored when `module_uri` is a full module name.
    :return: a mod object.
    """
    if _is_valid_path(module_uri):
        module_name = get_module_name_by_uri(module_uri, pkg_depth)
        spec = spec_from_file_location(module_name, module_uri)
        mod_obj = mod_obj_from_spec(spec)
        if module_name not in sys.modules:
            spec.loader.exec_module(mod_obj)
            sys.modules[module_name] = mod_obj
        return mod_obj
    else:
        return import_as_mod_obj(module_uri)


def load_so_module(module_uri: str, pkg_depth=0) -> ModuleType:
    if not _is_valid_path(module_uri):
        raise Exception("load so module failed.")
    global loading_so_plugin_name
    module_uri_without_ext = module_uri[:module_uri.find(_CPYTHON_MODULE_VERSION_SYMBOL)]
    module_name = get_module_name_by_uri(module_uri_without_ext, pkg_depth)
    loading_so_plugin_name = module_name.split('.')[0]
    spec = spec_from_file_location(module_name, module_uri)
    mod_obj = mod_obj_from_spec(spec)
    if module_name not in sys.modules:
        spec.loader.exec_module(mod_obj)
        sys.modules[module_name] = mod_obj
    loading_so_plugin_name = None
    return mod_obj


def _is_valid_path(string):
    """ what if string is not absolute path? can it be recognized? """
    return '\\' in string or '/' in string


def _resolve_package_info(package_uri, pkg_depth):
    path_components = os.path.realpath(package_uri).split(os.sep)
    pkg_python_path = os.sep.join(path_components[: -pkg_depth])
    pkg_name = '.'.join(path_components[-pkg_depth:])
    return pkg_python_path, pkg_name


def _resolve_package_info_related_to_current_file(package_uri, package_depth):
    path_components = list(os.path.split(package_uri))
    fitframework_path_components = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))).split(
        os.sep)
    package_python_path = os.sep.join(fitframework_path_components + path_components[: -package_depth])
    package_name = '.'.join(path_components[-package_depth:])
    return package_python_path, package_name


def _update_pkg_python_paths(path):
    if path not in _pkg_python_paths_cache:
        sys.path.append(path)
        _pkg_python_paths_cache.add(path)


def _is_python_module(full_path):
    return os.path.isfile(full_path) and full_path.endswith(".py") and os.path.basename(full_path) != '__init__.py'


def _is_python_so_module(full_path):
    return os.path.isfile(full_path) and full_path.endswith(".so") and full_path.find(
        _CPYTHON_MODULE_VERSION_SYMBOL) != -1 and os.path.basename(full_path) != '__init__.py'


def _is_python_package(full_path):
    if not os.path.isdir(full_path):
        return False
    dirname = os.path.basename(full_path)
    return not dirname.startswith('_') and not dirname.startswith('.') \
        and dirname not in ('__pycache__', 'test')


if __name__ == '__main__':
    mod = import_package(r"D:\Developers\Codes\Python_Codes\PackageTest\plugin\p2", recursive=True)
