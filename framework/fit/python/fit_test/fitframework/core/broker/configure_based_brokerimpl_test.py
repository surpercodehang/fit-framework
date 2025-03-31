# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：configure_based_brokerimpl单元测试
"""
import unittest
from unittest.mock import patch

from fit_common_struct.core import Address
from fitframework.core.broker import configure_based_brokerimpl, select_broker, trace
from fitframework.core.broker import remote_invoker
from fitframework.core.broker.broker import BrokerTemplate
from fitframework.core.broker.broker_utils import FitableIdentifier, IdType
from fitframework.core.broker.configure_based_brokerimpl import ConfigureBasedBroker
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.core.repo import service_repo
from fitframework.testing.test_support import FitTestSupport

_MOCK_FITABLE_ID = '925d87507d00400294aeca71b9f19eh8'


def mock_function(in_param: str) -> int:
    pass


class ConfigureBasedBrokerTest(FitTestSupport):
    @classmethod
    def setUpClass(cls):
        super(ConfigureBasedBrokerTest, cls).setUpClass()

    class MockBroker(BrokerTemplate):
        def fit_ffp_invoke(self, generic_id: str, fitable_identifier: str, *args, route_filter=None,
                           address_filter=None):
            return f"{generic_id}{fitable_identifier}{len(args)}"

    @patch.object(BrokerTemplate, 'fit_ffp_invoke')
    def test_invoke(self, mock_fit_ffp_invoke):
        args = 'gen1', FitableIdentifier('a1', IdType.alias), None, None, 'abc', 1, False, None, None
        ConfigureBasedBroker().fit_ffp_invoke(*args, timeout=1, is_async=True, retry=False, route_filter=None,
                                              address_filter=None)
        mock_fit_ffp_invoke.assert_called_once_with(*args, timeout=1, is_async=True, retry=False, route_filter=None,
                                                    address_filter=None)
        self.assertTrue(mock_fit_ffp_invoke.called)

    @patch.object(trace, '_ignore_trace')
    @patch.object(service_repo, 'query_fit_or_fitable_ref', return_value='fun')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value='f1')
    @patch.object(BrokerTemplate, 'fit_ffp_invoke')
    def test_on_validate(self, mock_fit_ffp_invoke, _, *__):
        ConfigureBasedBroker().on_validate('gen1', None, 'abc')
        mock_fit_ffp_invoke.assert_called_once_with('gen1', FitableIdentifier('f1', IdType.id),
                                                    'fun',
                                                    'abc', timeout=None, is_async=None, retry=None, route_filter=None,
                                                    address_filter=None)

    @patch.object(trace, '_ignore_trace')
    @patch.object(select_broker.BrokerBuilder, '_get_fit_ref')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value='f1')
    @patch.object(BrokerTemplate, 'fit_ffp_invoke')
    def test_on_before(self, mock_fit_ffp_invoke, _, mock_fun, __):
        mock_fun.return_value = 'fun'
        ConfigureBasedBroker().on_before('gen1', None, 'abc')
        mock_fit_ffp_invoke.assert_called_once_with('gen1', FitableIdentifier('f1', IdType.id),
                                                    'fun',
                                                    'abc', timeout=None, is_async=None, retry=None, route_filter=None,
                                                    address_filter=None)

    @patch.object(trace, '_ignore_trace')
    @patch.object(select_broker.BrokerBuilder, '_get_fit_ref', return_value='fun')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value='f1')
    @patch.object(BrokerTemplate, 'fit_ffp_invoke')
    def test_on_after(self, mock_fit_ffp_invoke, _, *__):
        ConfigureBasedBroker().on_after('gen1', None, 'abc')
        mock_fit_ffp_invoke.assert_called_once_with('gen1', FitableIdentifier('f1', IdType.id), 'fun',
                                                    'abc', timeout=None, is_async=None, retry=None, route_filter=None,
                                                    address_filter=None)

    @patch.object(trace, '_ignore_trace')
    @patch.object(select_broker.BrokerBuilder, '_get_fit_ref', return_value='fun')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value='f1')
    @patch.object(BrokerTemplate, 'fit_ffp_invoke')
    def test_on_error(self, mock_fit_ffp_invoke, _, *__):
        ConfigureBasedBroker().on_error('gen1', None, 'abc')
        mock_fit_ffp_invoke.assert_called_once_with('gen1', FitableIdentifier('f1', IdType.id), 'fun',
                                                    'abc', timeout=None, is_async=None, retry=None, route_filter=None,
                                                    address_filter=None)

    @patch.object(trace, '_ignore_trace')
    @patch.object(select_broker.BrokerBuilder, '_get_fit_ref', return_value='fun')
    @patch.object(configure_based_brokerimpl, '_routing_by_rule', return_value=None)
    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=[])
    @patch.object(BrokerTemplate, 'fit_ffp_invoke', return_value='f1')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id')
    def test_routing_default(self, mock_get_fit_ffp_fitable_id, mock_invoke, _, *__):
        ffp_dict = {'route.dynamic': 'rid'}
        mock_get_fit_ffp_fitable_id.side_effect = lambda _, key: ffp_dict.get(key)
        self.assertListEqual(['f1'], ConfigureBasedBroker().routing('gen1', None, None, 'abc',
                                                                    route_filter=None))
        mock_invoke.assert_called_once_with('gen1', FitableIdentifier('rid', IdType.id), 'fun',
                                            'abc', timeout=None, is_async=None, retry=None, route_filter=None,
                                            address_filter=None)

    @patch.object(configure_based_brokerimpl, 'get_all_fitables_from_config')
    def test_routing_with_filter(self, mock_get_all_fitables_from_config):
        mock_get_all_fitables_from_config.return_value = [('%s' % _MOCK_FITABLE_ID)]
        route_filter1 = lambda t: t == _MOCK_FITABLE_ID
        self.assertListEqual([_MOCK_FITABLE_ID],
                             ConfigureBasedBroker().routing(_MOCK_FITABLE_ID,
                                                            None, None, None,
                                                            route_filter=route_filter1))

    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=['invokeAll'])
    @patch.object(configure_based_brokerimpl, 'get_all_fitables_from_config',
                  return_value=['fitable1', 'fitable2'])
    def test_routing_invoke_all(self, *_):
        self.assertListEqual(['fitable1', 'fitable2'],
                             ConfigureBasedBroker().routing('gen1', None, None, 'abc'))

    @patch.object(configure_based_brokerimpl, '_is_invoke_all', return_value=False)
    @patch.object(configure_based_brokerimpl, 'default_envs', return_value=["alpha"])
    @patch.object(configure_based_brokerimpl, 'worker_env', return_value='alpha')
    @patch.object(configure_based_brokerimpl, 'get_genericable_rule', return_value={
        configure_based_brokerimpl._FIT_META_GEN_RULE_ID_KEY: 'test_rule',
        configure_based_brokerimpl._FIT_META_GEN_RULE_TYPE_KEY: 'T, P'})
    @patch.object(configure_based_brokerimpl, 'get_genericable_params', return_value={
        'a': {'taggers': None}, 'b': {'taggers': {'big_id': {}, 'small_id': {}}},
        'c': {'taggers': {}}, 'd': {}})
    @patch.object(configure_based_brokerimpl, 'routing_rule_execute',
                  return_value='test_fitable_id')
    @patch.object(configure_based_brokerimpl, 'resolve_tags', return_value=['filtered_big'])
    def test_routing_by_rule(self, mock_resolve_tags, mock_routing_rule_execute,
                             mock_get_genericable_params, mock_get_genericable_rule, mock_worker_env,
                             *_):
        def test_genericable(a: int, b: str, c: list, d: tuple, e: dict):
            pass

        test_gen_id = '123'

        fitable_id_s = ConfigureBasedBroker().routing(test_gen_id, None,
                                                      test_genericable,
                                                      *(1, '2', [3], (4,), {5: '5'}))
        self.assertListEqual(fitable_id_s, ['test_fitable_id'])
        self.assertEqual(mock_worker_env.call_count, 2)
        mock_get_genericable_rule.assert_called_once_with(test_gen_id)
        mock_get_genericable_params.assert_called_once_with(test_gen_id)
        mock_routing_rule_execute.assert_called_once_with('alpha', 'test_rule',
                                                          '{"P": {"arg0": 1, "arg1": "2", '
                                                          '"arg2": [3], "arg3": [4], '
                                                          '"arg4": {"5": "5"}}, '
                                                          '"T": {"arg1": ["filtered_big"]}}')
        mock_resolve_tags.assert_called_once_with('alpha', ['big_id', 'small_id'],
                                                  '{"P": {"arg1": "2"}}')

    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=[])
    @patch.object(service_repo, 'get_fitable_ref')
    def test_load_balancing_local_first(self, mock_get_fitable_ref, _):
        func = lambda in_param: in_param + ' -> out'
        mock_get_fitable_ref.return_value = func
        result = ConfigureBasedBroker().load_balancing('gen1', 'fitable1')
        self.assertEqual(func, result)

    @patch.object(configure_based_brokerimpl, '_get_fit_service_address_with_priorities')
    @patch.object(configure_based_brokerimpl, '_load_balance_env_filtering')
    @patch.object(configure_based_brokerimpl, "_worker_id", return_value="test_id")
    def test_load_balancing_filter(self, mock_get_fit_service_address_with_priorities, mock_load_balance_env_filtering,
                                   _):
        address1 = Address('host1', 8001, 'id1', 2, 1, "prod", context_path="")
        address2 = Address('host2', 8002, 'id2', 4, 2, "beta", context_path="")
        addresses = [address1, address2]

        mock_get_fit_service_address_with_priorities.return_value = addresses
        mock_load_balance_env_filtering.return_value = addresses
        address_filter = lambda endpoint: endpoint.environment == "beta"

        result = ConfigureBasedBroker().load_balancing('gen1', 'fitable1',
                                                       address_filter=address_filter)
        self.assertEqual(8002, result.port)

    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=['localOnly'])
    @patch.object(service_repo, 'get_fitable_ref', return_value=None)
    def test_load_balancing_force_local(self, *_):
        result = ConfigureBasedBroker().load_balancing('gen1', 'fitable1')
        self.assertIsNone(result)

    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=[])
    @patch.object(configure_based_brokerimpl, 'get_fit_service_address_list', return_value=[])
    @patch.object(service_repo, 'get_fitable_ref', return_value=None)
    def test_load_balancing_remote_no_available(self, *_):
        result = ConfigureBasedBroker().load_balancing('gen1', 'fitable1')
        self.assertIsNone(result)

    @patch.object(trace, '_ignore_trace')
    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=[])
    @patch.object(service_repo, 'get_fitable_ref', return_value=None)
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value=None)
    @patch.object(configure_based_brokerimpl, 'get_registered_clients', return_value=[2, 3])
    @patch.object(remote_invoker, 'get_supported_formats', return_value=[0, 1])
    @patch.object(remote_invoker, 'protocol_priors', return_value=remote_invoker.default_priority)
    @patch.object(BrokerTemplate, 'fit_ffp_invoke')
    @patch.object(configure_based_brokerimpl, '_load_balance_env_filtering')
    @patch.object(configure_based_brokerimpl, '_load_balancing')
    @patch.object(configure_based_brokerimpl, 'get_fit_service_address_list')
    def test_load_balancing_remote(self, mock_get_address_list, mock_lb, mock_filtering,
                                   *_):
        address1 = Address("host2", 200, "host2:200", 3, [0], 'unittest', "")
        address2 = Address("host1", 100, "host1:100", 2, [1], 'unittest', "")
        address3 = Address("host2", 200, "host2:200", 2, [0], 'unittest', "")

        mock_get_address_list.return_value = [address1, address2, address3]
        mock_filtering.side_effect = lambda addresses: addresses
        mock_lb.side_effect = lambda _, addresses: addresses
        results = ConfigureBasedBroker().load_balancing('gen1', 'fitable1', address_filter=None)
        self.assertEqual(2, len(results))
        self.assertEqual('host1:100', results[0].id)
        self.assertEqual('host2:200', results[1].id)

    @patch.object(trace, "_ignore_trace", return_value=True)
    def test_execute_local(self, _):
        result = ConfigureBasedBroker().fit_execute(lambda in_param: in_param + ' -> out', 'gen1', 'fitable1', None,
                                                    'in', timeout=None, is_async=None)
        self.assertEqual('in -> out', result)

    @patch.object(trace, "_ignore_trace", return_value=True)
    @patch.object(remote_invoker, 'request_response',
                  return_value=FitResponse(ResponseMetadata(1, False, 0, "OK", {}), b"970823"))
    @patch.object(remote_invoker, 'get_supported_formats', return_value=[1])
    @patch.object(remote_invoker, 'protocol_priors', return_value=remote_invoker.default_priority)
    @patch.object(configure_based_brokerimpl, 'print_trace')
    def test_execute_remote(self, *_):
        remote_address = Address("host1", 8080, "worker-id", 0, [1], "unittest", "")
        result = ConfigureBasedBroker().fit_execute(remote_address, '1aa1', 'fitable1', mock_function,
                                                    'in', timeout=None, is_async=None)
        self.assertEqual(result, 970823)


if __name__ == '__main__':
    unittest.main()
