# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit HTTP 异步服务端所需工具
"""
import base64
import threading
import time
from queue import Queue
from typing import Dict, Optional

from fitframework.api.decorators import value
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.http_header import HttpHeader
from fitframework.core.network.metadata.metadata_utils import TagLengthValuesUtil


@value('async.result-save-duration', converter=int)
def get_result_save_duration():
    pass


class WorkerInfo:
    """
    表示 FIT 进程所执行任务的信息。
    """

    def __init__(self, instance_id: str):
        """
        构造 FIT 进程所执行任务信息的实例。

        @param instance_id: 实例标识。
        """
        self.instance_id: str = instance_id
        self.result_queue: Queue = Queue()
        self.results: Dict[str: FitResponse] = {}
        self.last_accessed_time: float = time.time()

    def refresh_last_access_time(self):
        self.last_accessed_time = time.time()


class UniqueTaskId:
    """
    表示任务唯一标识。
    """

    def __init__(self, worker_id: Optional[str], instance_id: Optional[str], task_id: Optional[str]):
        """
        构造任务唯一标识实例。

        @param worker_id: FIT 进程标识。
        @param instance_id: 实例标识。
        @param task_id: 任务标识。
        """
        self.worker_id: Optional[str] = worker_id
        self.instance_id: Optional[str] = instance_id
        self.task_id: Optional[str] = task_id


class WorkerInfoManager:
    """
    表示 FIT 进程所执行任务信息的管理器。
    """

    def __init__(self):
        # 以 worker_id 为键的字典
        self._worker_infos: Dict[str, WorkerInfo] = {}
        self._worker_infos_lock = threading.Lock()

    def create_and_get_result_queue(self, worker_id: str, instance_id: str) -> Queue:
        """
        在有异步任务提交时，根据指定的 worker_id 和 instance_id 创建并获取结果队列，
        如果当前 worker_id 不存在或者 instance_id 与先前记录的不一致则创建新的结果队列。

        @param worker_id: FIT 进程标识。
        @param instance_id: 实例标识。
        @return: 结果队列。
        """
        with self._worker_infos_lock:
            if worker_id not in self._worker_infos:
                sys_plugin_logger.info(f"new instance initialized. [worker_id={worker_id}, instance_id={instance_id}]")
                self._create_new_worker_info(worker_id, instance_id)
            if self._worker_infos.get(worker_id).instance_id != instance_id:
                sys_plugin_logger.info(f"old instance discarded. [worker_id={worker_id}, instance_id={instance_id}]")
                self._create_new_worker_info(worker_id, instance_id)
            worker_info = self._worker_infos.get(worker_id)
            worker_info.refresh_last_access_time()
            return worker_info.result_queue

    def get_result_queue(self, worker_id: str, instance_id: str) -> Optional[Queue]:
        """
        在有异步任务轮询时，根据指定的 worker_id 和 instance_id 获取结果队列，
        如果当前 worker_id 不存在或者 instance_id 与先前记录的不一致则返回 None。

        @param worker_id: FIT 进程标识。
        @param instance_id: 实例标识。
        @return: 所获取的结果队列
        """
        with self._worker_infos_lock:
            if worker_id not in self._worker_infos:
                sys_plugin_logger.info(f"cannot get result queue because old instance has been removed. "
                                       f"[worker_id={worker_id}, instance_id={instance_id}]")
                return None
            if self._worker_infos.get(worker_id).instance_id != instance_id:
                sys_plugin_logger.info(f"cannot get result queue because old instance has been replaced. "
                                       f"[worker_id={worker_id}, instance_id={instance_id}]")
                return None
            worker_info = self._worker_infos.get(worker_id)
            worker_info.refresh_last_access_time()
            return worker_info.result_queue

    def get_execute_result(self, worker_id: str, instance_id: str, task_id: str) -> Optional[FitResponse]:
        """
        根据指定的 worker_id、instance_id 和 task_id 获取执行结果，
        如果当前 worker_id 不存在返回 None；
        如果当前 instance_id 与先前记录的不一致返回 None；
        如果结果不存在返回 None。

        @param worker_id: FIT 进程标识。
        @param instance_id: 实例标识。
        @param task_id: 任务标识。
        @return: 任务执行结果。
        """
        with self._worker_infos_lock:
            if worker_id not in self._worker_infos:
                sys_plugin_logger.info(f"cannot get execute result because worker cannot found. "
                                       f"[worker_id={worker_id}, instance_id={instance_id}, task_id={task_id}]")
                return None
            if self._worker_infos.get(worker_id).instance_id != instance_id:
                sys_plugin_logger.info(f"cannot get execute result because instance cannot found. "
                                       f"[worker_id={worker_id}, instance_id={instance_id}, task_id={task_id}]")
                return None
            result = self._worker_infos.get(worker_id).results.get(task_id, None)
            if result is None:
                sys_plugin_logger.info(f"cannot get execute result because task cannot found. "
                                       f"[worker_id={worker_id}, instance_id={instance_id}, task_id={task_id}]")
            return result

    def put_execute_result(self, worker_id: str, instance_id: str, task_id: str, result: FitResponse) -> None:
        """
        根据指定的 worker_id、instance_id 和 task_id 设定执行结果。
        如果当前 worker_id 不存在则不执行实际操作；
        如果当前 instance_id 与先前记录的不一致则不执行实际操作。

        @param worker_id: FIT 进程标识。
        @param instance_id: 实例标识。
        @param task_id: 任务标识。
        @param result: 执行结果。
        """
        with self._worker_infos_lock:
            if worker_id not in self._worker_infos:
                sys_plugin_logger.info(f"cannot put execute result because worker cannot found. "
                                       f"[worker_id={worker_id}, instance_id={instance_id}, task_id={task_id}]")
            if self._worker_infos.get(worker_id).instance_id != instance_id:
                sys_plugin_logger.info(f"cannot put execute result because instance id is not match. "
                                       f"[worker_id={worker_id}, instance_id={instance_id}, task_id={task_id}]")
            self._worker_infos.get(worker_id).results[task_id] = result

    def clear_expired_worker_info(self):
        """
        清理过期的 FIT 执行进程信息。
        """
        with self._worker_infos_lock:
            to_remove_workers = []
            for worker_id, worker_info in self._worker_infos.items():
                if worker_info.last_accessed_time + get_result_save_duration() < time.time():
                    to_remove_workers.append(worker_id)
            for worker_id in to_remove_workers:
                sys_plugin_logger.info(f"worker info removed because not accessed for a long time. [worker_id="
                                       f"{worker_id}, instance_id={self._worker_infos[worker_id].instance_id}]")
                self._remove_expired_worker_info(worker_id)

    def _create_new_worker_info(self, worker_id: str, instance_id: str):
        self._worker_infos[worker_id] = WorkerInfo(instance_id)

    def _remove_expired_worker_info(self, worker_id: str):
        del self._worker_infos[worker_id]


def parse_unique_task_id(headers: Dict[str, str]) -> UniqueTaskId:
    """
    通过 HTTP 报文头解析异步任务的各种标识。

    @param headers: HTTP 报文头。
    @return: HTTP 异步任务的进程、实例与任务标识，当无法解析时则返回 None。
    """
    encoded_tlv_data = headers.get(HttpHeader.TLV.value, None)
    if encoded_tlv_data is None:
        return UniqueTaskId(None, None, None)
    tlvs = TagLengthValuesUtil.deserialize(base64.b64decode(encoded_tlv_data))
    if tlvs is None:
        return UniqueTaskId(None, None, None)
    worker_id = tlvs.get(TagLengthValuesUtil.WORKER_ID, None)
    instance_id = tlvs.get(TagLengthValuesUtil.INSTANCE_ID, None)
    task_id = tlvs.get(TagLengthValuesUtil.TASK_ID, None)
    return UniqueTaskId(worker_id, instance_id, task_id)


def is_sync_request(unique_task_id: UniqueTaskId):
    """
    用于判断任务是否为同步任务。

    @param unique_task_id: 任务唯一标识。
    @return: 判断结果。
    """
    if unique_task_id.worker_id is None or len(unique_task_id.worker_id) == 0:
        return True
    if unique_task_id.instance_id is None or len(unique_task_id.instance_id) == 0:
        return True
    if unique_task_id.task_id is None or len(unique_task_id.task_id) == 0:
        return True
    return False
