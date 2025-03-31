# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：map 操作的测试。
"""
import unittest

from fit_flowable.core.choir import Choir
from fit_flowable.emitter.default_emitter import DefaultEmitter
from fit_flowable.subscriber.record_subscriber import RecordSubscriber


class MapTest(unittest.TestCase):
    """
    表示 map 操作的测试。
    """

    def test_convert_two_elements_by_map_then_get_two_correct_elements(self):
        subscriber = RecordSubscriber()
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).map(lambda value: f"{2 * value}").subscribe(subscriber)
        emitter.emit(1)
        emitter.emit(2)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), ["2", "4"])
        self.assertTrue(subscriber.received_completed())
        self.assertFalse(subscriber.received_failed())

    def test_failed_occur_in_mapper_then_received_fail(self):
        subscriber = RecordSubscriber()
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).map(lambda value: 10 / value).subscribe(subscriber)
        emitter.emit(1)
        emitter.emit(0)
        emitter.emit(2)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), [10])
        self.assertFalse(subscriber.received_completed())
        self.assertTrue(subscriber.received_failed())
        self.assertIsInstance(subscriber.get_fails()[0], ZeroDivisionError)


if __name__ == '__main__':
    unittest.main()
