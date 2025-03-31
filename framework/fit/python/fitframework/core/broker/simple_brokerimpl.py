# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：用于启动插件中fitable调用代理
"""
from typing import Any, List

from fitframework.api.logging import fit_logger
from fitframework.core.broker.broker import BrokerTemplate
from fitframework.core.broker.broker_utils import Singleton


class SimpleBroker(BrokerTemplate, metaclass=Singleton):
    """
    从/bootstrap目录加载的所有plugin中的fitable，对它们调用需要对一般FFP模板做裁剪。只保留三项功能：
    本地路由，本地负载均衡，本地调用。
    """

    def routing(self, generic_id, *_, route_filter=None) -> List[str]:
        from fitframework.core.repo import service_repo
        uuids = service_repo.get_glued_fitable_ids(generic_id)
        # generic_id对应的fitable有且仅有一个
        if not uuids or len(set(uuids)) != 1:
            return []
        if len(uuids) != 1:
            fit_logger.warning("some bootstrap plugins are loaded more than once.")
            del uuids[1:]
        return uuids

    def load_balancing(self, generic_id, fitable_id, address_filter=None):
        from fitframework.core.repo import service_repo
        return service_repo.get_fitable_ref(generic_id, fitable_id)

    def fit_execute(self, address, generic_id, fitable_id, fit_ref, *args, timeout=None, is_async=None) -> Any:
        return address(*args)
