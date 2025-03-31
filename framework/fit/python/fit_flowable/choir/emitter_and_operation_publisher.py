# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Publisher 的基于数据发送器且请求和取消操作可自定义的实现。
"""
from typing import Callable, Optional

from fit_flowable.core.emitter import Emitter
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.pubilsher.abstract_publisher import AbstractPublisher
from fit_flowable.subscription.abstract_subscription import AbstractSubscription


class EmitterAndOperationPublisher(AbstractPublisher):
    """
    表示 Publisher 的基于数据发送器且请求和取消操作可自定义的实现。
    """

    class EmitterAndOperationSubscription(AbstractSubscription, Emitter.Observer):
        def __init__(self, subscriber: Subscriber, request_operation: Callable[[int], None],
                     cancel_operation: Callable[[], None]):
            AbstractSubscription.__init__(self)
            self._subscriber: Subscriber = subscriber
            self._request_operation: Optional[Callable[[int], None]] = request_operation
            self._cancel_operation: Optional[Callable[[], None]] = cancel_operation

        def do_request(self, count) -> None:
            self._request_operation(count)

        def do_cancel(self) -> None:
            self._cancel_operation()

        def on_data_emitted(self, data) -> None:
            if self.get_cancelled():
                return
            self._subscriber.consume(data)

        def on_completed(self) -> None:
            if self.get_cancelled():
                return
            self._subscriber.complete()

        def on_failed(self, cause: Exception) -> None:
            if self.get_cancelled():
                return
            self._subscriber.fail(cause)

    def __init__(self, emitter: Emitter, request_operation: Callable[[int], None],
                 cancel_operation: Callable[[], None]):
        if emitter is None:
            raise ValueError("the emitter cannot be none.")
        self._emitter = emitter
        self._request_operation = request_operation
        self._cancel_operation = cancel_operation

    def do_subscribe(self, subscriber: Subscriber):
        subscription = EmitterAndOperationPublisher.EmitterAndOperationSubscription(subscriber, self._request_operation,
                                                                                    self._cancel_operation)
        subscriber.on_subscribed(subscription)
        self._emitter.observe(subscription)
