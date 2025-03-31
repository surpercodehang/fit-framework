# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：用于发送元素、正常终结、异常终结信号的发送器。
"""
from abc import abstractmethod


class Emitter:
    """
    表示数据的发送者。
    """

    class Observer:
        """
        表示 Emitter 的观察者。
        """

        @abstractmethod
        def on_data_emitted(self, data):
            """
            表示当 Emitter 的 emit 方法被调用时触发的事件。

            :param data: 表示待发送的数据。
            """
            pass

        @abstractmethod
        def on_completed(self):
            """
            表示当 Emitter 的 complete 方法被调用时触发的事件。
            """
            pass

        @abstractmethod
        def on_failed(self, cause: Exception):
            """
            表示当 Emitter 的 fail 方法被调用时触发的事件。

            :param cause: 表示的失败原因。
            """
            pass

    @abstractmethod
    def emit(self, data) -> None:
        """
        发送一个指定的数据。

        :param data: 表示所发送的数据。
        """
        pass

    @abstractmethod
    def complete(self) -> None:
        """
        发送一个正常终结信号。
        """
        pass

    @abstractmethod
    def fail(self, cause: Exception) -> None:
        """
        发送一个异常终结信号。

        :param cause: 表示所发送的异常终结的原因。
        """
        pass

    @abstractmethod
    def observe(self, observer: Observer) -> None:
        """
        添加一个观察者，用于观察数据发送者的一系列行为。

        :param observer: 表示待添加的观察者
        """
        pass
