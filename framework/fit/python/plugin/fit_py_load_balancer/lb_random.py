# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：提供随机load balance服务
"""

import random
from typing import List

from fit_common_struct.core import Address, Fitable
from fitframework import const
from fitframework.api.decorators import fitable


@fitable(const.LOAD_BALANCING_GEN_ID, const.LOAD_BALANCING_RANDOM_FIT_ID)
def load_balance(fitable_info: Fitable,
                 target_addresses: List[Address]) -> Address:
    # 当前为同权重进行随机，后续增加按照target_endpoints的权重进行随机路由路由功能
    if not target_addresses:
        return None
    return random.choice(target_addresses)
