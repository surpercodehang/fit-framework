# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：同时是订阅者和订阅关系的抽象父类。
"""
from abc import abstractmethod
from typing import Optional

from fitframework.core.exception.fit_exception import FlowableException
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.core.subscription import Subscription
from fit_flowable.util.concurrent import AtomicBool


class AbstractOperation(Subscriber, Subscription):
    """
    同时是订阅者和订阅关系的抽象父类。
    """

    def __init__(self, next_subscriber: Subscriber):
        """
        通过下游订阅者构造。

        :param next_subscriber: 下游的订阅者。
        """
        if next_subscriber is None:
            raise ValueError("the next subscriber cannot be null.")
        self._next_subscriber: Subscriber = next_subscriber
        self._pre_subscription: Optional[Subscription] = None
        self._completed: AtomicBool = AtomicBool()
        self._failed: AtomicBool = AtomicBool()
        self._cancelled: AtomicBool = AtomicBool()

    @abstractmethod
    def do_consume(self, data) -> None:
        """
        用于定义消费订阅的数据时的实际行为。

        :param data: 待消费的数据。
        """
        pass

    def do_fail(self, cause: Exception) -> None:
        """
        用于定义处理异常终结信号时的实际行为。

        :param cause: 待处理的异常。
        """
        self._next_subscriber.fail(cause)

    def do_complete(self) -> None:
        """
        用于定义数据发送完成时的实际行为。
        """
        self._next_subscriber.complete()

    def get_next_subscriber(self) -> Subscriber:
        """
        获取下游的订阅者。

        :return: 获取到的下游订阅者。
        """
        return self._next_subscriber

    def get_pre_subscription(self) -> Subscription:
        """
        获取上游订阅关系。

        :return: 获取到的订阅关系。
        """
        if self._pre_subscription is None:
            raise FlowableException("could not access property subscription before on subscribed.")
        return self._pre_subscription

    def get_completed(self) -> bool:
        """
        获取是否已经正常终结。

        :return: 如果已经正常终结则返回 True，否则，返回 False。
        """
        return self._completed.get()

    def get_failed(self) -> bool:
        """
        获取是否已经异常终结。

        :return: 如果已经异常终结则返回 True，否则，返回 False。
        """
        return self._failed.get()

    def get_cancelled(self) -> bool:
        """
        获取是否已经取消。

        :return: 如果已经取消则返回 True，否则，返回 False。
        """
        return self._cancelled.get()

    def on_subscribed(self, subscription: Subscription) -> None:
        if self._pre_subscription is not None:
            raise FlowableException("on_subscribed could not called twice.")
        self._pre_subscription = subscription
        self._next_subscriber.on_subscribed(self)

    def consume(self, data) -> None:
        if self._is_terminated():
            return
        self.do_consume(data)

    def fail(self, cause: Exception) -> None:
        if self._is_terminated() or not self._failed.compare_and_set(False, True):
            return
        self.do_fail(cause)

    def complete(self) -> None:
        if self._is_terminated() or not self._completed.compare_and_set(False, True):
            return
        self.do_complete()

    def request(self, count: int) -> None:
        if self._is_terminated():
            return
        if count <= 0:
            raise ValueError(f"The number of elements to request must be positive. [count={count}]")
        self._pre_subscription.request(count)

    def cancel(self) -> None:
        if self._cancelled.compare_and_set(False, True):
            return
        self._pre_subscription.cancel()

    def _is_terminated(self) -> bool:
        return self._cancelled.get() or self._failed.get() or self._completed.get()
