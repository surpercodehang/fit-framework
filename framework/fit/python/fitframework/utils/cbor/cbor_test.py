# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：cbor 测试
"""
import io
import unittest
from unittest import TestCase

from cbor import encode, decodes, encodes, decode, Tagging, Undefined, decode_hex, encode_hex


class TestCBOR(TestCase):
    def test_encode_and_decode_function(self):
        data = {"name": "John", "age": 30, "city": "New York"}
        target_byte = b'\xa3dnamedJohncage\x18\x1edcityhNew York'
        output = io.BytesIO()
        encode(data, output)
        encoded_data = output.getvalue()
        assert encoded_data == target_byte
        decodes_data = decodes(encoded_data)
        assert decodes_data == data
        decode_data = decode(io.BytesIO(target_byte))
        assert decode_data == data

    # Tagging 用于处理CBOR数据中的标签
    def test_tagging(self):
        tagged_data = Tagging(0, "2022-01-01T00:00:00Z")
        target_byte = b'\xc0t2022-01-01T00:00:00Z'
        encoded_data = encodes(tagged_data)
        assert encoded_data == target_byte
        decoded_data = decodes(encoded_data)
        assert tagged_data == decoded_data

    # Undefined 用于未定义值
    def test_undefined_test(self):
        undefined_data = Undefined
        target_byte = b'\xf7'
        encoded_data = encodes(undefined_data)
        assert encoded_data == target_byte
        decoded_data = decodes(encoded_data)
        assert decoded_data == undefined_data

    def test_encode_hex(self):
        data = {"name": "Alice", "age": 30}
        target_byte = b'\xa2dnameeAlicecage\x18\x1e'
        target_hex = "52A2646E616D6565416C69636563616765181E"
        encoded_data = encodes(data)
        assert encoded_data == target_byte
        hex_str = encode_hex(encoded_data)
        assert hex_str == target_hex

    def test_decode_hex(self):
        hex_str = "52A2646E616D6565416C69636563616765181E"
        target_data = {"name": "Alice", "age": 30}
        target_byte = b'\xa2dnameeAlicecage\x18\x1e'
        encoded_data = decode_hex(hex_str)
        assert target_byte == encoded_data
        data = decodes(encoded_data)
        assert target_data == data

    def test_different_num_type(self):
        # 测试正整数
        data = b'\x17'  # 23
        assert decodes(data) == 23
        data = 42
        encoded_data = encodes(data)
        assert encoded_data == b'\x18\x2a'

        # 大整数
        data = 1234324232342342
        encoded_data = encodes(data)
        assert encoded_data == b'\x1b\x00\x04b\x9c\x81h\x0bF'

        # 测试负整数
        data = b'\x20'  # -1
        assert decodes(data) == -1
        data = -43
        encoded_data = encodes(data)
        assert encoded_data == b'\x38\x2a'

        # 单精度浮点数
        data = 3.14
        encoded_data = encodes(data)
        encoded_hex = encode_hex(encoded_data)
        assert encoded_hex == "49FB40091EB851EB851F"

        # 双精度浮点数
        data = 3.141592653589793
        encoded_data = encodes(data)
        encoded_hex = encode_hex(encoded_data)
        assert encoded_hex == "49FB400921FB54442D18"

        # 测试整数长度
        data = b'\x1b\x00\x00\x00\x01\x00\x00\x00\x00'  # 2^32
        assert decodes(data) == 4294967296

    def test_different_str_type(self):
        # 测试空字节串
        data = b'\x40'
        assert decodes(data) == b''

        # 测试非空字节串
        data = b'\x44\x01\x02\x03\x04'
        assert decodes(data) == b'\x01\x02\x03\x04'

        # 简单字符串
        data = 'hello'
        encoded_data = encodes(data)
        assert encoded_data == b'\x65hello'

        # UTF-8字符串
        data = '你好'
        encoded_data = encodes(data)
        encoded_hex = encode_hex(encoded_data)
        assert encoded_data == b'f\xe4\xbd\xa0\xe5\xa5\xbd'
        assert encoded_hex == "4766E4BDA0E5A5BD"
        assert data == decodes(decode_hex(encoded_hex))

        # 测试空文本串
        data = b'\x60'
        assert decodes(data) == ''

    def test_different_arr_type(self):
        # 测试空数组
        data = b'\x80'
        assert decodes(data) == []

        # 测试非空数组
        data = b'\x83\x01\x02\x03'
        assert decodes(data) == [1, 2, 3]

        # 测试空映射
        data = b'\xa0'
        assert decodes(data) == {}

        # 测试非空映射
        data = b'\xa2\x01\x02\x03\x04'
        assert decodes(data) == {1: 2, 3: 4}

    def test_different_data_type(self):
        # 测试简单类型 0
        data = b'\xf4'
        assert not decodes(data)

        # 测试简单类型 1
        data = b'\xf5'
        assert decodes(data)

        # 无穷大
        data = float('inf')
        encoded_data = encodes(data)
        assert encoded_data == b'\xfb\x7f\xf0\x00\x00\x00\x00\x00\x00'

    def test_json_aggregate_data(self):
        data = {
            "name": "John",
            "age": 30,
            "city": "New York",
            "hobbies": ["reading", "traveling", "cooking"],
            "education": {
                "degree": "Bachelor's",
                "major": "Computer Science",
                "university": "Harvard"
            }
        }
        target_byte = (b"\xa5dnamedJohncage\x18\x1edcityhNew Yorkghobbies\x83greadingitravelinggcookingieducation\xa3"
                       b"fdegreejBachelor'semajorpComputer SciencejuniversitygHarvard")
        encode_data = encodes(data)
        assert target_byte == encode_data
        decode_data = decodes(encode_data)
        assert decode_data == data

    def test_json_aggregate_data2(self):
        data = {
            "name": "John",
            "age": 40,
            "floatValue": "0.15625",
            "city": "New York",
            "hobbies": ["reading", "traveling", "cooking", "hiking"],
            "education": {
                "degree": "Bachelor's",
                "major": "Computer Science",
                "university": "MIT"
            }
        }
        target_byte = (b"\xa6dnamedJohncage\x18(jfloatValueg0.15625dcityhNew Yorkghobbies\x84greadingitravelinggcooking"
                       b"fhikingieducation\xa3fdegreejBachelor'semajorpComputer SciencejuniversitycMIT")
        encode_data = encodes(data)
        assert encode_data == target_byte
        decode_data = decodes(encode_data)
        assert decode_data == data

    def test_object_encode(self):
        def _degrade_dumps(obj):
            return obj.__dict__

        person = Person("John", 30, "New York")
        target_data = b'\xa3dnamedJohncage\x18\x1edcityhNew York'
        assert encodes(person, _degrade_dumps) == target_data


class Person:
    def __init__(self, name: str, age: int, city: str):
        self.name = name
        self.age = age
        self.city = city


if __name__ == '__main__':
    unittest.main()
