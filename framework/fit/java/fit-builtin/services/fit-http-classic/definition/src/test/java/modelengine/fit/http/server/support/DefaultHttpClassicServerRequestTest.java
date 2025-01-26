/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.support;

import static modelengine.fit.http.protocol.MimeType.TEXT_PLAIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.AttributeCollection;
import modelengine.fit.http.HttpResource;
import modelengine.fit.http.Serializers;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.EntitySerializer;
import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fit.http.protocol.RequestLine;
import modelengine.fit.http.protocol.ServerRequest;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link DefaultHttpClassicServerRequest} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 DefaultHttpClassicServerRequest 类")
class DefaultHttpClassicServerRequestTest {
    private final HttpResource httpResource = mock(HttpResource.class);
    private final ServerRequest serverRequest = mock(ServerRequest.class);

    private DefaultHttpClassicServerRequest request;

    @BeforeEach
    void setup() {
        RequestLine startLine = mock(RequestLine.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(this.serverRequest.startLine()).thenReturn(startLine);
        when(this.serverRequest.headers()).thenReturn(headers);
        ReadableMessageBody body = mock(ReadableMessageBody.class);
        when(this.serverRequest.body()).thenReturn(body);
        when(startLine.requestUri()).thenReturn("?");
        this.request = new DefaultHttpClassicServerRequest(this.httpResource, this.serverRequest);
    }

    @Test
    @DisplayName("返回 Http 请求的所有属性集合")
    void shouldReturnAttributes() {
        final AttributeCollection attributes = this.request.attributes();
        assertThat(attributes.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("返回 Http 消息的消息体的结构化数据")
    void shouldReturnEntity() {
        Serializers serializers = mock(Serializers.class);
        EntitySerializer<?> entitySerializer = mock(EntitySerializer.class);
        Map<MimeType, EntitySerializer<?>> map =
                MapBuilder.<MimeType, EntitySerializer<?>>get().put(TEXT_PLAIN, entitySerializer).build();
        Entity entity = mock(Entity.class);
        when(this.httpResource.serializers()).thenReturn(serializers);
        when(serializers.entities()).thenReturn(map);
        when(entitySerializer.deserializeEntity(ObjectUtils.<InputStream>cast(any()),
                any(),
                any())).thenAnswer(ans -> entity);
        Optional<Entity> actual = this.request.entity();
        assertThat(actual).isPresent();
    }

    @Test
    @DisplayName("返回 Http 请求是否为安全的的标记")
    void shouldReturnIsSecure() {
        when(this.serverRequest.isSecure()).thenReturn(true);
        final boolean isSecure = this.request.isSecure();
        assertThat(isSecure).isTrue();
    }

    @Test
    @DisplayName("返回 Http 请求的本地地址")
    void shouldReturnLocalAddress() {
        final Address mock = mock(Address.class);
        when(this.serverRequest.localAddress()).thenReturn(mock);
        final Address address = this.request.localAddress();
        assertThat(address).isEqualTo(mock);
    }

    @Test
    @DisplayName("返回 Http 请求的远端地址")
    void shouldReturnRemoteAddress() {
        final Address mock = mock(Address.class);
        when(this.serverRequest.remoteAddress()).thenReturn(mock);
        final Address address = this.request.remoteAddress();
        assertThat(address).isEqualTo(mock);
    }
}
