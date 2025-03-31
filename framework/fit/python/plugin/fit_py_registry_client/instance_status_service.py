# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：实例是否有效状态相关功能。
"""
from fit_common_struct.core import Address
from fitframework import fitable, const
from fitframework.api.decorators import private_fitable, scheduled_executor, value
from fitframework.utils.scheduler import TimerDict


@value('registry-center.client.invalid_address_ttl', 60, converter=int)
def _get_invalid_address_ttl():
    pass


@value("registry-center.client.expired_invalid_address_clear_frequency", default_value=60 * 60 * 24, converter=int)
def _get_clear_expired_invalid_address_frequency():
    pass


_INVALID_ADDRESS_CACHE = TimerDict(lambda address: hash(tuple([address.host, address.port, address.protocol])),
                                   _get_invalid_address_ttl())


@fitable(const.MARK_ADDRESS_STATUS_GEN_ID, const.MARK_ADDRESS_STATUS_FITABLE_ID)
def set_fitable_instance_status(address: Address, valid: bool) -> None:
    """
    设定实例状态。

    @param address: 实例地址。
    @param valid: 实例状态。
    """
    if not valid:
        _INVALID_ADDRESS_CACHE.put(address)
    else:
        _INVALID_ADDRESS_CACHE.remove(address)


@private_fitable
def get_fitable_instance_status(address: Address) -> bool:
    """
    获取实例状态。

    @param address: 实例地址。
    @return: 实例状态。
    """
    return address not in _INVALID_ADDRESS_CACHE


@scheduled_executor(_get_clear_expired_invalid_address_frequency())
def _remove_invalid_cache():
    """
    定期清理失效的缓存。
    """
    _INVALID_ADDRESS_CACHE.clear_expired_cache()
