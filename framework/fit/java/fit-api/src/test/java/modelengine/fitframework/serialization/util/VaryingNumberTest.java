/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.serialization.ByteSerializer;
import modelengine.fitframework.util.Convert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * {@link VaryingNumber} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-21
 */
@DisplayName("测试 VaryingNumber 接口")
class VaryingNumberTest {
    @Test
    @DisplayName("提供 Serializer 类序列化时，返回正常信息")
    void givenSerializerWhenSerializeThenReturnValue() throws IOException {
        ByteSerializer<VaryingNumber> serializer = VaryingNumber.serializer();
        int expected = 123;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            VaryingNumber varyingNumber = VaryingNumber.valueOf(expected);
            serializer.serialize(varyingNumber, out);
            try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
                Integer actual = serializer.deserialize(in).intValue();
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 byteValue 方法时，返回正常信息")
    void givenVaryingNumberWhenSerializeThenReturnByteValue() {
        long expected = 123L;
        VaryingNumber varyingNumber = VaryingNumber.valueOf(expected);
        byte[] bytes = {varyingNumber.byteValue()};
        int actual = Convert.toInteger(bytes);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 shortValue 方法时，返回正常信息")
    void givenVaryingNumberWhenSerializeThenReturnShortValue() {
        short expected = 123;
        VaryingNumber varyingNumber = VaryingNumber.valueOf(expected);
        byte[] bytes = {(byte) varyingNumber.shortValue()};
        short actual = Convert.toShort(bytes);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 longValue 方法时，返回正常信息")
    void givenVaryingNumberWhenSerializeThenReturnLongValue() {
        long expected = 123L;
        VaryingNumber varyingNumber = VaryingNumber.valueOf(expected);
        byte[] bytes = {(byte) varyingNumber.longValue()};
        long actual = Convert.toLong(bytes);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 floatValue 方法时，返回正常信息")
    void givenVaryingNumberWhenSerializeThenReturnFloatValue() {
        long expected = 123L;
        VaryingNumber varyingNumber = VaryingNumber.valueOf(expected);
        byte[] bytes = {(byte) varyingNumber.floatValue()};
        long actual = Convert.toLong(bytes);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 doubleValue 方法时，返回正常信息")
    void givenVaryingNumberWhenSerializeThenReturnDoubleValue() {
        long expected = 123L;
        VaryingNumber varyingNumber = VaryingNumber.valueOf(expected);
        byte[] bytes = {(byte) varyingNumber.doubleValue()};
        long actual = Convert.toLong(bytes);
        assertThat(actual).isEqualTo(expected);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    @DisplayName("提供 VaryingNumber 类 valueOf 方法参数为 {@code null} 时，抛出异常")
    void givenNullParameterShouldThrowException() {
        assertThatThrownBy(() -> VaryingNumber.valueOf(null)).isInstanceOf(IllegalVaryingNumberException.class);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 valueOf 方法参数在字节范围内时，抛出异常")
    void givenParameterByteErrorShouldThrowException() {
        byte[] bytes = {0, 1};
        assertThatThrownBy(() -> VaryingNumber.valueOf(bytes)).isInstanceOf(IllegalVaryingNumberException.class);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 valueOf 方法参数超过字节范围且数量唯一时，抛出异常")
    void givenOneOutOfByteParameterShouldThrowException() {
        byte[] bytes = {(byte) 129};
        assertThatThrownBy(() -> VaryingNumber.valueOf(bytes)).isInstanceOf(IllegalVaryingNumberException.class);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 valueOf 方法 7bit 参数类型时，返回正常信息")
    void given7BitParameterShouldReturnVaryingNumber() {
        byte[] bytes = {(byte) 123};
        VaryingNumber expected = VaryingNumber.valueOf(bytes);
        VaryingNumber actual = VaryingNumber.valueOf((byte) 123);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 valueOf 方法 8bit 参数类型时，返回正常信息")
    void given8BitParameterShouldReturnVaryingNumber() {
        byte[] bytes = {(byte) 129, 0x48};
        VaryingNumber expected = VaryingNumber.valueOf(bytes);
        VaryingNumber actual = VaryingNumber.valueOf((byte) 200);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 valueOf 方法字节参数类型 short 时，返回正常信息")
    void givenShortParameterShouldReturnVaryingNumber() {
        short shortValue = 1;
        VaryingNumber expected = VaryingNumber.valueOf(1);
        VaryingNumber actual = VaryingNumber.valueOf(shortValue);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 hashCode 方法时，返回正常信息")
    void givenVaryingNumberShouldReturnHasCode() {
        short shortValue = 1;
        int expected = VaryingNumber.valueOf(1).hashCode();
        int actual = VaryingNumber.valueOf(shortValue).hashCode();
        assertThat(actual).isEqualTo(expected);
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    @DisplayName("提供 VaryingNumber 类 equals 方法参数类型不同时，返回 false")
    void givenOtherParameterShouldReturnFalse() {
        VaryingNumber varyingNumber = VaryingNumber.valueOf(1);
        assertThat(Objects.equals(varyingNumber, 1)).isFalse();
    }

    @Test
    @DisplayName("提供 VaryingNumber 类 toString 方法时，返回正常信息")
    void givenVaryingNumberShouldReturnStringValue() {
        short shortValue = 1;
        String expected = VaryingNumber.valueOf(1).toString();
        String actual = VaryingNumber.valueOf(shortValue).toString();
        assertThat(actual).isEqualTo(expected);
    }
}
