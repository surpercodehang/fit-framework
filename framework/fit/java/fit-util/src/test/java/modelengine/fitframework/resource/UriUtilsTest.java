/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.charset.StandardCharsets;

/**
 * 表示 {@link UriUtils} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-12-23
 */
@DisplayName("测试 UriUtils")
public class UriUtilsTest {
    @Nested
    @DisplayName("测试方法：encodeQuery")
    class TestEncodeQuery {
        @ParameterizedTest
        @CsvSource({"?k=你好,?k=%E4%BD%A0%E5%A5%BD", "%,%25"})
        @DisplayName("当待编码字符不符合要求时，需要进行编码")
        void shouldEncode(String toEncode, String encoded) {
            String actual = UriUtils.encodeQuery(toEncode, StandardCharsets.UTF_8);
            assertThat(actual).isEqualTo(encoded);
        }

        @ParameterizedTest
        @CsvSource({"?k1=v1&k2=v2,?k1=v1&k2=v2", "ABC,ABC"})
        @DisplayName("当待编码字符符合要求时，不需要进行编码")
        void shouldNotEncode(String toEncode, String encoded) {
            String actual = UriUtils.encodeQuery(toEncode, StandardCharsets.UTF_8);
            assertThat(actual).isEqualTo(encoded);
        }
    }

    @Nested
    @DisplayName("测试方法：encodeFragment")
    class TestEncodeFragment {
        @ParameterizedTest
        @CsvSource({"?k=你好,?k=%E4%BD%A0%E5%A5%BD", "%,%25"})
        @DisplayName("当待编码字符不符合要求时，需要进行编码")
        void shouldEncode(String toEncode, String encoded) {
            String actual = UriUtils.encodeFragment(toEncode, StandardCharsets.UTF_8);
            assertThat(actual).isEqualTo(encoded);
        }

        @ParameterizedTest
        @CsvSource({"?k1=v1&k2=v2,?k1=v1&k2=v2", "ABC,ABC", "'',''"})
        @DisplayName("当待编码字符符合要求时，不需要进行编码")
        void shouldNotEncode(String toEncode, String encoded) {
            String actual = UriUtils.encodeFragment(toEncode, StandardCharsets.UTF_8);
            assertThat(actual).isEqualTo(encoded);
        }
    }
}
