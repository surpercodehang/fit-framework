# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：动态插件加载相关工具。
"""
import tarfile
import zipfile
from typing import List


class FileNameException(Exception):
    def __init__(self, file_name: str, message: str):
        self.file_name: str = file_name
        self.message: str = message


def _get_tar_file_and_dir_names(tar_file_path) -> List[str]:
    filenames = []
    with tarfile.open(tar_file_path, 'r') as tar:
        for member in tar.getmembers():
            parts = member.name.split('/')
            if len(parts[-1]) == 0:
                parts.pop(-1)
            if len(parts) >= 1:
                filenames.append(parts[-1])
    return filenames


def _get_zip_file_and_dir_names(zip_file_path) -> List[str]:
    filenames = []
    with zipfile.ZipFile(zip_file_path, 'r') as zip_file:
        for member in zip_file.infolist():
            parts = member.filename.split('/')
            if len(parts[-1]) == 0:
                parts.pop(-1)
            if len(parts) >= 1:
                filenames.append(parts[-1])
    return filenames


def _get_zipped_file_and_dir_names(file_path: str) -> List[str]:
    if file_path.endswith("zip"):
        return _get_zip_file_and_dir_names(file_path)
    else:
        return _get_tar_file_and_dir_names(file_path)


def _validate_by_black_list(file_name: str) -> None:
    if file_name.find("..") != -1:
        raise FileNameException(file_name, f"The file name contains illegal character \"..\". [file_name={file_name}]]")


def validate_zipped_file_and_dir_names(file_path: str) -> None:
    """
    校验某个压缩文件中的所有子文件名是否符合规则。
    @param file_path: 压缩文件路径。
    """
    file_and_dir_names = _get_zipped_file_and_dir_names(file_path)
    for file_and_dir_name in file_and_dir_names:
        _validate_by_black_list(file_and_dir_name)
