/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.server.FitServer;
import modelengine.fit.service.Registry;
import modelengine.fit.service.RegistryLocator;
import modelengine.fitframework.broker.Endpoint;
import modelengine.fitframework.broker.Format;
import modelengine.fitframework.broker.LocalExecutorFactory;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.broker.TargetLocator;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.conf.runtime.MatataConfig;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link TargetLocator} 的默认实现。
 *
 * @author 梁济时
 * @author 张越
 * @author 季聿阶
 * @since 2020-09-02
 */
public class DefaultTargetLocator implements TargetLocator {
    private static final Logger log = Logger.get(DefaultTargetLocator.class);

    private final BeanContainer container;
    private final LazyLoader<LocalExecutorFactory> localExecutorFactoryLoader;

    private final WorkerConfig worker;
    private final List<MatataConfig.Registry.AvailableService> registryServices;

    public DefaultTargetLocator(BeanContainer container, WorkerConfig worker,
            List<MatataConfig.Registry.AvailableService> services) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.localExecutorFactoryLoader = new LazyLoader<>(() -> this.container.factory(LocalExecutorFactory.class)
                .map(BeanFactory::<LocalExecutorFactory>get)
                .orElseThrow(() -> new IllegalStateException("No local executor factory.")));

        this.worker = notNull(worker, "The worker config cannot be null.");
        this.registryServices = notNull(services, "The available services config cannot be null.");
    }

    @Override
    public List<Target> lookup(UniqueFitableId id) {
        notNull(id, "The fitable key cannot be null when looking-up fitable targets.");
        if (this.isFitableLocal(id, true)) {
            return Collections.singletonList(this.local());
        }
        if (this.isRegistryFitable(id)) {
            return this.getRegistryTargets(id);
        }
        return this.getFitableTargets(id);
    }

    private boolean isRegistryFitable(UniqueFitableId id) {
        return this.registryServices.stream().anyMatch(service -> Objects.equals(service.toUniqueId(), id));
    }

    private List<Target> getRegistryTargets(UniqueFitableId id) {
        List<Target> targets = this.container.lookup(RegistryLocator.class)
                .map(BeanFactory::<RegistryLocator>get)
                .map(RegistryLocator::targets)
                .orElseGet(Collections::emptyList);
        MatataConfig.Registry.AvailableService availableService = this.registryServices.stream()
                .filter(registryService -> Objects.equals(id, registryService.toUniqueId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("No registry service. [id={0}]", id)));
        List<Format> availableFormats = availableService.formatCodes()
                .stream()
                .map(code -> Format.custom()
                        .name(SerializationFormat.from(code).name())
                        .code(code)
                        .build())
                .collect(Collectors.toList());
        return targets.stream()
                .map(target -> Target.custom()
                        .workerId(target.workerId())
                        .host(target.host())
                        .environment(target.environment())
                        .endpoints(target.endpoints())
                        .formats(availableFormats)
                        .extensions(target.extensions())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Target> getFitableTargets(UniqueFitableId id) {
        return this.container.lookup(Registry.class)
                .map(BeanFactory::<Registry>get)
                .map(registry -> this.getTargetsFromRegistry(registry, id))
                .filter(CollectionUtils::isNotEmpty)
                .orElseGet(() -> this.getTargetsFromLocal(id));
    }

    private List<Target> getTargetsFromRegistry(Registry registry, UniqueFitableId id) {
        List<Target> fitableTargets = registry.getFitableTargets(id);
        log.debug("Got targets from registry. [id={}, targetSize={}]", id, fitableTargets.size());
        return fitableTargets;
    }

    private List<Target> getTargetsFromLocal(UniqueFitableId id) {
        if (this.isFitableLocal(id, false)) {
            return Collections.singletonList(this.local());
        }
        return Collections.emptyList();
    }

    private boolean isFitableLocal(UniqueFitableId id, boolean isMicro) {
        return this.localExecutorFactoryLoader.get()
                .get(id)
                .filter(localExecutor -> localExecutor.isMicro() == isMicro)
                .isPresent();
    }

    @Override
    public Target local() {
        List<Endpoint> endpoints = this.getFitServers()
                .stream()
                .map(FitServer::endpoints)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        Map<String, String> extensions =
                this.getFitServers().stream().map(FitServer::extensions).reduce(new HashMap<>(), (m1, m2) -> {
                    m1.putAll(m2);
                    return m1;
                });
        return Target.custom()
                .workerId(this.worker.id())
                .host(this.worker.host())
                .environment(this.worker.environment())
                .endpoints(endpoints)
                .formats(Collections.emptyList())
                .extensions(extensions)
                .build();
    }

    private List<FitServer> getFitServers() {
        return this.container.all(FitServer.class)
                .stream()
                .map(BeanFactory::<FitServer>get)
                .collect(Collectors.toList());
    }
}
