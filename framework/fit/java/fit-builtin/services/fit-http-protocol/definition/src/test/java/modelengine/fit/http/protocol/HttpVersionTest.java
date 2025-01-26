/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * {@link HttpVersion} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-07-10
 */
@DisplayName("测试 HttpVersion")
public class HttpVersionTest {
    @Nested
    @DisplayName("当调用 from(String version) 方法时")
    class WhenFrom {
        @DisplayName("当版本号为标准时，返回标准的版本枚举")
        @ParameterizedTest(name = "{0} 属于 HttpVersion 的枚举")
        @ValueSource(strings = {"HTTP/1.0", "HTTP/1.1"})
        void givenStandardVersionThenReturnStandardHttpVersion(String version) {
            HttpVersion actual = HttpVersion.from(version);
            assertThat(actual).isIn(Arrays.asList(HttpVersion.values()));
        }

        @Test
        @DisplayName("当版本号为未知时，返回 null")
        void givenUnknownVersionThenReturnNull() {
            HttpVersion actual = HttpVersion.from("unknown");
            assertThat(actual).isNull();
        }
    }

    @Nested
    @DisplayName("当给定具体的枚举对象时")
    class GivenEnum {
        @DisplayName("调用 Get 方法获取对应的属性值")
        @ParameterizedTest(name = "{0} 的协议为 {1}，主版本号为 {2}，次版本号为 {3}，显示字符串为 {4}")
        @MethodSource("modelengine.fit.http.protocol.HttpVersionTest#httpVersionProvider")
        void whenGetProtocolThenReturnCorrectProtocol(HttpVersion httpVersion, String protocol, int major, int minor,
                String display) {
            assertThat(httpVersion.protocol()).isEqualTo(protocol);
            assertThat(httpVersion.major()).isEqualTo(major);
            assertThat(httpVersion.minor()).isEqualTo(minor);
            assertThat(httpVersion.toString()).isEqualTo(display);
        }
    }

    static Stream<Arguments> httpVersionProvider() {
        return Stream.of(arguments(HttpVersion.HTTP_1_0, "HTTP", 1, 0, "HTTP/1.0"),
                arguments(HttpVersion.HTTP_1_1, "HTTP", 1, 1, "HTTP/1.1"));
    }
}
