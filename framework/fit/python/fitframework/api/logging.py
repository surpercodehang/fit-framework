# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：日志模块
"""
import logging
from functools import wraps
from logging import Logger, StreamHandler, FileHandler
from typing import Union, Tuple

from fitframework import const


def fetch_logging_filename():  # run once? what if dynamic
    for hdlr in _root.handlers:
        full_filename = getattr(hdlr, 'baseFilename', None)
        if full_filename is not None:
            return full_filename
    raise Exception("cannot fetch logging file.")


def _redirect(log_func):
    """
    日志输出重定向装饰器。
    适用于某些详细信息不适合输出到屏幕，但默认配置不符合该需求时，使用该装饰器临时对输出进行重定向

    :param log_func: logging模块标准打印函数
    :return:
    """

    @wraps(log_func)
    def wrapper(*args, dests=None, **kwargs):
        stacklevel = kwargs.pop('stacklevel', 1) + 1
        from fitframework.utils.context import runtime_context
        if runtime_context.get_item(const.DEBUGGER_CONSOLE_ACTIVE):
            dests = FileHandler
        if dests is None:
            log_func(*args, stacklevel=stacklevel, **kwargs)  # 此处非实际log调用位置，而是装饰后的位置，故使用stacklevel关键字进行复原
            return
        if not isinstance(dests, tuple):
            dests = tuple((dests,))
        hdlrs_removed = _filter_root_hdlrs(dests)
        log_func(*args, stacklevel=stacklevel, **kwargs)
        _recover_root_hdlrs(hdlrs_removed)

    return wrapper


def _filter_root_hdlrs(hdlr_types: Tuple[Union[StreamHandler, FileHandler], ...]):
    hdlrs_to_remove = []
    for hdlr in _root.handlers:
        if type(hdlr) not in hdlr_types:
            hdlrs_to_remove.append(hdlr)
    for hdlr in hdlrs_to_remove:
        _root.removeHandler(hdlr)
    return hdlrs_to_remove


def _recover_root_hdlrs(hdlrs_removed):
    for hdlr in hdlrs_removed:
        _root.addHandler(hdlr)


_root = logging.getLogger()
fit_logger = logging.getLogger('fit')
bootstrap_logger = logging.getLogger('bootstrap')
sys_plugin_logger = logging.getLogger('system_plugin')
plugin_logger = logging.getLogger('user_plugin')

for f in (Logger.debug, Logger.info, Logger.warning, Logger.error, Logger.exception):
    setattr(Logger, f.__name__, _redirect(f))
