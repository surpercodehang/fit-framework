# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：订阅关系的抽象父类。
"""
from abc import abstractmethod

from fit_flowable.core.subscription import Subscription
from fit_flowable.util.concurrent import AtomicBool


class AbstractSubscription(Subscription):
    """
    订阅关系的抽象父类。
    """

    def __init__(self):
        self._cancelled: AtomicBool = AtomicBool()

    def request(self, count: int) -> None:
        if count <= 0:
            raise ValueError(f"The number of elements to request must be positive. [count={count}]")
        if self._cancelled.get():
            return
        self.do_request(count)

    @abstractmethod
    def do_request(self, count) -> None:
        """
        请求指定数量的数据时的具体行为，仅当前请求没有被取消时有效。

        :param count: 请求的数据的数量
        """
        pass

    def cancel(self) -> None:
        if not self._cancelled.compare_and_set(False, True):
            return
        self.do_cancel()

    def do_cancel(self) -> None:
        """
        取消当前的订阅关系的具体行为，仅当前请求没有被取消时有效，默认为空。
        """
        pass

    def get_cancelled(self) -> bool:
        return self._cancelled.get()
