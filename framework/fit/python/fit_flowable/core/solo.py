# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：处理单个值数据流的发布者。
"""
from fit_flowable.core.publisher import Publisher
from fit_flowable.core.subscriber import Subscriber


class Solo(Publisher):
    def subscribe(self, subscriber: Subscriber) -> None:
        pass
