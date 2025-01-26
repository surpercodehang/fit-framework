/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import static modelengine.fitframework.inspection.Validation.greaterThan;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.LocalExecutorFactory;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitRuntimeStartedObserver;
import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolScheduler;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * 用于向注册中心注册泛服务实现的客户端类。
 *
 * @author 李鑫
 * @author 季聿阶
 * @since 2021-11-25
 */
@Component
public class RegistryClient
        implements PluginStartedObserver, PluginStoppingObserver, FitRuntimeStartedObserver, Runnable {
    private static final Logger log = Logger.get(RegistryClient.class);
    private static final long DEFAULT_DELAYED = 5L;

    private final BeanContainer container;
    private final RegisterFitableService registerFitableService;
    private final LocalExecutorFactory localExecutorFactory;

    private final Map<UniqueFitableId, LocalExecutor> fitableMetadataMapping = new ConcurrentSkipListMap<>();
    private final long registrySyncPeriod;
    private final ThreadPoolScheduler registerFitablesExecutor;

    private volatile boolean isFitablesAlreadyRegistered = false;
    private final Object lock = LockUtils.newSynchronizedLock();

    /**
     * 构建注册中心客户端对象。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param registerFitableService 表示注册服务实现的服务的 {@link RegisterFitableService}。
     * @param localExecutorFactory 表示本地执行器的 {@link LocalExecutorFactory}。
     * @param registrySyncPeriod 表示插件服务周期性注册的时间的 {@code registrySyncPeriod}。以秒为单位。
     */
    public RegistryClient(BeanContainer container, RegisterFitableService registerFitableService,
            LocalExecutorFactory localExecutorFactory,
            @Value("${plugin.registry.sync.period}") long registrySyncPeriod) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.registerFitableService = notNull(registerFitableService, "The register fitable service cannot be null.");
        this.localExecutorFactory = notNull(localExecutorFactory, "The local executor factory cannot be null.");

        this.registrySyncPeriod = greaterThan(registrySyncPeriod,
                0,
                "The registry sync period must be positive. [plugin.registry.sync.period={0}]",
                registrySyncPeriod);
        log.debug("Config 'plugin.registry.sync.period' is {} seconds.", this.registrySyncPeriod);
        this.registerFitablesExecutor = ThreadPoolScheduler.custom()
                .corePoolSize(1)
                .isDaemonThread(true)
                .threadPoolName("registry-client")
                .build();
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        List<LocalExecutor> pluginFitableMetadata = this.getFitableMetadataList(plugin);
        if (CollectionUtils.isEmpty(pluginFitableMetadata)) {
            return;
        }
        synchronized (this.lock) {
            pluginFitableMetadata.forEach(this::addFitableProxy);
        }
        if (this.isFitablesAlreadyRegistered) {
            List<LocalExecutor> toRegister = this.constructToRegisterFitableProxies();
            this.registerFitableService.register(toRegister);
            log.info("Register fitables successfully. [plugin={}, size={}]",
                    plugin.metadata().name(),
                    pluginFitableMetadata.size());
        } else {
            log.debug("Fitables are ready to register. [plugin={}, size={}]",
                    plugin.metadata().name(),
                    pluginFitableMetadata.size());
        }
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        List<LocalExecutor> pluginFitableMetadata = this.getFitableMetadataList(plugin);
        if (CollectionUtils.isEmpty(pluginFitableMetadata)) {
            return;
        }
        synchronized (this.lock) {
            pluginFitableMetadata.forEach(this::removeFitableProxy);
        }
        List<LocalExecutor> toRegister = this.constructToRegisterFitableProxies();
        this.registerFitableService.register(toRegister);
        log.info("Unregister fitables successfully. [plugin={}, size={}]",
                plugin.metadata().name(),
                pluginFitableMetadata.size());
    }

    @Override
    public void onRuntimeStarted(FitRuntime runtime) {
        if (MapUtils.isEmpty(this.fitableMetadataMapping)) {
            log.debug("No fitables need to register, skip.");
            return;
        }
        this.submitRegisterFitablesTask();
    }

    private List<LocalExecutor> getFitableMetadataList(Plugin plugin) {
        return this.localExecutorFactory.get(plugin, false)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void addFitableProxy(LocalExecutor pluginFitableMetadata) {
        this.fitableMetadataMapping.put(pluginFitableMetadata.id(), pluginFitableMetadata);
    }

    private void removeFitableProxy(LocalExecutor pluginFitableMetadata) {
        this.fitableMetadataMapping.remove(pluginFitableMetadata.id());
    }

    private void submitRegisterFitablesTask() {
        this.registerFitablesExecutor.schedule(Task.builder()
                .runnable(this)
                .policy(ExecutePolicy.fixedDelay(this.registrySyncPeriod * 1000))
                .uncaughtExceptionHandler((thread, exception) -> this.fallback(exception))
                .build(), 0);
    }

    @Override
    public void run() {
        List<LocalExecutor> toRegister = this.constructToRegisterFitableProxies();
        this.registerFitableService.register(toRegister);
        if (!this.isFitablesAlreadyRegistered) {
            FitablesRegisteredObserver.notify(this.container);
            this.isFitablesAlreadyRegistered = true;
            log.info("Register fitables successfully. All fitable services are ready.");
        }
    }

    private void fallback(Throwable cause) {
        log.warn("Failed to register fitables. Retrying in {} seconds...", DEFAULT_DELAYED);
        log.debug("Failed to register fitables.", cause);
    }

    private List<LocalExecutor> constructToRegisterFitableProxies() {
        synchronized (this.lock) {
            return new ArrayList<>(this.fitableMetadataMapping.values());
        }
    }
}
