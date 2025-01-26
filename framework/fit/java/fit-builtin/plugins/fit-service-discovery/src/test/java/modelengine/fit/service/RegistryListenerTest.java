/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.service.entity.Address;
import modelengine.fit.service.entity.Application;
import modelengine.fit.service.entity.ApplicationInstance;
import modelengine.fit.service.entity.FitableAddressInstance;
import modelengine.fit.service.entity.FitableInfo;
import modelengine.fit.service.entity.Worker;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.conf.runtime.ApplicationConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RegistryListener} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-24
 */
@DisplayName("测试 RegistryListener 类")
class RegistryListenerTest {
    private static final String GENERICABLE_ID = "genericableIdA";
    private static final String VERSION = "1.0.0";
    private static final String FITABLE_ID = "fitableIdA";
    private static final String WORKER_ID = "workerId";
    private static final String WORKER_ENVIRONMENT = "debug";

    private RegistryListener registryListener;
    private FitableInfo fitable;
    private Address address;

    private void createRegister(String mode, boolean isSubscribe) {
        this.fitable = this.buildFitable();
        this.address = this.buildAddress();
        List<FitableInfo> fitables = new ArrayList<>();
        fitables.add(this.fitable);
        RegistryService client = mock(RegistryService.class);
        BeanContainer container = mock(BeanContainer.class);
        LocalFitableCache cache = Mockito.mock(LocalFitableCache.class);

        ApplicationConfig application = mock(ApplicationConfig.class);
        when(application.name()).thenReturn("appName");
        when(application.extensions()).thenReturn(new HashMap<>());
        when(application.visualExtensions()).thenReturn(new HashMap<>());
        WorkerConfig worker = mock(WorkerConfig.class);
        when(worker.id()).thenReturn(WORKER_ID);
        when(worker.environment()).thenReturn(WORKER_ENVIRONMENT);
        this.registryListener = new RegistryListener(container, cache, client, application, worker, mode, 60);
        UniqueFitableId uniqueFitableId = UniqueFitableId.create(GENERICABLE_ID, FITABLE_ID);
        when(cache.contains(uniqueFitableId)).thenReturn(true);
        if (isSubscribe) {
            FitableAddressInstance serviceAddress = this.buildServiceAddress(this.fitable, this.address);
            List<FitableAddressInstance> fitableInstances = new ArrayList<>();
            fitableInstances.add(serviceAddress);
            when(client.subscribeFitables(eq(fitables), eq(WORKER_ID), anyString())).thenReturn(fitableInstances);
        }
    }

    @Test
    @DisplayName("提供 RegistryListener 类监听类型是 push 时，正常执行")
    void givenRegistryListenerWhenListenerModePushThenNotThrow() {
        RegistryService client = mock(RegistryService.class);
        BeanContainer container = mock(BeanContainer.class);
        LocalFitableCache cache = Mockito.mock(LocalFitableCache.class);
        ApplicationConfig application = mock(ApplicationConfig.class);
        when(application.name()).thenReturn("appName");
        WorkerConfig worker = mock(WorkerConfig.class);
        when(worker.id()).thenReturn(WORKER_ID);
        when(worker.environment()).thenReturn(WORKER_ENVIRONMENT);
        assertDoesNotThrow(() -> new RegistryListener(container, cache, client, application, worker, "push", 60));
    }

    @Test
    @DisplayName("提供 RegistryListener 类推送订阅任务时，返回 true")
    void givenRegistryListenerWhenPushSubscribeThenReturnTrue() {
        List<UniqueFitableId> fitableIds = this.getSingleUniqueFitableIds();
        this.createRegister("push-and-pull", false);
        boolean isSubscribed = this.registryListener.subscribeFitables(fitableIds);
        assertThat(isSubscribed).isTrue();
    }

    @Test
    @DisplayName("提供 RegistryListener 类拉取订阅任务时，返回 true")
    void givenRegistryListenerWhenPullSubscribeThenReturnTrue() {
        List<UniqueFitableId> fitableIds = this.getSingleUniqueFitableIds();
        this.createRegister("pull", false);
        boolean isSubscribed = this.registryListener.subscribeFitables(fitableIds);
        assertThat(isSubscribed).isTrue();
    }

    @Test
    @DisplayName("提供 RegistryListener 类查不到实例时，返回地址列表")
    void givenRegistryListenerWhenNotQueryInstallThenReturnNewAddress() {
        UniqueFitableId fitableId = UniqueFitableId.create(GENERICABLE_ID, VERSION, FITABLE_ID, VERSION);
        this.createRegister("push-and-pull", false);
        List<Target> fitableTargets = this.registryListener.getFitableTargets(fitableId);
        assertThat(fitableTargets).hasSize(1);
    }

    @Test
    @DisplayName("提供 RegistryListener 类有实例时，返回地址列表")
    void givenRegistryListenerWhenQueryInstallThenReturnNewAddress() {
        UniqueFitableId fitableId = UniqueFitableId.create(GENERICABLE_ID, VERSION, FITABLE_ID, VERSION);
        this.createRegister("push-and-pull", true);
        List<Target> fitableTargets = this.registryListener.getFitableTargets(fitableId);
        assertThat(fitableTargets).hasSize(1);
    }

    @Test
    @DisplayName("提供 RegistryListener 类有实例时，正常执行")
    void givenRegistryListenerWhenSubscribeNotThrow() {
        this.createRegister("push-and-pull", true);
        FitableAddressInstance fitableInstance = this.buildServiceAddress(this.fitable, this.address);
        List<FitableAddressInstance> fitableInstances = new ArrayList<>();
        fitableInstances.add(fitableInstance);
        assertDoesNotThrow(() -> this.registryListener.notifyFitables(fitableInstances));
    }

    private Address buildAddress() {
        Address constructAddress = new Address();
        constructAddress.setHost("A");
        return constructAddress;
    }

    private List<UniqueFitableId> getSingleUniqueFitableIds() {
        UniqueFitableId fitableIdA = UniqueFitableId.create(GENERICABLE_ID, VERSION, FITABLE_ID, VERSION);
        return Collections.singletonList(fitableIdA);
    }

    private FitableAddressInstance buildServiceAddress(FitableInfo fitable, Address... addresses) {
        Worker worker = new Worker();
        worker.setAddresses(Arrays.asList(addresses));
        worker.setId(WORKER_ID);
        worker.setEnvironment(WORKER_ENVIRONMENT);
        List<Worker> workers = new ArrayList<>();
        workers.add(worker);
        ApplicationInstance applicationInstance = new ApplicationInstance();
        applicationInstance.setWorkers(workers);
        Application application = new Application();
        application.setName("appName");
        application.setNameVersion(fitable.getFitableId());
        application.setExtensions(new HashMap<>());
        applicationInstance.setApplication(application);
        FitableAddressInstance instance = new FitableAddressInstance();
        instance.setFitable(fitable);
        instance.setApplicationInstances(Collections.singletonList(applicationInstance));
        return instance;
    }

    private FitableInfo buildFitable() {
        FitableInfo constructFitable = new FitableInfo();
        constructFitable.setGenericableId(RegistryListenerTest.GENERICABLE_ID);
        constructFitable.setGenericableVersion(RegistryListenerTest.VERSION);
        constructFitable.setFitableId(RegistryListenerTest.FITABLE_ID);
        constructFitable.setFitableVersion(RegistryListenerTest.VERSION);
        return constructFitable;
    }
}
