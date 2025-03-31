# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：filter 操作的测试。
"""
import unittest

from fit_flowable.core.choir import Choir
from fit_flowable.emitter.default_emitter import DefaultEmitter
from fit_flowable.subscriber.record_subscriber import RecordSubscriber


class FilterTest(unittest.TestCase):
    """
    表示 filter 操作的测试。
    """

    def test_filter_three_elements_then_get_one_correct_element(self):
        subscriber = RecordSubscriber()
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).filter(lambda value: value % 2 == 1).subscribe(subscriber)
        emitter.emit(0)
        emitter.emit(1)
        emitter.emit(2)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), [1])
        self.assertTrue(subscriber.received_completed())
        self.assertFalse(subscriber.received_failed())

    def test_filter_three_elements_by_incorrect_method_then_get_one_correct_element(self):
        subscriber = RecordSubscriber()
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).filter(lambda value: 10 / value >= 5).subscribe(subscriber)
        emitter.emit(1)
        emitter.emit(0)
        emitter.emit(2)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), [1])
        self.assertFalse(subscriber.received_completed())
        self.assertTrue(subscriber.received_failed())

    def test_filter_three_elements_and_request_one_then_get_one_correct_element_without_completed(self):
        subscriber = RecordSubscriber(on_subscribed_request=1)
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).filter(lambda value: value % 2 == 1).subscribe(subscriber)
        emitter.emit(0)
        emitter.emit(1)
        emitter.emit(2)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), [1])
        self.assertFalse(subscriber.received_failed())

    def test_filter_three_elements_and_request_two_then_get_one_element_with_completed(self):
        subscriber = RecordSubscriber(on_subscribed_request=2)
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).filter(lambda value: value % 2 == 1).subscribe(subscriber)
        emitter.emit(0)
        emitter.emit(1)
        emitter.emit(2)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), [1])
        self.assertTrue(subscriber.received_completed())
        self.assertFalse(subscriber.received_failed())


if __name__ == '__main__':
    unittest.main()
