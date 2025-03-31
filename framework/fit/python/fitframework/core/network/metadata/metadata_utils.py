# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：用于metadata和response_metadata的工具包
1.TLV扩展字段类
    - MetaData元数据中的TlvTag字段类
    - 用于远程传输场景中，某些自定义字段需要放在Metadata元数据里的场景。
2.服务于Metadata和ResponseMetadata的一般意义工具类。总共两层：
    - 第一层为一般意义的处理比特粒度的BitStream类
    - 第二层为基于Stream类实现的IntEncoder和IntDecoder类，用于整形数据的序列化和反序列化
    - Metadata和ResponseMetadata类即基于IntEncoder和IntDecoder类进行实现，用于元数据的序列化和反序列化
"""
import math
from itertools import islice
from typing import Dict, Iterator, Optional, Any

from fitframework.const import DEFAULT_CODECS


class TagLengthValuesUtil:
    """ 表示进程唯一标识的标签值 """
    WORKER_ID = 0x00

    """ 表示进程实例唯一标识的标签值 """
    INSTANCE_ID = 0x01

    """ 表示 http 异步任务唯一标识的标签值 """
    TASK_ID = 0x40

    @classmethod
    def serialize(cls, tlv_data: Dict[int, Any]) -> bytes:
        tlv_data_bytes = b''
        for tag, value in tlv_data.items():
            serialized_val = cls._serialize_value(value)
            tlv_data_bytes += cls._serialize_tlv_datum(tag, serialized_val)
        return tlv_data_bytes

    @classmethod
    def deserialize(cls, bytes_: bytes) -> Dict[int, Any]:
        tlv_data_bytes_iter = iter(bytes_)
        tlv_data = {}
        while True:
            tlv_datum = cls._deserialize_tlv_datum(tlv_data_bytes_iter)
            if not tlv_datum:
                break
            tlv_data.update(tlv_datum)
        return tlv_data

    @classmethod
    def _serialize_value(cls, value: Any) -> bytes:
        # 目前TLV字段中value均为字符串
        return bytes(value, DEFAULT_CODECS) if value else b''

    @classmethod
    def _serialize_tlv_datum(cls, tag: int, value: bytes) -> bytes:
        return IntEncoder.to_varying_bytes(tag) + IntEncoder.to_varying_bytes(len(value)) + value

    @classmethod
    def _deserialize_tlv_datum(cls, bytes_iter: Iterator) -> Optional[Dict[int, Any]]:
        tag = IntDecoder.from_varying_bytes(bytes_iter)
        if tag is None:
            return None
        value_len = IntDecoder.from_varying_bytes(bytes_iter)
        value = cls._deserialize_value(bytes(islice(bytes_iter, value_len)))
        return {tag: value}

    @classmethod
    def _deserialize_value(cls, val_bytes):
        # 目前TLV字段中value均为字符串
        return val_bytes.decode(DEFAULT_CODECS) if val_bytes else None


class IntEncoder:
    @classmethod
    def to_bytes(cls, int_, len_, sign) -> bytes:
        """
        Args:
            int_:  待编码的整形数据
            len_: 期望编码的字节长度
            sign: 表明当前整形为有符号或无符号，其值为`signed`或`unsigned`.
        Returns:
        """
        signed = True if sign == 'signed' else False
        return int_.to_bytes(len_, byteorder='big', signed=signed)

    @classmethod
    def to_varying_bytes(cls, int_) -> bytes:
        """
        7为每个字节有效存储数据的比特数
        剩下的1位用于标志当前有效数据是否还有下一个字节

        Args:
            int_: 待序列化的整形数据
        Returns: 序列化后的字节数据
        """
        origin_bits_iter = reversed(BitStream(int_))
        result_bits = BitStream()

        has_more_bytes_flag = 0
        while True:
            real_bits = BitStream(islice(origin_bits_iter, 7))
            if not real_bits:
                break
            if len(real_bits) < 7:
                real_bits += BitStream.from_zeros(7 - len(real_bits))

            result_bits += real_bits + BitStream([has_more_bytes_flag])
            has_more_bytes_flag = 1

        result_bits.reverse()
        return result_bits.to_bytes()


class IntDecoder:
    @classmethod
    def from_bytes(cls, bytes_, sign='unsigned') -> int:
        signed = True if sign == 'signed' else False
        return int.from_bytes(bytes_, byteorder='big', signed=signed)

    @classmethod
    def from_varying_bytes(cls, bytes_iter) -> Optional[int]:
        result_bits = BitStream()
        while True:
            try:
                # bytes_iter每次next出来的是一个int
                origin_bits = BitStream(next(bytes_iter))
                if len(origin_bits) < 8:
                    origin_bits = BitStream.from_zeros(8 - len(origin_bits)) + origin_bits
                result_bits += origin_bits[1: 8]
                has_more_bytes_flag = origin_bits[0]
                if not has_more_bytes_flag:
                    return result_bits.to_integer()
            except StopIteration:
                # 整个tlv_data字节流读取完毕
                return None


class BitStream(list):
    """ 比特粒度的数组，每一个元素为0或1，代表一个二进制位，默认认定其为无符号的数据 """

    def __init__(self, value=()):
        """
        支持3种构造方式
        1.通过整形构造，出来的结果为对应该整形数据的二进制流
        2.通过包含0或1（int类型）的列表构造，出来的结果为直接对应这些0或1的二进制流
        3.指定长度构造 (见~:func:`BitStream.from_zeros`)；结果为指定长度的全为0的二进制流
        暂未考虑支持通过字节流构造
        """
        if isinstance(value, int):
            value = BitStream._to_binary_list(value)
        super().__init__(value)

    def __add__(self, other):
        return BitStream(super().__add__(other))

    @classmethod
    def from_zeros(cls, size):
        return cls(value=[0] * size)

    @classmethod
    def _to_binary_list(cls, int_):
        return cls._to_binary_list(int_ // 2) + [int_ % 2] if int_ > 1 else [int_]

    def to_integer(self):
        return sum(self[-(i + 1)] * 2 ** i for i in range(len(self)))

    def to_bytes(self):
        bytes_len_needed = math.ceil(len(self) / 8)
        return self.to_integer().to_bytes(bytes_len_needed, 'big', signed=False)


if __name__ == '__main__':
    bs = BitStream([1, 23]) + BitStream([4, 5])
