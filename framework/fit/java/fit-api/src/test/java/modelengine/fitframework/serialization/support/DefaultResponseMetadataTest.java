/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.serialization.ByteSerializer;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.serialization.TagLengthValues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link DefaultResponseMetadata} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-17
 */
@DisplayName("测试 DefaultResponseMetadata 类")
class DefaultResponseMetadataTest {
    private ResponseMetadata metadata;
    private final byte dataFormat = 5;
    private final int code = 200;
    private final String message = "success";
    private final TagLengthValues tagLengthValues = this.initTagLengthValues();

    private TagLengthValues initTagLengthValues() {
        TagLengthValues values = new DefaultTagLengthValues();
        byte[] bytes = {1, 2, 3};
        values.putTag(1, bytes);
        return values;
    }

    @BeforeEach
    @DisplayName("初始化 DefaultResponseMetadata 类")
    void init() {
        this.metadata = ResponseMetadata.custom()
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(this.tagLengthValues)
                .build();
    }

    @Test
    @DisplayName("提供 DefaultResponseMetadata 类，返回正常信息")
    void givenDefaultResponseMetadataShouldReturnVersion() {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadata expected = ResponseMetadata.custom()
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(this.tagLengthValues)
                .build();
        assertThat(this.metadata).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 ResponseMetadata 类 copy 方法时，返回正常信息")
    void givenResponseMetadataWhenCopyThenReturnMetadata() {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadata build = ResponseMetadata.custom()
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(this.tagLengthValues)
                .build();
        ResponseMetadata responseMetadataV2 = build.copy().build();
        assertThat(responseMetadataV2).isEqualTo(this.metadata);
    }

    @Test
    @DisplayName("提供 ResponseMetadata 类 deserialize 方法时，返回正常信息")
    void givenRequestMetadataShouldReturnMetadata() {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadata build = ResponseMetadata.custom()
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(this.tagLengthValues)
                .build();
        ResponseMetadata responseMetadataV2 = ResponseMetadata.deserialize(build.serialize());
        assertThat(responseMetadataV2).isEqualTo(build);
    }

    @Test
    @DisplayName("提供 DefaultResponseMetadata 类 equals 方法与不同类型比较时，返回 false")
    void givenOtherTypeResponseMetadataShouldReturnFalse() {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadata expected = ResponseMetadata.custom()
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(null)
                .build();
        DefaultResponseMetadata.Builder builder = new DefaultResponseMetadata.Builder(expected);
        assertThat(this.metadata.equals(builder)).isFalse();
    }

    @Test
    @DisplayName("提供 DefaultResponseMetadata 类 toString 方法时，返回正常信息")
    void givenDefaultResponseMetadataShouldReturnStringValue() {
        String meta = this.metadata.toString();
        assertThat(meta).contains(this.message);
    }

    @Test
    @DisplayName("提供 ResponseMetadata 类序列化方法时，返回正常信息")
    void givenResponseMetadataWhenSerializeThenReturnMetadata() throws IOException {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadata actual = ResponseMetadata.custom()
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(null)
                .build();
        DefaultResponseMetadata.Serializer serializer = DefaultResponseMetadata.Serializer.INSTANCE;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(actual, out);
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        ResponseMetadata expected = serializer.deserialize(in);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 ByteSerializer 类序列化方法异常时，抛出异常")
    void givenByteSerializerWhenSerializeExceptionThrowException() throws IOException {
        byte[] bytes = {1, 2, 3};
        ByteSerializer<?> byteSerializer = mock(ByteSerializer.class);
        when(byteSerializer.deserialize(any(ByteArrayInputStream.class))).thenThrow(new IOException());
        assertThatThrownBy(() -> ByteSerializer.deserialize(byteSerializer,
                bytes)).isInstanceOf(SerializationException.class);
    }
}
