/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.cbor;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.DigitUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link CborEncoder} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-01-26
 */
@DisplayName("测试 CborEncoder")
public class CborEncoderTest {
    private CborEncoder encoder;

    @BeforeEach
    void setup() {
        this.encoder = new CborEncoder();
    }

    @AfterEach
    void teardown() {
        this.encoder = null;
    }

    @Test
    @DisplayName("当输入数字小于 24 时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsLessThan24AndUnsigned() throws IOException {
        byte[] encoded = this.encoder.encode(0);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("00");
    }

    @Test
    @DisplayName("当输入数字为 1 个字节的正整数时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIs1ByteUnsignedNumber() throws IOException {
        byte[] encoded = this.encoder.encode(24);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("1818");
    }

    @Test
    @DisplayName("当输入数字为 2 个字节的正整数时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIs2BytesUnsignedNumber() throws IOException {
        byte[] encoded = this.encoder.encode(0x01_00);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("190100");
    }

    @Test
    @DisplayName("当输入数字为 4 个字节的正整数时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIs4BytesUnsignedNumber() throws IOException {
        byte[] encoded = this.encoder.encode(0x01_00_00_00);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("1A01000000");
    }

    @Test
    @DisplayName("当输入数字为 8 个字节的正整数时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIs8BytesUnsignedNumber() throws IOException {
        byte[] encoded = this.encoder.encode(0x01_00_00_00_00_00_00_00L);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("1B0100000000000000");
    }

    @Test
    @DisplayName("当输入数字为 1 个字节的负整数时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIs1ByteNegativeNumber() throws IOException {
        byte[] encoded = this.encoder.encode(-100);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("3863");
    }

    @Test
    @DisplayName("当输入字节数组时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsByteArray() throws IOException {
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("4568656C6C6F");
    }

    @Test
    @DisplayName("当输入字符串时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsString() throws IOException {
        String data = "hello";
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("6568656C6C6F");
    }

    @Test
    @DisplayName("当输入嵌套数组时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsNestedArray() throws IOException {
        int[][] data = {{1}, {2, 3}, {4, 5}};
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("838101820203820405");
    }

    @Test
    @DisplayName("当输入数组时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsIntArray() throws IOException {
        int[] data = {24, 25, 26};
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("8318181819181A");
    }

    @Test
    @DisplayName("当输入整数键值对时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsIntMap() throws IOException {
        Map<Integer, Integer> data = new HashMap<>();
        data.put(1, 2);
        data.put(3, 4);
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("A201020304");
    }

    @Test
    @DisplayName("当输入键值不同类型的键值对时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsNestedMap() throws IOException {
        Map<String, Integer> data = new HashMap<>();
        data.put("a", 1);
        data.put("b", 44);
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("A26161016162182C");
    }

    @Test
    @DisplayName("当输入单精度浮点数时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsFloat() throws IOException {
        float data = 3.1415f;
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("FA40490E56");
    }

    @Test
    @DisplayName("当输入双精度浮点数时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsDouble() throws IOException {
        double data = 3.141592653589793d;
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("FB400921FB54442D18");
    }

    @Test
    @DisplayName("当输入 BigDecimal 时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsBigDecimal() throws IOException {
        BigDecimal data = new BigDecimal("3.141592653589793");
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("FB400921FB54442D18");
    }

    @Test
    @DisplayName("当输入简单类型为 null 时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsNull() throws IOException {
        byte[] encoded = this.encoder.encode(null);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("F6");
    }

    @Test
    @DisplayName("当输入简单类型为 false 时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsFalse() throws IOException {
        byte[] encoded = this.encoder.encode(false);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("F4");
    }

    @Test
    @DisplayName("当输入简单类型为 true 时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsTrue() throws IOException {
        byte[] encoded = this.encoder.encode(true);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo("F5");
    }

    @Test
    @DisplayName("当输入 Json 数据时，返回正确的序列化值")
    void shouldReturnCorrectBytesWhenDataIsJsonString() throws IOException {
        String data = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        byte[] encoded = this.encoder.encode(data);
        String actual = DigitUtils.toHex(encoded);
        assertThat(actual).isEqualTo(
                "782A7B226E616D65223A224A6F686E222C22616765223A33302C2263697479223A224E657720596F726B227D");
    }
}
