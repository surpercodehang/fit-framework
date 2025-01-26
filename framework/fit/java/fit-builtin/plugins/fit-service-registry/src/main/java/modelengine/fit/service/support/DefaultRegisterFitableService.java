/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.support;

import static modelengine.fitframework.inspection.Validation.greaterThan;
import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.heartbeat.HeartbeatMonitor;
import modelengine.fit.serialization.MessageSerializer;
import modelengine.fit.service.RegisterFitableService;
import modelengine.fit.service.RegistryService;
import modelengine.fit.service.entity.Application;
import modelengine.fit.service.entity.FitableInfo;
import modelengine.fit.service.entity.FitableMeta;
import modelengine.fit.service.entity.Worker;
import modelengine.fit.service.util.AppNameVersionUtils;
import modelengine.fit.service.util.WorkerUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.conf.runtime.ApplicationConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link RegisterFitableService} 的默认实现。
 *
 * @author 李鑫
 * @author 季聿阶
 * @since 2021-11-29
 */
@Component
public class DefaultRegisterFitableService implements RegisterFitableService {
    private static final Logger log = Logger.get(DefaultRegisterFitableService.class);
    private static final String HEARTBEAT_TYPE_REGISTRY = "fit_registry";

    private final BeanContainer container;
    private final List<MessageSerializer> messageSerializers;
    private final RegistryService registryService;

    private final ApplicationConfig application;
    private final WorkerConfig worker;
    private final int workerExpireTime;

    /**
     * 创建注册服务实现的服务对象。
     *
     * @param heartbeatMonitor 表示心跳服务客户端的 {@link HeartbeatMonitor}。
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param messageSerializers 表示泛服务的输入输出序列化器的集合的 {@link List}{@code <}{@link MessageSerializer}{@code >}。
     * @param registryService 表示注册中心服务的 {@link RegistryService}。
     * @param application 表示应用配置的 {@link ApplicationConfig}。
     * @param worker 表示进程配置的 {@link WorkerConfig}。
     * @param workerExpireTime 表示进程过期时间的 {@code int}。
     * @param heartbeatSyncInitialDelay 表示心跳初始延迟时间的 {@code int}。
     * @param heartbeatSyncPeriod 表示心跳周期时间的 {@code int}。
     * @param heartbeatSyncAliveTime 表示心跳存活时间的 {@code int}。
     */
    public DefaultRegisterFitableService(HeartbeatMonitor heartbeatMonitor, BeanContainer container,
            List<MessageSerializer> messageSerializers, RegistryService registryService, ApplicationConfig application,
            WorkerConfig worker, @Value("${worker.expire-time}") int workerExpireTime,
            @Value("${plugin.heartbeat.sync.initial-delay}") int heartbeatSyncInitialDelay,
            @Value("${plugin.heartbeat.sync.period}") int heartbeatSyncPeriod,
            @Value("${plugin.heartbeat.sync.alive-time}") int heartbeatSyncAliveTime) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.messageSerializers = notNull(messageSerializers, "The message serializers cannot be null.");
        this.registryService = notNull(registryService, "The registry service cannot be null.");

        this.application = notNull(application, "The application config cannot be null.");
        this.worker = notNull(worker, "The worker config cannot be null.");
        this.workerExpireTime = greaterThan(workerExpireTime,
                0,
                "The worker expire time must be positive. [worker.expire-time={0}]",
                workerExpireTime);
        log.debug("Config 'worker.expire-time' is {} seconds.", this.workerExpireTime);

