/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.serializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.EntitySerializer;
import modelengine.fit.http.entity.MultiValueEntity;
import modelengine.fit.http.entity.support.DefaultMultiValueEntity;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.model.support.DefaultMultiValueMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 为 {@link FormUrlEncodedEntitySerializer} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-21
 */
@DisplayName("测试 FormUrlEncodedEntitySerializer 类")
public class FormUrlEncodedEntitySerializerTest {
    private final EntitySerializer<MultiValueEntity> formUrlEncodedEntitySerializer =
            FormUrlEncodedEntitySerializer.INSTANCE;
    private final HttpMessage httpMessage = mock(HttpMessage.class);
    private MultiValueEntity entity;
    private final Charset charset = StandardCharsets.UTF_8;
    private final byte[] givenByte = "testKey=testValue".getBytes(StandardCharsets.UTF_8);

    @BeforeEach
    void setup() {
        MultiValueMap<String, String> values = new DefaultMultiValueMap<>();
        values.add("testKey", "testValue");
        this.entity = new DefaultMultiValueEntity(this.httpMessage, values);
        when(this.httpMessage.headers()).thenReturn(ConfigurableMessageHeaders.create()
                .add(MessageHeaderNames.CONTENT_LENGTH, String.valueOf(this.givenByte.length)));
    }

    @Test
    @DisplayName("调用 serializeEntity() 方法，返回值与给定值相等")
    void invokeSerializeEntityMethodThenReturnIsEqualsToTheGivenValue() {
        byte[] actualByte = this.formUrlEncodedEntitySerializer.serializeEntity(this.entity, this.charset);
        assertThat(actualByte).isEqualTo(this.givenByte);
    }

    @Test
    @DisplayName("调用 deserializeEntity() 方法，返回值与给定值相等")
    void invokeDeserializeEntityMethodThenReturnIsEqualsToTheGivenValue() {
        MultiValueEntity multiValueEntity =
                this.formUrlEncodedEntitySerializer.deserializeEntity(this.givenByte, this.charset, this.httpMessage);
        assertThat(multiValueEntity.toString()).isEqualTo(this.entity.toString());
    }
}
