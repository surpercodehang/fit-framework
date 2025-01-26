/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import modelengine.fit.http.Serializers;
import modelengine.fit.http.server.HttpDispatcher;
import modelengine.fit.http.server.netty.support.DefaultNettyServerConfig;
import modelengine.fit.server.http.HttpConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ThreadUtils;
import modelengine.fitframework.value.ValueFetcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

/**
 * 表示 {@link NettyHttpClassicServer} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-23
 */
@DisplayName("测试 NettyHttpClassicServer 类")
class NettyHttpClassicServerTest {
    private NettyHttpClassicServer classicServer;
    private ValueFetcher valueFetcher;
    private Map<String, ObjectSerializer> serializersMap;

    @BeforeEach
    void setup() throws IOException {
        this.valueFetcher = mock(ValueFetcher.class);
        this.serializersMap = MapBuilder.<String, ObjectSerializer>get().build();
        DefaultNettyServerConfig nettyServerConfig = new DefaultNettyServerConfig();
        nettyServerConfig.setMaxThreadNum(3);
        nettyServerConfig.setDisplayError(true);
        HttpConfig httpConfig = mock(HttpConfig.class);
        BeanContainer container = mock(BeanContainer.class);
        this.classicServer = new NettyHttpClassicServer(container,
                this.serializersMap,
                this.valueFetcher,
                nettyServerConfig,
                httpConfig,
                true);
        int port = this.getIdlePort();
        this.classicServer.bind(port, false);
    }

    @Test
    @DisplayName("当提供 netty 的服务端，测试启动和关闭功能成功")
    void testStartAndStopSuccessfully() {
        try (MockedStatic<LockUtils> mockedStatic = Mockito.mockStatic(LockUtils.class)) {
            mockedStatic.when(() -> LockUtils.synchronize(any(), any(Runnable.class))).thenAnswer(ans -> {
                ans.callRealMethod();
                ThreadUtils.sleep(1000);
                return null;
            });
            assertDoesNotThrow(this.classicServer::start);
            assertDoesNotThrow(this.classicServer::stop);
        }
    }

    @Test
    @DisplayName("当提供 netty 的服务端绑定端口为负数时，抛出异常")
    void givenNegatePortThenThrowException() {
        assertThatThrownBy(() -> this.classicServer.bind(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("当提供 netty 的服务端绑定端口超过 65535 时，抛出异常")
    void givenLargePortThenThrowException() {
        assertThatThrownBy(() -> this.classicServer.bind(65536)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("当提供 netty 的服务端绑定 Https 端口超过 65535 时，抛出异常")
    void givenLargeHttpsPortThenThrowException() {
        assertThatThrownBy(() -> this.classicServer.bind(65536, true)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("获取序列化器的集合")
    void shouldReturnSerializers() {
        Serializers actual = this.classicServer.serializers();
        Serializers expected = Serializers.create(this.serializersMap);
        assertThat(actual.entities()).isEqualTo(expected.entities());
    }

    @Test
    @DisplayName("获取求值器")
    void shouldReturnValueFetcher() {
        ValueFetcher fetcher = this.classicServer.valueFetcher();
        assertThat(fetcher).isEqualTo(this.valueFetcher);
    }

    @Test
    @DisplayName("获取 Http 请求的分发器")
    void shouldReturnDispatcher() {
        HttpDispatcher actual = this.classicServer.httpDispatcher();
        HttpDispatcher expected = HttpDispatcher.create();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private int getIdlePort() throws IOException {
        // 读取空闲的可用端口
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