        int initialDelay = greaterThanOrEquals(heartbeatSyncInitialDelay,
                0,
                "The heartbeat sync initial delay must not be negative. [plugin.heartbeat.sync.initial-delay={0}]",
                heartbeatSyncInitialDelay);
        log.debug("Config 'plugin.heartbeat.sync.initial-delay' is {} seconds.", initialDelay);
        int period = greaterThan(heartbeatSyncPeriod,
                0,
                "The heartbeat sync period must be positive. [plugin.heartbeat.sync.period={0}]",
                heartbeatSyncPeriod);
        log.debug("Config 'plugin.heartbeat.sync.period' is {} seconds.", period);
        int aliveTime = greaterThan(heartbeatSyncAliveTime,
                0,
                "The heartbeat sync alive time must be positive. [plugin.heartbeat.sync.alive-time={0}]",
                heartbeatSyncAliveTime);
        log.debug("Config 'plugin.heartbeat.sync.alive-time' is {} seconds.", aliveTime);
        notNull(heartbeatMonitor, "The heartbeat monitor cannot be null.").keepAlive(HEARTBEAT_TYPE_REGISTRY,
                period,
                initialDelay,
                aliveTime);
    }

    @Override
    public void register(List<LocalExecutor> fitableMetadata) {
        if (CollectionUtils.isEmpty(fitableMetadata)) {
            log.warn("No fitables need to register, skip.");
            return;
        }
        List<FitableMeta> fitableMetas = fitableMetadata.stream().map(this::toFitableMeta).collect(Collectors.toList());
        Worker localWorker = this.buildWorker();
        this.registerFitables(fitableMetas, localWorker);
    }

    @Override
    public void unregister(List<LocalExecutor> fitableMetadata) {
        if (CollectionUtils.isEmpty(fitableMetadata)) {
            log.warn("No fitables need to unregister, skip.");
            return;
        }
        this.unregisterFitables(this.toFitables(fitableMetadata));
    }

    private FitableMeta toFitableMeta(LocalExecutor localExecutor) {
        FitableInfo fitable = new FitableInfo();
        fitable.setGenericableId(localExecutor.id().genericableId());
        fitable.setGenericableVersion(localExecutor.id().genericableVersion());
        fitable.setFitableId(localExecutor.id().fitableId());
        fitable.setFitableVersion(localExecutor.id().fitableVersion());
        Set<Integer> supportedFormats = this.getSupportedFormats(localExecutor.method());
        FitableMeta fitableMeta = new FitableMeta();
        fitableMeta.setFitable(fitable);
        fitableMeta.setAliases(new ArrayList<>(localExecutor.aliases()));
        fitableMeta.setFormats(new ArrayList<>(supportedFormats));
        return fitableMeta;
    }

    private Set<Integer> getSupportedFormats(Method method) {
        return this.messageSerializers.stream()
                .filter(serializer -> serializer.isSupported(method))
                .map(MessageSerializer::getFormat)
                .collect(Collectors.toSet());
    }

    private Worker buildWorker() {
        Worker localWorker = WorkerUtils.getLocalWorker(this.container, this.worker);
        localWorker.getExtensions().put("expire", String.valueOf(this.workerExpireTime));
        return localWorker;
    }

    private void registerFitables(List<FitableMeta> fitableMetas, Worker worker) {
        try {
            log.debug("Prepare to register fitables. [fitables={}, worker={}]", fitableMetas, worker);
            this.registryService.registerFitables(fitableMetas, worker, this.buildApplication(fitableMetas));
            log.debug("Register fitables successfully. [fitables={}, worker={}", fitableMetas, worker);
        } catch (Exception e) {
            log.error("Failed to register fitables. [fitables={}, worker={}]", fitableMetas, worker, e);
        }
    }

    private Application buildApplication(List<FitableMeta> fitableMetas) {
        Application constructApplication = new Application();
        constructApplication.setName(this.application.name());
        constructApplication.setNameVersion(AppNameVersionUtils.calculateAppNameVersion(fitableMetas,
                this.worker.environment()));
        constructApplication.setExtensions(this.application.visualExtensions());
        return constructApplication;
    }

    private List<FitableInfo> toFitables(List<LocalExecutor> executors) {
        return executors.stream().filter(Objects::nonNull).map(this::toFitable).collect(Collectors.toList());
    }

    private FitableInfo toFitable(LocalExecutor executor) {
        FitableInfo fitable = new FitableInfo();
        fitable.setGenericableId(executor.id().genericableId());
        fitable.setGenericableVersion(executor.id().genericableVersion());
        fitable.setFitableId(executor.id().fitableId());
        fitable.setFitableVersion(executor.id().fitableVersion());
        return fitable;
    }

    private void unregisterFitables(List<FitableInfo> fitables) {
        try {
            log.debug("Prepare to unregister fitables. [fitables={}, worker={}]", fitables, this.worker);
            this.registryService.unregisterFitables(fitables, this.worker.id());
            log.debug("Unregister fitables successfully. [fitables={}, worker={}]", fitables, this.worker);
        } catch (Exception e) {
            log.warn("Failed to unregister fitables. [fitables={}, worker={}]", fitables, this.worker, e);
        }
    }
}
