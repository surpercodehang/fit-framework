# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：线程相关工具类
"""

import threading
from functools import wraps

_global_pool = {}


def keyed_lock(key: str = None, lock_pool=None, mutex_lock=threading.Lock()):
    """ 对key锁定实现线程安全，可以指定锁池和获取锁的互斥锁 """
    if lock_pool is None:
        lock_pool = _global_pool

    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            key_string = str(args[0]) if key is None else key
            with _get_lock(key_string, func, lock_pool, mutex_lock):
                return func(*args, **kwargs)

        return wrapper

    return decorator


def _get_lock(key, func, lock_pool, mutex_lock):
    # 使用全局变量时key后缀function name，防止冲突
    the_key = f"{key}.{func.__name__}" if lock_pool == _global_pool else key
    if the_key in lock_pool:
        return lock_pool[the_key]

    with mutex_lock:
        if the_key not in lock_pool:
            lock_pool[the_key] = threading.Lock()
        return lock_pool[the_key]
