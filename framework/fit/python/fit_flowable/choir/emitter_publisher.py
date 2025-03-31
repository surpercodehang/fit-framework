# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Publisher 的基于数据发送器的实现。
"""
from fit_flowable.core.emitter import Emitter
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.pubilsher.abstract_publisher import AbstractPublisher
from fit_flowable.subscription.abstract_subscription import AbstractSubscription
from fit_flowable.util.concurrent import AtomicInt64


class EmitterPublisher(AbstractPublisher):
    """
    表示 Publisher 的基于数据发送器的实现。
    """

    class EmitterSubscription(AbstractSubscription, Emitter.Observer):
        def __init__(self, subscriber: Subscriber):
            AbstractSubscription.__init__(self)
            self._subscriber = subscriber
            self._requested = AtomicInt64()

        def do_request(self, count) -> None:
            self._requested.add_and_get(count)

        def on_data_emitted(self, data) -> None:
            if self.get_cancelled():
                return
            if self._requested.decrement_and_get() >= 0:
                self._subscriber.consume(data)
            else:
                self._requested.increment_and_get()

        def on_completed(self) -> None:
            if self.get_cancelled():
                return
            self._subscriber.complete()

        def on_failed(self, cause: Exception) -> None:
            if self.get_cancelled():
                return
            self._subscriber.fail(cause)

    def __init__(self, emitter: Emitter):
        if emitter is None:
            raise ValueError("the emitter cannot be none.")
        self._emitter = emitter

    def do_subscribe(self, subscriber: Subscriber):
        subscription = EmitterPublisher.EmitterSubscription(subscriber)
        subscriber.on_subscribed(subscription)
        self._emitter.observe(subscription)
