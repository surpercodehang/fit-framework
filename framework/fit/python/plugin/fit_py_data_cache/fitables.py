# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：提供缓存访问接口的系统插件。
"""
from fitframework.api.decorators import fitable, local_context
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.exception.fit_exception import CapacityOverflowException
from fitframework.utils.tools import get_memory_usage
from .cache_manager import cache_manager


@fitable("modelengine.fit.bigdata.cache.is_databus", "local-worker")
def is_databus() -> bool:
    return cache_manager.is_databus_running()


class CacheValueMetadata:
    def __init__(self, type_: str, length: int):
        self.type = type_
        self.length = length


@fitable("modelengine.fit.bigdata.cache.create.str", "local-worker")
def create_str_in_cache(content: str) -> str:
    return cache_manager.create(content)


@fitable("modelengine.fit.bigdata.cache.create.bytes", "local-worker")
def create_bytes_in_cache(content: bytes) -> str:
    return cache_manager.create(content)


@fitable("modelengine.fit.bigdata.cache.read.meta", "local-worker")
def read_meta_from_cache(index: str) -> CacheValueMetadata:
    data_type, data_size = cache_manager.read_meta(index)
    return CacheValueMetadata(data_type, data_size)


@fitable("modelengine.fit.bigdata.cache.read.str", "local-worker")
def read_str_from_cache(index: str) -> str:
    return cache_manager.read_content(index, str)


@fitable("modelengine.fit.bigdata.cache.read.bytes", "local-worker")
def read_bytes_from_cache(index: str) -> bytes:
    return cache_manager.read_content(index, bytes)


@fitable("modelengine.fit.bigdata.cache.delete", "local-worker")
def delete_value_from_cache(index: str) -> None:
    return cache_manager.delete_from_cache(index)


@fitable("modelengine.fit.bigdata.cache.get.info", "local-worker")
def get_info_from_cache() -> (int, int):
    return cache_manager.get_cache_info()


@local_context('memory.max-size', converter=int, default_value=1099511627776)
def _memory_max_size():
    pass


@fitable("modelengine.fit.bigdata.cache.validate.capacity", "local-worker")
def validate_capacity_from_cache(to_allocate_memory: int) -> None:
    memory_usage = get_memory_usage()
    if to_allocate_memory + memory_usage > _memory_max_size():
        sys_plugin_logger.warning(f"validate not passed. "
                                  f"[configuredMaxMem={'{:,}'.format(_memory_max_size())}, "
                                  f"usedMem={'{:,}'.format(memory_usage)}, "
                                  f"freeMem={'{:,}'.format(_memory_max_size() - memory_usage)}, "
                                  f"toAllocatedMem={'{:,}'.format(to_allocate_memory)}]")
        raise CapacityOverflowException(
            f"No enough remaining space for data caching. [configuredMaxMem={_memory_max_size()}, "
            f"usedMem={memory_usage}, freeMem={_memory_max_size() - memory_usage}, "
            f"toAllocatedMem={to_allocate_memory}].", _memory_max_size(), memory_usage, to_allocate_memory)
