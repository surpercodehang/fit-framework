# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：用于测试 json 序列化器的数据结构定义。
"""
from typing import List, Dict, Generic, TypeVar

from numpy import int32

T = TypeVar('T')
K = TypeVar('K')
O = TypeVar('O')
P = TypeVar('P')
Q = TypeVar('Q')


class _TestA:
    """  【公共结构体】"""

    def __init__(self, a_arg_1: str, a_arg_2: int, a_arg_3: str):
        self.a_arg_1 = a_arg_1
        self.a_arg_2 = a_arg_2
        self.a_arg_3 = a_arg_3

    def __eq__(self, other):
        return isinstance(other, _TestA) and self.a_arg_1 == other.a_arg_1 and \
            self.a_arg_2 == other.a_arg_2 and self.a_arg_3 == other.a_arg_3


class _TestB:
    """
    b_arg_1：【公共结构体】
    b_arg_2：【公共结构体】数组
    """

    def __init__(self, b_arg_1: _TestA, b_arg_2: List[List[_TestA]]):
        self.b_arg_1 = b_arg_1
        self.b_arg_2 = b_arg_2

    def __eq__(self, other):
        return isinstance(other, _TestB) and self.b_arg_1 == other.b_arg_1 and self.b_arg_2 == other.b_arg_2


class _TestC:
    """  c_arg_2：【公共结构体】字典"""

    def __init__(self, c_arg_1: dict, c_arg_2: Dict[str, _TestA], c_arg_3: Dict[str, int]):
        self.c_arg_1 = c_arg_1
        self.c_arg_2 = c_arg_2
        self.c_arg_3 = c_arg_3

    def __eq__(self, other):
        return isinstance(other, _TestC) and self.c_arg_1 == other.c_arg_1 and self.c_arg_2 == other.c_arg_2 and \
            self.c_arg_3 == other.c_arg_3


class _TestD(Generic[T]):
    """  data：【泛型公共结构体】"""

    def __init__(self, code: int, message: str, data: T):
        self.code = code
        self.message = message
        self.data = data

    def __eq__(self, other):
        return isinstance(other, _TestD) and self.code == other.code and self.message == other.message \
            and self.data == other.data


class _TestE(Generic[K]):
    """  data2：【泛型公共结构体】数组"""

    def __init__(self, data1: List[K], data2: Dict[str, K]):
        self.data1 = data1
        self.data2 = data2

    def __eq__(self, other):
        return isinstance(other, _TestE) and self.data1 == other.data1 and self.data2 == other.data2


class _TestF:
    """
    f1~f3：在【公共结构体】声明里进行泛型实例化
    f1：泛型实例化使用【基本类型】
    f2：泛型实例化使用【公共结构体】
    f3：泛型实例化使用【泛型公共结构体】
    """

    def __init__(self, f1: _TestD[int32], f2: _TestE[_TestA],
                 f3: _TestE[_TestD[_TestA]]):
        self.f1 = f1
        self.f2 = f2
        self.f3 = f3

    def __eq__(self, other):
        return isinstance(other, _TestF) and self.__dict__ == other.__dict__


class _TestG(Generic[O]):
    """
    g1~g3：在【泛型公共结构体】声明里进行泛型实例化
    g4~6：泛型传递
    g7：泛型嵌套传递
    """

    def __init__(self, g1: _TestD[int32], g2: _TestE[_TestA],
                 g3: _TestE[_TestD[_TestA]], g4: O, g5: _TestE[O], g6: _TestD[O], g7: List[_TestD[O]]):
        self.g1 = g1
        self.g2 = g2
        self.g3 = g3
        self.g4 = g4
        self.g5 = g5
        self.g6 = g6
        self.g7 = g7

    def __eq__(self, other):
        return isinstance(other, _TestG) and self.__dict__ == other.__dict__


class _TestH(Generic[P, Q]):
    """
    h1, h2：多个泛型【公共结构体】
    """

    def __init__(self, h1: Dict[str, Q], h2: List[P]):
        self.h1 = h1
        self.h2 = h2

    def __eq__(self, other):
        return isinstance(other, _TestH) and self.__dict__ == other.__dict__

