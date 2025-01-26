/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.heartbeat.HeartbeatMonitor;
import modelengine.fit.serialization.MessageSerializer;
import modelengine.fit.service.RegisterFitableService;
import modelengine.fit.service.RegistryService;
import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.conf.runtime.ApplicationConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link RegisterFitableService} 接口的测试类
 *
 * @author 李鑫
 * @author 季聿阶
 * @since 2021-11-30
 */
public class RegisterFitableServiceTest {
    private RegisterFitableService registerFitableService;

    @BeforeEach
    void setUp() {
        HeartbeatMonitor heartbeatMonitor = mock(HeartbeatMonitor.class);
        BeanContainer container = mock(BeanContainer.class);
        MessageSerializer messageSerializer = mock(MessageSerializer.class);
        RegistryService registryService = mock(RegistryService.class);
        ApplicationConfig application = mock(ApplicationConfig.class);
        when(application.name()).thenReturn("appName");
        WorkerConfig worker = mock(WorkerConfig.class);
        when(worker.id()).thenReturn("workerId");
        when(worker.host()).thenReturn("host");
        when(worker.domain()).thenReturn("domain");
        when(worker.environment()).thenReturn("workerEnvironment");
        this.registerFitableService = new DefaultRegisterFitableService(heartbeatMonitor,
                container,
                Collections.singletonList(messageSerializer),
                registryService,
                application,
                worker,
                80,
                5,
                5,
                15);
        when(messageSerializer.isSupported(any())).thenReturn(true);
        when(messageSerializer.getFormat()).thenReturn(1);
        doNothing().when(registryService).registerFitables(any(), any(), any());
        doNothing().when(registryService).unregisterFitables(any(), any());
    }

    @Test
    @DisplayName("given fitable metas when register fitables then register fitables successfully")
    void testOnlineFitables() {
        Exception exception =
                catchThrowableOfType(() -> this.registerFitableService.register(this.buildFitableProxies()),
                        Exception.class);
        assertThat(exception).isNull();
    }

    @Test
    @DisplayName("given fitable metas when offline fitables then unregister fitables successfully")
    void testOfflineFitables() {
        Exception exception =
                catchThrowableOfType(() -> this.registerFitableService.unregister(this.buildFitableProxies()),
                        Exception.class);
        assertThat(exception).isNull();
    }

    private List<LocalExecutor> buildFitableProxies() {
        LocalExecutor p1 = mock(LocalExecutor.class);
        when(p1.id()).thenReturn(UniqueFitableId.create("gId", "1.0.0", "fId1", "1.0.0"));
        LocalExecutor p2 = mock(LocalExecutor.class);
        when(p2.id()).thenReturn(UniqueFitableId.create("gId", "1.0.0", "fId2", "1.0.0"));
        return Arrays.asList(p1, p2);
    }
}
