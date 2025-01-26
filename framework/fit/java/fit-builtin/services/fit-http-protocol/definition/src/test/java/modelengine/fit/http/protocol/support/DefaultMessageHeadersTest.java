/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.MessageHeaders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * {@link DefaultMessageHeaders} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-07-10
 */
@DisplayName("测试 DefaultMessageHeaders")
public class DefaultMessageHeadersTest {
    @Nested
    @DisplayName("测试只读接口 MessageHeaders 的方法")
    class TestMessageHeaders {
        @Nested
        @DisplayName("当调用 names() 方法时")
        class WhenNames {
            @Test
            @DisplayName("当消息头中仅含有 1 组消息时，返回这 1 组消息的键")
            void givenOnly1PairHeaderThenReturnItsKey() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                List<String> actual = headers.names();
                assertThat(actual).isNotEmpty().hasSize(1).containsSequence("key");
            }
        }

        @Nested
        @DisplayName("当调用 contains(String name) 方法时")
        class WhenContains {
            @Test
            @DisplayName("当消息头中包含指定消息时，返回 true")
            void givenHeaderExistThenReturnTrue() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                boolean isContains = headers.contains("key");
                assertThat(isContains).isTrue();
            }
        }

        @Nested
        @DisplayName("当调用 first(String name) 方法时")
        class WhenFirst {
            @Test
            @DisplayName("当获取单值消息时，返回这组消息的唯一值")
            void givenUniValueHeaderThenReturnItsUniqueValue() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                Optional<String> actual = headers.first("key");
                assertThat(actual).isNotEmpty().get().asString().isEqualTo("value");
            }

            @Test
            @DisplayName("当获取多值消息时，返回这组消息的第一个值")
            void givenMultiValuesHeaderThenReturnItsFirstValue() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", Arrays.asList("v1", "v2"));
                Optional<String> actual = headers.first("key");
                assertThat(actual).isNotEmpty().get().asString().isEqualTo("v1");
            }

            @Test
            @DisplayName("当获取不存在的消息时，返回 null")
            void givenNonexistentHeaderThenReturnNull() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                Optional<String> actual = headers.first("nonexistent");
                assertThat(actual).isEmpty();
            }
        }

        @Nested
        @DisplayName("当调用 require(String name) 方法时")
        class WhenRequire {
            @Test
            @DisplayName("当获取单值消息时，返回这组消息的唯一值")
            void givenUniValueHeaderThenReturnItsUniqueValue() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                String actual = headers.require("key");
                assertThat(actual).isEqualTo("value");
            }

            @Test
            @DisplayName("当获取多值消息时，返回这组消息的第一个值")
            void givenMultiValuesHeaderThenReturnItsFirstValue() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", Arrays.asList("v1", "v2"));
                String actual = headers.require("key");
                assertThat(actual).isEqualTo("v1");
            }

            @Test
            @DisplayName("当获取不存在的消息时，抛出异常")
            void givenNonexistentHeaderThenThrowException() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                IllegalStateException actual =
                        catchThrowableOfType(() -> headers.require("nonexistent"), IllegalStateException.class);
                assertThat(actual).hasMessage("No specified header. [header=nonexistent]");
            }
        }

        @Nested
        @DisplayName("当调用 all(String name) 方法时")
        class WhenAll {
            @Test
            @DisplayName("当获取单值消息时，返回这组消息的唯一值")
            void givenUniValueHeaderThenReturnItsUniqueValue() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                List<String> actual = headers.all("key");
                assertThat(actual).isNotEmpty().hasSize(1).containsSequence("value");
            }

            @Test
            @DisplayName("当获取多值消息时，返回这组消息的所有值")
            void givenMultiValuesHeaderThenReturnAllItsValues() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", Arrays.asList("v1", "v2"));
                List<String> actual = headers.all("key");
                assertThat(actual).isNotEmpty().hasSize(2).containsSequence("v1", "v2");
            }

            @Test
            @DisplayName("当获取不存在的消息时，返回空列表")
            void givenNonexistentHeaderThenReturnEmpty() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                List<String> actual = headers.all("nonexistent");
                assertThat(actual).hasSize(0);
            }
        }

        @Nested
        @DisplayName("当调用 size() 方法时")
        class WhenSize {
            @Test
            @DisplayName("当消息头中仅含有 1 组消息时，返回 1")
            void givenOnly1PairHeaderThenReturn1() {
                MessageHeaders headers = ConfigurableMessageHeaders.create().set("key", "value");
                int actual = headers.size();
                assertThat(actual).isEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("测试可写接口 ConfigurableMessageHeaders 的方法")
    class TestConfigurableMessageHeaders {
        @Nested
        @DisplayName("当调用 add(String name, String header) 方法时")
        class WhenAdd {
            @Test
            @DisplayName("当添加的值为空白字符串时，该消息头没有变化")
            void givenValueIsBlankThenPreValuesNotChanged() {
                ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create().add("key", "value");
                assertThat(headers.all("key")).hasSize(1);
                headers.add("key", "");
                assertThat(headers.all("key")).hasSize(1);
            }

            @Test
            @DisplayName("当对一个不存在的消息头添加值后，该消息头拥有 1 个值")
            void givenKeyIsNonexistentThenValuesSizeIncrBy1() {
                ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create();
                assertThat(headers.all("key")).hasSize(0);
                headers.add("key", "value");
                assertThat(headers.all("key")).hasSize(1);
            }

            @Test
            @DisplayName("当对一个存在的消息头添加值后，该消息头值数量增加 1")
            void givenKeyIsExistentThenValuesSizeIncrBy1() {
                ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create().add("key", "value");
                assertThat(headers.all("key")).hasSize(1);
                headers.add("key", "value");
                assertThat(headers.all("key")).hasSize(2);
            }
        }

        @Nested
        @DisplayName("测试设值方法")
        class TestSet {
            @Nested
            @DisplayName("当调用 set(String name, String header) 方法时")
            class WhenSetUniValue {
                @Test
                @DisplayName("当待设置的键为空白字符串时，抛出 IllegalArgumentException")
                void givenKeyIsBlankThanThrowIllegalArgumentException() {
                    ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create();
                    IllegalArgumentException exception =
                            catchThrowableOfType(() -> headers.set("", "value"), IllegalArgumentException.class);
                    assertThat(exception).isNotNull().hasMessage("The name of header cannot be blank.");
                }
            }

            @Nested
            @DisplayName("当调用 set(String name, List<String> headers) 方法时")
            class WhenSetMultiValues {
                @Test
                @DisplayName("当对一个存在的消息头设置空后，该消息头被清除")
                void givenKeyIsExistentAndValueIsNullThenTheKeyCleared() {
                    ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create().add("key", "value");
                    assertThat(headers.all("key")).hasSize(1);
                    headers.set("key", (List<String>) null);
                    assertThat(headers.all("key")).hasSize(0);
                }
            }
        }

        @Nested
        @DisplayName("当调用 clear(String name) 方法时")
        class WhenClear {
            @Test
            @DisplayName("当对一个存在的消息头清除后，该消息头被清除")
            void givenKeyIsExistentThenTheKeyCleared() {
                ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create().add("key", "value");
                assertThat(headers.all("key")).hasSize(1);
                headers = headers.clear("key");
                assertThat(headers.all("key")).hasSize(0);
            }
        }
    }
}
