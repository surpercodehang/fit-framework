# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：提供DataBus缓存的存取功能。
"""
from typing import Optional, Tuple
import uuid
from fitframework import value

from .databus_cache_type import DataBusCacheContentType
from .databus_proxy import DataBusClient, DataBusErrorCode, WriteRequest, ReadRequest


@value("worker.id")
def _worker_id() -> str:
    pass


@value("databus.host")
def _databus_host() -> str:
    pass


@value("databus.port", converter=int)
def _databus_port() -> int:
    pass


class DataBusCache:
    def __init__(self):
        self._client = DataBusClient()
        error = self._client.open(core_host=_databus_host(), core_port=_databus_port())
        if error != DataBusErrorCode.None_:
            raise ConnectionError
        self._info = dict()

    def __del__(self):
        self._client.close()

    def create(self, content: bytes, content_type: DataBusCacheContentType) -> Optional[str]:
        """
        缓存用户提供的数据并返回数据的键
        :param content: 被缓存数据, 要求是bytes
        :param content_type: 被缓存数据的类型信息
        :return: 数据的键
        :raise IOError: 内存分配或写入失败
        """
        mem_key = uuid.uuid4().hex

        error = self._client.shared_malloc(mem_key, len(content))
        if error != DataBusErrorCode.None_:
            raise IOError("Failed to allocate memory in DataBus.")
        write_request = WriteRequest(mem_key, contents=content, is_operating_user_data=True,
                                     user_data=content_type.to_bytecode())
        error = self._client.write_once(write_request)
        if error != DataBusErrorCode.None_:
            raise IOError("Failed to write to memory in DataBus.")
        self._info[mem_key] = len(content)
        return mem_key

    def read(self, key: str):
        """
        根据指定键读取缓存中的数据
        :param key: 被缓存数据的键
        :return: 缓存中的数据，如果找不到则返回 None
        :raise IOError: 内存读取失败
        """
        request = ReadRequest(user_key=key)
        err, response = self._client.read_once(request)
        if err == DataBusErrorCode.KeyNotFound:
            raise KeyError("No such memory key in DataBus.")
        if err != DataBusErrorCode.None_:
            raise IOError("Failed to read from memory in DataBus.")
        return response.contents

    def read_meta(self, key: str) -> Tuple[Optional[str], Optional[int]]:
        """
        根据指定键读取DataBus中的元数据
        :param key: 被缓存数据的键
        :return: 元数据(type_name, memory_size)，如果找不到则返回(None, None)
        :raise KeyError: 内存无对应元数据
        :raise ValueError: 元数据读取失败
        """
        err, data = self._client.get_meta_data(key)
        if err == DataBusErrorCode.KeyNotFound:
            raise KeyError("No such memory key in DataBus.")
        elif err != DataBusErrorCode.None_:
            raise ValueError("Cannot get metadata from DataBus.")
        size, user_data = data
        return DataBusCacheContentType.from_bytecode(user_data).to_name(), size

    def delete(self, key: str) -> None:
        """
        根据指定键删除缓存中的数据
        :param key: 被删除数据的键
        """
        self._client.shared_free(key)
        if key in self._info:
            del self._info[key]

    def get_cache_info(self) -> (int, int):
        """
        获取缓存中有多少个键，以及缓存中包含多少 byte 的数据。
        Returns:缓存中包含的键的个数，缓存数据大小(单位 byte)
        """
        return len(self._info), sum(self._info.values())
