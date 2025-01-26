/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.service.RegisterAuthService;
import modelengine.fit.service.entity.ClientTokenInfo;
import modelengine.fit.service.entity.TokenInfo;
import modelengine.fitframework.broker.Format;
import modelengine.fitframework.conf.runtime.DefaultAvailableService;
import modelengine.fitframework.conf.runtime.DefaultMatata;
import modelengine.fitframework.conf.runtime.DefaultRegistry;
import modelengine.fitframework.conf.runtime.DefaultSecureAccess;
import modelengine.fitframework.conf.runtime.MatataConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.serialization.RequestMetadata;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link RemoteFitableExecutor} 的测试类。
 *
 * @author 李金绪
 * @since 2024-08-15
 */
@DisplayName("测试 RemoteFitableExecutor")
public class RemoteFitableExecutorTest {
    private RemoteFitableExecutor remoteFitableExecutor;

    @BeforeEach
    void setUp() {
        DefaultSecureAccess mockSecureAccess = new DefaultSecureAccess();
        mockSecureAccess.setAccessKey("testAk3");
        mockSecureAccess.setSecretKey("testSk3");
        mockSecureAccess.setEnabled(true);
        DefaultAvailableService mockAvailableService = new DefaultAvailableService();
        mockAvailableService.setGenericableId("testGenericableId");
        List<DefaultAvailableService> mockAuthRequiredServices = new ArrayList<>();
        mockAuthRequiredServices.add(mockAvailableService);
        DefaultRegistry mockRegistry = new DefaultRegistry();
        mockRegistry.setSecureAccess(mockSecureAccess);
        mockRegistry.setAuthRequiredServices(mockAuthRequiredServices);
        DefaultMatata mockMatata = new DefaultMatata();
        mockMatata.setRegistry(mockRegistry);
        RegisterAuthService mockService = mock(RegisterAuthService.class);
        BeanContainer container = mock(BeanContainer.class);
        remoteFitableExecutor = new RemoteFitableExecutor(container);
        BeanFactory matataConfigFactory = mock(BeanFactory.class);
        BeanFactory registerFactory = mock(BeanFactory.class);
        when(container.all(MatataConfig.class)).thenReturn(Collections.singletonList(matataConfigFactory));
        when(container.all(RegisterAuthService.class)).thenReturn(Collections.singletonList(registerFactory));
        when(matataConfigFactory.get()).thenReturn(mockMatata);
        when(registerFactory.get()).thenReturn(mockService);
        TokenInfo accessTokenInfo = new TokenInfo("mockAccessToken", "normal", 1, "access_token");
        TokenInfo refreshTokenInfo = new TokenInfo("mockRefreshToken", "normal", 1, "refresh_token");
        when(mockService.getToken()).thenReturn(ClientTokenInfo.convert(new ArrayList<>(Arrays.asList(refreshTokenInfo,
                accessTokenInfo)), Instant.now()));
    }

    @Test
    @DisplayName("测试正确返回元数据")
    void shouldReturnMeta() {
        Format format = Format.custom().name("1").code(1).build();
        DefaultFitable fitable = new DefaultFitable(null, null, null, "1", "1.0.0");
        DefaultGenericable genericable = new DefaultGenericable(null, "testGenericableId", "1.0.0");
        fitable.genericable(genericable);
        RequestMetadata requestMetadata = this.remoteFitableExecutor.getRequestMetadataBytes(format, fitable);
        Assertions.assertEquals("mockAccessToken", requestMetadata.accessToken());
        Assertions.assertEquals("testGenericableId", requestMetadata.genericableId());
    }
}
