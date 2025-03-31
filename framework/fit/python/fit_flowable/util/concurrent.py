# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：并发操作所需的工具类。
"""
import threading


class AtomicBool:
    """
    表示提供支持原子操作的布尔值。
    """

    def __init__(self, value=False):
        """
        构造原子布尔值对象。

        :param value: 给定的初始值，缺省值为 False。
        """
        self._lock = threading.Lock()
        self._value = value

    def get(self):
        """
        获取当前值。

        :return: 当前布尔值。
        """
        with self._lock:
            return self._value

    def set(self, value):
        """
        设定当前值。

        :param value: 目标设定值。
        """
        with self._lock:
            self._value = value

    def compare_and_set(self, expect, update):
        """
        比较当前值是否等于期望值，并在相等时更新当前值。

        :param expect: 期望值。
        :param update: 待更新为的值。
        :return: 是否更新成功。
        """
        with self._lock:
            if self._value == expect:
                self._value = update
                return True
            else:
                return False


class AtomicInt64:
    """
    表示支持原子操作的 64 位整数。
    """

    def __init__(self, value=0):
        """
        构造AtomicInt64 对象。

        :param value: 初始值，默认为0
        """
        self._lock = threading.Lock()
        self._value = value

    def get(self):
        """
        获取当前存储的整数值。

        :return: 当前存储的整数值。
        """
        with self._lock:
            return self._value

    def set(self, value):
        """
        设置存储的整数值为指定值。

        :param value: 新的整数值。
        :return:
        """
        with self._lock:
            self._value = value

    def compare_and_set(self, expect, update):
        """
        比较存储的整数值与期望值，如果相等，则更新为新值。

        :param expect: 期望的整数值。
        :param update: 新的整数值。
        :return: 操作是否成功。
        """
        with self._lock:
            if self._value == expect:
                self._value = update
                return True
            else:
                return False

    def increment_and_get(self):
        """
        原子增加1并返回新的整数值。

        :return: 增加后的整数值。
        """
        with self._lock:
            self._value += 1
            return self._value

    def decrement_and_get(self):
        """
        原子减少1并返回新的整数值。

        :return: 减少后的整数值。
        """
        with self._lock:
            self._value -= 1
            return self._value

    def add_and_get(self, delta):
        """
        原子增加指定的增量并返回新的整数值。

        :param delta: 增量值。
        :return: 增加后的整数值。
        """
        with self._lock:
            self._value += delta
            return self._value

    def get_and_add(self, delta):
        """
        返回当前存储的整数值，并将其原子增加指定的增量。

        :param delta: 增量值。
        :return: 当前存储的整数值。
        """
        with self._lock:
            old_value = self._value
            self._value += delta
            return old_value

    def get_and_set(self, value):
        """
        获取当前存储的整数值，并设置为新的指定值。

        :param value: 新的整数值。
        :return: 原来存储的整数值。
        """
        with self._lock:
            old_value = self._value
            self._value = value
            return old_value
