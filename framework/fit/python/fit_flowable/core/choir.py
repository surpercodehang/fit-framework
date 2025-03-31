# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：处理多个值数据流的发布者。
"""
from typing import Callable, Any

from fit_flowable.choir.emitter_publisher import EmitterPublisher
from fit_flowable.choir.emitter_and_operation_publisher import EmitterAndOperationPublisher
from fit_flowable.core.emitter import Emitter
from fit_flowable.core.publisher import Publisher
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.filt_operation.filter_publisher_decorator import FilterPublisherDecorator
from fit_flowable.transform_operation.map_publisher_decorator import MapPublisherDecorator


class Choir(Publisher):
    """
    拥有 0 - n 个数据的数据流的发布者。
    """

    @classmethod
    def from_publisher(cls, publisher: Publisher):
        """
        将一个 Publisher 适配成 Choir 响应式流。

        :param publisher: 指定的待适配的发布者。
        :return: 适配后的 Choir 响应式流。
        """
        return ChoirPublisherAdapter(publisher)

    @classmethod
    def from_emitter(cls, emitter: Emitter):
        """
        通过一个发送器创建一个 Choir 响应式流。

        :param emitter: 指定的数据发送器。
        :return: 所创建的 Choir 响应式流。
        """
        return Choir.from_publisher(EmitterPublisher(emitter))

    @classmethod
    def from_emitter_and_operation(cls, emitter: Emitter, request_operation: Callable[[int], None] = lambda x: None,
                                   cancel_operation: Callable[[], None] = lambda: None):
        """
        通过一个发送器创建一个请求和取消操作可自定义的 Choir 响应式流。

        :param request_operation: 指定的请求操作。
        :param cancel_operation: 指定的取消操作。
        :param emitter: 指定的数据发送器。
        :return: 所创建的 Choir 响应式流。
        """
        return Choir.from_publisher(EmitterAndOperationPublisher(emitter, request_operation, cancel_operation))

    def subscribe(self, subscriber: Subscriber) -> None:
        pass

    def map(self, mapper: Callable[[Any], Any]):
        """
        将每个数据通过指定的方式进行转换后继续发送。

        :param mapper: 指定的转换方式。
        :return: 表示包含当前数据转换操作的新的响应式流。
        """
        return Choir.from_publisher(MapPublisherDecorator(self, mapper))

    def filter(self, filter_: Callable[[Any], bool]):
        """
        将每个数据通过按照规则过滤后继续发送。

        :param filter_: 指定的过滤方式。
        :return: 表示包含当前数据过滤操作的新的响应式流。
        """
        return Choir.from_publisher(FilterPublisherDecorator(self, filter_))


class ChoirPublisherAdapter(Choir):
    """
    将 Publisher 适配为 Choir 响应式流的适配器。
    """

    def __init__(self, publisher: Publisher):
        """
        :param publisher: 待适配的 Publisher。
        """
        self._publisher: Publisher = publisher

    def subscribe(self, subscriber: Subscriber) -> None:
        self._publisher.subscribe(subscriber)
