# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：cbor 编解码逻辑，相关实现参考 FIT for Java。
"""
import base64
import collections
import collections.abc as abc
import io
import struct
from enum import Enum

Tagging = collections.namedtuple("Tagging", ["tag", "object"])


class Undefined(object):
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not isinstance(cls._instance, cls):
            cls._instance = object.__new__(cls)
        return cls._instance

    def __str__(self):
        return "Undefined"

    def __repr__(self):
        return "Undefined"


class Major(Enum):
    """ 通过枚举定义 CBOR 高 3 位的主类型值 """
    UNSIGNED_INTEGER = 0
    NEGATIVE_INTEGER = 1
    BYTE_STRING = 2
    TEXT_STRING = 3
    ARRAY = 4
    MAP = 5
    TAGGING = 6
    BOOL_NULL = 7
    CHUNK_SIZE = 8


class EncoderError(Exception):
    pass


class Encoder(object):
    def __init__(self, output):
        self.output = output

    def encode(self, obj, default=None):
        if isinstance(obj, list):
            self.cbor_encode_array(obj, default)
        elif isinstance(obj, dict):
            self.cbor_encode_map(obj, default)
        elif isinstance(obj, Tagging):
            self.cbor_encode_tagging(obj, default)
        elif isinstance(obj, bytes):
            self.cbor_encode_byte(obj)
        elif isinstance(obj, str):
            self.cbor_encode_text(obj)
        elif isinstance(obj, float):
            self.cbor_encode_float(obj)
        elif isinstance(obj, bool):
            self.cbor_encode_boolean(obj)
        elif isinstance(obj, int):
            self.cbor_encode_integer(obj)
        elif obj is Undefined:
            self.cbor_encode_undefined()
        elif obj is None:
            self.cbor_encode_null()
        elif isinstance(obj, abc.Iterable):
            self.encode_infinite_list(obj)
        elif default is not None:
            return self.encode(default(obj), default)
        else:
            raise EncoderError("Object of type {} is not serializable".format(type(obj)))

    def cbor_encode_integer(self, integer):
        """ 定义整数（包含无符号整数与负数）的编码逻辑 """
        if integer < -18446744073709551616 or integer > 18446744073709551615:
            EncoderError(
                f"cannot encode int value more than 18446744073709551615 or less than -18446744073709551616. "
                f"[value={integer}]")
        if integer >= Major.UNSIGNED_INTEGER.value:
            major = Major.UNSIGNED_INTEGER.value
        else:
            major = Major.NEGATIVE_INTEGER.value
        positive_value = integer if integer >= 0 else -integer - Major.NEGATIVE_INTEGER.value
        return self._write(_encode_major_and_length_to_bytes(major, positive_value))

    def cbor_encode_byte(self, byte_value):
        """ 定义字节数组的编码逻辑 """
        self._write(_encode_major_and_length_to_bytes(Major.BYTE_STRING.value, len(byte_value)))
        self._write(byte_value)

    def cbor_encode_text(self, text_value):
        """ 定义字符串数组的编码逻辑 """
        text_ = text_value.encode("utf-8")
        self._write(_encode_major_and_length_to_bytes(Major.TEXT_STRING.value, len(text_)))
        self._write(text_)

    def cbor_encode_array(self, array, default=None):
        """ 定义数组的编码逻辑 """
        self._write(_encode_major_and_length_to_bytes(Major.ARRAY.value, len(array)))
        for elem in array:
            self.encode(elem, default)

    def cbor_encode_map(self, map_value, default=None):
        """ 定义键值对的编码逻辑 """
        self._write(_encode_major_and_length_to_bytes(Major.MAP.value, len(map_value)))
        for key, value in map_value.items():
            self.encode(key, default)
            self.encode(value, default)

    def cbor_encode_float(self, float_value):
        """ 定义扩展类型：浮点数的编码逻辑 """
        self._write(b"\xfb")
        self._write(struct.pack(">d", float_value))

    def cbor_encode_tagging(self, tagging, default=None):
        try:
            self._write(_encode_major_and_length_to_bytes(Major.TAGGING.value, tagging[0]))
        except TypeError:
            raise EncoderError("Encoding tag larger than 18446744073709551615 is not supported")
        self.encode(tagging.object, default)

    def cbor_encode_boolean(self, boolean):
        if boolean:
            self._write(_encode_major_and_length_to_bytes(Major.BOOL_NULL.value, 21))
        else:
            self._write(_encode_major_and_length_to_bytes(Major.BOOL_NULL.value, 20))

    def cbor_encode_null(self):
        self._write(_encode_major_and_length_to_bytes(Major.BOOL_NULL.value, 22))

    def cbor_encode_undefined(self):
        self._write(b"\xf7")

    def encode_infinite_list(self, iterable):
        self._write(b"\x9f")
        for elem in iterable:
            self.encode(elem)
        self._write(b"\xff")

    def _write(self, val):
        self.output.write(val)


def encode(obj, io_stream, default=None):
    """
    对外暴露的编码方法，接受两个参数：
        1. obj：待编码的 Python 对象。
        2. io：表示输出流。
    """
    Encoder(io_stream).encode(obj, default)


def encodes(obj, default=None):
    """
    对外暴露的编码方法，接受一个参数，即待编码的 Python 对象。
    """
    buf = io.BytesIO()
    encode(obj, buf, default)
    return buf.getvalue()


def _encode_major_and_length_to_bytes(major: int, length: int):
    """
    编码字段的类型信息以及该字段的长度
    :param major: 该字段的类型标识
    :param length: 该字段的长度，如果字段的类型是 int 那么该字段的长度即该字段的值
    """
    if length < 24:
        return _convert_int_to_bytes((major << 5) | length, 1)
    elif length < 256:
        return _convert_int_to_bytes((major << 5) | 24, 1) + _convert_int_to_bytes(length, 1)
    elif length < 65536:
        return _convert_int_to_bytes((major << 5) | 25, 1) + _convert_int_to_bytes(length, 2)
    elif length < 4294967296:
        return _convert_int_to_bytes((major << 5) | 26, 1) + _convert_int_to_bytes(length, 4)
    elif length < 18446744073709551616:
        return _convert_int_to_bytes((major << 5) | 27, 1) + _convert_int_to_bytes(length, 8)
    else:
        return None


def _convert_int_to_bytes(val: int, length: int):
    return val.to_bytes(length, "big")


class InvalidCborError(Exception):
    pass


class _Break(InvalidCborError):
    def __init__(self):
        super().__init__("Invalid BREAK code occurred")


class Decoder(object):
    def __init__(self, input_value):
        self._jump_table = [
            lambda *args: self.decode_integer(*args, sign=False),
            lambda *args: self.decode_integer(*args, sign=True),
            self.cbor_decode_byte,
            self.cbor_decode_text,
            self.cbor_decode_array,
            self.cbor_decode_map,
            self.cbor_decode_tagging,
            self.cbor_decode_other
        ]
        self.input = input_value

    def decode(self):
        major_type, minor_type = self._decode_ibyte()
        try:
            decoder = self._jump_table[major_type]
        except KeyError as cause:
            raise InvalidCborError(f"Invalid major type {major_type}") from cause
        return decoder(major_type, minor_type)

    def decode_integer(self, major_type, minor_type, sign=False):
        """ 对整数进行解码 """
        res = self._decode_length(major_type, minor_type)
        if sign:
            return -1 - res
        else:
            return res

    def cbor_decode_byte(self, major_type, minor_type):
        """ 对字节数组进行解码 """
        length = self._decode_length(major_type, minor_type)
        if length is None:
            res = bytearray()
            for chunk in iter(lambda: self.cbor_decode_byte(*self._decode_ibyte()), b''):
                res += chunk
            return bytes(res)
        else:
            return self._read(length)

    def cbor_decode_text(self, major_type, minor_type):
        """ 对字符串进行解码 """
        length = self._decode_length(major_type, minor_type)
        if length is None:
            res = bytearray()
            for chunk in iter(lambda: self.cbor_decode_byte(*self._decode_ibyte()), b''):
                res += chunk
            return res.decode("utf-8")
        else:
            return self._read(length).decode("utf-8")

    def cbor_decode_array(self, major_type, minor_type):
        """ 对数组进行解码 """
        length = self._decode_length(major_type, minor_type)
        if length is None:
            res = [elem for elem in iter(lambda: self.decode(), _Break)]
            return res
        else:
            res = [self.decode() for _ in range(length)]
        return res

    def cbor_decode_map(self, major_type, minor_type):
        """ 对键值对进行解码 """
        length = self._decode_length(major_type, minor_type)
        if length is None:
            res = {}
            try:
                while True:
                    key = self.decode()
                    value = self.decode()
                    res[key] = value
            except _Break:
                pass
            return res
        else:
            res = {}
            for _ in range(length):
                key, value = self.decode(), self.decode()
                res[key] = value
            return res

    def cbor_decode_tagging(self, major_type, minor_type):
        length = self._decode_length(major_type, minor_type)
        return Tagging(length, self.decode())

    def decode_half_float(self):
        half = int.from_bytes(self._read(2), "big")
        sign = half >> 15
        exponent = (half >> 10) & 0x1F
        significand = half & 0x3FF
        if exponent != 0b11111:
            significand |= 0x400
            value = (-1) ** sign * significand * 2 ** (exponent - 15)
        else:
            value = float("inf" if significand == 0 else "nan")
        return value

    def decode_single_float(self):
        return struct.unpack(">f", self._read(4))[0]

    def decode_double_float(self):
        return struct.unpack(">d", self._read(8))[0]

    def cbor_decode_other(self, _, minor_type):
        if minor_type == 20:
            return False
        elif minor_type == 21:
            return True
        elif minor_type == 22:
            return None
        elif minor_type == 23:
            return Undefined
        elif minor_type == 25:
            return self.decode_half_float()
        elif minor_type == 26:
            return self.decode_single_float()
        elif minor_type == 27:
            return self.decode_double_float()
        elif minor_type == 31:
            raise _Break()

    def _decode_ibyte(self):
        byte = self._read(1)[0]
        return divmod(byte, 32)

    def _decode_length(self, _, minor_type):
        length_dict = {
            31: None,
            24: lambda: int.from_bytes(self._read(1), "big"),
            25: lambda: int.from_bytes(self._read(2), "big"),
            26: lambda: int.from_bytes(self._read(4), "big"),
            27: lambda: int.from_bytes(self._read(8), "big")
        }

        if minor_type < 24:
            return minor_type
        elif minor_type in length_dict:
            return length_dict[minor_type]()
        else:
            raise InvalidCborError("Invalid additional information {}".format(minor_type))

    def _read(self, n):
        m = self.input.read(n)
        if len(m) != n:
            raise InvalidCborError("Expected {} bytes, got {} bytes instead".format(n, len(m)))
        return m


def decode(fp):
    """ CBOR 对外暴露的解码方法，输出参数为 BytesIO 对象 """
    return Decoder(fp).decode()


def decodes(data):
    """ CBOR 对外暴露的解码方法，输入参数为待解码的 Python 数据 """
    return Decoder(io.BytesIO(data)).decode()


def encode_hex(data):
    """ 将二进制数据进行十六进制编码 """
    return base64.b16encode(encodes(data)).decode("utf-8")


def decode_hex(data):
    """ 将十六进制的编码的字符串进行解码为二进制数据 """
    return decodes(base64.b16decode(data))
