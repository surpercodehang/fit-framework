/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.cbor;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link CborDecoder} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-01-28
 */
@DisplayName("测试 CborDecoder")
public class CborDecoderTest {
    private CborDecoder decoder;

    @BeforeEach
    void setup() {
        this.decoder = new CborDecoder();
    }

    @AfterEach
    void teardown() {
        this.decoder = null;
    }

    @Test
    @DisplayName("当输入为小于 24 的非负整数序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectUnsignedNumberLessThan24() throws IOException {
        byte[] encoded = convertHexStringToByteArray("00");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo(0L);
    }

    @Test
    @DisplayName("当输入为 1 个字节正整数序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrect1ByteUnsignedNumber() throws IOException {
        byte[] encoded = convertHexStringToByteArray("1818");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo(24L);
    }

    @Test
    @DisplayName("当输入为 2 个字节的正整数序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrect2BytesUnsignedNumber() throws IOException {
        byte[] encoded = convertHexStringToByteArray("190100");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo(0x01_00L);
    }

    @Test
    @DisplayName("当输入为 4 个字节的正整数序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrect4BytesUnsignedNumber() throws IOException {
        byte[] encoded = convertHexStringToByteArray("1A01000000");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo(0x01_00_00_00L);
    }

    @Test
    @DisplayName("当输入为 8 个字节的正整数序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrect8BytesUnsignedNumber() throws IOException {
        byte[] encoded = convertHexStringToByteArray("1B0100000000000000");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo(0x01_00_00_00_00_00_00_00L);
    }

    @Test
    @DisplayName("当输入为 1 个字节的负整数序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrect1ByteNegativeNumber() throws IOException {
        byte[] encoded = convertHexStringToByteArray("3863");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo(-100L);
    }

    @Test
    @DisplayName("当输入为字节数组序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectBytes() throws IOException {
        byte[] encoded = convertHexStringToByteArray("4568656C6C6F");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo("hello".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("当输入为字符串序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectString() throws IOException {
        byte[] encoded = convertHexStringToByteArray("6568656C6C6F");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo("hello");
    }

    @Test
    @DisplayName("当输入为嵌套数组序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectNestedArray() throws IOException {
        byte[] encoded = convertHexStringToByteArray("838101820203820405");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isInstanceOf(List.class).asList().hasSize(3);
        List<Object> list = cast(decoded);
        assertThat(list.get(0)).isInstanceOf(List.class).asList().hasSize(1).contains(1L);
        assertThat(list.get(1)).isInstanceOf(List.class).asList().hasSize(2).contains(2L, 3L);
        assertThat(list.get(2)).isInstanceOf(List.class).asList().hasSize(2).contains(4L, 5L);
    }

    @Test
    @DisplayName("当输入为数组序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectIntArray() throws IOException {
        byte[] encoded = convertHexStringToByteArray("8318181819181A");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isInstanceOf(List.class).asList().hasSize(3).contains(24L, 25L, 26L);
    }

    @Test
    @DisplayName("当输入为整数键值对序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectIntMap() throws IOException {
        byte[] encoded = convertHexStringToByteArray("A201020304");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isInstanceOf(Map.class);
        Map<Long, Long> map = cast(decoded);
        assertThat(map).hasSize(2).containsEntry(1L, 2L).containsEntry(3L, 4L);
    }

    @Test
    @DisplayName("当输入为键值不同类型的键值对序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectNestedMap() throws IOException {
        byte[] encoded = convertHexStringToByteArray("A26161016162182C");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isInstanceOf(Map.class)
                .hasFieldOrPropertyWithValue("a", 1L)
                .hasFieldOrPropertyWithValue("b", 44L);
    }

    @Test
    @DisplayName("当输入为单精度浮点数序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectFloat() throws IOException {
        byte[] encoded = convertHexStringToByteArray("FA40490E56");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo(3.1415f);
    }

    @Test
    @DisplayName("当输入为双精度浮点数序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectDouble() throws IOException {
        byte[] encoded = convertHexStringToByteArray("FB400921FB54442D18");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo(3.141592653589793D);
    }

    @Test
    @DisplayName("当输入为 null 序列化后的二进制数组时，返回 null")
    void shouldReturnNull() throws IOException {
        byte[] encoded = convertHexStringToByteArray("F6");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isNull();
    }

    @Test
    @DisplayName("当输入为 false 序列化后的二进制数组时，返回 false")
    void shouldReturnFalse() throws IOException {
        byte[] encoded = convertHexStringToByteArray("F4");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isInstanceOf(Boolean.class);
        assertThat((boolean) decoded).isFalse();
    }

    @Test
    @DisplayName("当输入为 true 序列化后的二进制数组时，返回 true")
    void shouldReturnTrue() throws IOException {
        byte[] encoded = convertHexStringToByteArray("F5");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isInstanceOf(Boolean.class);
        assertThat((boolean) decoded).isTrue();
    }

    @Test
    @DisplayName("当输入为 Json 字符串序列化后的二进制数组时，返回正确的反序列化值")
    void shouldReturnCorrectJsonString() throws IOException {
        byte[] encoded = convertHexStringToByteArray(
                "782A7B226E616D65223A224A6F686E222C22616765223A33302C2263697479223A224E657720596F726B227D");
        Object decoded = this.decoder.decode(encoded);
        assertThat(decoded).isEqualTo("{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}");
    }

    private static byte[] convertHexStringToByteArray(String hexString) {
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            int index = i * 2;
            int value = Integer.parseInt(hexString.substring(index, index + 2), 16);
            byteArray[i] = (byte) value;
        }
        return byteArray;
    }
}
