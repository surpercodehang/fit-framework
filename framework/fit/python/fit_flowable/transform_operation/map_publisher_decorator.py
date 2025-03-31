# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：对于数据进行转换的发布者的实现。
"""
from typing import Callable, Any

from fit_flowable.core.publisher import Publisher
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.operation.abstract_operation import AbstractOperation
from fit_flowable.pubilsher.abstract_publisher import AbstractPublisher


class MapPublisherDecorator(AbstractPublisher):
    """
    对于数据进行转换的发布者的实现。
    """

    class MapOperation(AbstractOperation):
        def __init__(self, mapper: Callable[[Any], Any], subscriber: Subscriber):
            super().__init__(subscriber)
            self._mapper: Callable[[Any], Any] = mapper
            self._subscriber: Subscriber = subscriber

        def do_consume(self, data) -> None:
            try:
                result = self._mapper(data)
                self.get_next_subscriber().consume(result)
            except Exception as cause:
                self.get_pre_subscription().cancel()
                self.get_next_subscriber().fail(cause)

    def __init__(self, decorated: Publisher, mapper: Callable[[Any], Any]):
        """
        构造将元素进行转换的发布者。

        :param decorated: 待进行元素转换的发布者。
        :param mapper: 指定的元素转换方式。
        """
        self._decorated = decorated
        self._mapper = mapper

    def do_subscribe(self, subscriber: Subscriber) -> None:
        self._decorated.subscribe(MapPublisherDecorator.MapOperation(self._mapper, subscriber))
