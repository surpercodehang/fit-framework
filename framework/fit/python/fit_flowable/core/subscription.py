# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Publisher 向 Subscriber 提供的订阅关系。
"""
from abc import abstractmethod


class Subscription:
    @abstractmethod
    def request(self, count: int) -> None:
        """
        请求指定数量的数据。

        * Subscriber 通过 Publisher 提供的本接口中的本方法请求指定数量的数据；
        * 在进行请求之前，Publisher 将不会发送任何数据给 Subscriber；
        * Publisher 所发送数据数量将不会超过请求的数量，当 Publisher 所拥有的元素数量小于所请求时将不会有数据将别发送；
        * Subscriber 可以随时通过该接口请求数据，并且每次请求的数量将会得到累加，但最多不超过 Int64.Max（请求 Int64.Max 个元素视为请求数量无限的元素）。
        :param count: 表示请求的数据的数量。
        """
        pass

    @abstractmethod
    def cancel(self) -> None:
        """
        取消当前的订阅关系。

        * 取消订阅关系后，Publisher 将不再会向 Subscriber 发送数据或信号；
        * 但由于 Publisher 可能无法接受到取消操作，因此仍有可能存在数据或信号被发送。
        """
        pass
