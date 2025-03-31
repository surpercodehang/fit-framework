# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit调用代理选择路由
"""
from typing import Dict

from numpy import int32

from fitframework import const
from fitframework.api.decorators import fit, private_fit
from fitframework.core.broker.trace import TraceInvoke
from fitframework.core.exception.fit_exception import FitException, InternalErrorCode
from fitframework.api.enums import FrameworkSubState
from fitframework.core.repo import service_repo


@fit(const.RUNTIME_GET_SUB_STATUS_GEN_ID)
def get_runtime_sub_state() -> int32:
    pass


@fit(const.CONFIGURATION_AGENT_DOWNLOAD_GEN_ID, 'py_impl')
def download_configuration(path: str) -> Dict[str, str]:
    pass


@private_fit
def get_configuration_item(conf_node_path: str, conf_sub_node_path: str):
    pass


class BrokerBuilder:
    """
    用于构建broker，以及相关的参数
    """

    def __init__(self, broker, genericable_id):
        self.broker = broker
        self._route_filter = None
        self._address_filter = None
        self._from_remote = False
        self._hop_id = None
        self._trace_id = None
        self._span_id = None
        self._from_fit_id = None
        self._genericable_id = genericable_id
        self._fitable_identifier = None
        self._fit_ref = None
        self._time_out = None
        self._is_async = None
        self._retry = None

    def route_filter(self, filter_func):
        """
        Args:
            filter_func: 用户自定义的方法过滤器，用于低阶调用
        Returns:
        """
        self._route_filter = filter_func
        return self

    def address_filter(self, filter_func):
        """
        Args:
            filter_func: 用户自定义地址过滤器，用于低阶调用
        Returns:
        """
        self._address_filter = filter_func
        return self

    def from_remote(self):
        self._from_remote = True
        return self

    def trace_id(self, trace_id):
        self._trace_id = trace_id
        return self

    def span_id(self, span_id):
        self._span_id = span_id
        return self

    def from_fit_id(self, from_fit_id):
        self._from_fit_id = from_fit_id
        return self

    def trace_hop_id(self, hop_id):
        self._hop_id = hop_id
        return self

    def fitable_identifier(self, fitable_identifier):
        self._fitable_identifier = fitable_identifier
        return self

    def fit_ref(self, fit_ref):
        self._fit_ref = fit_ref
        return self

    def timeout(self, timeout):
        self._time_out = timeout
        return self

    def is_async(self, is_async: bool):
        self._is_async = is_async
        return self

    def retry(self, retry):
        self._retry = retry
        return self

    def fit_selector_invoke(self, *args):
        fit_ref = self._fit_ref if self._fit_ref else self._get_fit_ref()
        with TraceInvoke(self._genericable_id, self._from_remote, self._hop_id, self._trace_id,
                         self._span_id, self._from_fit_id):
            return self.broker.fit_ffp_invoke(self._genericable_id, self._fitable_identifier, fit_ref,
                                              *args, timeout=self._time_out, is_async=self._is_async, retry=self._retry,
                                              route_filter=self._route_filter, address_filter=self._address_filter)

    def _get_fit_ref(self):
        fit_ref = service_repo.query_fit_or_fitable_ref(self._genericable_id)
        if not fit_ref:
            raise FitException(InternalErrorCode.GENERICABLE_NOT_FOUND,
                               f"genericable not found for {self._genericable_id}")
        return fit_ref


def select(generic_id: str):
    from fitframework.core.broker.simple_brokerimpl import SimpleBroker
    from fitframework.core.broker.configure_based_brokerimpl import ConfigureBasedBroker

    if service_repo.is_glued(generic_id):
        return BrokerBuilder(SimpleBroker(), generic_id)
    else:
        return BrokerBuilder(ConfigureBasedBroker(), generic_id)


def _is_dynamic_configuration_required(generic_id) -> bool:
    """
    决定是否需要动态下载，即运行期来下载配置
    正常情况下，引擎启动完毕即会下载好所有配置；但对于用户的低阶调用，由于无法获取
        genericable id相关信息，所以必须放到运行期来下载获取相关配置信息。
    注：以下情形不在动态下载的范围：
        - 服务器插件尚未启动，无法发起远程调用

    Args:
        generic_id: 服务id
    Returns: 是否需要动态下载配置
    """
    return get_configuration_item(const.GEN_CONF_NODE_KEY_PREFIX + generic_id, None) is None \
        and _after_server_started()


def _after_server_started():
    return get_runtime_sub_state() in (
        FrameworkSubState.SERVER_STARTED, FrameworkSubState.CONFIGURATION_SUBSCRIBED,
        FrameworkSubState.FITABLE_REGISTERED, FrameworkSubState.HEART_BEAT_STARTED,
        FrameworkSubState.RUNNING, FrameworkSubState.SHUTDOWN)
