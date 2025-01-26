/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.serialization.RequestMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.Version;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link DefaultRequestMetadata} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-17
 */
@DisplayName("测试 DefaultRequestMetadata 类以及相关类")
class DefaultRequestMetadataTest {
    private RequestMetadata metadata;
    private final byte dataFormat = 5;
    private final String genericableId = "9588e5fc63cc4f1fbdcf2567bce0a454";
    private final Version genericableVersion = new DefaultVersion((byte) 20, (byte) 1, (byte) 1);
    private final String fitableId = "9588e5fc63cc4f1fbdcf2567bce0a453";
    private final Version fitableVersion = new DefaultVersion((byte) 10, (byte) 1, (byte) 1);
    private final TagLengthValues tagLengthValues = this.initTagLengthValues();

    private TagLengthValues initTagLengthValues() {
        TagLengthValues tlv = new DefaultTagLengthValues();
        byte[] bytes = {1, 2, 3};
        tlv.putTag(1, bytes);
        return tlv;
    }

    @BeforeEach
    @DisplayName("初始化 DefaultRequestMetadata 类")
    void init() {
        this.metadata = RequestMetadata.custom()
                .dataFormat(this.dataFormat)
                .genericableId(this.genericableId)
                .genericableVersion(this.genericableVersion)
                .fitableId(this.fitableId)
                .fitableVersion(this.fitableVersion)
                .tagValues(this.tagLengthValues)
                .build();
    }

    @Test
    @DisplayName("提供 DefaultRequestMetadata 类，返回正常信息")
    void givenDefaultRequestMetadataShouldReturnMetadata() {
        RequestMetadata build = RequestMetadata.custom()
                .dataFormat(this.dataFormat)
                .genericableId(this.genericableId)
                .genericableVersion(this.genericableVersion)
                .fitableId(this.fitableId)
                .fitableVersion(this.fitableVersion)
                .tagValues(this.tagLengthValues)
                .build();
        assertThat(this.metadata).isEqualTo(build);
    }

    @Test
    @DisplayName("提供 RequestMetadata 类 copy 方法时，返回正常信息")
    void givenRequestMetadataWhenCopyThenReturnMetadata() {
        RequestMetadata build = RequestMetadata.custom()
                .dataFormat(this.dataFormat)
                .genericableId(this.genericableId)
                .genericableVersion(this.genericableVersion)
                .fitableId(this.fitableId)
                .fitableVersion(this.fitableVersion)
                .tagValues(this.tagLengthValues)
                .build();
        RequestMetadata requestMetadata = build.copy().build();
        assertThat(requestMetadata).isEqualTo(this.metadata);
    }

    @Test
    @DisplayName("提供 RequestMetadata 类 deserialize 方法时，返回正常信息")
    void givenRequestMetadataShouldReturnMetadata() {
        RequestMetadata build = RequestMetadata.custom()
                .dataFormat(this.dataFormat)
                .genericableId(this.genericableId)
                .genericableVersion(this.genericableVersion)
                .fitableId(this.fitableId)
                .fitableVersion(this.fitableVersion)
                .tagValues(this.tagLengthValues)
                .build();
        RequestMetadata requestMetadata = RequestMetadata.deserialize(build.serialize());
        assertThat(requestMetadata).isEqualTo(build);
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    @DisplayName("提供 DefaultRequestMetadata 类 equals 方法与不同类型比较时，返回 false")
    void givenOtherTypeRequestMetadataShouldReturnFalse() {
        RequestMetadata newBuild = RequestMetadata.custom()
                .dataFormat(this.dataFormat)
                .genericableId(this.genericableId)
                .genericableVersion(this.genericableVersion)
                .fitableId(this.fitableId)
                .fitableVersion(this.fitableVersion)
                .tagValues(null)
                .build();
        DefaultRequestMetadata.Builder build = new DefaultRequestMetadata.Builder(newBuild);
        assertThat(this.metadata.equals(build)).isFalse();
    }

    @Test
    @DisplayName("提供 DefaultRequestMetadata 类 toString 方法时，返回正常信息")
    void givenDefaultRequestMetadataShouldReturnStringValue() {
        String meta = this.metadata.toString();
        assertThat(meta).contains(this.genericableId);
    }

    @Test
    @DisplayName("提供 RequestMetadata 类序列化方法时，返回正常信息")
    void givenRequestMetadataWhenSerializeThenReturnMetadata() throws IOException {
        RequestMetadata actualMetadata = RequestMetadata.custom()
                .dataFormat(this.dataFormat)
                .genericableId(this.genericableId)
                .genericableVersion(this.genericableVersion)
                .fitableId(this.fitableId)
                .fitableVersion(this.fitableVersion)
                .tagValues(null)
                .build();
        DefaultRequestMetadata.Serializer serializer = DefaultRequestMetadata.Serializer.INSTANCE;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            serializer.serialize(actualMetadata, out);
            try (InputStream in = new ByteArrayInputStream(out.toByteArray())) {
                RequestMetadata expected = serializer.deserialize(in);
                assertThat(actualMetadata).isEqualTo(expected);
            }
        }
    }
}
