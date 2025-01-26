/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.merge.ConflictException;
import modelengine.fitframework.merge.ConflictResolutionPolicy;
import modelengine.fitframework.merge.ConflictResolverCollection;
import modelengine.fitframework.merge.list.ListAppendConflictResolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 为 {@link CollectionUtils} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class CollectionUtilsTest {
    @Nested
    @DisplayName("Test binarySearch")
    class TestBinarySearch {
        /**
         * 目标方法：{@link CollectionUtils#binarySearch(List, Comparable)}。
         */
        @Nested
        @DisplayName("Test method: binarySearch(List<E> list, E key)")
        class TestDefaultBinarySearch {
            @Test
            @DisplayName("List is [0, 1, 2, 3, 4], key is 3, output is 3")
            void givenListWithKeyAndKeyGreaterThanMiddleThenReturnItsIndex() {
                List<Integer> list = Arrays.asList(0, 1, 2, 3, 4);
                int actual = CollectionUtils.binarySearch(list, 3);
                assertThat(actual).isEqualTo(3);
            }

            @Test
            @DisplayName("List is [0, 1, 2, 3], key is 2, output is 2")
            void givenListWithKeyAndKeyLessThanMiddleThenReturnItsIndex() {
                List<Integer> list = Arrays.asList(0, 1, 2, 3, 4);
                int actual = CollectionUtils.binarySearch(list, 1);
                assertThat(actual).isEqualTo(1);
            }

            @Test
            @DisplayName("List is [0, 2], key is 1, output is -2")
            void givenListWithoutKeyThenReturnNegative() {
                List<Integer> list = Arrays.asList(0, 2);
                int actual = CollectionUtils.binarySearch(list, 1);
                assertThat(actual).isEqualTo(-2);
            }

            @Test
            @DisplayName("List is [0, 1], key is 0, output is 0")
            void givenKeyMatchTheFirstElementInListThenReturn0() {
                List<Integer> list = Arrays.asList(0, 1);
                int actual = CollectionUtils.binarySearch(list, 0);
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("List is [0, 1], key is 1, output is 1")
            void givenKeyMatchTheLastElementInListThenReturnLengthMinus1() {
                List<Integer> list = Arrays.asList(0, 1);
                int actual = CollectionUtils.binarySearch(list, 1);
                assertThat(actual).isEqualTo(1);
            }

            @Test
            @DisplayName("List is [0, 1], key is -1, output is -1")
            void givenKeyLessThanTheFirstElementInListThenReturnMinus1() {
                List<Integer> list = Arrays.asList(0, 1);
                int actual = CollectionUtils.binarySearch(list, -1);
                assertThat(actual).isEqualTo(-1);
            }

            @Test
            @DisplayName("List is [0, 1], key is 2, output is -3")
            void givenKeyGreaterThanTheLastElementInListThenReturnMinus1MinusLength() {
                List<Integer> list = Arrays.asList(0, 1);
                int actual = CollectionUtils.binarySearch(list, 2);
                assertThat(actual).isEqualTo(-3);
            }

            @Test
            @DisplayName("List is [0], key is 1, output is -2")
            void givenKeyGreaterThanTheLastElementInListAndLengthIs1ThenReturnMinus2() {
                List<Integer> list = Collections.singletonList(0);
                int actual = CollectionUtils.binarySearch(list, 1);
                assertThat(actual).isEqualTo(-2);
            }

            @Test
            @DisplayName("List is [null, 1], key is null, output is 0")
            void givenNullIsTheFirstAndKeyIsNullThenReturn0() {
                List<Integer> list = Arrays.asList(null, 1);
                int actual = CollectionUtils.binarySearch(list, null);
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("List is [null, 1], key is null, output is 0")
            void givenNullIsTheFirstAndKeyIsInListThenReturnItsIndex() {
                List<Integer> list = Arrays.asList(null, 1);
                int actual = CollectionUtils.binarySearch(list, 1);
                assertThat(actual).isEqualTo(1);
            }

            @Test
            @DisplayName("List is null, output is exception")
            void givenListNullThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> CollectionUtils.binarySearch(null, 1),
                        IllegalArgumentException.class);
                assertThat(exception).hasMessage("The list to binary search cannot be null.");
            }

            @Test
            @DisplayName("List is [0, 1, 1, 2], key is 1, output is between [1, 2]")
            void givenListWithMultipleKeyThenReturnAnyIndex() {
                List<Integer> list = Arrays.asList(0, 1, 1, 2);
                int actual = CollectionUtils.binarySearch(list, 1);
                assertThat(actual).isBetween(1, 2);
            }
        }

        /**
         * 目标方法：{@link CollectionUtils#binarySearch(List, Comparable, Function)}。
         */
        @Nested
        @DisplayName("Test method: binarySearch(List<E> list, K key, Function<E, K> mapper)")
        class TestBinarySearchWithMapper {
            @Test
            @DisplayName("List is [], key is 0, mapper is Function.identity(), output is -1")
            void givenListIsEmptyThenReturnMinus1() {
                int actual = CollectionUtils.binarySearch(Collections.emptyList(), 0, Function.identity());
                assertThat(actual).isEqualTo(-1);
            }

            @Test
            @DisplayName("List is [], key is null, mapper is null, output is exception")
            void givenMapperIsNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> CollectionUtils.binarySearch(Collections.emptyList(), 1, null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The mapper to map element cannot be null.");
            }
        }

        /**
         * 目标方法：{@link CollectionUtils#binarySearch(List, Comparable, Function, Comparator)}。
         */
        @Nested
        @DisplayName("Test method: binarySearch(List<E> list, K key, Function<E, K> mapper, Comparator<K> comparator)")
        class TestBinarySearchWithMapperAndComparator {
            @Test
            @DisplayName(
                    "List is [], key is null, mapper is Function.identity(), comparator is null, output is exception")
            void givenComparatorIsNullThenThrowException() {
                Function<Integer, Integer> mapper = Function.identity();
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> CollectionUtils.binarySearch(Collections.emptyList(),
                                1,
                                mapper,
                                null), IllegalArgumentException.class);
                assertThat(exception).hasMessage("The comparator to compare elements cannot be null.");
            }
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#cast(Map, Function, Function)}。
     */
    @Nested
    @DisplayName("Test method: cast(Map<K1, V1> map, Function<K1, K2> keyMapper, Function<V1, V2> valueMapper")
    class TestCast {
        @Test
        @DisplayName(
                "Map is {1: 1, 2: 2}, keyMapper is ObjectUtils::toString, valueMapper is Function.identity(), output "
                        + "is {'1': 1, '2': 2}")
        void givenNotEmptyMapAndCorrectKeyValueMappersThenReturnCastedMap() {
            Map<Integer, Integer> map = MapBuilder.<Integer, Integer>get().put(1, 1).put(2, 2).build();
            Map<String, Integer> actual = CollectionUtils.cast(map, ObjectUtils::toString, Function.identity());
            assertThat(actual).isNotNull().hasSize(map.size());
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                assertThat(actual).extracting(String.valueOf(entry.getKey())).isEqualTo(entry.getValue());
            }
        }

        @Test
        @DisplayName(
                "Map is {1: 1, 2: 2}, keyMapper is value -> StringUtils.EMPTY, valueMapper is Function.identity(), "
                        + "output is exception")
        void givenKeyMapperGenerateDuplicatedKeysThenThrowException() {
            Map<Integer, Integer> originalMap = MapBuilder.<Integer, Integer>get().put(1, 1).put(2, 2).build();
            IllegalStateException exception = catchThrowableOfType(() -> CollectionUtils.cast(originalMap,
                    value -> StringUtils.EMPTY,
                    Function.identity()), IllegalStateException.class);
            assertThat(exception).isNotNull();
        }

        @Test
        @DisplayName(
                "Map is null, keyMapper is ObjectUtils::toString, valueMapper is Function.identity(), output is null")
        void givenMapIsNullThenReturnNull() {
            Map<?, ?> actual = CollectionUtils.cast(null, ObjectUtils::toString, Function.identity());
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Map is null, keyMapper is null, valueMapper is Function.identity(), output is exception")
        void givenKeyMapperIsNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> CollectionUtils.cast(null, null, Function.identity()),
                            IllegalArgumentException.class);
            assertThat(exception).hasMessage("The mapper to cast keys of map cannot be null.");
        }

        @Test
        @DisplayName("Map is null, keyMapper is Function.identity(), valueMapper is null, output is exception")
        void givenValueMapperIsNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> CollectionUtils.cast(null, Function.identity(), null),
                            IllegalArgumentException.class);
            assertThat(exception).hasMessage("The mapper to cast values of map cannot be null.");
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#connect(List[])}。
     */
    @Nested
    @DisplayName("Test method: connect(List<T>... lists)")
    class TestConnect {
        @Test
        @DisplayName("Input is [[1, 2], [3, 4]], output is [1, 2, 3, 4]")
        void givenNormalListsThenReturnConnectedList() {
            List<Integer> actual = CollectionUtils.connect(Arrays.asList(1, 2), Arrays.asList(3, 4));
            assertThat(actual).containsSequence(1, 2, 3, 4);
        }

        @Test
        @DisplayName("Input is null, output is []")
        void givenNullThenReturnEmptyList() {
            List<Integer> actual = CollectionUtils.connect((List<Integer>[]) null);
            assertThat(actual).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Input is [], output is []")
        void givenEmptyListThenReturnEmptyList() {
            List<Integer> actual = CollectionUtils.connect(Collections.emptyList());
            assertThat(actual).isNotNull().isEmpty();
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#difference(Collection, Collection)}。
     */
    @Nested
    @DisplayName("Test method: difference(Collection<E> include, Collection<E> exclude)")
    class TestDifference {
        @Test
        @DisplayName("Include is [1, 2, 3], exclude is null, output is [1, 2, 3]")
        void givenNotEmptyIncludeAndExcludeNullThenReturnInclude() {
            Set<Integer> include = new HashSet<>(Arrays.asList(1, 2, 3));
            Set<Integer> actual = CollectionUtils.difference(include, null);
            assertThat(actual).containsAll(include);
        }

        @Test
        @DisplayName("Include is [1, 2, 3], exclude is [1, 2, 4], output is [3]")
        void givenNotEmptyIncludeAndExcludeThenReturnDifferenceResult() {
            Set<Integer> include = new HashSet<>(Arrays.asList(1, 2, 3));
            Set<Integer> exclude = new HashSet<>(Arrays.asList(1, 2, 4));
            Set<Integer> expected = new HashSet<>(Collections.singletonList(3));
            Set<Integer> actual = CollectionUtils.difference(include, exclude);
            assertThat(actual).containsAll(expected);
        }
    }

    @Nested
    @DisplayName("Test equals")
    class TestEquals {
        /**
         * 目标方法：{@link CollectionUtils#equals(Iterable, Iterable)}。
         */
        @Nested
        @DisplayName("Test method: equals(Iterable<E> first, Iterable<E> second)")
        class TestEqualsWithoutEqualizer {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is null, second is null, output is true")
            void givenNullAndNullThenReturnTrue() {
                boolean actual = CollectionUtils.equals(null, null);
                assertThat(actual).isTrue();
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is null, second is [], output is false")
            void givenNullAndEmptyThenReturnFalse() {
                List<Integer> list = Collections.emptyList();
                boolean actual = CollectionUtils.equals(null, list);
                assertThat(actual).isFalse();
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is [], second is null, output is false")
            void givenEmptyAndNullThenReturnFalse() {
                List<Integer> list = Collections.emptyList();
                boolean actual = CollectionUtils.equals(list, null);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("First is [0, 1, 2], second is [0, 1], output is false")
            void givenFirstHasMoreElementThanSecondThenReturnFalse() {
                List<Integer> first = Arrays.asList(0, 1, 2);
                List<Integer> second = Arrays.asList(0, 1);
                boolean actual = CollectionUtils.equals(first, second);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("First is [0, 1], second is [0, 1, 2], output is false")
            void givenFirstHasLessElementThanSecondThenReturnFalse() {
                List<Integer> first = Arrays.asList(0, 1);
                List<Integer> second = Arrays.asList(0, 1, 2);
                boolean actual = CollectionUtils.equals(first, second);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("First is [0, 1], second is [1, 0], output is false")
            void givenFirstAndSecondSequenceNotMatchThenReturnFalse() {
                List<Integer> first = Arrays.asList(0, 1);
                List<Integer> second = Arrays.asList(1, 0);
                boolean actual = CollectionUtils.equals(first, second);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("First is [0, 1], second is [0, 1], output is true")
            void givenFirstAndSecondTotalEqualsThenReturnTrue() {
                List<Integer> first = Arrays.asList(0, 1);
                List<Integer> second = Arrays.asList(0, 1);
                boolean actual = CollectionUtils.equals(first, second);
                assertThat(actual).isTrue();
            }

            @Test
            @DisplayName("当提供两个含相同元素的列表时，返回 true")
            void givenFirstAndSecondIterableThenReturnTrue() {
                List<Integer> first = Arrays.asList(0, 1);
                List<Integer> second = Arrays.asList(0, 1);
                Iterable<?> iterable1 = ObjectUtils.cast(first);
                Iterable<?> iterable2 = ObjectUtils.cast(second);
                boolean actual = CollectionUtils.equals(iterable1, iterable2);
                assertThat(actual).isTrue();
            }
        }

        /**
         * 目标方法：{@link CollectionUtils#equals(Iterable, Iterable, Equalizer)}。
         */
        @Nested
        @DisplayName("Test method: equals(Iterable<E> first, Iterable<E> second, Equalizer<E> equalizer)")
        class TestEqualsWithEqualizer {
            @Test
            @DisplayName("First is ['a', 'b'], second is ['A', 'B'], equalizer is toUpperCase compare, output is true")
            void given() {
                List<String> first = Arrays.asList("a", "b");
                List<String> second = Arrays.asList("A", "B");
                boolean isEqual = CollectionUtils.equals(first,
                        second,
                        (s1, s2) -> Objects.equals(s1.toUpperCase(Locale.ROOT), s2.toUpperCase(Locale.ROOT)));
                assertThat(isEqual).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("测试方法: ignoreElements(Collection<E> collection, Predicate<E> predicate, Supplier<C> creator)")
    class TestIgnoreElements {
        @Test
        @DisplayName("当提供 [1, 2, 3] 列表，忽略条件为元素大于 1 时，返回 [1]")
        void givenIgnorePredicateThenReturnList() {
            List<Integer> list = Arrays.asList(1, 2, 3);
            final ArrayList<Integer> elements = CollectionUtils.ignoreElements(list, (ele) -> ele > 1, ArrayList::new);
            assertThat(elements).isNotNull().hasSize(1);
            assertThat(elements).contains(1);
        }

        @Test
        @DisplayName("当提供 [] 列表，忽略条件为元素大于 1 时，返回 []")
        void givenEmptyListAndIgnorePredicateThenReturnList() {
            List<Integer> list = new ArrayList<>();
            final ArrayList<Integer> elements = CollectionUtils.ignoreElements(list, (ele) -> ele > 1, ArrayList::new);
            assertThat(elements).isEmpty();
        }
    }

    @Nested
    @DisplayName("测试方法: ignoreNullToList(Collection<E> collection)")
    class TestIgnoreNullToList {
        @Test
        @DisplayName("当提供 [1, 2, 3, null] 列表，返回 [1, 2, 3]")
        void givenListThenReturnNotIncludeNullList() {
            List<Integer> list = Arrays.asList(1, 2, 3, null);
            final List<Integer> ignoreNullList = CollectionUtils.ignoreNullToList(list);
            assertThat(ignoreNullList).isNotNull().hasSize(3);
            assertThat(ignoreNullList).contains(1, 2, 3);
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#intersect(Collection, Collection)}。
     */
    @Nested
    @DisplayName("Test method: intersect(Collection<E> first, Collection<E> second)")
    class TestIntersect {
        @Test
        @DisplayName("当提供 [1, 2, 3] 列表和 null 时，返回 [1, 2, 3]")
        void givenFirstAndNullThenReturnFirst() {
            Set<Integer> first = new HashSet<>(Arrays.asList(1, 2, 3));
            Set<Integer> actual = CollectionUtils.intersect(first, null);
            assertThat(actual).containsAll(first);
        }

        @Test
        @DisplayName("当提供 [1, 2, 3] 列表和 [1, 2, 4] 时，返回 [1, 2]")
        void givenFirstAndSecondNotEmptyThenReturnIntersected() {
            Set<Integer> first = new HashSet<>(Arrays.asList(1, 2, 3));
            Set<Integer> second = new HashSet<>(Arrays.asList(1, 2, 4));
            Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2));
            Set<Integer> actual = CollectionUtils.intersect(first, second);
            assertThat(actual).containsAll(expected);
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#isEmpty(Collection)}。
     */
    @Nested
    @DisplayName("Test method: isEmpty(Collection<T> collection)")
    class TestIsEmpty {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, return true")
        void givenNullThenReturnTrue() {
            boolean actual = CollectionUtils.isEmpty((Collection<?>) null);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is [], return true")
        void givenEmptyThenReturnTrue() {
            boolean actual = CollectionUtils.isEmpty(Collections.emptyList());
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is [1, 2], return false")
        void givenNotEmptyThenReturnFalse() {
            boolean actual = CollectionUtils.isEmpty(Arrays.asList(1, 2));
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#isNotEmpty(Collection)}。
     */
    @Nested
    @DisplayName("Test method: isNotEmpty(Collection<T> collection)")
    class TestIsNotEmpty {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, return false")
        void givenNullThenReturnFalse() {
            boolean actual = CollectionUtils.isNotEmpty((Collection<?>) null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is [], return false")
        void givenEmptyThenReturnFalse() {
            boolean actual = CollectionUtils.isNotEmpty(Collections.emptyList());
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is [1, 2], return true")
        void givenNotEmptyThenReturnTrue() {
            boolean actual = CollectionUtils.isNotEmpty(Arrays.asList(1, 2));
            assertThat(actual).isTrue();
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#iterator(Iterable)}。
     */
    @Nested
    @DisplayName("Test method: iterator(Iterable<T> iterable) ")
    class TestIterator {
        @Test
        @DisplayName("Input is null, output is null")
        void givenNullThenReturnNull() {
            Iterator<Integer> actual = CollectionUtils.iterator(null);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is [1, 2], output is its iterator")
        void givenListThenReturnItsIterator() {
            Iterator<Integer> actual = CollectionUtils.iterator(Arrays.asList(1, 2));
            assertThat(actual).isNotNull().hasNext().toIterable().containsExactly(1, 2);
        }
    }

    @Nested
    @DisplayName("Test mapFirst")
    class TestMapFirst {
        /**
         * 目标方法：{@link CollectionUtils#mapFirst(Iterable, Function)}。
         */
        @Nested
        @DisplayName("Test method: mapFirst(Iterable<E> iterable, Function<E, R> mapper)")
        class TestMapFirstWithoutDefaultValue {
            @Test
            @DisplayName("Input is null, mapper is Function.identity(), output is null")
            void givenNullAndMapperThenReturnNull() {
                Object actual = CollectionUtils.mapFirst(null, Function.identity());
                assertThat(actual).isNull();
            }
        }

        /**
         * 目标方法：{@link CollectionUtils#mapFirst(Iterable, Function, Object)}。
         */
        @Nested
        @DisplayName("Test method: mapFirst(Iterable<E> iterable, Function<E, R> mapper, R defaultValue)")
        class TestMapFirstWithDefaultValue {
            private Integer convert0ToNull(Integer i1) {
                if (i1 == 0) {
                    return null;
                } else {
                    return i1;
                }
            }

            @Test
            @DisplayName("Input is [0, 1], mapper is this::convert0ToNull, defaultValue is 0, output is 1")
            void givenFirstElementMapToNullThenReturnSecond() {
                Integer actual = CollectionUtils.mapFirst(Arrays.asList(0, 1), this::convert0ToNull, 0);
                assertThat(actual).isEqualTo(1);
            }

            @Test
            @DisplayName("Input is [1, 2], mapper is this::convert0ToNull, defaultValue is 0, output is 1")
            void givenNoElementMapToNullThenReturnFirst() {
                Integer actual = CollectionUtils.mapFirst(Arrays.asList(1, 2), this::convert0ToNull, 0);
                assertThat(actual).isEqualTo(1);
            }

            @Test
            @DisplayName("Input is [0, 0], mapper is this::convert0ToNull, defaultValue is 0, output is 0")
            void givenAllElementsMapToNullThenReturnDefaultValue() {
                Integer actual = CollectionUtils.mapFirst(Arrays.asList(0, 0), this::convert0ToNull, 0);
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("测试方法: merge")
    class TestMerge {
        @Nested
        @DisplayName("测试方法: merge(List<E> first, List<E> second)")
        class TestMergeNormal {
            @Test
            @DisplayName("当提供 [0, 1] 和 [0, 1, 2] 时，返回合并后的列表 [0, 1, 0, 1, 2]")
            void givenTwoIncludeSameElementListThenReturnMergedList() {
                List<Integer> first = Arrays.asList(0, 1);
                List<Integer> second = Arrays.asList(0, 1, 2);
                final List<Integer> merge = CollectionUtils.merge(first, second);
                assertThat(merge).isNotNull().hasSize(5);
                assertThat(merge).contains(0, 1, 0, 1, 2);
            }

            @Test
            @DisplayName("当提供 [3] 和 [0, 1, 2] 时，返回合并后的列表 [3, 0, 1, 2]")
            void givenTwoNotIncludeSameElementListThenReturnMergedList() {
                List<Integer> first = Arrays.asList(3);
                List<Integer> second = Arrays.asList(0, 1, 2);
                final List<Integer> merge = CollectionUtils.merge(first, second);
                assertThat(merge).isNotNull().hasSize(4);
                assertThat(merge).contains(3, 0, 1, 2);
            }
        }

        @Nested
        @DisplayName("测试方法: merge(List<E> first, List<E> second, ConflictResolutionPolicy defaultPolicy)")
        class TestMergeWithPolicy {
            private final List<Integer> first = Arrays.asList(0, 1);
            private final List<Integer> second = Arrays.asList(0, 1, 2);

            @Test
            @DisplayName("当提供 [0, 1] 和 [0, 1, 2]，冲突处理策略为跳过时，返回第一个列表 [0, 1]")
            void givenSkipPolicyThenReturnMergedList() {
                final List<Integer> merge = CollectionUtils.merge(first, second, ConflictResolutionPolicy.SKIP);
                assertThat(merge).isNotNull().hasSize(2);
                assertThat(merge).contains(0, 1);
            }

            @Test
            @DisplayName("当提供 [0, 1] 和 [0, 1, 2]，冲突处理策略为跳过时，返回第一个列表 [0, 1]")
            void givenSkipPolicyThenReturnMergedList1() {
                final List<Integer> merge = CollectionUtils.merge(first, second, ConflictResolutionPolicy.SKIP);
                assertThat(merge).isNotNull().hasSize(2);
                assertThat(merge).contains(0, 1);
            }

            @Test
            @DisplayName("当提供 [0, 1] 和 [0, 1, 2]，冲突处理策略为终止并抛异常时，抛出冲突异常")
            void givenAbortPolicyThenThrowException() {
                assertThatThrownBy(() -> CollectionUtils.merge(first,
                        second,
                        ConflictResolutionPolicy.ABORT)).isInstanceOf(ConflictException.class);
            }

            @Test
            @DisplayName("当提供 [0, 1] 和 [0, 1, 2]，冲突处理策略为内容覆盖时，返回合并后列表 [0, 1, 2]")
            void givenOverridePolicyThenReturnMergedList() {
                final List<Integer> merge = CollectionUtils.merge(first, second, ConflictResolutionPolicy.OVERRIDE);
                assertThat(merge).isNotNull().hasSize(3);
                assertThat(merge).contains(0, 1, 2);
            }
        }

        @Nested
        @DisplayName("测试方法: merge(List<E> first, List<E> second, List<E> first, List<E> second,"
                + " ConflictResolverCollection conflictResolvers)")
        class TestMergeWithResolver {
            @Test
            @DisplayName("当提供 [0, 1] 和 [0, 1, 2]，冲突处理器为追加时，返回合并后列表 [0, 1, 0, 1, 2]")
            void givenConflictResolverThenReturnMergedList() {
                final List<Integer> first = Arrays.asList(0, 1);
                final List<Integer> second = Arrays.asList(0, 1, 2);
                ConflictResolverCollection conflictResolvers = ConflictResolverCollection.create();
                conflictResolvers.add(ObjectUtils.cast(new ListAppendConflictResolver<>()));
                final List<Integer> merge = CollectionUtils.merge(first, second, conflictResolvers);
                assertThat(merge).isNotNull().hasSize(5);
                assertThat(merge).contains(0, 1, 0, 1, 2);
            }
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#toArray(Collection, Class)}。
     */
    @Nested
    @DisplayName("Test method: toArray(Collection<T> collection, Class<T> clazz)")
    class TestToArray {
        @Test
        @DisplayName("Input is ['key'], class is String.class, output is ['key']")
        void givenCollectionWithStringValueThenReturnStringArray() {
            String[] array = CollectionUtils.toArray(Collections.singletonList("key"), String.class);
            assertThat(array).isNotNull().hasSize(1).containsExactly("key");
        }

        @Test
        @DisplayName("Input is null, class is String.class, output is exception")
        void givenCollectionIsNullThenThrowException() {
            IllegalArgumentException exception = catchThrowableOfType(() -> CollectionUtils.toArray(null, String.class),
                    IllegalArgumentException.class);
            assertThat(exception).hasMessage("The collection to convert to array cannot be null.");
        }

        @Test
        @DisplayName("Input is [], class is null, output is exception")
        void givenClassIsNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> CollectionUtils.toArray(Collections.emptyList(), null),
                            IllegalArgumentException.class);
            assertThat(exception).hasMessage("Class of list elements cannot be null.");
        }
    }

    @Nested
    @DisplayName("Test toMap")
    class TestToMap {
        /**
         * 目标方法：{@link CollectionUtils#toMap(Collection, Function)}。
         */
        @Nested
        @DisplayName("toMap(Collection<V> collection, Function<V, K> keyMapper)")
        class TestToMapWithoutExceptionSupplier {
            @Test
            @DisplayName("Input is ['key', 'value'], keyMapper is String::length, output is {3: 'key', 5: 'value'}")
            void givenListAndLengthMapperThenReturnLengthValueMap() {
                List<String> collection = Arrays.asList("key", "value");
                Map<Integer, String> actual = CollectionUtils.toMap(collection, String::length);
                assertThat(actual).hasSize(collection.size()).containsEntry(3, "key").containsEntry(5, "value");
            }

            @Test
            @DisplayName("Input is ['key1', 'key2'], keyMapper is String::length, output is exception")
            void givenListWithTheSameLengthValuesAndLengthMapperThenThrowException() {
                List<String> collection = Arrays.asList("key1", "key2");
                IllegalStateException exception =
                        catchThrowableOfType(() -> CollectionUtils.toMap(collection, String::length),
                                IllegalStateException.class);
                assertThat(exception).isNotNull();
            }
        }

        /**
         * 目标方法：{@link CollectionUtils#toMap(Collection, Function, BiFunction)}。
         */
        @Nested
        @DisplayName("Test method: toMap(Collection<V> collection, Function<V, K> keyMapper, BiFunction<V, V, E> "
                + "exceptionSupplier)")
        class TestToMapWithExceptionSupplier {
            @Test
            @DisplayName("Input is ['key', 'value'], keyMapper is String::length, output is {3: 'key', 5: 'value'}")
            void givenListAndLengthMapperThenReturnLengthValueMap() {
                List<String> collection = Arrays.asList("key", "value");
                Map<Integer, String> actual = CollectionUtils.toMap(collection,
                        String::length,
                        (v1, v2) -> new IllegalArgumentException(StringUtils.format(
                                "Duplicated key with values: {0} vs {1}.",
                                v1,
                                v2)));
                assertThat(actual).hasSize(collection.size()).containsEntry(3, "key").containsEntry(5, "value");
            }

            @Test
            @DisplayName("Input is ['key1', 'key2'], keyMapper is String::length, output is exception")
            void givenListWithTheSameLengthValuesAndLengthMapperThenThrowException() {
                List<String> collection = Arrays.asList("key1", "key2");
                IllegalArgumentException exception = catchThrowableOfType(() -> CollectionUtils.toMap(collection,
                        String::length,
                        (v1, v2) -> new IllegalArgumentException(StringUtils.format(
                                "Duplicated key with values: {0} vs {1}.",
                                v1,
                                v2))), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Duplicated key with values: key1 vs key2.");
            }
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#toString(Iterable)}。
     */
    @Nested
    @DisplayName("Test method: toString(Iterable<T> iterable)")
    class TestToString {
        @Test
        @DisplayName("Input is null, output is ''")
        void givenNullThenReturnEmpty() {
            String actual = CollectionUtils.toString(null);
            assertThat(actual).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        @DisplayName("Input is [], output is '[]'")
        void givenEmptyThenReturnEmptyListString() {
            String actual = CollectionUtils.toString(Collections.emptyList());
            assertThat(actual).isEqualTo(Collections.emptyList().toString());
        }

        @Test
        @DisplayName("Input is [1, 2], output is '[1, 2]'")
        void givenNotEmptyListThenReturnNotEmptyListString() {
            List<Integer> expectedList = Arrays.asList(1, 2);
            String actual = CollectionUtils.toString(expectedList);
            assertThat(actual).isEqualTo(expectedList.toString());
        }
    }

    /**
     * 目标方法：{@link CollectionUtils#union(Collection, Collection)}。
     */
    @Nested
    @DisplayName("Test method: union(Collection<E> first, Collection<E> second)")
    class TestUnion {
        @Test
        @DisplayName("First is [1, 2, 3], second is null, output is [1, 2, 3]")
        void givenFirstAndNullThenReturnFirst() {
            Set<Integer> first = new HashSet<>(Arrays.asList(1, 2, 3));
            Set<Integer> actual = CollectionUtils.union(first, null);
            assertThat(actual).containsAll(first);
        }

        @Test
        @DisplayName("First is [1, 2, 3], second is [1, 2, 4], output is [1, 2, 3, 4]")
        void givenFirstAndSecondNotEmptyThenReturnIntersected() {
            Set<Integer> first = new HashSet<>(Arrays.asList(1, 2, 3));
            Set<Integer> second = new HashSet<>(Arrays.asList(1, 2, 4));
            Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2, 3, 4));
            Set<Integer> actual = CollectionUtils.union(first, second);
            assertThat(actual).containsAll(expected);
        }
    }

    @Test
    @DisplayName("当提供一个列表时，返回一个枚举类")
    void givenListThenReturnEnumeration() {
        List<String> collection = Collections.singletonList("key");
        final Enumeration<String> enumeration = CollectionUtils.enumeration(collection.iterator());
        assertThat(enumeration).isNotNull();
    }

    @Test
    @DisplayName("当提供一个列表和过滤器时，返回过滤后的迭代器")
    void givenIteratorAndFilterThenReturnFilteredIterator() {
        List<String> collection = Collections.singletonList("key");
        final Predicate<String> filter = (String ele) -> ele.startsWith("q");
        final Iterator<String> iterator = CollectionUtils.filtered(collection.iterator(), filter);
        assertThat(iterator).isNotNull();
        assertThat(iterator.hasNext()).isFalse();
    }

    @Nested
    @DisplayName("测试方法: firstOrDefault(Iterable<E> values)")
    class TestFirstOrDefault {
        private static final String KEY = "key";

        private final List<String> collection = new ArrayList<>();

        @Test
        @DisplayName("当参数为 null 时，返回默认值 null")
        void givenNullThenReturnNull() {
            // noinspection ConstantValue
            Object defaultNull = CollectionUtils.firstOrDefault(null);
            // noinspection ConstantValue
            assertThat(defaultNull).isNull();
        }

        @Test
        @DisplayName("当参数为一个含多个元素的集合时，返回集合的第一个元素")
        void givenFirstAndSecondNotEmptyThenReturnIntersected() {
            collection.add(KEY);
            collection.add(null);
            final Object element = CollectionUtils.firstOrDefault(this.collection);
            assertThat(element).isEqualTo(KEY);
        }
    }
}
