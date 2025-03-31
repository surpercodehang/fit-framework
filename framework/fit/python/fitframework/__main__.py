# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：框架启动入口
"""
import time

from fitframework import const
from fitframework.api.decorators import fit


@fit(const.RUNTIME_SHUTDOWN_GEN_ID)
def shutdown() -> None:
    pass


def _on_ready():
    """
    在 main 函数未通过循环 sleep 保持不退出时，将执行到本函数，从而保证主线程不会退出。
    """
    while True:
        time.sleep(3600)


try:
    _on_ready()
except KeyboardInterrupt:
    shutdown()
