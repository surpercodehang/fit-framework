/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.ConfigurableFitable;
import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.FitableFactory;
import modelengine.fitframework.broker.LoadBalancer;
import modelengine.fitframework.broker.TargetLocator;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.ioc.BeanContainer;

/**
 * 表示 {@link FitableFactory} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-24
 */
public class DefaultFitableFactory implements FitableFactory {
    private final BeanContainer container;
    private final LoadBalancer loadBalancer;
    private final TargetLocator targetLocator;

    /**
     * 使用指定的容器、负载均衡器和目标定位器初始化 {@link DefaultFitableFactory} 的新实例。
     *
     * @param container 表示容器的 {@link BeanContainer}。
     * @param loadBalancer 表示负载均衡器的 {@link LoadBalancer}。
     * @param targetLocator 表示目标定位器的 {@link TargetLocator}。
     * @throws IllegalArgumentException 当 {@code container}、{@code loadBalancer} 或 {@code targetLocator} 为 {@code null}
     * 时。
     */
    public DefaultFitableFactory(BeanContainer container, LoadBalancer loadBalancer, TargetLocator targetLocator) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.loadBalancer = notNull(loadBalancer, "The load balancer cannot be null.");
        this.targetLocator = notNull(targetLocator, "The target locator cannot be null.");
    }

    @Override
    public ConfigurableFitable create(String id, String version) {
        return new DefaultFitable(this.container, this.loadBalancer, this.targetLocator, id, version);
    }

    @Override
    public ConfigurableFitable create(UniqueFitableId id) {
        notNull(id, "The unique fitable id cannot be null.");
        return this.create(id.fitableId(), id.fitableVersion());
    }

    @Override
    public ConfigurableFitable create(Fitable fitable) {
        notNull(fitable, "The fitable cannot be null.");
        return this.create(fitable.id(), fitable.version())
                .aliases(fitable.aliases().all())
                .tags(fitable.tags().all())
                .degradationFitableId(fitable.degradationFitableId())
                .genericable(fitable.genericable());
    }
}
