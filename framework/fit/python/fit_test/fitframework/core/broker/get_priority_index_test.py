# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：broker_utils.get_priority_index单元测试
"""
import unittest

from fit_common_struct.core import Address
from fitframework.core.broker.broker_utils import get_priority_index


class GetPriorityIndex(unittest.TestCase):
    def test_grpc_protobuf_priority(self):
        addresses = [
            Address(host='localhost', port=8889, protocol=3, formats=[0, 1], worker_id='localhost:8889',
                    environment='test', context_path=""),
            Address(host='localhost', port=8888, protocol=2, formats=[0, 1], worker_id='localhost:8888',
                    environment='test', context_path="")
        ]
        priority = ['GRPC:PROTOBUF']
        addresses.sort(
            key=lambda _address: get_priority_index(_address.protocol, _address.formats, priority))
        assert addresses[0].protocol == 3

        addresses[0].formats.sort(
            key=lambda _format: get_priority_index(addresses[0].protocol, [_format], priority))
        assert addresses[0].formats[0] == 0

    def test_grpc_json_priority(self):
        addresses = [
            Address(host='localhost', port=8889, protocol=3, formats=[0, 1], worker_id='localhost:8889',
                    environment='test', context_path=""),
            Address(host='localhost', port=8888, protocol=2, formats=[0, 1], worker_id='localhost:8888',
                    environment='test', context_path="")
        ]
        priority = ['GRPC:JSON']
        addresses.sort(
            key=lambda _address: get_priority_index(_address.protocol, _address.formats, priority))
        assert addresses[0].protocol == 3

        addresses[0].formats.sort(
            key=lambda _format: get_priority_index(addresses[0].protocol, [_format], priority))
        assert addresses[0].formats[0] == 1

    def test_http_json_priority(self):
        addresses = [
            Address(host='localhost', port=8889, protocol=3, formats=[0, 1], worker_id='localhost:8889',
                    environment='test', context_path=""),
            Address(host='localhost', port=8888, protocol=2, formats=[0, 1], worker_id='localhost:8888',
                    environment='test', context_path="")
        ]
        priority = ['HTTP:JSON']
        addresses.sort(
            key=lambda _address: get_priority_index(_address.protocol, _address.formats, priority))
        assert addresses[0].protocol == 2

        addresses[0].formats.sort(
            key=lambda _format: get_priority_index(addresses[0].protocol, [_format], priority))
        assert addresses[0].formats[0] == 1

    def test_http_protobuf_priority(self):
        addresses = [
            Address(host='localhost', port=8889, protocol=3, formats=[0, 1], worker_id='localhost:8889',
                    environment='test', context_path=""),
            Address(host='localhost', port=8888, protocol=2, formats=[0, 1], worker_id='localhost:8888',
                    environment='test', context_path="")
        ]
        priority = ['HTTP:PROTOBUF']
        addresses.sort(
            key=lambda _address: get_priority_index(_address.protocol, _address.formats, priority))
        assert addresses[0].protocol == 2

        addresses[0].formats.sort(
            key=lambda _format: get_priority_index(addresses[0].protocol, [_format], priority))
        assert addresses[0].formats[0] == 0


if __name__ == '__main__':
    unittest.main()
