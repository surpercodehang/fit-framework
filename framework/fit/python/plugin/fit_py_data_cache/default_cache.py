# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：简易的数据缓存
"""
import threading


class DefaultCache:
    def __init__(self):
        self._cache = {}
        self._meta = {}
        self._lock = threading.Lock()
        self._next_id = 0

    def create(self, value) -> str:
        """
        缓存用户提供的数据并返回数据的键
        :param value: 被缓存数据
        :return: 数据的键
        """
        with self._lock:
            key = hex(self._next_id)[2:].zfill(8)
            self._next_id += 1
            self._cache[key] = value
            self._meta[key] = ("str" if isinstance(value, str) else "bytes", len(value))
            return key

    def read(self, key: str):
        """
        根据指定键读取缓存中的数据
        :param key: 被缓存数据的键
        :return: 缓存中的数据，如果找不到则返回 None
        """
        with self._lock:
            return self._cache.get(key)

    def read_meta(self, key: str):
        """
        根据指定键读取缓存中的元数据
        :param key: 被缓存数据的键
        :return: 元数据(user_data, memory_size)，如果找不到则返回(None, None)
        """
        meta = self._meta.get(key)
        if not meta:
            return None, None
        return meta

    def delete(self, key: str) -> None:
        """
        根据指定键删除缓存中的数据
        :param key: 被删除数据的键
        """
        with self._lock:
            if key in self._cache:
                del self._cache[key]
                del self._meta[key]

    def get_cache_info(self) -> (int, int):
        """
        获取缓存中有多少个键，以及缓存中包含多少 byte 的数据。
        Returns:缓存中包含的键的个数，缓存数据大小(单位 byte)
        """
        with self._lock:
            return len(self._cache), sum(map(lambda x: len(x[1]), self._cache.items()))
