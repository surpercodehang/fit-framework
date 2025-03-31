# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Trace相关功能支持
"""
import secrets
import uuid
from datetime import datetime, timezone

from fit_common_struct.core import FitableTrace, BaseFitTrace, Address
from fitframework import const
from fitframework.api.decorators import fit, local_context
from fitframework.api.logging import fit_logger
from fitframework.core.broker.trace_log_utils import TrustStage, Stage, FlowType, TraceType, \
    CallType
from fitframework.core.network import fit_response
from fitframework.core.repo.repo_utils import inspect_plugin_name_by_func
from fitframework.utils.context import call_context

_FIT_META_GEN_TAGS_TRACEABLE_VALUE = 'nonTraceable'
_SAMPLE_BOUND = 10000


@local_context("local_ip")
def local_ip():
    pass


@fit(const.SERVICE_DB_GET_GENERICABLE_TAGS_GEN_ID)
def get_genericable_tags(generic_id: str) -> None:
    pass


@fit(const.RUNTIME_TRACE_LOGGER_GEN_ID)
def add_fitable_trace(fitable_trace: FitableTrace) -> None:
    pass


@fit(const.RUNTIME_GET_WORKER_ID_GEN_ID)
def get_runtime_worker_id() -> str:
    pass


class FitableContext:
    """
    维护Fitable调用上下文相关信息，被TraceInvoke消费，用于构造FitableTrace结构体，进行tracing日志输出。
    """

    def __init__(self, span_id: str, hop_id: int, from_remote: bool, from_fit_id: str):
        self.span_id = span_id
        self.hop_id = hop_id
        self.from_remote = from_remote
        self.from_fit_id = from_fit_id
        self.fit_id = None

    def next_hop(self, hop_id_override):
        hop_part = str(hop_id_override) if hop_id_override else f".{self.hop_id}"
        return self.span_id + hop_part

    def move_forward(self, hop_id_override):
        if not hop_id_override:
            self.hop_id += 1

    def trust_stage(self):
        parts = self.span_id.split("_")
        if parts[-1].isalpha():
            return TrustStage[parts[-1].upper()].value
        else:
            return TrustStage.PROCESS.value


class TracePrint:
    """
    打印上下文，根据FitableContext上下文相关信息进行tracing打印。
    """

    def __init__(self, address: Address, generic_id, fitable_id, fit_ref):
        self._result_code = None
        self._ignore_trace = _ignore_trace(generic_id)
        if not self._ignore_trace:
            _fitable_id_into_trace(fitable_id)
            self._fitable_trace = TracePrint._init_fitable(address, generic_id, fitable_id, fit_ref)

    def __enter__(self):
        if self._ignore_trace:
            return
        self._fitable_trace.baseFitTrace.traceTimestamp = TracePrint.current_time_ms()
        add_fitable_trace(self._fitable_trace)

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self._ignore_trace:
            return
        start_time = self._fitable_trace.baseFitTrace.traceTimestamp
        end_time = TracePrint.current_time_ms()
        self._fitable_trace.baseFitTrace.traceTimestamp = end_time
        self._fitable_trace.baseFitTrace.timeCost = end_time - start_time
        self._fitable_trace.baseFitTrace.stage = Stage.OUT.value
        self._result_code = fit_response.get_error_code_by_exc_val(exc_val)
        add_fitable_trace(self._fitable_trace)

    @staticmethod
    def current_time_ms():
        return int(datetime.now(tz=timezone.utc).timestamp() * 1000)

    @staticmethod
    def _init_fitable(address, generic_id, fitable_id, fit_ref):
        trace_stack = call_context.get_context_value(call_context.TRACING_STACK_PROP)
        if not trace_stack:
            fit_logger.warning(f"trace stack is empty when printing trace")
            return None

        fitable_context = trace_stack[-1]
        target_host, target_port = get_target_address(address)

        return FitableTrace(
            baseFitTrace=BaseFitTrace(
                traceTimestamp=0,
                traceId=call_context.get_context_value(call_context.GLOBAL_TRACING_ID_PROP),
                span=fitable_context.span_id,
                traceType=TraceType.RPC.value,
                host=local_ip(),
                port=call_context.get_and_del_context_value(call_context.GLOBAL_PORT_PROP),
                workerId=get_runtime_worker_id(),
                resultCode='',
                timeCost=0,
                flowType=FlowType.NORMAL.value,
                stage=Stage.IN.value,
            ),
            genericId=generic_id,
            genericVersion=const.FIXED_GENERICABLE_VERSION,
            fitId=fitable_id,
            serviceName=inspect_plugin_name_by_func(fit_ref),
            targetHost=target_host,
            targetPort=target_port,
            callType=_get_call_type(address, fitable_context.from_remote),
            fromFitId=fitable_context.from_fit_id,
            trustStage=fitable_context.trust_stage()
        )


class TraceInvoke:
    """
    在Fitable调用时进行tracing相关信息的维护。

    1. from_remote = True, span stack不变
    2. hop_id_override is not None, span stack push/pop hop_id_override
    3. not #1, #2, span stack push/pop thread_context.fitable_invoked_times+1
    """

    def __init__(self, generic_id: str, from_remote: bool, hop_id_override: str, trace_id: str,
                 span_id: str, from_fit_id: str):
        self._ignore_trace = _ignore_trace(generic_id)
        self._from_remote = from_remote
        self._hop_id_override = hop_id_override
        self._trace_id = trace_id
        self._span_id_override = span_id
        self._from_fit_id = from_fit_id

    def __enter__(self):
        if self._ignore_trace:
            return self

        trace_stack = self._get_or_init_trace()
        if trace_stack:
            last_stack_element = trace_stack[-1]
            trace_stack.append(
                FitableContext(last_stack_element.next_hop(self._hop_id_override), 1,
                               self._from_remote, last_stack_element.fit_id))
            last_stack_element.move_forward(self._hop_id_override)
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self._ignore_trace:
            return

        trace_stack = call_context.get_context_value(call_context.TRACING_STACK_PROP)
        if trace_stack is None:
            raise Exception("track stack cannot be none.")
        trace_stack.pop()
        if not trace_stack:
            _clear_trace()

    def _get_or_init_trace(self):
        trace_stack = call_context.get_context_value(call_context.TRACING_STACK_PROP)
        trace_id = call_context.get_context_value(call_context.GLOBAL_TRACING_ID_PROP)
        if trace_stack and trace_id:
            return trace_stack
        elif trace_stack or trace_id:
            fit_logger.warning(f"trace id is not sync with trace_stack. reset... ")
            _clear_trace()

        call_context.put_context_value(call_context.GLOBAL_TRACING_ID_PROP, self._init_trace_id())
        call_context.put_context_value(call_context.TRACING_STACK_PROP, self._init_fitable_context())
        return None

    def _init_trace_id(self):
        return self._trace_id if self._trace_id else _generate_trace_id()

    def _init_fitable_context(self):
        return [FitableContext(self._span_id_override if self._span_id_override else '1', 1,
                               self._from_remote, self._from_fit_id)]


def print_trace(broker_execute):
    def wrapper(*args, **kwargs):
        with TracePrint(address=args[1], generic_id=args[2],
                        fitable_id=args[3], fit_ref=args[4]):
            return broker_execute(*args, **kwargs)

    return wrapper


def _ignore_trace(generic_id):
    from fitframework.core.repo import service_repo
    return service_repo.is_glued(generic_id) or _in_trace_blacklist(generic_id)


def _in_trace_blacklist(generic_id):
    return _FIT_META_GEN_TAGS_TRACEABLE_VALUE in get_genericable_tags(generic_id)


def _get_call_type(address, from_remote: bool):
    if isinstance(address, Address):
        return CallType.LOCAL_TO_REMOTE.value
    elif from_remote:
        return CallType.REMOTE_TO_LOCAL.value
    else:
        return CallType.LOCAL_TO_LOCAL.value


def _generate_trace_id():
    return f"{str(uuid.uuid4()).replace('-', '')}{secrets.randbelow(_SAMPLE_BOUND):04d}"


def get_target_address(address_):
    return (address_.host, address_.port) if isinstance(address_, Address) else ('', '')


def _fitable_id_into_trace(fit_id):
    trace_stack = call_context.get_context_value(call_context.TRACING_STACK_PROP)
    last_stack_element = trace_stack[-1]
    last_stack_element.fit_id = fit_id


def _clear_trace():
    call_context.del_context_value(call_context.GLOBAL_TRACING_ID_PROP)
    call_context.del_context_value(call_context.TRACING_STACK_PROP)
