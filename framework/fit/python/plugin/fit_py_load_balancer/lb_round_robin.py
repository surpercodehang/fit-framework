# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：提供轮询load balance服务
"""
import secrets
from typing import List

from fit_common_struct.core import Address, Fitable
from fitframework import const
from fitframework.api.decorators import fitable

_fitable_pos_dict = dict()


@fitable(const.LOAD_BALANCING_GEN_ID, const.LOAD_BALANCING_ROUND_ROBIN_FIT_ID)
def load_balance(fitable_: Fitable, target_addresses: List[Address]) -> Address:
    if len(target_addresses) == 0:
        return None
    size = len(target_addresses)

    if fitable_ not in _fitable_pos_dict:
        _fitable_pos_dict[fitable_] = secrets.randbelow(size)
    else:
        _fitable_pos_dict[fitable_] = (_fitable_pos_dict[fitable_] + 1) % size
    return target_addresses[_fitable_pos_dict[fitable_]]
