# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：eventing单元测试
"""
import unittest

from fitframework.api.decorators import register_event
from fitframework.api.enums import FrameworkEvent
from fitframework.testing.test_support import FitTestSupport, Counter
from fitframework.utils.eventing import notify, _registered_events


class EventingTest(FitTestSupport):

    def setUp(self) -> None:
        _registered_events.clear()

    def test_registry_without_filter_function(self):
        counter = Counter()

        @register_event(FrameworkEvent.APPLICATION_STARTED)
        def fire(arg1):
            counter.count += 1
            self.assertEqual(arg1, "value1")

        notify(FrameworkEvent.APPLICATION_STARTED, **{"arg1": "value1"})
        self.assertEqual(counter.count, 1)

    def test_registry_with_filter_function(self):
        counter = Counter()

        def my_filter(arg1) -> bool:
            return arg1 % 2 == 0

        @register_event(FrameworkEvent.APPLICATION_STARTED, event_filter=my_filter)
        def fire(arg1):
            counter.count += 1
            self.assertEqual(arg1, 0)

        notify(FrameworkEvent.APPLICATION_STARTED, **{"arg1": 0})
        notify(FrameworkEvent.APPLICATION_STARTED, **{"arg1": 1})
        self.assertEqual(counter.count, 1)


if __name__ == '__main__':
    unittest.main()
