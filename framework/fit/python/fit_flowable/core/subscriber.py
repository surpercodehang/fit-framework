# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：订阅者，可以处理发布者所发布的数据，以及订阅开始、正常终结和异常终结信号。
"""
from abc import abstractmethod

from fit_flowable.core.subscription import Subscription


class Subscriber:
    """
    表示订阅者，可以处理发布者所发布的数据，以及订阅开始、正常终结和异常终结信号。

    在对于发布者进行订阅之后，其 onSubscribed 方法将被调用，并通过该方法发布者提供一个 Subscription；
    作为订阅者其通过发布者所提供的 Subscription 请求自己所需要数量的元素，而在未对于其订阅的发布者通过 Subscription 的 request 接口请求数据前将不会收到任何数据；
    在进行订阅后，发布者为其提供以下保证：
    发布者所发布元素数量不大于其通过 Subscription 所请求的元素数量
    发布者最多发布一个异常或数据结束事件
    """

    @abstractmethod
    def on_subscribed(self, subscription: Subscription) -> None:
        """
        表示处理订阅关系。将在进行订阅即调用 Publisher 的 subscribe 后被调用。

        :param subscription: 表示给定的订阅关系。
        """
        pass

    @abstractmethod
    def consume(self, data) -> None:
        """
        表示处理数据。发布者所发布的数据将由该方法进行发布，订阅者可在该方法中定义数据的处理逻辑。

        :param data: 表示待处理的数据。
        """
        pass

    @abstractmethod
    def fail(self, cause: Exception) -> None:
        """
        表示异常终结信号。在 Publisher 发送异常终结信号后，该 Subscriber 的任何方法将不会再被 Publisher 调用。

        :param cause: 表示订阅中的错误处理信息。
        """
        pass

    @abstractmethod
    def complete(self) -> None:
        """
        表示正常终结信号。在 Publisher 发送正常终结信号后，该 Subscriber 的任何方法将不会再被 Publisher 调用。
        """
        pass
