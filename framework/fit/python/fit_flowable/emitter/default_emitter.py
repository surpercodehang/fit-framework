# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Emitter 的可观测实现。
"""
from threading import Lock
from typing import List

from fit_flowable.core.emitter import Emitter


class DefaultEmitter(Emitter):
    """
    Emitter 的可观测的实现。
    """

    def __init__(self):
        self._observers: List[Emitter.Observer] = []
        self._lock: Lock = Lock()

    def emit(self, data) -> None:
        observer_list = self._get_observers()
        for observer in observer_list:
            observer.on_data_emitted(data)

    def complete(self) -> None:
        observer_list = self._get_observers()
        for observer in observer_list:
            observer.on_completed()

    def fail(self, cause: Exception) -> None:
        observer_list = self._get_observers()
        for observer in observer_list:
            observer.on_failed(cause)

    def observe(self, observer: Emitter.Observer) -> None:
        if observer is None:
            return
        with self._lock:
            self._observers.append(observer)

    def _get_observers(self) -> List[Emitter.Observer]:
        with self._lock:
            return self._observers.copy()
