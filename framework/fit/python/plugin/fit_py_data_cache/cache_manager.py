# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：数据缓存的实现管理
"""
import threading
import time
from typing import Union, Tuple

from fitframework.api.decorators import value
from fitframework.api.logging import sys_plugin_logger

from .databus_cache_type import DataBusCacheContentType
from .default_cache import DefaultCache


@value("databus.enabled", default_value=False)
def _databus_enabled():
    pass


_has_databus_impl = False
try:
    if _databus_enabled():
        from .databus_cache import DataBusCache

        _has_databus_impl = True
except ImportError:
    sys_plugin_logger.warning("Cannot find DataBus Impl. Fall back to naive impl.")


class _CacheManager:
    RECONNECT_INTERVAL = (5, 10, 20, 40, 80, 160, 320)

    def __init__(self):
        self._databus_cv = threading.Condition()
        self._is_databus_running = False
        self._default_cache = DefaultCache()
        self._retry_interval_index = 0
        if _has_databus_impl:
            self._try_init_databus()
            self._reconnect_thread = threading.Thread(target=self._try_reconnect_databus)
            self._reconnect_thread.daemon = True
            self._reconnect_thread.start()

    def __del__(self):
        self._is_databus_running = False
        self._reconnect_thread = None

    def _try_init_databus(self):
        try:
            self._databus_cache = DataBusCache()
            with self._databus_cv:
                self._is_databus_running = True
            sys_plugin_logger.info("Using DataBus Impl.")
        except NotImplementedError:
            sys_plugin_logger.warning("No real implementation of DataBus. Fall back to default impl.")
            global _has_databus_impl
            _has_databus_impl = False
        except ConnectionError:
            sys_plugin_logger.warning("Cannot connect to DataBus. Fall back to default impl.")
        except Exception as e:
            sys_plugin_logger.warning("Unknown exception occurred. Fall back to default impl.", stack_info=e)

    def _try_reconnect_databus(self):
        while True:
            with self._databus_cv:
                self._databus_cv.wait_for(lambda: not self._is_databus_running)
            sleep_time = self.RECONNECT_INTERVAL[self._retry_interval_index]
            sys_plugin_logger.info("Will try to reconnect to DataBus after %ds.", sleep_time)
            time.sleep(sleep_time)
            self._try_init_databus()
            if self._is_databus_running:
                self._retry_interval_index = 0
            self._retry_interval_index = min(self._retry_interval_index + 1, len(self.RECONNECT_INTERVAL) - 1)

    def _handle_databus_failure(self):
        # 在DataBus出错的情况下fallback到default实现
        sys_plugin_logger.warning("Failed to connect to DataBus.")
        with self._databus_cv:
            self._is_databus_running = False
            self._databus_cv.notify()

    def is_databus_running(self):
        with self._databus_cv:
            return self._is_databus_running

    def create(self, contents: Union[str, bytes]) -> str:
        type_code = DataBusCacheContentType.STRING if isinstance(contents, str) else DataBusCacheContentType.BYTES
        if self.is_databus_running():
            content_bytes = contents.encode(encoding="utf-8") if isinstance(contents, str) else contents
            try:
                return self._databus_cache.create(content_bytes, type_code)
            except IOError:
                self._handle_databus_failure()
        sys_plugin_logger.info("Fallback to Default Cache Impl.")
        return self._default_cache.create(contents)

    def read_meta(self, index: str) -> Tuple[str, int]:
        if self.is_databus_running():
            try:
                return self._databus_cache.read_meta(index)
            except ValueError:
                self._handle_databus_failure()
            except KeyError:
                pass
        sys_plugin_logger.info("Fallback to Default Cache Impl.")
        return self._default_cache.read_meta(index)

    def read_content(self, index: str, expect_type: object):
        if self.is_databus_running():
            try:
                ret = self._databus_cache.read(index)
                if expect_type is str:
                    # DataBus默认返回bytes
                    return ret.decode(encoding="utf-8")
                return ret
            except IOError:
                self._handle_databus_failure()
            except KeyError:
                pass
        sys_plugin_logger.info("Fallback to Default Cache Impl.")
        return self._default_cache.read(index)

    def get_cache_info(self):
        if self.is_databus_running():
            return self._databus_cache.get_cache_info()
        return self._default_cache.get_cache_info()

    def delete_from_cache(self, index):
        if self.is_databus_running():
            self._databus_cache.delete(index)
        else:
            self._default_cache.delete(index)


cache_manager: _CacheManager = _CacheManager()
