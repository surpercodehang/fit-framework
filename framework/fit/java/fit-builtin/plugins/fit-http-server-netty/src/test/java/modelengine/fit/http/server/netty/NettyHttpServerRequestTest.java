/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.local.LocalChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.ReadableMessageBody;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 表示 {@link NettyHttpServerRequest} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-24
 */
@DisplayName("测试 NettyHttpServerRequest 类")
class NettyHttpServerRequestTest {
    private ChannelHandlerContext ctx;
    private NettyHttpServerRequest serverRequest;

    @BeforeEach
    void setup() throws IOException {
        this.ctx = mock(ChannelHandlerContext.class);
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaderNames.ALLOW, HttpHeaderValues.NONE);
        final DefaultHttpRequest httpRequest =
                new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/a", headers);
        this.serverRequest = new NettyHttpServerRequest(httpRequest, this.ctx, false, 2048);
        LastHttpContent content = new DefaultLastHttpContent();
        this.serverRequest.receiveLastHttpContent(content);
    }

    @Test
    @DisplayName("获取 Http 消息的消息体")
    void shouldReturnBody() {
        ReadableMessageBody body = this.serverRequest.body();
        assertThat(body).isNotNull();
    }

    @Test
    @DisplayName("获取 Http 请求是否为安全的的标记")
    void shouldReturnIsSecure() {
        final boolean isSecure = this.serverRequest.isSecure();
        assertThat(isSecure).isFalse();
    }

    @Test
    @DisplayName("当收到最后一个 Http 请求内容时触发的回调")
    void testReceiveLastHttpContent() {
        final LastHttpContent httpContent = new DefaultLastHttpContent();
        assertDoesNotThrow(() -> this.serverRequest.receiveLastHttpContent(httpContent));
        assertDoesNotThrow(() -> this.serverRequest.close());
    }

    @Nested
    @DisplayName("测试获取 Http 地址功能")
    class TestHttpAddress {
        private Channel channel;
        private InetSocketAddress socketAddress;

        @BeforeEach
        void setup() {
            this.channel = mock(Channel.class);
            when(NettyHttpServerRequestTest.this.ctx.channel()).thenReturn(this.channel);
            this.socketAddress = new InetSocketAddress("localhost", 8100);
        }

        @Test
        @DisplayName("当 netty 通道处理不含 Ip 地址时，返回值为 null")
        void givenLocalChanelThenReturnNull() {
            final Channel localChannel = new LocalChannel();
            when(NettyHttpServerRequestTest.this.ctx.channel()).thenReturn(localChannel);
            final Address address = NettyHttpServerRequestTest.this.serverRequest.localAddress();
            assertThat(address).isNull();
        }

        @Test
        @DisplayName("当 netty 通道处理包含 Ip 地址时，获取 Http 请求的本地地址")
        void givenChanelWithSocketAddressThenReturnLocalAddress() {
            when(this.channel.localAddress()).thenReturn(this.socketAddress);
            final Address address = NettyHttpServerRequestTest.this.serverRequest.localAddress();
            assertThat(address.socketAddress()).isEqualTo(this.socketAddress);
            assertThat(address.port()).isEqualTo(this.socketAddress.getPort());
        }

        @Test
        @DisplayName("当 netty 通道处理包含 Ip 地址时，获取 Http 请求的远端地址")
        void givenChanelWithSocketAddressThenReturnRemoteAddress() {
            when(this.channel.remoteAddress()).thenReturn(this.socketAddress);
            final Address address = NettyHttpServerRequestTest.this.serverRequest.remoteAddress();
            assertThat(address.socketAddress()).isEqualTo(this.socketAddress);
            assertThat(address.port()).isEqualTo(this.socketAddress.getPort());
        }
    }
}
