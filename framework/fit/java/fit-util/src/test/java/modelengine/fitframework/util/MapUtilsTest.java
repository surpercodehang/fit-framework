/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.merge.ConflictException;
import modelengine.fitframework.merge.ConflictResolverCollection;
import modelengine.fitframework.merge.list.ListAppendConflictResolver;
import modelengine.fitframework.merge.list.ListRemoveDuplicationConflictResolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 为 {@link MapUtils} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2020-09-17
 */
@DisplayName("测试 MapUtils")
public class MapUtilsTest {
    /**
     * 目标方法：{@link MapUtils#count(Map)}。
     */
    @Nested
    @DisplayName("Test method: count(Map<K, V> map)")
    class TestCount {
        @Test
        @DisplayName("Input is null, output is 0")
        void givenNullThenReturn0() {
            int actual = MapUtils.count(null);
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("Input is {}, output is 0")
        void givenEmptyThenReturn0() {
            int actual = MapUtils.count(new HashMap<>());
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("Input is {'key': 'value'}, output is 1")
        void givenMapWith1EntryThenReturn1() {
            int actual = MapUtils.count(MapBuilder.get().put("key", "value").build());
            assertThat(actual).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Test method: flat(Map<String, Object> map, String connector)")
    class TestFlat {
        @Test
        @DisplayName("Input is null, output is empty map")
        void givenNullMapThenReturnEmptyMap() {
            Map<String, Object> map = null;
            Map<String, String> flattenedMap = MapUtils.flat(map, ".");
            assertThat(flattenedMap.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Input is empty map, output is empty map")
        void givenEmptyMapThenReturnEmptyMap() {
            Map<String, Object> map = new HashMap<>();
            Map<String, String> flattenedMap = MapUtils.flat(map, ".");
            assertThat(flattenedMap.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Input is {'k1': 'v1', 'k2': 'v2'} and connector is '.', output is  {'k1' : 'v1', 'k2' : 'v2'}")
        void givenFlattenedMapThenReturnSameMap() {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("k1", "v1");
            map1.put("k2", "v2");
            Map<String, String> flattenedMap = MapUtils.flat(map1, ".");
            assertThat(flattenedMap).hasSize(2);
            assertThat(flattenedMap.get("k1")).isEqualTo("v1");
            assertThat(flattenedMap.get("k2")).isEqualTo("v2");
        }

        @Test
        @DisplayName(
                "Input is {'k1': {'k2' : {'k4' : 'v4', 'k5' : 'v5'}, {'k3': { 'k6' : 'v6'}}}} and connector is '.', "
                        + "output is {'k1.k2.k4' : 'v4', 'k1.k2.k5' : 'v5', 'k1.k3.k6' : 'v6'}")
        void givenHierarchicalMapThenReturnFlattenedMap() {
            Map<String, String> map1 = new HashMap<>();
            map1.put("k4", "v4");
            map1.put("k5", "v5");
            Map<String, Object> map2 = new HashMap<>();
            map2.put("k2", map1);
            Map<String, String> map3 = new HashMap<>();
            map3.put("k6", "v6");
            map2.put("k3", map3);
            Map<String, Object> map4 = new HashMap<>();
            map4.put("k1", map2);
            Map<String, String> flattenedMap = MapUtils.flat(map4, ".");
            assertThat(flattenedMap).hasSize(3);
            assertThat(flattenedMap.get("k1.k2.k4")).isEqualTo("v4");
            assertThat(flattenedMap.get("k1.k2.k5")).isEqualTo("v5");
            assertThat(flattenedMap.get("k1.k3.k6")).isEqualTo("v6");
        }
    }

    /**
     * 目标方法：{@link MapUtils#getIfEmpty(Map, Supplier)}。
     */
    @Nested
    @DisplayName("Test method: getIfEmpty(Map<K, V> map, Supplier<Map<K, V>> factory)")
    class TestGetIfEmpty {
        @Test
        @DisplayName("Input is null, output is supplier.get()")
        void givenMapNullThenReturnSupplierResult() {
            Map<String, Integer> actual =
                    MapUtils.getIfEmpty(null, () -> MapBuilder.<String, Integer>get().put("Hello", 10086).build());
            assertThat(actual).hasSize(1).extracting("Hello").isEqualTo(10086);
        }

        @Test
        @DisplayName("Input is {'Hello': 10086}, output is {'Hello': 10086}")
        void givenMapIsNotNullThenReturnItself() {
            Map<String, Integer> map = MapBuilder.<String, Integer>get().put("Hello", 10086).build();
            Map<String, Integer> actual = MapUtils.getIfEmpty(map, HashMap::new);
            assertThat(actual).hasSize(1).extracting("Hello").isEqualTo(10086);
        }
    }

    /**
     * 目标方法：{@link MapUtils#isEmpty(Map)}。
     */
    @Nested
    @DisplayName("Test method: isEmpty(Map<K, V> map)")
    class TestIsEmpty {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is true")
        void givenNullThenReturnTrue() {
            boolean actual = MapUtils.isEmpty(null);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is {}, output is true")
        void givenEmptyThenReturnTrue() {
            boolean actual = MapUtils.isEmpty(new HashMap<>());
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is {'key': 'value'}, output is false")
        void givenNotEmptyThenReturnFalse() {
            boolean actual = MapUtils.isEmpty(MapBuilder.get().put("key", "value").build());
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link MapUtils#isNotEmpty(Map)}。
     */
    @Nested
    @DisplayName("Test method: isNotEmpty(Map<K, V> map)")
    class TestIsNotEmpty {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is false")
        void givenNullThenReturnFalse() {
            boolean actual = MapUtils.isNotEmpty(null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is {}, output is false")
        void givenEmptyThenReturnFalse() {
            boolean actual = MapUtils.isNotEmpty(new HashMap<>());
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is {'key': 'value'}, output is true")
        void givenNotEmptyThenReturnTrue() {
            boolean actual = MapUtils.isNotEmpty(MapBuilder.get().put("key", "value").build());
            assertThat(actual).isTrue();
        }
    }

    @Nested
    @DisplayName("测试合并方法")
    class TestMerge {
        @Nested
        @DisplayName("测试方法：merge(Map<K, V> first, Map<K, V> second)")
        class TestSimpleMerge {
            @Test
            @DisplayName("当没有冲突时，返回合并完成的键值对")
            void shouldReturnMergedMapWhenNoConflict() {
                // given
                Map<String, Object> m1 = MapBuilder.<String, Object>get()
                        .put("a", 1)
                        .put("b", MapBuilder.get().put("b1", 2).build())
                        .build();
                Map<String, Object> m2 = MapBuilder.<String, Object>get()
                        .put("c", 1)
                        .put("b", MapBuilder.get().put("b2", 2).build())
                        .build();

                // when
                Map<String, Object> merged = MapUtils.merge(m1, m2);

                // then
                assertThat(merged).isNotEmpty()
                        .hasSize(3)
                        .hasFieldOrPropertyWithValue("a", 1)
                        .hasFieldOrPropertyWithValue("c", 1)
                        .extracting("b")
                        .isInstanceOf(Map.class)
                        .hasFieldOrPropertyWithValue("b1", 2)
                        .hasFieldOrPropertyWithValue("b2", 2);
            }

            @Test
            @DisplayName("当存在列表元素冲突时，抛出 ConflictException")
            void shouldThrowExceptionWhenListConflict() {
                // given
                Map<String, Object> m1 = MapBuilder.<String, Object>get().put("a", Arrays.asList(1, 2)).build();
                Map<String, Object> m2 = MapBuilder.<String, Object>get().put("a", Arrays.asList(2, 3)).build();

                // when
                ConflictException exception =
                        catchThrowableOfType(() -> MapUtils.merge(m1, m2), ConflictException.class);

                // then
                assertThat(exception).isNotNull()
                        .hasMessage("Conflict in merge map process. [key=a, v1=[1, 2], v2=[2, 3]]");
            }

            @Test
            @DisplayName("当存在第二级元素冲突时，抛出 ConflictException")
            void shouldThrowExceptionWhenSubElementConflict() {
                // given
                Map<String, Object> m1 =
                        MapBuilder.<String, Object>get().put("a", MapBuilder.get().put("b", 2).build()).build();
                Map<String, Object> m2 =
                        MapBuilder.<String, Object>get().put("a", MapBuilder.get().put("b", 2).build()).build();

                // when
                ConflictException exception =
                        catchThrowableOfType(() -> MapUtils.merge(m1, m2), ConflictException.class);

                // then
                assertThat(exception).isNotNull().hasMessage("Conflict in merge map process. [key=a.b, v1=2, v2=2]");
            }
        }

        @Nested
        @DisplayName("测试方法：merge(Map<K, V> first, Map<K, V> second, ConflictResolver.Registry registry)")
        class TestBaseMerge {
            @Nested
            @DisplayName("当给定列表元素追加的冲突处理器时")
            class GivenListAppendConflictResolver {
                ConflictResolverCollection registry;

                @BeforeEach
                void setup() {
                    this.registry = ConflictResolverCollection.create();
                    this.registry.add(List.class, ObjectUtils.cast(new ListAppendConflictResolver<>()));
                }

                @Test
                @DisplayName("当存在列表元素冲突时，返回合并完成的键值对")
                void shouldReturnMergedMapWhenListConflict() {
                    // given
                    Map<String, Object> m1 = MapBuilder.<String, Object>get().put("a", Arrays.asList(1, 2)).build();
                    Map<String, Object> m2 = MapBuilder.<String, Object>get().put("a", Arrays.asList(2, 3)).build();

                    // when
                    Map<String, Object> merged = MapUtils.merge(m1, m2, this.registry);

                    // then
                    assertThat(merged).isNotEmpty()
                            .hasSize(1)
                            .extracting("a")
                            .isInstanceOf(List.class)
                            .asList()
                            .containsSequence(1, 2, 2, 3);
                }
            }

            @Nested
            @DisplayName("当给定列表元素去重的冲突处理器时")
            class GivenListRemoveDuplicationConflictResolver {
                ConflictResolverCollection registry;

                @BeforeEach
                void setup() {
                    this.registry = ConflictResolverCollection.create();
                    this.registry.add(List.class, ObjectUtils.cast(new ListRemoveDuplicationConflictResolver<>()));
                }

                @Test
                @DisplayName("当存在列表元素冲突时，返回合并完成的键值对")
                void shouldReturnMergedMapWhenListConflict() {
                    // given
                    Map<String, Object> m1 = MapBuilder.<String, Object>get().put("a", Arrays.asList(1, 2)).build();
                    Map<String, Object> m2 = MapBuilder.<String, Object>get().put("a", Arrays.asList(2, 3)).build();

                    // when
                    Map<String, Object> merged = MapUtils.merge(m1, m2, this.registry);

                    // then
                    assertThat(merged).isNotEmpty()
                            .hasSize(1)
                            .extracting("a")
                            .isInstanceOf(List.class)
                            .asList()
                            .containsSequence(1, 2, 3);
                }
            }
        }
    }
}
