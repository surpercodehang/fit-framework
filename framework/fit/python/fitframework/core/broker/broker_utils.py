# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Broker工具类
"""
import sys
import traceback
from enum import Enum, auto
from typing import List, Tuple, Optional

from fitframework.api.exception import FitBaseException
from fitframework.api.logging import fit_logger
from fitframework.core.exception.fit_exception import FitException, InternalErrorCode, \
    NetworkException
from fitframework.core.network.enums import ProtocolEnum, SerializingStructureEnum

_NOT_IN_PRIORITY_LIST_INDEX = 100


class IdType(Enum):
    id = auto(),
    alias = auto(),
    rule = auto()


class GenericableTagEnum(Enum):
    """
    Genericable相关fit python框架使用tags;
    Fit元数据中的tag管理。用于识别框架中用到的tag
    """
    LOCAL_ONLY = 'localOnly'
    INVOKE_ALL = 'invokeAll'
    RECYCLE = 'recycle'

    @classmethod
    def is_local_only(cls, tag: str) -> bool:
        return cls.LOCAL_ONLY.value == tag

    @classmethod
    def is_invoke_all(cls, tag: str) -> bool:
        return cls.INVOKE_ALL.value == tag


class FitableTagEnum(Enum):
    """ Fitable相关python框架使用tag """
    PRIMARY = 'primary'
    PROTOCOL = 'protocol'

    @classmethod
    def extract_protocol_id(cls, tag: str) -> int:
        if not tag.startswith(cls.PROTOCOL.value + ":"):
            return -1

        return tag[len(cls.PROTOCOL.value) + 1:]


class FitableIdentifier:
    def __init__(self, value, id_type: IdType):
        self.value = value
        self.id_type = id_type

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__


class Singleton(type):
    """
    metaclass implements singleton pattern. Not thread-safe
    """
    _instances = {}

    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args, **kwargs)
        return cls._instances[cls]


def retryable(retry_times, trigger=NetworkException):
    def decorator(_execute):
        def wrapper(*args, **kws):
            nonlocal retry_times
            while True:
                try:
                    return _execute(*args, **kws)
                except trigger:
                    if retry_times:
                        retry_times -= 1
                    else:
                        raise

        return wrapper

    return decorator


def exception_handler(_execute):
    def wrapper(*args, **kws):
        try:
            return _execute(*args, **kws)
        except FitBaseException as err:
            # 业务异常，正常交付上游业务代码处理，或转入可能的降级链路（此处无感知）
            # 框架异常一般不会在此出现，如果出现，则应认定其为无法处理的异常，直接往顶层抛出
            fit_logger.warning(f"fit invoke error with fitable info ({args[1]}:{args[2]}): {err}")
            raise
        except Exception as err:
            # 此处Exception为原生的未封装的异常，比如python的系统异常、java的运行异常等等
            # 一般不会出现此情形；如果发生，则应认定为用户预期外的异常
            # 可能是用户本身的代码错误导致；也可能是用户未按规范来封装为FitBaseException；在此由框架负责封装
            except_type, except_value, except_traceback = sys.exc_info()
            fit_logger.warning(f"user exception type: {except_type}")
            fit_logger.warning(f"user exception value: {except_value}")
            fit_logger.warning(f"user exception trace back:\n{''.join(traceback.format_tb(except_traceback))}")
            raise FitException(InternalErrorCode.EXCEPTION_FROM_USER_CODE_OCCURRED.value,
                               str(err), degradable=False) from None

    return wrapper


def degradable(_execute):
    def wrapper(broker, generic_id, fitable_id, fit_ref, *fit_args, **fit_kws):
        cur_fitable_id = fitable_id
        while True:
            try:
                return _execute(broker, generic_id, cur_fitable_id, fit_ref, *fit_args, **fit_kws)
            except FitBaseException as err:
                can_degrade, degrade_id = _can_degrade(broker, generic_id, cur_fitable_id, err)
                if can_degrade:
                    fit_logger.warning(f"fit invoke with fitable info "
                                       f"({generic_id}:{cur_fitable_id}) degrade to {degrade_id}")
                    cur_fitable_id = degrade_id
                else:
                    raise

    def _can_degrade(broker, generic_id, cur_fitable_id, err) -> Tuple[bool, Optional[str]]:
        if not err.degradable:  # 可降级flag为False时直接退出
            fit_logger.error(f"non-degradable fit exception found: {err}")
            return False, None
        degrade_id = broker.get_degradation(generic_id, cur_fitable_id)
        if degrade_id is None:
            fit_logger.error(f"degradation fitable for current fit exception not found: {err}")
            return False, None
        _detect_cycle_degradation(cur_fitable_id, degrade_id, err)
        return True, degrade_id

    _executed_fitables_cache = {}

    def _detect_cycle_degradation(cur_fitable_id, degrade_id, err):
        if degrade_id in _executed_fitables_cache:
            fit_logger.error('cycling degrading found, may be the user has set '
                             'a wrong configuration in degradation strategy.')
            first_err = _executed_fitables_cache[degrade_id]
            first_err.degradable = False
            raise first_err
        _executed_fitables_cache[cur_fitable_id] = err

    return wrapper


def get_priority_index(protocol_id, _formats: List[str], priorities: List[str]):
    indexes = []
    protocol_name = ProtocolEnum(protocol_id).name
    for _format in _formats:
        format_name = SerializingStructureEnum(_format).name
        protocol_and_format_id = f'{protocol_name}:{format_name}'
        if protocol_and_format_id in priorities:
            indexes.append(priorities.index(protocol_and_format_id))
        else:
            indexes.append(_NOT_IN_PRIORITY_LIST_INDEX)
    return min(indexes)
