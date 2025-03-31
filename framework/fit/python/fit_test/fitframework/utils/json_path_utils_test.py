# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：测试用于分析 jsonpath 并对于特定字段进行替换的工具类。
"""
import unittest
from typing import List, Dict
from unittest import TestCase

from fitframework.utils.json_path_utlis import _split_json_path_to_units, convert_field_by_json_path


class Location:
    def __init__(self, country, province, districts):
        self.country: str = country
        self.province: str = province
        self.districts: List[str] = districts


class Education:
    def __init__(self, undergraduate, master):
        self.undergraduate: str = undergraduate
        self.master: str = master


class Person:
    def __init__(self, name: str, age: int, description: bytes, location: Location, education: Education,
                 transcript: Dict[str, str], extensions: Dict[str, object]):
        self.name = name
        self.age = age
        self.description = description
        self.location = location
        self.education = education
        self.transcript = transcript
        self.extensions = extensions


def convert_method(value):
    return value * 2


class TestSplitJsonPathToUnits(TestCase):

    def test_split_json_path_to_units(self):
        self.assertEqual(_split_json_path_to_units("$"), ['$'])
        self.assertEqual(_split_json_path_to_units("$.name"), ['$', 'name'])
        self.assertEqual(_split_json_path_to_units("$.extensions.ext_key2.ext_key3"),
                         ['$', 'extensions', 'ext_key2', 'ext_key3'])
        self.assertEqual(_split_json_path_to_units("$.education.*"), ['$', 'education', '*'])
        self.assertEqual(_split_json_path_to_units("$[*].location.districts[*]"),
                         ['$', '[*]', 'location', 'districts', '[*]'])
        self.assertEqual(_split_json_path_to_units("$.data.forecast[*].api"), ['$', 'data', 'forecast', '[*]', 'api'])

        with self.assertRaises(Exception):
            _split_json_path_to_units("$[1:2]")
        with self.assertRaises(Exception):
            _split_json_path_to_units("$..")
        with self.assertRaises(Exception):
            _split_json_path_to_units("$.")


class TestConvertFieldByJsonPath(TestCase):
    def setUp(self):
        self.alice = Person("Alice", 18, "alice".encode("utf-8"),
                            Location("China", "ZheJiang", ["xiaoshan", "binjiang"]),
                            Education("PKU", "THU"),
                            {"data_structure": "excellent", "network": "good"},
                            {"ext_key1": "value1", "ext_key2": {"ext_key3": "ext_value3"}})
        self.bob = Person("Bob", 20, "bob".encode("utf-8"),
                          Location("USA", "Oklahoma", []),
                          Education("UCLA", "UCB"), {}, {})
        self.persons = [self.alice, self.bob]

    def test_replace_string_variable_self(self):
        string_value = "abc"
        self.assertEqual(convert_field_by_json_path(string_value, "$", convert_method), "abc" * 2)

    def test_replace_bytes_variable_self(self):
        bytes_value = b"abc"
        self.assertEqual(convert_field_by_json_path(bytes_value, "$", convert_method), b"abc" * 2)

    def test_replace_string_field(self):
        result: Person = convert_field_by_json_path(self.alice, "$.name", convert_method)
        self.assertEqual(result.name, "Alice" * 2)

    def test_replace_bytes_field(self):
        result: Person = convert_field_by_json_path(self.alice, "$.description", convert_method)
        self.assertEqual(result.description, ("alice" * 2).encode("utf-8"))

    def test_replace_string_field_in_dict(self):
        result: Person = convert_field_by_json_path(self.alice, "$.extensions.ext_key2.ext_key3", convert_method)
        self.assertEqual(result.extensions["ext_key2"]["ext_key3"], "ext_value3" * 2)

    def test_replace_each_field(self):
        result: Person = convert_field_by_json_path(self.alice, "$.education.*", convert_method)
        self.assertEqual(result.education.undergraduate, "PKU" * 2)
        self.assertEqual(result.education.master, "THU" * 2)

    def test_replace_field_of_dict(self):
        result: Person = convert_field_by_json_path(self.alice, "$.transcript.*", convert_method)
        self.assertEqual(result.transcript["data_structure"], "excellent" * 2)
        self.assertEqual(result.transcript["network"], "good" * 2)

    def test_replace_each_element(self):
        result: Person = convert_field_by_json_path(self.alice, "$.location.districts[*]", convert_method)
        self.assertEqual(result.location.districts, ["xiaoshan" * 2, "binjiang" * 2])

    def test_replace_field_of_each_object(self):
        result: List[Person] = convert_field_by_json_path(self.persons, "$[*].name", convert_method)
        self.assertEqual(result[0].name, "Alice" * 2)
        self.assertEqual(result[1].name, "Bob" * 2)

    def test_replace_each_field_of_each_element(self):
        result: List[Person] = convert_field_by_json_path(self.persons, "$[*].location.districts[*]", convert_method)
        self.assertEqual(result[0].location.districts, ["xiaoshan" * 2, "binjiang" * 2])

    def test_replace_nonexistent_field_then_only_existent_field_replaced(self):
        result: List[Person] = convert_field_by_json_path(self.persons, "$[*].transcript.data_structure",
                                                          convert_method)
        self.assertEqual(result[0].transcript["data_structure"], "excellent" * 2)
        self.assertEqual(len(result[1].transcript), 0)


if __name__ == "__main__":
    unittest.main()
