# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：框架对外提供的装饰器包
关于微化Fitable的支持：微化Fitable只支持进程内调用。定义Fitable时，@Fitable不需要指定genericable id
和fitable id。这两个Id通过方法名自动生成。对应的@Fit不需要指定genericable，也不能指定alias。
1. @Fit的方法名和@Fitable的方法名必须一致
2. 不同的微化@Fitable方法名不能一样
"""
from functools import wraps
from inspect import signature, Parameter
from time import perf_counter
from typing import Union, Optional, get_args, List, Callable

from fit_common_struct.core import Address
from fitframework import const
from fitframework.api.enums import FrameworkEvent, PluginEvent, FitState, PluginState, FitEvent
from fitframework.api.logging import fit_logger, StreamHandler, FileHandler, fetch_logging_filename
from fitframework.core.broker.broker_utils import FitableIdentifier, IdType
from fitframework.utils.context import runtime_context


def fit(generic_id, alias=None, use_rule=False, *, timeout=None, is_async: bool = None, retry=None,
        address_filter: Callable[[Address], bool] = None, fitable_filter: Callable[[str], bool] = None):
    """
    装饰器：@fit调用方法

    Args:
        generic_id (str): genericable id
        alias (Optional[str]): 指定fitable别名调用
        use_rule (Optional[bool]): 是否指定路由规则调用
        timeout (int) 预期设定的超时中断调用的时间，单位为秒
    """
    if generic_id is None:
        raise Exception("generic_id CANNOT be None for a fit function")

    def decorator(func):
        if signature(func).return_annotation == Parameter.empty:
            raise Exception(
                f"the return value type of the function decorated by \"fit\" must be specified. "
                f"[func_name={func.__name__}]")
        fit_logger.info(f"decorate fit function: generic_id = %s, alias = %s", generic_id, alias, stacklevel=2)
        from fitframework.core.repo.fit_register import register_at_fit_function
        register_at_fit_function(generic_id, func)

        @wraps(func)
        def fit_invoke_wrapper(*args):
            from fitframework.core.broker import select_broker
            return select_broker.select(generic_id) \
                .fitable_identifier(create_fitable_identifier(alias, use_rule=use_rule)).fit_ref(func) \
                .timeout(timeout).is_async(is_async).retry(retry).address_filter(address_filter) \
                .route_filter(fitable_filter).fit_selector_invoke(*args)

        return fit_invoke_wrapper

    return decorator


def private_fit(func):
    """
    装饰器：@private_fit微化方法调用

    Args:
        func (function): 方法指针
    """
    generic_id = _get_private_fitable_gid(func)
    fit_logger.info(f"decorate private fit function: generic_id = %s", generic_id, stacklevel=2)

    @wraps(func)
    def wrapper(*args):
        from fitframework.core.broker import select_broker
        return select_broker.select(generic_id).fit_ref(func).fit_selector_invoke(*args)

    return wrapper


def fitable(generic_id, fitable_id, aliases: List[str] = None):
    """
    装饰器：@fitable方法

    Args:
        generic_id (str): genericable id
        fitable_id (str): fitable id
        is_callback (bool): 表明该服务的实现是否用于回调，
            该参数主要用于动态下载配置的时候过滤掉此回调的情形
    """
    aliases = aliases or []
    if generic_id is None or fitable_id is None:
        raise Exception("generic_id/fitable_id CANNOT be None for a fitable function")

    def decorate(func):
        for _, parameter in signature(func).parameters.items():
            if parameter.annotation == Parameter.empty:
                raise Exception(
                    f"the parameter type of the function decorated by \"fitable\" must be specified. "
                    f"[func_name={func.__name__}]")
        fit_logger.info("decorate fitable function. generic_id = %s, fitable_id = %s",
                        generic_id, fitable_id, stacklevel=2)
        from fitframework.core.repo.fitable_register import register_fitable
        register_fitable(generic_id, fitable_id, False, aliases, func)

        @wraps(func)
        def wrapper(*args, **kw):
            return func(*args, **kw)

        return wrapper

    return decorate


def private_fitable(func):
    """
    装饰器：@private_fitable微化方法

    Args:
        func (function): 方法指针
    """
    generic_id = _get_private_fitable_gid(func)
    fitable_id = _get_private_fitable_fid(func)
    fit_logger.info("decorate private fitable function. generic_id = %s, fitable_id = %s",
                    generic_id, fitable_id, stacklevel=2)

    from fitframework.core.repo.fitable_register import register_fitable
    register_fitable(generic_id, fitable_id, True, [], func)

    @wraps(func)
    def wrapper(*args, **kw):
        return func(*args, **kw)

    return wrapper


def value(key: str, default_value=None, converter=None):
    """
    装饰器：配置项值注入，优先读取环境或进程入参配置，然后读取插件配置，最后取default_value。都没有配，返回None
    优先级序列如下：
        1. 命令行参数
        2. 环境变量
        3. fit_startup.yml 指定的启动参数
        4. conf/application.yml 指定的应用默认参数
        5. 各插件 conf/application.yml 指定的插件默认参数
        6. 装饰器中所指定的 default_value
    Args:
        key (str): 配置键值
        default_value (Optional[Any]): 配置项找不到时的缺省值
        converter (Optional[function]): 配置值转换器指针。str转目标对象。因为default_value可以传入任何对象，
            converter不针对default_value做转化
    """

    def decorator(func):
        from fitframework.core.repo.value_register import register_value_ref
        register_value_ref(func, key)

        @wraps(func)
        def wrapper(*_):
            val = runtime_context.get_item(key)
            from fitframework.core.repo import service_repo
            if val is None:
                val = service_repo.get_configuration_item(const.GLOBAL_PLUGIN_NAME, key)
            if val is None:
                val = service_repo.get_value(func)
            if val is None:
                return default_value
            return converter(val) if converter else val

        return wrapper

    return decorator


def local_context(key: str, default_value=None, converter=None):
    """
    装饰器：环境或进程入参配置注入，优先级序列如下：
        1. 命令行参数
        2. 环境变量
        3. fit_startup.yml 指定的启动参数
        4. conf/application.yml 指定的应用默认参数
        5. 装饰器中所指定的 default_value
    相比于 value 装饰器，减少了对于插件中指定参数的检索。
    Args:
        key (str): 配置键值
        default_value (Optional[Any]): 配置项找不到时的缺省值
        converter (Optional[function]): 配置值转换器指针。str转目标对象。因为default_value可以传入任何对象，
            converter不针对default_value做转化
    """

    def decorator(func):
        @wraps(func)
        def wrapper(*_):
            val = runtime_context.get_item(key)
            from fitframework.core.repo import service_repo
            if val is None:
                val = service_repo.get_configuration_item(const.GLOBAL_PLUGIN_NAME, key)
            if val is None:
                return default_value
            return converter(val) if converter else val

        return wrapper

    return decorator


def on_configuration_changed(plugin_name_of_interest: str, key_of_interest: str):
    """
    装饰器：配置中心配置项变更监听
    <=>
    @register_event(FrameworkEvent.CONFIGURATION_CHANGED, lambda conf_change_data: ...key...)
    @key(...)
    def func(conf_change_data):
        # ...

    :param plugin_name_of_interest:
    :param key_of_interest: 监听的配置键值
    :return:
    """

    def decorator(func):
        from fitframework.utils import eventing
        eventing.register(
            FrameworkEvent.CONFIGURATION_CHANGED,
            func,
            lambda item: item.key == key_of_interest and item.plugin_name == plugin_name_of_interest)
        fit_logger.info(f"register event CONFIGURATION_CHANGED on function: %s", func.__name__)
        return func

    return decorator


def register_event(event: Union[FrameworkEvent, PluginEvent], event_filter=None):
    """
     装饰器：通用事件监听注册。

    使用场景：当某事件发生时，需要关注此事件的对象，或应该收到该通知的对象，
        可考虑对该事件进行处理。对象本身可以是引擎自己，或系统插件，或用户插件
    使用方法：定义一个处理函数，然后通过@register_event来将其注册即可。
        后续由引擎本身负责实际的调度和调用（通过notify方法来实现）
    概念定义：
        - 事件的注册：注册一个处理函数，即等价于对应的事件注册；注册的函数和注册的事件
            为多对多（n~m）的关系
        - 事件的关联数据：事件触发后会进行广播，收到的信息为该事件对象本身，以及和该事件
            相关的数据，以关键字参数形式传递，故用户的处理函数也需要按此参数形式来定义函数
            签名。以START_BEFORE事件为例，该事件的广播数据为：
            {
                plugin_name：插件名，
                plugin_ver：插件版本，
                fitables_info：插件包含的服务的信息列表，每个元素为（服务ID，实现ID）二元组，
                plugin_state：插件状态
            }
            此时，针对该事件的一个合法的处理函数，需要满足这四个参数的支持，比如：
            @register_event(PluginEvent.PLUGIN_STARTING)
            def handle(plugin, plugin_ver, fitables_info, plugin_state):
                # ...
    支持的事件类型：见:param: event参数说明

    Args:
        event (Event): 事件类型，包括框架事件和用户插件事件
            其中框架事件如下：
            - CONFIGURATION_CHANGED：配置已变更。需要关注此配置的可考虑处理该事件
            - APPLICATION_STARTED：所有用户插件已加载完毕。(started)
                用户在此事件发生后即可正常调用服务
            - FRAMEWORK_STARTED：引擎底座已加载完毕，所有系统服务已注册。用户在此
                事件发生后即可正常使用引擎全部能力
            - FRAMEWORK_STOPPING：引擎即将停止；所有系统服务应在接收到该事件注销
            用户插件事件如下：
            - PLUGIN_STARTING：任一用户插件准备开始启动
            - PLUGIN_STARTED：任一用户插件已经启动完毕。注意启动过程中报错不会触发该事件
            - PLUGIN_STOPPING：任一用户插件准备开始停止
            - PLUGIN_STOPPED：任一用户插件已经停止完毕。停止过程中报错也不会触发该事件
            上述四个用户插件事件的关联数据相同，具体如下：
            {
                plugin_name：插件名，
                plugin_ver：插件版本，
                fitables_info：插件包含的服务的信息列表，每个元素为（服务ID，实现ID）二元组，
                plugin_state：插件状态
            }
        event_filter (Optional[function]): filter_func(event_data)，根据event_data进行事件筛选。
    """

    def decorator(func):
        from fitframework.utils import eventing
        eventing.register(event, func, event_filter)
        fit_logger.info(f"register event {event.name} on function: %s", func.__name__, stacklevel=2)
        return func

    return decorator


def scheduled_executor(period: int):
    """
    装饰器：定时任务执行。目前不支持被装饰的方法带参数
    @schedule_executor(5)
    async def function():
      ...

    Args:
        period (int): 执行间隔second
    """

    def decorator(func):
        from fitframework.utils import scheduler
        safe_period = period if period else 0
        scheduler.register(func, safe_period)
        fit_logger.info(f"register scheduler task on function: %s with period: %d",
                        func.__name__, safe_period, stacklevel=2)

        @wraps(func)
        def wrapper():
            return func()

        return wrapper

    return decorator


def plugin_start(func):
    from fitframework.core.repo.plugin_repo import add_plugin_start_func
    add_plugin_start_func(func)
    fit_logger.info(f"register start function {func.__name__}", stacklevel=2)
    return func


def plugin_stop(func):
    from fitframework.core.repo.plugin_repo import add_plugin_stop_func
    add_plugin_stop_func(func)
    fit_logger.info(f"register stop function {func.__name__}", stacklevel=2)
    return func


def run_once(func):
    @wraps(func)
    def wrapper(*args, **kw):
        if wrapper.has_run:
            return wrapper.stored_result
        wrapper.stored_result = func(*args, **kw)
        wrapper.has_run = True
        return wrapper.stored_result

    wrapper.has_run = False
    return wrapper


def timer(func):
    @wraps(func)
    def wrapper(*args, **kw):
        start_time = perf_counter()
        ret = func(*args, **kw)
        fit_logger.info('[Time Elapse] function `{0}` took {1:.3f}s to execute.'.format(
            func.__name__, perf_counter() - start_time))
        return ret

    return wrapper


def shutdown_on_error(func):
    @fit(const.RUNTIME_SHUTDOWN_GEN_ID)
    def shutdown() -> None:
        pass

    @wraps(func)
    def wrapper(*args, **kw):
        try:
            return func(*args, **kw)
        except (KeyboardInterrupt, RuntimeError) as err:
            fit_logger.warning(f"{err}: it's a terminating")
            shutdown()
        except Exception as err:
            fit_logger.exception('booting failed, terminating', dests=FileHandler)
            fit_logger.error(
                f"{err}. \n"
                f"------------------------------------------------------------------------------------------------\n"
                f"SEE LOGGING FILE FOR DETAILED INFORMATION: {fetch_logging_filename()}\n"
                f"------------------------------------------------------------------------------------------------\n",
                dests=StreamHandler)
            shutdown()

    return wrapper


def state_broadcast(state_updater, state_before: Optional[FitState], state_after: Optional[FitState], **event_data):
    """
    共同逻辑抽取：更新状态 -> 触发事件广播（附带数据，如存在）
    前后两个状态必须保持同样类型
    此装饰器非业务能力，仅用于代码优化、模块整合，精简结构用
    """

    def decorator(func):
        def _execute(new_state):
            if new_state is not None:
                # 当state_updater为fit接口时，无法直接传入枚举值，故使用其name
                state_updater(new_state.name)
                # state枚举值的value直接对应了绑定event枚举值，如存在（注意此处FitEvent是一个Annotation而非真正的类型）
                new_event = new_state.value
                if isinstance(new_event, get_args(FitEvent)):
                    from fitframework.utils.eventing import notify
                    notify(new_event, **event_data)

        @wraps(func)
        def wrapper(*args, **kw):
            nonlocal state_updater, event_data
            state_to_check = state_before if state_before is not None else state_after
            if isinstance(state_to_check, PluginState):
                # 当更新的状态为插件状态时，第一个参数为插件对象，
                # 需将更新方法转化为实例方法，同时将插件对象自身作为数据进行广播
                state_updater = getattr(args[0], state_updater.__name__)
                event_data = vars(args[0])

            _execute(state_before)
            ret = func(*args, **kw)
            _execute(state_after)
            return ret

        return wrapper

    return decorator


def create_fitable_identifier(alias, use_rule=None):
    if alias:
        return FitableIdentifier(alias, IdType.alias)
    if use_rule:
        return FitableIdentifier(use_rule, IdType.rule)
    return None


# helper functions #
def _get_private_fitable_gid(func):
    """ 微化Fitable自动生成genericable id """
    return func.__name__


def _get_private_fitable_fid(func):
    """ 微化Fitable自动生成fitable id """
    return func.__name__
