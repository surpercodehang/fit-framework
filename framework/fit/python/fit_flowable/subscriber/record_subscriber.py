# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：测试用工具类。
"""
from sys import maxsize
from typing import List, Optional

from fitframework.core.exception.fit_exception import FlowableException
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.core.subscription import Subscription


class RecordSubscriber(Subscriber):
    """
    测试用工具类，可以记录 Subscriber 各个方法被调用时的信息，并且能够灵活的进行元素请求和订阅取消。
    """

    class Record:
        """
        表示单条记录。
        """

        def __init__(self, data, index: int):
            self._data = data
            self._index = index

        def get_index(self) -> int:
            """
            获取当前记录的序号。

            :return: 所获取的记录序号。
            """
            return self._index

        def get_data(self):
            """
            获取当前记录的内容。

            :return: 所获取的记录内容。
            """
            return self._data

    def __init__(self, on_subscribed_request: int = maxsize, consume_request: int = 0, cancel_when: int = 0):
        """
        创建测试用工具类实例。

        :param on_subscribed_request: 表示发生订阅时请求元素数量。
        :param consume_request: 表示每次有元素被消费时请求数量。
        :param cancel_when: 表示在元素数量达到指定值时取消订阅的数量值。
        """

        self._subscription: Optional[Subscription] = None
        self._on_subscribed_request: int = on_subscribed_request
        self._consume_request: int = consume_request
        self._cancel_when: int = cancel_when
        self._on_subscribed_records: List[RecordSubscriber.Record] = []
        self._consume_records: List[RecordSubscriber.Record] = []
        self._fail_records: List[RecordSubscriber.Record] = []
        self._complete_records: List[RecordSubscriber.Record] = []
        self._index: int = 1

    def on_subscribed(self, subscription: Subscription) -> None:
        self._subscription = subscription
        self._on_subscribed_records.append(RecordSubscriber.Record(None, self._index))
        self._index += 1
        if self._on_subscribed_request > 0:
            self._subscription.request(self._on_subscribed_request)

    def consume(self, data) -> None:
        self._consume_records.append(RecordSubscriber.Record(data, self._index))
        self._index += 1
        if 0 < self._cancel_when <= len(self._consume_records):
            self._subscription.cancel()
            return
        if self._consume_request > 0:
            self._subscription.request(self._consume_request)

    def fail(self, cause: Exception):
        self._fail_records.append(RecordSubscriber.Record(cause, self._index))
        self._index += 1

    def complete(self):
        self._complete_records.append(RecordSubscriber.Record(None, self._index))
        self._index += 1

    def get_subscription(self) -> Subscription:
        """
        获取订阅关系。

        :return: 表示所获取的订阅关系。
        """
        return self._subscription

    def get_elements(self) -> List:
        """
        获取所有元素消费记录。

        :return: 表示所获取的元素消费记录。
        """
        return [record.get_data() for record in self._consume_records]

    def get_fails(self) -> List:
        """
        获取所有失败终结记录。

        :return: 表示所获取的失败终结记录。
        """
        return [record.get_data() for record in self._fail_records]

    def received_completed(self):
        """
        获取是否收到正常终结信号。

        :return: 表示是否收到正常终结信号。
        """
        if len(self._complete_records) > 1:
            raise FlowableException("complete called more than once.")
        return len(self._complete_records) == 1

    def received_failed(self):
        """
        获取是否收到异常终结信号。

        :return: 表示是否收到异常终结信号。
        """
        if len(self._fail_records) > 1:
            raise FlowableException("fail called more than once.")
        return len(self._fail_records) == 1
