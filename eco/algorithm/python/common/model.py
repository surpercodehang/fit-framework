# -*- coding: utf-8 -*-
# Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
"""
since: 2023/11/02 22:00
"""

import base64
from typing import Dict

from dataclasses import dataclass


@dataclass
class Content:
    """待处理的Content对象"""

    def __init__(self, data, text, meta):
        self.data: bytes = data if isinstance(data, bytes) else base64.b64decode(data)  # 文件、音频、图片等进行编码传输
        self.text: str = text  # 字符串直接传输
        self.meta: Dict[str, str] = meta  # 待处理数据携带的元数据信息

    # a function that output this class to a string format in dev view
    def __repr__(self):
        return str(vars(self))

    def to_dict(self):
        return vars(self)


@dataclass
class FlowData:
    """http接口传入数据映射的python对象"""
    businessData: Dict[str, object]
    passData: Content
    contextData: Dict[str, str]

    def __init__(self, businessData=None, passData=None, contextData=None):
        if businessData is None:
            businessData = {}
        if passData is None:
            passData = {}
        if contextData is None:
            contextData = {}

        self.businessData: Dict[str, object] = businessData
        self.passData: Content = passData
        self.contextData: Dict[str, str] = contextData
