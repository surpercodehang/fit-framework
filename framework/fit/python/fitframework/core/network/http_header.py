# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：HTTP 请求头与响应头相关工具。
"""
from enum import Enum


class HttpHeader(Enum):
    FORMAT = "FIT-Data-Format"
    GENERICABLE_VERSION = "FIT-Genericable-Version"
    DEGRADABLE = "FIT-Degradable"
    CODE = "FIT-Code"
    MESSAGE = "FIT-Message"
    TLV = "FIT-TLV"
