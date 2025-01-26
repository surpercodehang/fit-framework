/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.serialization.TagLengthValues;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link DefaultTagLengthValues} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-17
 */
@DisplayName("测试 DefaultTagLengthValues 类以及相关类")
class DefaultTagLengthValuesTest {
    private final DefaultTagLengthValues tagLengthValues = new DefaultTagLengthValues();

    @Test
    @DisplayName("提供 DefaultTagLengthValues 类添加空 tag 列表时，返回空信息")
    void givenDefaultTagLengthValuesWhenAddEmptyTagsThenReturnEmpty() {
        Map<Integer, byte[]> tagValues = new HashMap<>();
        this.tagLengthValues.putTags(tagValues);
        assertThat(this.tagLengthValues.getTags()).isEmpty();
    }

    @Test
    @DisplayName("提供 DefaultTagLengthValues 类添加 tag 列表时，返回 tag 信息")
    void givenDefaultTagLengthValuesWhenAddTagsThenReturnTags() {
        Map<Integer, byte[]> tagValues = new HashMap<>();
        byte[] bytes = {1, 2, 3};
        tagValues.put(1, bytes);
        this.tagLengthValues.putTags(tagValues);
        assertThat(this.tagLengthValues.getTags()).hasSize(1);
    }

    @Test
    @DisplayName("提供 DefaultTagLengthValues 类删除 tag 列表时，返回空信息")
    void givenDefaultTagLengthValuesWhenRemoveTagsThenReturnEmpty() {
        Map<Integer, byte[]> tagValues = new HashMap<>();
        byte[] bytes = {1, 2, 3};
        tagValues.put(1, bytes);
        this.tagLengthValues.putTags(tagValues);
        this.tagLengthValues.remove(1);
        assertThat(this.tagLengthValues.getTags()).isEmpty();
    }

    @Test
    @DisplayName("提供 Serializer 类序列化时，返回正常信息")
    void givenSerializerWhenSerializeThenReturnTagValue() throws IOException {
        Map<Integer, byte[]> tagValues = new HashMap<>();
        byte[] bytes = {1, 2, 3};
        tagValues.put(1, bytes);
        TagLengthValues comparedTagLengthValues = new DefaultTagLengthValues();
        comparedTagLengthValues.putTags(tagValues);
        DefaultTagLengthValues.Serializer serializer = DefaultTagLengthValues.Serializer.INSTANCE;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            serializer.serialize(comparedTagLengthValues, out);
            try (InputStream in = new ByteArrayInputStream(out.toByteArray())) {
                TagLengthValues expected = serializer.deserialize(in);
                assertThat(comparedTagLengthValues).usingRecursiveComparison().isEqualTo(expected);
            }
        }
    }

    @Test
    @DisplayName("提供 TagLengthValues 类序列化时，返回正常信息")
    void givenTagLengthValuesWhenSerializeThenReturnTagValue() {
        Map<Integer, byte[]> tagValues = new HashMap<>();
        byte[] bytes = {1, 2, 3};
        tagValues.put(1, bytes);
        TagLengthValues comparedTagLengthValues = new DefaultTagLengthValues();
        comparedTagLengthValues.putTags(tagValues);
        TagLengthValues deserialize = TagLengthValues.deserialize(comparedTagLengthValues.serialize());
        assertThat(comparedTagLengthValues).usingRecursiveComparison().isEqualTo(deserialize);
    }
}