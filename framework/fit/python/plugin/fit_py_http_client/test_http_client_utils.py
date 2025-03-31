# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：HTTP客户端工具的测试
"""
from unittest import TestCase

from .http_client_utils import TaskStorage


class TestTaskStorage(TestCase):
    def test_add_task_to_same_instance(self):
        storage = TaskStorage()
        self.assertEqual(storage.get_task_count_of_instance("1", "1-1"), 0)
        storage.add_task("1", "1-1", "1-1-1")
        self.assertEqual(storage.get_task_count_of_instance("1", "1-1"), 1)
        storage.add_task("1", "1-1", "1-1-2")
        storage.add_task("1", "1-1", "1-1-3")
        self.assertEqual(storage.get_task_count_of_instance("1", "1-1"), 3)
        self.assertEqual(storage.instances, {"1": {"1-1"}})
        self.assertEqual(storage.tasks, {"1-1": {"1-1-1", "1-1-2", "1-1-3"}})

    def test_add_task_to_diffierent_instance(self):
        storage = TaskStorage()
        self.assertEqual(storage.get_task_count_of_instance("1", "1-1"), 0)
        storage.add_task("1", "1-1", "1-1-1")
        self.assertEqual(storage.get_task_count_of_instance("1", "1-1"), 1)
        storage.add_task("1", "1-2", "1-2-1")
        self.assertEqual(storage.get_task_count_of_instance("1", "1-2"), 1)
        self.assertEqual(storage.instances, {"1": {"1-1", "1-2"}})
        self.assertEqual(storage.tasks, {"1-1": {"1-1-1"}, "1-2": {"1-2-1"}})

    def test_remove_tasks_partly(self):
        storage = TaskStorage()
        storage.add_task("1", "1-1", "1-1-1")
        storage.add_task("1", "1-1", "1-1-2")
        storage.add_task("1", "1-1", "1-1-3")
        storage.remove_tasks("1", "1-1", ["1-1-1", "1-1-3"])
        self.assertEqual(storage.get_task_count_of_instance("1", "1-1"), 1)
        self.assertEqual(storage.instances, {"1": {"1-1"}})
        self.assertEqual(storage.tasks, {"1-1": {"1-1-2"}})

    def test_remove_tasks_completely(self):
        storage = TaskStorage()
        storage.add_task("1", "1-1", "1-1-1")
        storage.add_task("1", "1-1", "1-1-2")
        storage.add_task("1", "1-1", "1-1-3")
        storage.remove_tasks("1", "1-1", ["1-1-1", "1-1-2", "1-1-3"])
        self.assertEqual(storage.get_task_count_of_instance("1", "1-1"), 0)
        self.assertEqual(storage.instances, {})
        self.assertEqual(storage.tasks, {})

    def test_remove_instance(self):
        storage = TaskStorage()
        storage.add_task("1", "1-1", "1-1-1")
        storage.add_task("1", "1-1", "1-1-2")
        storage.add_task("1", "1-2", "1-2-1")
        self.assertEqual(set(storage.remove_instance("1", "1-1")), {"1-1-1", "1-1-2"})
        self.assertEqual(storage.get_task_count_of_instance("1", "1-1"), 0)
        self.assertEqual(storage.get_task_count_of_instance("1", "1-2"), 1)
        self.assertEqual(storage.instances, {"1": {"1-2"}})
        self.assertEqual(storage.tasks, {"1-2": {"1-2-1"}})

    def test_remove_tasks_except_instance_from_three_instances(self):
        storage = TaskStorage()
        storage.add_task("1", "1-1", "1-1-1")
        storage.add_task("1", "1-1", "1-1-2")
        storage.add_task("1", "1-2", "1-2-1")
        storage.add_task("1", "1-3", "1-3-1")
        self.assertEqual(set(storage.remove_tasks_except_instance("1", "1-1")), {"1-2-1", "1-3-1"})
        self.assertEqual(storage.instances, {"1": {"1-1"}})
        self.assertEqual(storage.tasks, {"1-1": {"1-1-1", "1-1-2"}})

    def test_remove_tasks_except_instance_from_one_instance(self):
        storage = TaskStorage()
        storage.add_task("1", "1-1", "1-1-1")
        storage.add_task("1", "1-1", "1-1-2")
        self.assertEqual(set(storage.remove_tasks_except_instance("1", "1-2")), {"1-1-1", "1-1-2"})
        self.assertEqual(storage.instances, {})
        self.assertEqual(storage.tasks, {})
