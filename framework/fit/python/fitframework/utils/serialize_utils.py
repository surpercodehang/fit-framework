# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：类型检验、参数类型获取、以及反序列化过程中通用类型转特定类型等相关的内部方法实现类
"""
import inspect
from itertools import repeat
from typing import _GenericAlias, List, Dict, Tuple, get_origin, get_args, TypeVar, Any, Union, Callable, get_type_hints
import numpy

from fitframework.api.logging import fit_logger
from fitframework.core.exception.fit_exception import FitException, InternalErrorCode
from fitframework.utils.tools import b64decode_from_str

_INT_TYPES = (int, numpy.int32, numpy.int64, numpy.uint32, numpy.uint64)
_FLOAT_TYPES = (float, numpy.float32, numpy.float64)
_COLLECTION_TYPES = (list, dict, tuple)
_PRIMITIVE_TYPES = _INT_TYPES + _FLOAT_TYPES + _COLLECTION_TYPES + (str, bool, bytes)
_COLLECTION_GENERICS = (List, Dict, Tuple)

_Type = Union[type, _GenericAlias]
_Stream = Union[str, bytes]


def validate_arguments(fit_ref, *args):
    try:
        types = get_parameter_types(fit_ref)
        if len(args) != len(types):  # 参数个数检查
            raise ValueError(f"annotated arguments number mismatch, actual: {len(args)} | expected: {len(types)}")
        for arg, type_ in zip(args, types):  # 参数类型检查
            if arg is not None and not isinstance(arg, _ORIGIN_TYPE[type_]):
                raise ValueError(f"value and type mismatch, actual type: {type(arg)} | expected type: {type_}")
    except ValueError as err:
        raise FitException(InternalErrorCode.INVALID_ARGUMENTS, str(err), degradable=False) from None


def get_parameter_types(obj: Union[Callable, Any], with_name: bool = False) \
        -> Union[Tuple[_Type, ...], Dict[str, _Type]]:
    """
    获取函数或公共结构体对应参数/属性的信息，和声明顺序一致
    当为公共结构体时，会根据类的__init__方法得到入参名字和类型
    e.g.
        get_parameter_types(func) -> (str, int)
        get_parameter_types(entity, with_name=True) -> {'a': str, 'b': int}

    :param obj: 函数对象或公共结构体类
    :param with_name: 是否获取类型对应的变量名
    :return: 对应的类型信息，tuple
    """
    if isinstance(obj, _GenericAlias):
        obj = get_origin(obj)
    if inspect.isclass(obj):
        obj = obj.__init__
    # 注意事项：如果采用 __annotations__ 方式获取注解，将导致无法正确解析 so 中 cyfunction 的函数签名
    sig = get_type_hints(obj)
    sig.pop('return', None)
    if with_name:
        return sig
    return tuple(sig.values())


def get_return_type(func: Callable) -> _Type:
    return func.__annotations__.get('return')


class _OriginType(dict):
    def __init__(self):
        super().__init__()
        self.update(dict.fromkeys(_INT_TYPES, int))
        self.update(dict.fromkeys(_FLOAT_TYPES, float))

    def __getitem__(self, type_):
        if isinstance(type_, _GenericAlias):
            type_ = get_origin(type_)
        return super().__getitem__(type_)

    def __missing__(self, type_):
        return type_


_ORIGIN_TYPE = _OriginType()


class _ChildTypesGetter(dict):
    def __init__(self):
        super().__init__()
        self.update(dict.fromkeys(_PRIMITIVE_TYPES, lambda t: ()))
        self.update({List: lambda t: repeat(get_args(t)[0])})
        self.update({Dict: lambda t: repeat(Tuple[get_args(t)])})
        self.update({Tuple: lambda t: get_args(t)})
        self.type_var_cache = {}

    def __getitem__(self, type_key):
        if isinstance(type_key, _GenericAlias):
            type_key, type_key_args = get_origin(type_key), get_args(type_key)
            if type_key in _COLLECTION_TYPES:  # type is of one of {List, Dict, Tuple}
                type_key = _COLLECTION_GENERICS[_COLLECTION_TYPES.index(type_key)]
            else:
                self.cache_type_variable_s(type_key, type_key_args)
        return super().__getitem__(type_key)

    def __missing__(self, entity_type_key):
        # 处理公共结构体类型
        return lambda _: (Tuple[str, type_] for type_ in get_parameter_types(entity_type_key))

    def cache_type_variable_s(self, origin_entity_type, type_var_value_s):
        type_var_s = origin_entity_type.__parameters__
        self.type_var_cache.update(zip(type_var_s, type_var_value_s))

    def resolve_type_variable(self, arg_type):
        while isinstance(arg_type, TypeVar):
            arg_type = self.type_var_cache.get(arg_type)
        return arg_type

    def fetch(self, arg_type):
        try:
            get_child_types = self[arg_type]
            return get_child_types(arg_type)
        except (AssertionError, TypeError, AttributeError) as e:
            raise FitException(InternalErrorCode.SERIALIZATION_ERROR,
                               f'deserialization of type: {arg_type} not supported; {e}') from None


class _ValueDecoder(dict):
    def __init__(self):
        super().__init__()
        # 针对不支持哈希key为非字符串类型限制的补偿性代码
        self.update({bool: self._bool_decode})
        self.update({bytes: b64decode_from_str})
        self.update(dict.fromkeys(_INT_TYPES, int))
        self.update(dict.fromkeys(_FLOAT_TYPES, float))
        # 针对三种主要泛型处理的代码，List泛型原则上无需处理，此处为了统一，一起加入
        self.update(zip(_COLLECTION_GENERICS, _COLLECTION_TYPES))

    def __getitem__(self, type_key):
        if isinstance(type_key, _GenericAlias):
            type_key = get_origin(type_key)
            if type_key in _COLLECTION_TYPES:
                type_key = _COLLECTION_GENERICS[_COLLECTION_TYPES.index(type_key)]
        return super().__getitem__(type_key)

    def __missing__(self, entity_type_key):
        return lambda kw: entity_type_key(**dict(kw))

    @classmethod
    def _bool_decode(cls, data_encoded):
        if not (isinstance(data_encoded, str) and data_encoded.upper() in ('TRUE', 'FALSE', 'NULL')):
            fit_logger.warning(f"invalid bool value detected during deserialization: {data_encoded}")
        return True if data_encoded.upper() == 'TRUE' else False if data_encoded.upper() == 'FALSE' else None

    def shallow_deserialize(self, arg_type, arg_in_common_data_model, child_arg_s):
        """
        :param arg_type:  待反序列化的类型
        :param arg_in_common_data_model:  待反序列化的节点数据
        :param child_arg_s:  已经反序列化的子节点数据（如list中的元素、公共结构体中的成员变量）
        :return: 反序列化后的数据
        # cannot swap the two sub-condition in the if-statement below
        # due to `isinstance` own restriction !
        """
        if arg_in_common_data_model is None or (type(arg_in_common_data_model) == arg_type and not child_arg_s):
            return arg_in_common_data_model

        data_encoded = child_arg_s if child_arg_s != [] else arg_in_common_data_model
        try:
            return self[arg_type](data_encoded)
        except (AssertionError, TypeError, ValueError) as e:
            raise FitException(InternalErrorCode.INVALID_ARGUMENTS,
                               f'value and its type defined mismatch; value: {data_encoded}, '
                               f'type: {arg_type}; {e}') from None


def _get_child_arg_s(arg_in_common_data_model, arg_type):
    try:
        if isinstance(arg_in_common_data_model, (tuple, list)):
            return iter(arg_in_common_data_model)
        elif isinstance(arg_in_common_data_model, dict):
            if arg_type == dict or get_origin(arg_type) == dict:
                return arg_in_common_data_model.items()
            else:
                arg_types = get_parameter_types(arg_type, with_name=True)
                return ((field, arg_in_common_data_model[field]) for field in arg_types)
        else:
            return ()
    except KeyError as e:
        raise FitException(InternalErrorCode.INVALID_ARGUMENTS,
                           'value to deserialize and its type defined mismatch; value:'
                           f' {arg_in_common_data_model}, type: {arg_type}; {e}') from None


def _deserialize_to_any(common_data_model):
    if isinstance(common_data_model, (int, float, bool, str, bytes, type(None))):
        return common_data_model
    elif isinstance(common_data_model, list):
        return [_deserialize_to_any(item) for item in common_data_model]
    elif isinstance(common_data_model, dict):
        return {key: _deserialize_to_any(value) for key, value in common_data_model.items()}
    else:
        class_name = common_data_model.pop("__class__")
        cls = globals()[class_name]
        obj = cls.__new__(cls)
        obj.__dict__.update(common_data_model)
        return obj


def common_data_model_deserialize(arg_type: _Type, common_data_model: Any,
                                  _child_types_getter: _ChildTypesGetter = None, _decoder: _ValueDecoder = None) -> Any:
    if arg_type is object:
        return _deserialize_to_any(common_data_model)

    if not _child_types_getter and not _decoder:
        _child_types_getter, _decoder = (_ChildTypesGetter(), _ValueDecoder())
    # 解析泛型的实际类型（如果存在）
    arg_type = _child_types_getter.resolve_type_variable(arg_type)

    child_arg_s = []
    for child_arg_type, child_arg in zip(_child_types_getter.fetch(arg_type),
                                         _get_child_arg_s(common_data_model, arg_type)):
        child_arg = common_data_model_deserialize(child_arg_type, child_arg, _child_types_getter, _decoder)
        child_arg_s.append(child_arg)
    return _decoder.shallow_deserialize(arg_type, common_data_model, child_arg_s)
