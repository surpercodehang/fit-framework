# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：load balance环境标filter服务
"""
from itertools import groupby
from typing import List

from fit_common_struct.core import Address
from fitframework import const
from fitframework.api.decorators import fitable, local_context, private_fit
from fitframework.api.logging import sys_plugin_logger


@local_context(key='worker-environment.env-seq')
def get_env_seq() -> str:
    """ a,b,c... """
    pass


@local_context(key="worker-environment.env")
def get_worker_env() -> str:
    """环境标"""
    pass


@private_fit
def get_fitable_instance_status(address: Address) -> bool:
    pass


def _filter_by_status(addresses: List[Address]) -> List[Address]:
    filtered_addresses = [address for address in addresses if get_fitable_instance_status(address)]
    if len(filtered_addresses) == 0:
        return addresses
    return filtered_addresses


@fitable(const.LOAD_BALANCING_FILTER_GEN_ID, const.LOAD_BALANCING_FILTER_FIT_ID)
def do_filter(addresses: List[Address]) -> List[Address]:
    addresses = _filter_by_status(addresses)
    if not get_env_seq():
        return addresses

    env_seq = get_env_seq().split(',')
    if get_worker_env() not in env_seq:
        return addresses

    worker_env = get_worker_env()

    addresses.sort(key=lambda _address: _address.environment)
    env_addresses = {env: list(val) for env, val in groupby(addresses, lambda address: address.environment)}
    for env in env_seq[env_seq.index(worker_env):]:
        if env in env_addresses:
            return env_addresses[env]
    sys_plugin_logger.warning(f"no env matched. [worker_env={worker_env}, env_seq={env_seq}, addresses={addresses}]")
    return None
