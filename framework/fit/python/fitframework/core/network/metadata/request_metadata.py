# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit通讯包头RequestMetadata结构体和解析
RequestMetadata:
version             short
data_format         byte
generic_version.major     byte
generic_version.minor     byte
generic_version.revision  byte
generic_id          string(hex)
fit_id length       short
fit_id              string
tags:
   tag.key          int
   tag.value length int
   tag.value        bytes （目前该value仅支持字符串数据的表达）
"""

CURRENT_VERSION = 2

# `METADATA_TAG`和`DATA_TAG`用于统一client和server两端的http json body的key使其一致
HTTP_METADATA_TAG = 'fit-metadata'
HTTP_DATA_TAG = 'data'

GRPC_METADATA_TAG = 'fit-metadata-bin'


class GenericVersion(object):
    def __init__(self, major: int, minor: int, revision: int):
        self.major = major
        self.minor = minor
        self.revision = revision

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __repr__(self):
        return str(tuple(self.__dict__.values()))

    @classmethod
    def from_string(cls, value: str):
        split_value = value.split(".")
        return GenericVersion(int(split_value[0]), int(split_value[1]), int(split_value[2]))

    def serialize(self) -> bytes:
        return bytes([self.major, self.minor, self.revision])


class RequestMetadata(object):
    def __init__(self, data_format: int, generic_version: GenericVersion, generic_id: str, fitable_id: str,
                 tlv_data=None):
        self.data_format = data_format
        self.generic_version = generic_version
        self.generic_id = generic_id
        self.fitable_id = fitable_id
        self.tlv_data = tlv_data if tlv_data is not None else {}

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __repr__(self):
        return str(tuple(self.__dict__.values()))

    @classmethod
    def default(cls, generic_id: str, fitable_id: str, data_format: int):
        return RequestMetadata(data_format, GenericVersion(1, 0, 0),
                               generic_id, fitable_id, {})

    def upset_a_tag(self, key: str, value: bytes):
        self.tlv_data[key] = value
