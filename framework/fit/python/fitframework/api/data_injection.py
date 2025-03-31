# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：大数据缓存接口对外提供的装饰器
"""
from functools import wraps
from typing import Callable, Optional

from fit_common_struct.core import Address
from fitframework.api.decorators import local_context, fit
from fitframework.api.logging import fit_logger
from fitframework.utils.json_path_utlis import convert_field_by_json_path


@local_context('worker.id')
def _worker_id():
    pass


# 缓存本pod的名称
_this_pod = None


def _get_this_pod() -> str:
    # 获取本pod的名称
    global _this_pod
    if not _this_pod:
        # 预期的worker_id格式为POD_NAME@WORKER_NAME，用以区分异同POD情况
        _this_pod = _worker_id().split("@", 1)[0]
    return _this_pod


def _address_filter_by_current_worker_id(endpoint: Address):
    return endpoint.id == _worker_id()


@fit("modelengine.fit.bigdata.cache.is_databus", address_filter=_address_filter_by_current_worker_id)
def is_backend_databus() -> bool:
    # 获取是否cache使用的是databus实现
    pass


class CacheValueMetadata:
    def __init__(self, type: str, length: int):
        self.type = type
        self.length = length


def _validate_content_type(content) -> None:
    if not isinstance(content, bytes) and not isinstance(content, str):
        raise Exception(f"only content of the bytes or string type can be converted.")


def _assemble_global_index(index: str, worker_id: str, content_type: str):
    if content_type == "str":
        return f"{index}@{worker_id}"
    else:
        return f"{index}@{worker_id}".encode("utf-8")


def _disassemble_global_index(global_index) -> (str, str):
    if isinstance(global_index, str):
        global_index_as_str = global_index
    else:
        global_index_as_str = global_index.decode("utf-8")
    index, worker_id = global_index_as_str.split("@", 1)
    return index, worker_id


def _get_cached_value_by_global_index(index: str, worker_id: str, content_type: str):
    def address_filter_remote(endpoint: Address):
        return endpoint.id == worker_id

    # 默认去远端取信息
    cache_address_filter = address_filter_remote
    # 同Pod的DataBus的Cache fitables应当使用本地的就行, 异Pod的还是要去远端拿的
    if is_backend_databus() and _get_this_pod() == worker_id.split("@", 1)[0]:
        cache_address_filter = _address_filter_by_current_worker_id

    @fit("modelengine.fit.bigdata.cache.read.meta", address_filter=cache_address_filter)
    def read_meta_from_cache(_: str) -> CacheValueMetadata:
        pass

    @fit("modelengine.fit.bigdata.cache.read.meta", address_filter=address_filter_remote)
    def read_meta_from_remote_cache(_: str) -> CacheValueMetadata:
        pass

    meta: Optional[CacheValueMetadata] = None
    is_local_cache = True
    try:
        meta = read_meta_from_cache(index)
    except Exception:
        meta = read_meta_from_remote_cache(index)
        is_local_cache = False
    if meta.type is None or not meta.type or meta.type == "unknown":
        meta = read_meta_from_remote_cache(index)
        is_local_cache = False
        fit_logger.info("Read meta type from remote, result is [%s]", meta.type)
        if meta.type is None:
            raise Exception(f"cannot read meta from cache. [worker_id={worker_id}, index={index}]")
    if meta.type != content_type:
        raise Exception(f"type of data is incorrect. " +
                        f"[worker_id={worker_id}, index={index}, data_type={meta.type}, content_type={content_type}]")
    if worker_id != _worker_id():
        @fit("modelengine.fit.bigdata.cache.validate.capacity", address_filter=_address_filter_by_current_worker_id)
        def validate_capacity(to_allocate_memory: int) -> None:
            pass

        validate_capacity(meta.length)
    is_local_cache, result = _cache_read(index, meta, cache_address_filter, address_filter_remote, is_local_cache)

    _cache_delete(index, cache_address_filter if is_local_cache else address_filter_remote)
    return result


def _cache_read(index, meta, address_filter, fallback_address_filter, is_readable_locally):
    result = None
    try:
        if is_readable_locally:
            result = _cache_fitable_read(address_filter, index, meta)
    except Exception:
        is_readable_locally = False

    if not is_readable_locally:
        fit_logger.debug("Read content from remote.")
        result = _cache_fitable_read(fallback_address_filter, index, meta)
    fit_logger.info(f"Read content [index={index}], read type {meta.type} from local {is_readable_locally}.")
    return is_readable_locally, result


def _cache_fitable_read(address_filter, index, meta):
    if meta.type == "str":
        @fit("modelengine.fit.bigdata.cache.read.str", address_filter=address_filter)
        def read_from_local_cache(_index: str) -> str:
            pass
    else:
        @fit("modelengine.fit.bigdata.cache.read.bytes", address_filter=address_filter)
        def read_from_local_cache(_index: str) -> bytes:
            pass
    return read_from_local_cache(index)


def _cache_delete(index, address_filter):
    @fit("modelengine.fit.bigdata.cache.delete", address_filter=address_filter)
    def delete_value_from_cache(_index: str) -> None:
        pass

    delete_value_from_cache(index)


def _replace_global_index_with_content(global_index):
    """
    将表示全局索引的值替换为缓存中 str 或 bytes 类型的内容
    :param global_index: 表示全局索引
    :return: 表示该索引值所对应的缓存中内容
    """
    if global_index is None:
        return None
    _validate_content_type(global_index)

    index, worker_id = _disassemble_global_index(global_index)
    content_type = "bytes" if isinstance(global_index, bytes) else "str"
    result = _get_cached_value_by_global_index(index, worker_id, content_type)
    if result is None:
        raise Exception(f"cannot get value from cache. [index={index}, worker_id={worker_id}]")
    return result


def _replace_content_with_global_index(content):
    """
    将 str 或 bytes 类型的值保存到缓存并替换为索引
    :param content: 表示待替换的 str 或 bytes 类型的内容
    :return: 表示该值在缓存中的索引
    """
    if content is None:
        return None
    _validate_content_type(content)

    def address_filter_by_current_worker_id(endpoint: Address):
        return endpoint.id == _worker_id()

    if isinstance(content, str):
        @fit("modelengine.fit.bigdata.cache.create.str", address_filter=address_filter_by_current_worker_id)
        def create_str_in_cache(_: str) -> str:
            pass

        return _assemble_global_index(create_str_in_cache(content), _worker_id(), "str")
    elif isinstance(content, bytes):
        @fit("modelengine.fit.bigdata.cache.create.bytes", address_filter=address_filter_by_current_worker_id)
        def create_bytes_in_cache(_: bytes) -> str:
            pass

        return _assemble_global_index(create_bytes_in_cache(content), _worker_id(), "bytes")


def _convert_arguments_with_json_paths_and_method(args, arguments_json_path, method):
    converted_args = []
    index = 0
    for arg in args:
        if index not in arguments_json_path:
            converted_args.append(arg)
            continue
        json_paths = arguments_json_path[index] if isinstance(arguments_json_path[index], list) else [
            arguments_json_path[index]]
        converted_arg = arg
        for json_path in json_paths:
            converted_arg = convert_field_by_json_path(converted_arg, json_path, method)
        converted_args.append(converted_arg)
        index += 1
    return converted_args


def data_injection(arguments_json_path: dict, return_json_paths) -> Callable:
    if arguments_json_path is not None and not isinstance(arguments_json_path, dict):
        raise Exception(f"type of parameters_json_path must be a dict.")

    def decorator(func):
        @wraps(func)
        def wrapper(*args):
            fit_logger.debug(f"data_injection decorator called with {args}")
            if arguments_json_path is not None:
                converted_args = _convert_arguments_with_json_paths_and_method(args, arguments_json_path,
                                                                               _replace_global_index_with_content)
            else:
                converted_args = args
            result = func(*converted_args)
            if return_json_paths is not None:
                json_paths = return_json_paths if isinstance(return_json_paths, list) else [return_json_paths]
                for json_path in json_paths:
                    result = convert_field_by_json_path(result, json_path, _replace_content_with_global_index)
            return result

        return wrapper

    return decorator
