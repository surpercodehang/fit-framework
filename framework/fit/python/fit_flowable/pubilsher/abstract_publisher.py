# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：发布者的抽象父类。
"""
from abc import abstractmethod

from fit_flowable.core.publisher import Publisher
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.subscriber.empty_subscriber import EmptySubscriber


class AbstractPublisher(Publisher):
    """
    表示发布者的抽象父类。
    """

    def subscribe(self, subscriber: Subscriber):
        if subscriber is None:
            self.do_subscribe(EmptySubscriber.INSTANCE)
        else:
            self.do_subscribe(subscriber)

    @abstractmethod
    def do_subscribe(self, subscriber: Subscriber) -> None:
        """
        向发布者订阅以启动数据发送时的具体行为。

        :param subscriber: 已订阅的订阅者。
        """
        pass
