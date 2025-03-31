# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：EmitterAndOperationChoir 的测试。
"""
import unittest

from fit_flowable.core.choir import Choir
from fit_flowable.emitter.default_emitter import DefaultEmitter
from fit_flowable.subscriber.record_subscriber import RecordSubscriber


class EmitterAndOperationChoirTest(unittest.TestCase):
    """
    表示 EmitterAndOperationChoir 的测试。
    """

    def test_request_twice_then_request_operation_called_twice(self):
        subscriber = RecordSubscriber(2, 1)
        emitter = DefaultEmitter()
        request_records = []
        Choir.from_emitter_and_operation(emitter, lambda value: request_records.append(value)).subscribe(subscriber)
        emitter.emit(0)
        self.assertEqual(request_records, [2, 1])

    def test_cancel_once_then_cancel_operation_called_twice(self):
        subscriber = RecordSubscriber(1, 0, 1)
        emitter = DefaultEmitter()
        cancelled = []
        Choir.from_emitter_and_operation(emitter, cancel_operation=lambda: cancelled.append(1)).subscribe(subscriber)
        emitter.emit(0)
        self.assertEqual(cancelled, [1])


if __name__ == '__main__':
    unittest.main()
