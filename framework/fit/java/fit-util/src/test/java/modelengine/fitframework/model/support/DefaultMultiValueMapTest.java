/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import modelengine.fitframework.model.MultiValueMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * {@link DefaultMultiValueMap} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-08-06
 */
@DisplayName("测试 DefaultMultiValueMap")
public class DefaultMultiValueMapTest {
    private MultiValueMap<String, Integer> multiValueMap;

    @BeforeEach
    void setup() {
        this.multiValueMap = MultiValueMap.create(LinkedHashMap::new);
    }

    @AfterEach
    void teardown() {
        this.multiValueMap = null;
    }

    @Test
    @DisplayName("当获取一个键的第一个值时，返回 null")
    void shouldReturnNullWhenGetFirst() {
        Integer first = this.multiValueMap.getFirst("k");
        assertThat(first).isNull();
    }

    @Nested
    @DisplayName("在添加了一个键的一系列值之后")
    class AfterAddAll {
        @BeforeEach
        void setup() {
            List<Integer> values = new ArrayList<>();
            values.add(1);
            values.add(2);
            DefaultMultiValueMapTest.this.multiValueMap.addAll("k", values);
        }

        @Test
        @DisplayName("当获取一个键的第一个值时，返回其第一个值")
        void shouldReturnTheFirstValueWhenGetFirst() {
            Integer first = DefaultMultiValueMapTest.this.multiValueMap.getFirst("k");
            assertThat(first).isEqualTo(1);
            List<Integer> values = DefaultMultiValueMapTest.this.multiValueMap.get("k");
            assertThat(values).hasSize(2);
        }

        @Test
        @DisplayName("当待添加的列表为空时，添加方法直接返回")
        void shouldReturnWhenValuesIsEmpty() {
            assertDoesNotThrow(() -> DefaultMultiValueMapTest.this.multiValueMap.addAll("k", Collections.emptyList()));
        }

        @Nested
        @DisplayName("在设置了一个键的一个值之后")
        class AfterSet {
            @BeforeEach
            void setup() {
                DefaultMultiValueMapTest.this.multiValueMap.set("k", 2);
            }

            @Test
            @DisplayName("当获取一个键的第一个值时，返回其第一个值")
            void shouldReturnTheFirstValueWhenGetFirst() {
                Integer first = DefaultMultiValueMapTest.this.multiValueMap.getFirst("k");
                assertThat(first).isEqualTo(2);
                List<Integer> values = DefaultMultiValueMapTest.this.multiValueMap.get("k");
                assertThat(values).hasSize(1);
            }
        }
    }

    @Nested
    @DisplayName("在设置了一个键的一个值之后")
    class AfterPut {
        @BeforeEach
        void setup() {
            List<Integer> values = new ArrayList<>();
            values.add(1);
            values.add(2);
            DefaultMultiValueMapTest.this.multiValueMap.put("k", values);
        }

        @Test
        @DisplayName("当获取一个键的第一个值时，返回其第一个值")
        void shouldReturnTheFirstValueWhenGetFirst() {
            Integer first = DefaultMultiValueMapTest.this.multiValueMap.getFirst("k");
            assertThat(first).isEqualTo(1);
            List<Integer> values = DefaultMultiValueMapTest.this.multiValueMap.get("k");
            assertThat(values).hasSize(2);
        }

        @Nested
        @DisplayName("在添加了一个键的一个值之后")
        class AfterAdd {
            @BeforeEach
            void setup() {
                DefaultMultiValueMapTest.this.multiValueMap.add("k", 2);
            }

            @Test
            @DisplayName("当获取一个键的第一个值时，返回其第一个值")
            void shouldReturnTheFirstValueWhenGetFirst() {
                Integer first = DefaultMultiValueMapTest.this.multiValueMap.getFirst("k");
                assertThat(first).isEqualTo(1);
                List<Integer> values = DefaultMultiValueMapTest.this.multiValueMap.get("k");
                assertThat(values).hasSize(3);
            }
        }
    }
}
