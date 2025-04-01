# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：HTTP客户端工具
"""
import functools
import inspect
from typing import List
from urllib3.connection import HTTPSConnection

from fitframework.api.decorators import value, fit
from fitframework.utils.tools import to_bool


@value("https.client.verify_enabled", converter=to_bool)
def get_client_verify_enabled():
    pass


@value('https.client.ca_path')
def get_client_ca_path():
    pass


@value('https.client.assert_host_name')
def get_client_assert_host_name():
    pass


@value("https.client.cert_enabled", converter=to_bool)
def get_cert_enabled():
    pass


@value('https.client.crt_path')
def get_client_crt_path():
    pass


@value('https.client.key_path')
def get_client_key_path():
    pass


@value('https.client.key_file_encrypted')
def get_client_key_encrypted():
    pass


@value('https.client.key_file_password')
def get_client_key_file_password():
    pass


@value('HTTPS__CLIENT__KEY__FILE__PASSWORD')
def get_client_key_file_password_env():
    pass


@value('https.client.key_file_password_encrypted')
def get_client_key_file_password_encrypted():
    pass


@value('client.polling-timeout', default_value=120)
def get_polling_timeout():
    pass


@fit("modelengine.fit.security.decrypt")
def decrypt(cipher: str) -> str:
    """
    对于加密后内容进行解密。
    特别注意：
    1. 该接口的 fitable 实现需要通过本地静态插件的方式给出；
    2. 必须在 fit.yml 中指定要调用的 fitable id。

    :param cipher: 待解密的内容。
    :return: 解密后的内容。
    """
    pass


def _get_client_password():
    if get_client_key_file_password_env() is not None:
        return get_client_key_file_password_env()
    else:
        return get_client_key_file_password()


@functools.lru_cache()
def get_decrypted_key_file_password():
    if not get_cert_enabled():  # 如果不需要服务端校验自身身份
        return None
    if not get_client_key_encrypted():  # 如果私钥未被加密
        return None
    if not get_client_key_file_password_encrypted():  # 如果私钥密码未被加密
        return _get_client_password()

    return decrypt(_get_client_password())


@functools.lru_cache()
def get_cert():
    if not get_cert_enabled():  # 如果不需要服务端校验自身身份
        return None
    crt_file_path = get_client_crt_path()
    key_file_path = get_client_key_path()
    return crt_file_path, key_file_path


@functools.lru_cache()
def get_verify():
    if get_client_verify_enabled():
        return get_client_ca_path()
    else:
        return False


def connection_init_wrapper(func):
    def wrapper(*args, **kwargs):
        stack = inspect.stack()
        for frame_info in stack:
            if frame_info.function.find("fit_request_response_https") >= 0:
                kwargs["assert_hostname"] = get_client_assert_host_name()
                kwargs["key_password"] = get_decrypted_key_file_password()
                break
        return func(*args, **kwargs)

    return wrapper


HTTPSConnection.__init__ = connection_init_wrapper(HTTPSConnection.__init__)


class PollingMetadata:
    def __init__(self, worker_id: str, instance_id: str, task_id: str):
        self.worker_id = worker_id
        self.instance_id = instance_id
        self.task_id = task_id


class TaskStorage:
    """
    用于管理任务的数据结构，可记录某个 worker 下的各个 instance 以及某个 instance 下的 task，
    并且提供一系列方法对于数据进行访问和操作。
    """

    def __init__(self):
        self.instances = {}  # worker_id 到 instance_id 集合的映射
        self.tasks = {}  # instance_id 到 task_id 集合的映射

    def add_task(self, worker_id: str, instance_id: str, task_id: str):
        """
        为某个 worker 某个 instance 添加 task
        args:
            worker_id: 待添加 task 的 worker
            instance_id: 待添加 task 的 instance
            worker_id: 待添加的 task
        """
        if worker_id not in self.instances:
            self.instances[worker_id] = set()
        self.instances[worker_id].add(instance_id)
        if instance_id not in self.tasks:
            self.tasks[instance_id] = set()
        self.tasks[instance_id].add(task_id)

    def get_task_count_of_instance(self, worker_id: str, instance_id: str) -> int:
        """
        查询某个 worker 某个 instance 当前的 task 数量
        args:
            worker_id: 需要查询 task 数量的 worker
            instance_id: 需要查询 task 数量的 instance
        returns:
            查询结果
        """
        if worker_id not in self.instances or instance_id not in self.tasks:
            return 0
        return len(self.tasks[instance_id])

    def remove_task(self, worker_id: str, instance_id: str, task_id: str) -> None:
        """
        移除掉某个 worker 某个 instance 的某个 task
        args:
            worker_id: 需要移除 task 的 worker
            instance_id: 需要移除 task 的 instance
            task_id: 需要移除的 task
        """
        self.remove_tasks(worker_id, instance_id, [task_id])

    def remove_tasks(self, worker_id: str, instance_id: str, task_ids: List):
        """
        移除掉某个 worker 某个 instance 的部分 task
        args:
            worker_id: 需要移除 task 的 worker
            instance_id: 需要移除 task 的 instance
            task_ids: 需要移除的 task 列表
        returns:
            被废弃的所有 task
        """
        if (worker_id not in self.instances) or (instance_id not in self.tasks):
            return
        self.tasks[instance_id].difference_update(set(task_ids))
        if len(self.tasks[instance_id]) == 0:
            del self.tasks[instance_id]
            self.instances[worker_id].discard(instance_id)
        if len(self.instances[worker_id]) == 0:
            del self.instances[worker_id]

    def remove_instance(self, worker_id: str, instance_id: str) -> List:
        """
        移除掉某个 worker 下该 instance 的所有 task
        args:
            worker_id: 需要废弃 task 的 worker
            instance_id: 需要废弃的 instance
        returns:
            被废弃的所有 task
        """
        if (worker_id not in self.instances) or (instance_id not in self.tasks):
            return []
        result = list(self.tasks[instance_id])
        del self.tasks[instance_id]
        self.instances[worker_id].discard(instance_id)
        if len(self.instances[worker_id]) == 0:
            del self.instances[worker_id]
        return result

    def remove_tasks_except_instance(self, worker_id: str, instance_id: str) -> List:
        """
        移除掉某个 worker 下除了该 instance 外所有的 task
        args:
            worker_id: 需要废弃 task 的 worker
            instance_id: 需要被保留的 instance
        returns:
            被废弃的所有 task
        """
        if worker_id not in self.instances:
            return []
        instances = self.instances.get(worker_id)
        result = []
        instance_to_delete = []
        for instance in instances:
            if instance == instance_id:
                continue
            result += list(self.tasks.get(instance))
            instance_to_delete.append(instance)
        for instance in instance_to_delete:
            del self.tasks[instance]
            self.instances[worker_id].discard(instance)
        if len(self.instances[worker_id]) == 0:
            del self.instances[worker_id]
        return result
