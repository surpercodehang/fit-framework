# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：分析 jsonpath 并对于特定字段进行替换的工具类。
"""
from inspect import ismethod
from typing import Callable


def get_user_fields(reference) -> list:
    return [each for each in dir(reference) if
            not each.startswith("__") and not each.endswith("__") and not ismethod(getattr(reference, each, None))]


def _split_json_path_to_units(json_path: str) -> list:
    """
    将完整的 jsonpath 切分为语法单元
    :param json_path: 待切分的 jsonpath，目前仅支持三种语法：
                        1.  .  : 对象或 dict 中的某个字段
                        2.  *  : 所有字段
                        3. [*] : 列表中的所有元素
    :return: 切分后的语法单元
    """
    if len(json_path) == 0:
        return []
    if json_path.startswith("$"):
        return ["$"] + _split_json_path_to_units(json_path[1:])
    if json_path.startswith("[*]"):
        return ["[*]"] + _split_json_path_to_units(json_path[3:])
    if json_path.startswith("."):
        if len(json_path) <= 1:
            raise Exception(f"illegal syntax of json path. [json_path={json_path}]")
        if json_path[1] == "*":
            return ["*"] + _split_json_path_to_units(json_path[2:])
        index = 1
        while index < len(json_path) and json_path[index] not in ["[", "."]:
            index += 1
        return [json_path[1:index]] + _split_json_path_to_units(json_path[index:])
    raise Exception(f"illegal syntax of json path. [json_path={json_path}]")


def _convert_field_by_json_path_units(reference, units: list, convert_method: Callable):
    if len(units) == 1:
        return _field_replace_operation(convert_method, reference, units)
    if units[0] == "$":
        _convert_field_by_json_path_units(reference, units[1:], convert_method)
    elif units[0] == "[*]":
        for each in reference:
            _convert_field_by_json_path_units(each, units[1:], convert_method)
    elif units[0] == "*":
        if isinstance(reference, dict):
            for key in reference.keys():
                _convert_field_by_json_path_units(reference[key], units[1:], convert_method)
        else:
            fields = get_user_fields(reference)
            for field in fields:
                _convert_field_by_json_path_units(getattr(reference, field), units[1:], convert_method)
    else:
        if isinstance(reference, dict):
            if units[0] not in reference:
                return reference
            new_reference = reference.get(units[0])
        else:
            if not hasattr(reference, units[0]):
                return reference
            new_reference = getattr(reference, units[0])
        _convert_field_by_json_path_units(new_reference, units[1:], convert_method)
    return reference


def _field_replace_operation(convert_method, reference, units):
    if units[0] == "$":
        return convert_method(reference)
    elif units[0] == "[*]":
        for index, content in enumerate(reference):
            reference[index] = convert_method(content)
    elif units[0] == "*":
        if isinstance(reference, dict):
            for key in reference.keys():
                reference[key] = convert_method(reference[key])
        else:
            fields = get_user_fields(reference)
            for field in fields:
                setattr(reference, field, convert_method(getattr(reference, field)))
    else:
        if isinstance(reference, dict):
            if units[0] not in reference:
                return reference
            reference[units[0]] = convert_method(reference[units[0]])
        else:
            if not hasattr(reference, units[0]):
                return reference
            setattr(reference, units[0], convert_method(getattr(reference, units[0])))
    return reference


def convert_field_by_json_path(reference, json_path: str, convert_method: Callable):
    """
    以指定的转换方式替换引用中所有匹配给定 jsonpath 的字段
    :param reference: 待替换各个字段的对象
    :param json_path: 按照 jsonpath 格式给出的匹配规则，目前仅支持三种语法：
                        1.  .  : 对象或 dict 中的某个字段
                        2.  *  : 所有字段
                        3. [*] : 列表中的所有元素
    :param convert_method: 给定的匹配字段的替换方式
    :return: 指定字段被替换后的对象，在不是对 reference 本身进行替换时将返回 reference 本身，否则将返回一个新构建的对象。
    """
    if not json_path.startswith("$"):
        json_path = f"${json_path}"
    json_path_units = _split_json_path_to_units(json_path)
    return _convert_field_by_json_path_units(reference, json_path_units, convert_method)
