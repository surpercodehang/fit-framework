/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

/**
 * 为 {@link ArrayUtils} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class ArrayUtilsTest {
    @Nested
    @DisplayName("Test binarySearch")
    class TestBinarySearch {
        /**
         * 目标方法：{@link ArrayUtils#binarySearch(Comparable[], Comparable)}。
         */
        @Nested
        @DisplayName("Test method: binarySearch(E[] array, E key)")
        class TestDefaultBinarySearch {
            @Test
            @DisplayName("Array is [0, 1, 2, 3, 4], key is 3, output is 3")
            void givenArrayWithKeyAndKeyGreaterThanMiddleThenReturnItsIndex() {
                Integer[] array = {0, 1, 2, 3, 4};
                int actual = ArrayUtils.binarySearch(array, 3);
                assertThat(actual).isEqualTo(3);
            }

            @Test
            @DisplayName("Array is [0, 1, 2, 3], key is 2, output is 2")
            void givenArrayWithKeyAndKeyLessThanMiddleThenReturnItsIndex() {
                Integer[] array = {0, 1, 2, 3, 4};
                int actual = ArrayUtils.binarySearch(array, 1);
                assertThat(actual).isEqualTo(1);
            }

            @Test
            @DisplayName("Array is [0, 2], key is 1, output is -2")
            void givenArrayWithoutKeyThenReturnNegative() {
                Integer[] array = {0, 2};
                int actual = ArrayUtils.binarySearch(array, 1);
                assertThat(actual).isEqualTo(-2);
            }

            @Test
            @DisplayName("Array is [0, 1], key is 0, output is 0")
            void givenKeyMatchTheFirstElementInArrayThenReturn0() {
                Integer[] array = {0, 1};
                int actual = ArrayUtils.binarySearch(array, 0);
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Array is [0, 1], key is 1, output is 1")
            void givenKeyMatchTheLastElementInArrayThenReturnLengthMinus1() {
                Integer[] array = {0, 1};
                int actual = ArrayUtils.binarySearch(array, 1);
                assertThat(actual).isEqualTo(1);
            }

            @Test
            @DisplayName("Array is [0, 1], key is -1, output is -1")
            void givenKeyLessThanTheFirstElementInArrayThenReturnMinus1() {
                Integer[] array = {0, 1};
                int actual = ArrayUtils.binarySearch(array, -1);
                assertThat(actual).isEqualTo(-1);
            }

            @Test
            @DisplayName("Array is [0, 1], key is 2, output is -3")
            void givenKeyGreaterThanTheLastElementInArrayThenReturnMinus1MinusLength() {
                Integer[] array = {0, 1};
                int actual = ArrayUtils.binarySearch(array, 2);
                assertThat(actual).isEqualTo(-3);
            }

            @Test
            @DisplayName("Array is [0], key is 1, output is -2")
            void givenKeyGreaterThanTheLastElementInArrayAndLengthIs1ThenReturnMinus2() {
                Integer[] array = {0};
                int actual = ArrayUtils.binarySearch(array, 1);
                assertThat(actual).isEqualTo(-2);
            }

            @Test
            @DisplayName("Array is [null, 1], key is null, output is 0")
            void givenNullIsTheFirstAndKeyIsNullThenReturn0() {
                Integer[] array = {null, 1};
                int actual = ArrayUtils.binarySearch(array, null);
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Array is [null, 1], key is null, output is 0")
            void givenNullIsTheFirstAndKeyIsInArrayThenReturnItsIndex() {
                Integer[] array = {null, 1};
                int actual = ArrayUtils.binarySearch(array, 1);
                assertThat(actual).isEqualTo(1);
            }

            @Test
            @DisplayName("Array is null, output is exception")
            void givenArrayNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ArrayUtils.binarySearch(null, 1), IllegalArgumentException.class);
                assertThat(exception).hasMessage("The array to binary search cannot be null.");
            }

            @Test
            @DisplayName("Array is [0, 1, 1, 2], key is 1, output is between [1, 2]")
            void givenArrayWithMultipleKeyThenReturnAnyIndex() {
                Integer[] array = {0, 1, 1, 2};
                int actual = ArrayUtils.binarySearch(array, 1);
                assertThat(actual).isBetween(1, 2);
            }
        }

        /**
         * 目标方法：{@link ArrayUtils#binarySearch(Object[], Comparable, Function)}。
         */
        @Nested
        @DisplayName("Test method: binarySearch(E[] array, K key, Function<E, K> mapper)")
        class TestBinarySearchWithMapper {
            @Test
            @DisplayName("Array is [], key is 0, mapper is Function.identity(), output is -1")
            void givenArrayIsEmptyThenReturnMinus1() {
                Function<Integer, Integer> mapper = Function.identity();
                int actual = ArrayUtils.binarySearch(new Integer[] {}, 0, mapper);
                assertThat(actual).isEqualTo(-1);
            }

            @Test
            @DisplayName("Array is [], key is null, mapper is null, output is exception")
            void givenMapperIsNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ArrayUtils.binarySearch(new Integer[] {}, 1, null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The mapper to map element cannot be null.");
            }
        }

        /**
         * 目标方法：{@link ArrayUtils#binarySearch(Object[], Comparable, Function, Comparator)}。
         */
        @Nested
        @DisplayName("Test method: binarySearch(E[] array, K key, Function<E, K> mapper, Comparator<K> comparator)")
        class TestBinarySearchWithMapperAndComparator {
            @Test
            @DisplayName(
                    "Array is [], key is null, mapper is Function.identity(), comparator is null, output is exception")
            void givenComparatorIsNullThenThrowException() {
                Function<Integer, Integer> mapper = Function.identity();
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> ArrayUtils.binarySearch(new Integer[] {}, 1, mapper, null),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("The comparator to compare elements cannot be null.");
            }
        }
    }

    /**
     * 目标方法：{@link ArrayUtils#concrete(Object)}。
     */
    @Nested
    @DisplayName("Test method: concrete(Object array)")
    class TestConcrete {
        @Test
        @DisplayName("Input is [0, 1], output is [0, 1]")
        void givenArrayObjectThenReturnObjectArray() {
            Object array = new int[] {0, 1};
            Object[] actual = ArrayUtils.concrete(array);
            assertThat(actual).hasSize(2).containsSequence(0, 1);
        }

        @Test
        @DisplayName("Input is null, output is exception")
        void givenNullThenThrowException() {
            IllegalArgumentException exception =
                    catchThrowableOfType(() -> ArrayUtils.concrete(null), IllegalArgumentException.class);
            assertThat(exception).hasMessage("Array cannot be null.");
        }
    }

    @Nested
    @DisplayName("测试方法: flat(Object... elements)")
    class TestFlat {
        @Test
        @DisplayName("当提供可变数组为 null 时，返回空的 object 数组")
        void givenNullThenReturnEmptyArray() {
            final Object[] objects = ArrayUtils.flat(ObjectUtils.cast(null));
            assertThat(objects).isEmpty();
        }

        @Test
        @DisplayName("当提供可变数组包含集合时，返回 object 数组")
        void givenListIncludeThenReturnObjectArray() {
            final List<Object> list = Arrays.asList("a", null, "b");
            final Object[] objects = ArrayUtils.flat(list);
            assertThat(objects).containsSequence("a", null, "b");
        }

        @Test
        @DisplayName("当提供可变数组包含迭代器时，返回 object 数组")
        void givenListIncludeIteratorThenReturnObjectArray() {
            final List<Object> otherList = Arrays.asList("c", "d");
            final List<Object> list = Arrays.asList("a", otherList.iterator());
            final Object[] objects = ArrayUtils.flat(list);
            assertThat(objects).containsSequence("a", "c", "d");
        }

        @Test
        @DisplayName("当提供可变数组包含枚举类时，返回 object 数组")
        void givenListIncludeEnumerationThenReturnObjectArray() {
            final Vector<Object> vector = new Vector<>();
            vector.addElement(10);
            vector.addElement('b');
            final Object[] objects = ArrayUtils.flat("a", vector.elements());
            assertThat(objects).containsSequence("a", 10, 'b');
        }

        @Test
        @DisplayName("当提供可变数组包含数组时，返回 object 数组")
        void givenListIncludeArrayThenReturnObjectArray() {
            final String[] array = {"c", "d"};
            final Object[] objects = ArrayUtils.flat("a", array);
            assertThat(objects).containsSequence("a", "c", "d");
        }
    }

    /**
     * 目标方法：{@link ArrayUtils#isEmpty(Object[])}。
     */
    @Nested
    @DisplayName("Test method: isEmpty(T[] array)")
    class TestIsEmpty {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, return true")
        void givenNullThenReturnTrue() {
            boolean actual = ArrayUtils.isEmpty((Object[]) null);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is [], return true")
        void givenEmptyThenReturnTrue() {
            boolean actual = ArrayUtils.isEmpty(ArrayUtils.EMPTY_OBJECT_ARRAY);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Input is [1, 2], return false")
        void givenNotEmptyThenReturnFalse() {
            boolean actual = ArrayUtils.isEmpty(new Integer[] {1, 2});
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link ArrayUtils#isNotEmpty(Object[])}。
     */
    @Nested
    @DisplayName("Test method: isNotEmpty(T[] array)")
    class TestIsNotEmpty {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, return false")
        void givenNullThenReturnFalse() {
            boolean actual = ArrayUtils.isNotEmpty(null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is [], return false")
        void givenEmptyThenReturnFalse() {
            boolean actual = ArrayUtils.isNotEmpty(ArrayUtils.EMPTY_OBJECT_ARRAY);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is [1, 2], return true")
        void givenNotEmptyThenReturnTrue() {
            boolean actual = ArrayUtils.isNotEmpty(new Integer[] {1, 2});
            assertThat(actual).isTrue();
        }
    }

    /**
     * 目标方法：{@link ArrayUtils#iterator(Object[])}。
     */
    @Nested
    @DisplayName("Test method: iterator(T... items)")
    class TestIterator {
        @Test
        @DisplayName("Input is [1, 2, 3], output is [1, 2, 3]")
        void givenNotEmptyArrayThenReturnIterator() {
            Integer[] array = new Integer[] {1, 2, 3};
            Iterator<Integer> actual = ArrayUtils.iterator(array);
            assertThat(actual).isNotNull().toIterable().hasSize(array.length).containsSequence(1, 2, 3);
        }
    }

    @Nested
    @DisplayName("Test toString")
    class TestToString {
        /**
         * 目标方法：{@link ArrayUtils#toString(Object[])}。
         */
        @Nested
        @DisplayName("Test method: toString(T[] array)")
        class TestToStringWithoutMapper {
            @Test
            @DisplayName("Input is null, output is ''")
            void givenNullThenReturnEmpty() {
                String actual = ArrayUtils.toString(null);
                assertThat(actual).isEqualTo(StringUtils.EMPTY);
            }

            @Test
            @DisplayName("Input is [], output is '[]'")
            void givenEmptyThenReturnEmptyListString() {
                String actual = ArrayUtils.toString(ArrayUtils.EMPTY_OBJECT_ARRAY);
                assertThat(actual).isEqualTo(Collections.emptyList().toString());
            }

            @Test
            @DisplayName("Input is [1, 2], output is '[1, 2]'")
            void givenNotEmptyListThenReturnNotEmptyListString() {
                String actual = ArrayUtils.toString(new Integer[] {1, 2});
                assertThat(actual).isEqualTo("[1, 2]");
            }
        }

        /**
         * 目标方法：{@link ArrayUtils#toString(Object[], Function)}。
         */
        @Nested
        @DisplayName("Test method: toString(T[] array, Function<T, String> toStringMapper)")
        class TestToStringWithMapper {
            @Test
            @DisplayName("Input is [1, 2], toStringMapper is i1 -> String.valueOf(i1 + 1), output is '[1, 2]'")
            void givenNotEmptyListThenReturnNotEmptyListString() {
                String actual = ArrayUtils.toString(new Integer[] {1, 2}, i1 -> String.valueOf(i1 + 1));
                assertThat(actual).isEqualTo("[2, 3]");
            }
        }
    }
}
