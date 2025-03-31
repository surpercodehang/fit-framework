# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：订阅者的抽象父类。
"""
from typing import Optional

from fitframework.core.exception.fit_exception import FlowableException
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.core.subscription import Subscription
from fit_flowable.util.concurrent import AtomicBool


class AbstractSubscriber(Subscriber):
    """
    表示订阅者的抽象父类。
    """

    def __init__(self):
        self._pre_subscription: Optional[Subscription] = None
        self._completed: AtomicBool = AtomicBool()
        self._failed: AtomicBool = AtomicBool()

    def do_on_subscribed(self) -> None:
        """
        订阅关系发生时的具体行为。
        """
        pass

    def do_consume(self, data) -> None:
        """
        消费数据时的具体行为。

        :param data: 待消费的数据。
        """
        pass

    def do_fail(self, cause: Exception) -> None:
        """
        消费异常终结信号时的具体行为。

        :param cause: 待消费的异常。
        """
        pass

    def do_complete(self) -> None:
        """
        消费正常终结信号时的具体行为。
        """
        pass

    def get_pre_subscription(self) -> Subscription:
        """
        获取上游的订阅关系。

        :return: 上游的订阅关系。
        """
        if self._pre_subscription is None:
            raise FlowableException("the previous subscription is not available before subscribed.")
        return self._pre_subscription

    def on_subscribed(self, subscription: Subscription) -> None:
        if self._pre_subscription is not None:
            raise FlowableException("the subscriber cannot be subscribed twice.")
        if subscription is None:
            raise ValueError("the subscription cannot be none.")
        self._pre_subscription: Subscription = subscription
        self.do_on_subscribed()

    def consume(self, data) -> None:
        if self._completed.get() or self._failed.get():
            return
        self.do_consume(data)

    def fail(self, cause: Exception) -> None:
        if self._failed.get() or not self._completed.compare_and_set(False, True):
            return
        self.do_fail(cause)

    def complete(self) -> None:
        if self._completed.get() or not self._failed.compare_and_set(False, True):
            return
        self.do_complete()
