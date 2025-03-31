# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：对于数据进行过滤的发布者的实现。
"""
from typing import Callable, Any

from fit_flowable.core.publisher import Publisher
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.operation.abstract_operation import AbstractOperation
from fit_flowable.pubilsher.abstract_publisher import AbstractPublisher


class FilterPublisherDecorator(AbstractPublisher):
    """
    对于数据进行过滤的发布者的实现。
    """

    class FilterOperation(AbstractOperation):
        def __init__(self, filter_: Callable[[Any], bool], subscriber: Subscriber):
            super().__init__(subscriber)
            self._filter: Callable[[Any], bool] = filter_
            self._subscriber: Subscriber = subscriber

        def do_consume(self, data) -> None:
            try:
                result = self._filter(data)
            except Exception as cause:
                self.get_pre_subscription().cancel()
                self.get_next_subscriber().fail(cause)
                return
            if result:
                self.get_next_subscriber().consume(data)
            else:
                self.get_pre_subscription().request(1)

    def __init__(self, decorated: Publisher, filter_: Callable[[Any], bool]):
        """
        构造将元素进行过滤的发布者。

        :param decorated: 待进行元素转换的发布者。
        :param filter_: 指定的元素转换方式。
        """
        self._decorated = decorated
        self._filter = filter_

    def do_subscribe(self, subscriber: Subscriber) -> None:
        self._decorated.subscribe(FilterPublisherDecorator.FilterOperation(self._filter, subscriber))
