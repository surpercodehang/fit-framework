# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：
扫描所有的genericable信息（目前仅包括generic id），提供三种使用方式
    1.直接运行，打印输出（需指定fitframework为PYTHONPATH，同时--scan-dir指定要扫描的目录）
    2.shell script调用，打印输出，供其捕获（配置同上）
    3.调用`get_all_gen_id`函数返回（参数：`scan_dir_s`）
"""

import argparse
import glob
import os
import re
from itertools import chain
from typing import Iterator

_LOG_MSG_S = []


def get_all_gen_id(scan_dir_s) -> Iterator:
    """
    获得所有注册的genericable id
    Args:
        scan_dir_s:

    Returns:
        List[str]: genericable id列表
    """
    gid_s = iter([])
    for file_name in _get_all_src_filename_s(scan_dir_s):
        with open(file_name, encoding='utf-8') as f:
            gid_s = chain(gid_s, _resolve_generic_id_s(f.read()))
    if _LOG_MSG_S:
        with os.fdopen(os.open(f"{__file__}.error.log", os.O_WRONLY | os.O_CREAT | os.O_TRUNC, 0o666), 'w',
                       encoding='utf-8') as f:
            f.writelines(_LOG_MSG_S)  # 需要注意 os.fdopen 的正确使用
    return filter(len, set(gid_s))  # 过滤掉gid=""的情形


def _get_all_src_filename_s(scan_dir_s):
    all_src_filename_s = iter([])
    for dir_ in scan_dir_s:
        all_src_filename_s = chain(all_src_filename_s, glob.iglob(f'{dir_}/**/*.py', recursive=True))
    return all_src_filename_s


def _resolve_generic_id_s(content):
    return map(_resolve_generic_id, re.findall("@fit(?:able)?\((.+?)[,|\)]", content))


def _resolve_generic_id(gid_expr: str) -> str:
    try:
        gid_expr = gid_expr.replace('const.', '', 1)
        return eval(gid_expr)
    except Exception as e:
        prefix = 'generic_id='
        if gid_expr.startswith(prefix):
            return _resolve_generic_id(gid_expr[len(prefix):])
        elif gid_expr != "?:able":
            # 过滤掉自己匹配到自己这个文件的情况
            _handle_message(
                f"un-resolvable generic id expression found: {gid_expr}; with error: {e}")
        return ""


def _handle_message(msg):
    if __name__ == '__main__':
        _LOG_MSG_S.append(msg)
    else:
        from fitframework.api.logging import fit_logger
        fit_logger.warning(msg)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Scan all genericables and print, given some specified directories.')
    parser.add_argument('--scan-dir', type=str, nargs='+', help='an integer for the accumulator')
    args = parser.parse_args()
