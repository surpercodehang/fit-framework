# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：提供DataBus的空实现，避免环境上没有DataBus导致ImportError。
"""
try:
    from databus import DataBusClient, DataBusErrorCode, WriteRequest, ReadRequest
except Exception:
    from dataclasses import dataclass


    class DataBusErrorCode:
        None_ = 0
        KeyNotFound = 1


    @dataclass
    class WriteRequest:
        user_key: str
        contents: bytes
        offset: int = 0
        is_operating_user_data: bool = False
        user_data: bytes = b""


    @dataclass
    class ReadRequest:
        user_key: str
        offset: int = 0
        size: int = None
        is_operating_user_data: bool = False


    class DataBusClient:
        def __init__(self):
            raise NotImplementedError

        def open(self, core_host, core_port): pass

        def close(self): pass

        def get_meta_data(self, key: str): pass

        def shared_malloc(self, user_key: str, size: int): pass

        def shared_free(self, user_key: str): pass

        def read_once(self, request: ReadRequest): pass

        def write_once(self, request: WriteRequest): pass
