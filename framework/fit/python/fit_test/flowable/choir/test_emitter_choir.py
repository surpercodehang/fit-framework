# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：EmitterChoir 的测试。
"""
import unittest

from fit_flowable.core.choir import Choir
from fit_flowable.emitter.default_emitter import DefaultEmitter
from fit_flowable.subscriber.record_subscriber import RecordSubscriber


class EmitterChoirTest(unittest.TestCase):
    """
    表示 EmitterChoir 的测试。
    """

    def test_emit_twice_then_consume_called_twice(self):
        subscriber = RecordSubscriber()
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).subscribe(subscriber)
        emitter.emit(0)
        emitter.emit(1)
        self.assertEqual(subscriber.get_elements(), [0, 1])
        self.assertFalse(subscriber.received_completed())
        self.assertFalse(subscriber.received_failed())

    def test_complete_once_then_complete_called_once(self):
        subscriber = RecordSubscriber()
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).subscribe(subscriber)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), [])
        self.assertTrue(subscriber.received_completed())
        self.assertFalse(subscriber.received_failed())

    def test_emit_twice_and_complete_once_then_complete_called_once(self):
        subscriber = RecordSubscriber()
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).subscribe(subscriber)
        emitter.emit(0)
        emitter.emit(1)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), [0, 1])
        self.assertTrue(subscriber.received_completed())
        self.assertFalse(subscriber.received_failed())

    def test_emit_twice_and_complete_once_but_cancel_when_got_one_then_got_one_item_and_not_complete(self):
        subscriber = RecordSubscriber(cancel_when=1)
        emitter = DefaultEmitter()
        Choir.from_emitter(emitter).subscribe(subscriber)
        emitter.emit(0)
        emitter.emit(1)
        emitter.complete()
        self.assertEqual(subscriber.get_elements(), [0])
        self.assertFalse(subscriber.received_completed())
        self.assertFalse(subscriber.received_failed())


if __name__ == '__main__':
    unittest.main()
