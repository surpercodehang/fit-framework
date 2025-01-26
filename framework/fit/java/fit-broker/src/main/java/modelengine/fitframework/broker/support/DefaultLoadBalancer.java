/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.client.Client;
import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.broker.LoadBalancer;
import modelengine.fitframework.broker.SerializationService;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.broker.TargetLocator;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.TargetNotFoundException;
import modelengine.fitframework.broker.client.filter.loadbalance.EnvironmentFilter;
import modelengine.fitframework.broker.client.filter.loadbalance.FirstMatchedEnvironmentFilter;
import modelengine.fitframework.broker.client.filter.loadbalance.ProtocolAndFormatSupportedFilter;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link LoadBalancer} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-28
 */
public class DefaultLoadBalancer implements LoadBalancer {
    private static final Logger log = Logger.get(DefaultLoadBalancer.class);

    private final BeanContainer container;
    private final LazyLoader<List<Client>> clientsLoader;
    private final SerializationService serializationService;
    private final TargetLocator targetLocator;

    public DefaultLoadBalancer(BeanContainer container, SerializationService serializationService,
            TargetLocator targetLocator) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.clientsLoader = new LazyLoader<>(this::getClients);
        this.serializationService = notNull(serializationService, "The serialization service cannot be null.");
        this.targetLocator = notNull(targetLocator, "The target locator cannot be null.");
    }

    @Override
    public List<Target> balance(Fitable fitable, InvocationContext context, Object[] args) {
        Invoker.Filter filter = Invoker.Filter.combine(this.getFirstMatchedEnvironmentFilter(fitable, context),
                this.getProtocolAndFormatSupportedFilter(context),
                context.loadBalanceFilter());
        return this.filterCandidateTargets(filter, fitable, context, this.getTargets(fitable.toUniqueId()));
    }

    private Invoker.Filter getFirstMatchedEnvironmentFilter(Fitable fitable, InvocationContext context) {
        if (context.specifiedEnvironment() != null) {
            log.debug("Environment is specified. [id={}, environment={}]",
                    fitable.toUniqueId(),
                    context.specifiedEnvironment());
            return new EnvironmentFilter(context.specifiedEnvironment());
        }
        return new FirstMatchedEnvironmentFilter(context.environmentPrioritySequence());
    }

    private Invoker.Filter getProtocolAndFormatSupportedFilter(InvocationContext context) {
        return new ProtocolAndFormatSupportedFilter(this.clientsLoader.get(),
                this.serializationService,
                context.protocol(),
                context.format());
    }

    private List<Client> getClients() {
        return this.container.all(Client.class).stream().map(BeanFactory::<Client>get).collect(Collectors.toList());
    }

    private List<Target> filterCandidateTargets(Invoker.Filter filter, Fitable fitable, InvocationContext context,
            List<Target> toFilterTargets) {
        List<Target> filteredTargets =
                filter.filter(fitable, context.localWorkerId(), toFilterTargets, context.filterExtensions());
        filteredTargets = this.filterWithOtherFitables(context, filteredTargets);
        if (CollectionUtils.isEmpty(filteredTargets)) {
            String message = StringUtils.format("No matched fitable targets left after loadbalance. [id={0}]",
                    fitable.toUniqueId());
            TargetNotFoundException exception = new TargetNotFoundException(message);
            exception.associateFitable(fitable.genericable().id(), fitable.id());
            throw exception;
        }
        return filteredTargets;
    }

    private List<Target> filterWithOtherFitables(InvocationContext context, List<Target> targets) {
        List<Target> intersection = targets;
        for (UniqueFitableId id : context.loadBalanceWith()) {
            intersection = this.intersect(intersection, this.getTargets(id));
        }
        return intersection;
    }

    private List<Target> intersect(List<Target> targets, List<Target> toFilterTargets) {
        Set<String> workerIds = toFilterTargets.stream().map(Target::workerId).collect(Collectors.toSet());
        return targets.stream().filter(target -> workerIds.contains(target.workerId())).collect(Collectors.toList());
    }

    private List<Target> getTargets(UniqueFitableId id) {
        return this.targetLocator.lookup(id);
    }
}
