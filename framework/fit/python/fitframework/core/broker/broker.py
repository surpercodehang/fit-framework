# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：Fit调用代理模板
"""
from contextlib import suppress
from typing import Any, List

from fitframework.api.logging import fit_logger
from fitframework.core.broker.broker_utils import FitableIdentifier, retryable, degradable, \
    exception_handler
from fitframework.core.exception.fit_exception import FitException, InternalErrorCode, \
    TargetNotFoundException


def _address_filter():
    pass


class BrokerTemplate:
    """ FFP broker模板类 """

    @staticmethod
    def _is_routing_error_or_load_balance_error(err: FitException):
        return err.error_code == InternalErrorCode.ROUTING_ERROR.value or \
            err.error_code == InternalErrorCode.TARGET_NOT_FOUND_ERROR.value

    def fit_ffp_invoke(self, generic_id: str, fitable_identifier: FitableIdentifier, fit_ref, *args, timeout, is_async,
                       retry, route_filter, address_filter):
        try:
            self.aop_before_trust(generic_id, fit_ref, *args)
            self.on_validate(generic_id, fit_ref, *args)
            self.on_before_quitely(generic_id, fit_ref, *args)
            result = self.fit_invoke(generic_id, fitable_identifier, fit_ref, *args, timeout=timeout, is_async=is_async,
                                     retry=retry, route_filter=route_filter, address_filter=address_filter)
            self.on_after_quitely(generic_id, fit_ref, *args)
            return result
        except Exception as err:
            if isinstance(err, FitException) and \
                    BrokerTemplate._is_routing_error_or_load_balance_error(err):
                if self.recycle(generic_id,
                                fitable_identifier.value if fitable_identifier else None):
                    return None
            self.on_error(generic_id, fit_ref, *args)
            raise err from None
        finally:
            self.aop_after_trust(generic_id)

    def recycle(self, generic_id: str, fitable_id: str) -> bool:
        """
        进行recycle以及返回是否进行recycle
        :param generic_id:
        :param fitable_id:
        :return: bool
        """
        pass

    def aop_before_trust(self, generic_id, fit_ref, *args):
        """ AOP before trust action
        :param generic_id:

        Args:
            fit_ref:
            *args:

        """
        pass

    def aop_after_trust(self, generic_id):
        """ AOP after trust action
        :param generic_id:
        """
        pass

    def aop_before_invoke(self, generic_id, fitable_identifier):
        pass

    def aop_after_invoke(self, generic_id, fitable_identifier):
        pass

    def on_validate(self, generic_id, fit_ref, *args):
        """ Fitable调用前的入参校验，失败抛Exception """
        pass

    def on_before_quitely(self, generic_id, fit_ref, *args):
        """ Fitable调用前可加入一些预备操作 """
        with suppress(Exception):
            self.on_before(generic_id, fit_ref, *args)

    def on_before(self, generic_id, fit_ref, *args):
        pass

    def on_after_quitely(self, generic_id, fit_ref, *args):
        """ Fitable调用后可加入一些后续操作 """
        with suppress(Exception):
            self.on_after(generic_id, fit_ref, *args)

    def on_after(self, generic_id, fit_ref, *args):
        pass

    def on_error(self, generic_id, fit_ref, *args):
        """ Fitable调用失败（包括降级失败）后操作 """
        pass

    def routing(self, generic_id, fitable_identify: FitableIdentifier, fit_ref, *args,
                route_filter=None) -> List[str]:
        """
        根据输入参数找到Fitable，业务路由
        """
        pass

    def load_balancing(self, generic_id, fitable_id, address_filter=None) -> str:
        """ 根据generic_id和fitable_id找到所有的instance，做负载均衡 """
        pass

    def fit_execute(self, address, generic_id, fitable_id, fit_ref, *args, timeout, is_async):
        """ 执行Fitable instance
        """
        pass

    def get_degradation(self, generic_id, fitable_id) -> str:
        """ Fitable调用失败后找到降低服务 """
        pass

    def fit_invoke(self, generic_id: str, fitable_identifier: FitableIdentifier, fit_ref, *args, timeout, is_async,
                   retry, route_filter, address_filter):
        @degradable
        @exception_handler
        @retryable(retry_times=retry)
        def fit_lb_and_execute(broker, generic_id, fitable_id, fit_ref, *args, timeout, is_async,
                               address_filter) -> Any:
            address = self.load_balancing(generic_id, fitable_id, address_filter=address_filter)
            fit_logger.debug("generic_id %s, fid %s LB on %s", generic_id, fitable_id, str(address))
            if address is None:
                raise TargetNotFoundException(generic_id, fitable_id)
            return broker.fit_execute(address, generic_id, fitable_id, fit_ref, *args, timeout=timeout,
                                      is_async=is_async)

        work_fit_ids = self.routing(generic_id, fitable_identifier, fit_ref, *args, route_filter=route_filter)
        if len(work_fit_ids) == 0:
            raise FitException(InternalErrorCode.ROUTING_ERROR.value,
                               f"routing failed by generic_id: {generic_id}")
        for the_id in work_fit_ids:
            fit_logger.debug("generic_id %s routed on %s", generic_id, the_id)
            return fit_lb_and_execute(self, generic_id, the_id, fit_ref, *args, timeout=timeout, is_async=is_async,
                                      address_filter=address_filter)
