# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：输出fit调用链的trace日志

格式说明：traceTimestamp|traceId|span|traceType|host|port|workerId|resultCode|timeCost|flowType|stage
|||genericId|genericVersion|fitId|serviceName|targetHost|targetPort|callType|fromFitId|trustStage
"""
import datetime
from typing import List

from fit_common_struct.core import FitableTrace
from fitframework.api.logging import bootstrap_logger
from fitframework import const
from fitframework.api.decorators import fitable

_LOG_INFO_DELIMITER = ' | '
_RESERVED_SEGMENT = ''

_DATE_FORMAT = "%Y-%m-%d %H:%M:%S,%f"


@fitable(const.RUNTIME_TRACE_LOGGER_GEN_ID, const.RUNTIME_TRACE_LOGGER_FIT_ID)
def add_fitable_trace(fitable_trace: FitableTrace):
    """
    trace日志打印接口
    Args:
        fitable_trace: FitableTrace日志信息结构体

    Returns:None

    """
    bootstrap_logger.debug(
        _LOG_INFO_DELIMITER.join(
            map(lambda x: x if x and x != 'None' else '', _get_log_segments(fitable_trace))))


def _get_log_segments(fitable_trace) -> List[str]:
    """
    获取trace全部属性信息列表
    Args:
        fitable_trace: FitableTrace日志信息结构体

    Returns: trace全部属性信息列表

    """
    basic_logs = _get_basic_log_segments(fitable_trace)
    extended_logs = _get_extended_log_segments(fitable_trace)
    return basic_logs + [_RESERVED_SEGMENT] * 3 + extended_logs


def _get_extended_log_segments(fitable_trace: FitableTrace) -> List[str]:
    """
    获取扩展信息日志列表
    Args:
        fitable_trace: FitableTrace日志信息结构体

    Returns: 扩展信息日志列表

    """
    trace_log_list = [
        fitable_trace.genericId, fitable_trace.genericVersion, fitable_trace.fitId, fitable_trace.fromFitId,
        str(fitable_trace.callType), fitable_trace.serviceName, fitable_trace.targetHost, str(fitable_trace.targetPort),
        str(fitable_trace.trustStage)
    ]
    return trace_log_list


def _get_basic_log_segments(fitable_trace) -> List[str]:
    """
    获取基础信息日志列表
    Args:
        fitable_trace: FitableTrace日志信息结构体

    Returns: 基础信息日志列表

    """
    base_info = fitable_trace.baseFitTrace
    base_log_list = [
        _timestamp_to_data_fmt(base_info.traceTimestamp, _DATE_FORMAT), base_info.traceId, base_info.span,
        str(base_info.traceType), base_info.host, str(base_info.port), base_info.workerId,
        str(base_info.stage), base_info.resultCode, str(base_info.timeCost), str(base_info.flowType)
    ]
    return base_log_list


def _timestamp_to_data_fmt(trace_time_stamp: int, fmt: str) -> str:
    """

    Args:
        trace_time_stamp: 时间戳 1619582175977
        fmt: 日期格式 "%Y-%m-%d %H:%M:%S,%f"

    Returns: 格式化的日期格式

    """
    return datetime.datetime.fromtimestamp(trace_time_stamp / 1000).strftime(fmt)
