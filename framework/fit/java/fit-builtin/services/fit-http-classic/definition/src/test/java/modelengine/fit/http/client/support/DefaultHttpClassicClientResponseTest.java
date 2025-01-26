/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.HttpResource;
import modelengine.fit.http.Serializers;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.protocol.ClientResponse;
import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fit.http.protocol.support.DefaultClientResponse;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.model.support.DefaultMultiValueMap;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 为 {@link DefaultHttpClassicClientResponse} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-16
 */
@DisplayName("测试 DefaultHttpClassicClientResponse 类")
public class DefaultHttpClassicClientResponseTest {
    private final HttpResource httpResource = mock(AbstractHttpClassicClient.class);
    private final int statusCode = 200;
    private final String reasonPhrase = "testHttpClientErrorException";
    private final MultiValueMap<String, String> headers = new DefaultMultiValueMap<>();
    private ClientResponse clientResponse;
    private InputStream responseStream;
    private DefaultHttpClassicClientResponse<?> defaultHttpClassicClientResponse;

    @BeforeEach
    void setup() {
        Map<String, ObjectSerializer> serializersMap = new HashMap<>();
        ObjectSerializer objectSerializer = mock(ObjectSerializer.class);
        serializersMap.put("testSerializersMapKey", objectSerializer);
        Serializers serializers = Serializers.create(serializersMap);
        when(this.httpResource.serializers()).thenReturn(serializers);
        this.responseStream =
                new ByteArrayInputStream("TestOfHttpClientErrorException".getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    void teardown() throws IOException {
        this.responseStream.close();
        this.clientResponse.close();
    }

    @Nested
    @DisplayName("测试 entity() 方法")
    class TestEntity {
        @Test
        @DisplayName("给定响应类型为 String，返回为 Optional 的实例")
        void givenResponseTypeIsStringThenReturnIsInstanceOfOptional() {
            DefaultHttpClassicClientResponseTest.this.headers.add("Content-Length", "30");
            DefaultHttpClassicClientResponseTest.this.clientResponse = new DefaultClientResponse(
                    DefaultHttpClassicClientResponseTest.this.statusCode,
                    DefaultHttpClassicClientResponseTest.this.reasonPhrase,
                    DefaultHttpClassicClientResponseTest.this.headers,
                    DefaultHttpClassicClientResponseTest.this.responseStream);
            Class<?> responseType = String.class;
            DefaultHttpClassicClientResponseTest.this.defaultHttpClassicClientResponse =
                    new DefaultHttpClassicClientResponse<>(DefaultHttpClassicClientResponseTest.this.httpResource,
                            DefaultHttpClassicClientResponseTest.this.clientResponse, responseType);
            Optional<Entity> entity =
                    DefaultHttpClassicClientResponseTest.this.defaultHttpClassicClientResponse.entity();
            assertThat(entity).isExactlyInstanceOf(Optional.class);
        }
    }

    @Test
    @DisplayName("获取对象实体不为空")
    void theObjectEntityShouldBeEqualsToTheGivenValue() {
        this.headers.add("Content-Length", "30");
        this.clientResponse =
                new DefaultClientResponse(this.statusCode, this.reasonPhrase, this.headers, this.responseStream);
        Class<?> responseType = String.class;
        this.defaultHttpClassicClientResponse = new DefaultHttpClassicClientResponse<>(this.httpResource,
                this.clientResponse,
                responseType);
        Optional<? extends ObjectEntity<?>> objectEntity = this.defaultHttpClassicClientResponse.objectEntity();
        assertThat(objectEntity).isPresent();
    }

    @Test
    @DisplayName("获取文本实体与给定值相等")
    void theTextEntityShouldBeEqualsToTheGivenValue() {
        this.headers.add(MessageHeaderNames.CONTENT_LENGTH, "30");
        this.headers.add(MessageHeaderNames.CONTENT_TYPE, MimeType.TEXT_PLAIN.value());
        this.clientResponse =
                new DefaultClientResponse(this.statusCode, this.reasonPhrase, this.headers, this.responseStream);
        Class<?> responseType = String.class;
        this.defaultHttpClassicClientResponse = new DefaultHttpClassicClientResponse<>(this.httpResource,
                this.clientResponse, responseType);
        Optional<TextEntity> textEntity = this.defaultHttpClassicClientResponse.textEntity();
        TextEntity expectEntity = null;
        if (this.defaultHttpClassicClientResponse.entity().isPresent()) {
            expectEntity = ObjectUtils.cast(this.defaultHttpClassicClientResponse.entity().get());
        }
        TextEntity finalExpect = expectEntity;
        assertThat(textEntity).isPresent().get().returns(expectEntity, expect -> finalExpect);
    }
}
