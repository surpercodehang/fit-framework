# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：json 序列化器的测试。
"""
import unittest
from typing import List, Dict

import numpy

from fit_test.fitframework.core.network.json_serializer_test_entity import \
    _TestA, _TestB, _TestC, _TestD, _TestE, _TestF, _TestG, _TestH
from fitframework.core.network.json_serializer import _JsonSerializer
from fitframework.utils.json_serialize_utils import json_serialize

_A1 = _TestA('1', 2, '3')
_A2 = _TestA('x', 20, 'y')
_A3 = _TestA('x3', 20, 'y3')
_B1 = _TestB(_A1, [[_A2, _A1]])
_B2 = _TestB(_A3, [[_A1, _A2]])
_C = _TestC({'1': 2}, {'a1': _A1, 'a3': _A3}, {'2': 2, '1': 1})
# D[A]：
_D1 = _TestD(200, 'ok', _TestA('1', 2, '3'))
# D[int]：
_D2 = _TestD(1, '2', 3)
# D[list]：
_D3 = _TestD(10, '10', [20, '20'])
# E[B]：
_E1 = _TestE([_B1], {'b1': _B1, 'b2': _B2})
# E[A]：
_E2 = _TestE([_A1, _A3], {'A': _A2})
# E[D[A]]：
_E3 = _TestE([_D1], {'D': _D1})
# E[bool]：
_E4 = _TestE([True, True, False], {'A': True})
# F：
_F = _TestF(_D2, _E2, _E3)
# G[A]：
_G = _TestG(_D2, _E2, _E3, _A1, _E2, _D1, [_D1])
# H[B, A]：
_H1 = _TestH({'H': _A3, 'HH': _A2}, [_B2, _B1])
# H[D[list], E[bool]]：
_H2 = _TestH({'H2': _E4}, [_D3])

_TEST_IN_TYPES = [str, list, List[int], Dict[str, int]]
_TEST_ARGS = ['1', [1, 2], [3], {'4': 4}]

_TEST_IN_TYPES_2 = [_TestB, List[int], List[_TestA]]
_TEST_ARGS_2 = [_B1, [1, 10, 100], [_A1, _A2, _A3]]

_TEST_IN_TYPES_3 = [_TestC, Dict[int, str], Dict[float, _TestB]]
_TEST_ARGS_3 = [_C, {1: '10'}, {1.1: _B1, 2.2: _B2}]

_TEST_IN_TYPES_4 = [
    numpy.int64, bytes, numpy.uint32, Dict[bool, numpy.float32],
    Dict[numpy.float64, float]
]
_TEST_ARGS_4 = [1, b'2', 3, {True: 1.1, False: 0, None: -1}, {3.2: 3.1, 2.2: 2.1, 1: 1}]

_TEST_IN_TYPES_5 = [Dict[bool, str], Dict[str, bool], dict, str]
_TEST_ARGS_5 = [
    {False: 'true', True: 'false', None: 'null'},
    {'true': False, 'false': True, 'null': None}, None, 'null'
]

_TEST_IN_TYPES_6 = [List[List[str]], _TestD[_TestA], _TestE[_TestB]]
_TEST_ARGS_6 = [[['1', '2'], ['a', 'b']], _D1, _E1]

_TEST_IN_TYPES_7 = [_TestF, _TestG[_TestA], List[Dict[int, _TestG[_TestA]]]]
_TEST_ARGS_7 = [_F, _G, [{1: _G, 2: _G}]]

_TEST_IN_TYPES_8 = [_TestH[_TestB, _TestA], _TestH[_TestD[list], _TestE[bool]]]
_TEST_ARGS_8 = [_H1, _H2]

_TEST_IN_TYPES_9 = [_TestA]
_TEST_ARGS_9 = [{'a_arg_2': 2, 'a_arg_1': '1', 'a_arg_3': '3'}]


class JsonSerializerTest(unittest.TestCase):
    def setUp(self):
        self.converter = _JsonSerializer()

    def tearDown(self):
        del self.converter

    def test_to_request_json_with_primitive_data_success(self):
        args_bytes = json_serialize(_TEST_ARGS, to_bytes=True)
        args = self.converter.to_request_json(_TEST_IN_TYPES, args_bytes)
        self.assertSequenceEqual(args, _TEST_ARGS)

    def test_to_request_json_with_list_data_success(self):
        args_bytes_2 = json_serialize(_TEST_ARGS_2, to_bytes=True)
        args_2 = self.converter.to_request_json(_TEST_IN_TYPES_2, args_bytes_2)
        self.assertSequenceEqual(args_2, _TEST_ARGS_2)

    def test_to_request_json_with_dict_data_success(self):
        args_bytes_3 = json_serialize(_TEST_ARGS_3, to_bytes=True)
        args_3 = self.converter.to_request_json(_TEST_IN_TYPES_3, args_bytes_3)
        self.assertSequenceEqual(args_3, _TEST_ARGS_3)

    def test_to_request_json_wtih_special_data_success(self):
        args_bytes = json_serialize(_TEST_ARGS_4, to_bytes=True)
        args_4 = self.converter.to_request_json(_TEST_IN_TYPES_4, args_bytes)
        self.assertSequenceEqual(args_4, _TEST_ARGS_4)

    def test_to_request_json_with_special_data_success_2(self):
        args_bytes = json_serialize(_TEST_ARGS_5, to_bytes=True)
        args_5 = self.converter.to_request_json(_TEST_IN_TYPES_5, args_bytes)
        self.assertSequenceEqual(args_5, _TEST_ARGS_5)

    def test_to_request_json_with_generic_data_success(self):
        args_bytes = json_serialize(_TEST_ARGS_6, to_bytes=True)
        args_6 = self.converter.to_request_json(_TEST_IN_TYPES_6, args_bytes)
        self.assertSequenceEqual(args_6, _TEST_ARGS_6)

    def test_to_request_json_with_generic_data_success_2(self):
        args_bytes = json_serialize(_TEST_ARGS_7, to_bytes=True)
        args_7 = self.converter.to_request_json(_TEST_IN_TYPES_7, args_bytes)
        self.assertSequenceEqual(args_7, _TEST_ARGS_7)

    def test_to_request_json_with_multiple_generic_data_success(self):
        args_bytes = json_serialize(_TEST_ARGS_8, to_bytes=True)
        args_8 = self.converter.to_request_json(_TEST_IN_TYPES_8, args_bytes)
        self.assertSequenceEqual(args_8, _TEST_ARGS_8)

    def test_to_request_json_with_dict_key_mis_order(self):
        args_bytes = json_serialize(_TEST_ARGS_9, to_bytes=True)
        __TEST_ARGS_9 = [_TestA(**_TEST_ARGS_9[0])]
        args_9 = self.converter.to_request_json(_TEST_IN_TYPES_9, args_bytes)
        self.assertSequenceEqual(args_9, __TEST_ARGS_9)

    def test_to_return_value_json_with_dict_data_success(self):
        ret_bytes = json_serialize(_C, to_bytes=True)
        ret = self.converter.to_return_value_json(_TestC, ret_bytes)
        self.assertEqual(ret, _C)


if __name__ == '__main__':
    unittest.main()
