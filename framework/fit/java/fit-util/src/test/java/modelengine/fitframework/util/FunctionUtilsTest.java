/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * {@link FunctionUtils} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-10-05
 */
public class FunctionUtilsTest {
    /**
     * 目標方法：{@link FunctionUtils#accept(Consumer, Object)}。
     */
    @Nested
    @DisplayName("Test method: accept(Consumer<T> consumer, T value)")
    class TestAccept {
        @Test
        @DisplayName("Consumer is null, execution has no exception")
        void givenConsumerIsNullThenReturnWithoutException() {
            assertThatNoException().isThrownBy(() -> FunctionUtils.accept(null, "Hello"));
        }

        @Test
        @DisplayName("Consumer is (map -> map.put('key', 'value')), input is {}, output is {'key': 'value'}")
        void givenConsumerIsNotNullThenValueHasBeenConsumed() {
            Map<String, Object> map = new HashMap<>();
            FunctionUtils.accept(mapParam -> mapParam.put("key", "value"), map);
            assertThat(map).hasSize(1).extractingByKey("key").isEqualTo("value");
        }
    }

    @Nested
    @DisplayName("Test and")
    class TestAnd {
        /**
         * 目标方法：{@link FunctionUtils#and(BiPredicate, BiPredicate)}。
         */
        @Nested
        @DisplayName("Test method: and(BiPredicate<T, R> first, BiPredicate<T, R> second)")
        class TestAnd2BiPredicate {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is null, second is null, output is null")
            void givenNullAndNullThenReturnNull() {
                BiPredicate<Integer, Integer> actual = FunctionUtils.and(null, (BiPredicate<Integer, Integer>) null);
                assertThat(actual).isNull();
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is null, second is (i1, i2) -> true, output is (i1, i2) -> true (second)")
            void givenNullAndSecondThenReturnSecond() {
                BiPredicate<Integer, Integer> first = null;
                BiPredicate<Integer, Integer> second = (i1, i2) -> true;
                BiPredicate<Integer, Integer> actual = FunctionUtils.and(first, second);
                assertThat(actual).isEqualTo(second);
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is (i1, i2) -> true, second is null, output is (i1, i2) -> true (first)")
            void givenFirstAndNullThenReturnFirst() {
                BiPredicate<Integer, Integer> first = (i1, i2) -> true;
                BiPredicate<Integer, Integer> second = null;
                BiPredicate<Integer, Integer> actual = FunctionUtils.and(first, second);
                assertThat(actual).isEqualTo(first);
            }

            @Test
            @DisplayName("First is (i1, i2) -> true, second is (i1, i2) -> false, output is (i1, i2) -> false (merged)")
            void givenFirstAndSecondThenReturnMerged() {
                BiPredicate<Integer, Integer> first = (i1, i2) -> true;
                BiPredicate<Integer, Integer> second = (i1, i2) -> false;
                BiPredicate<Integer, Integer> actual = FunctionUtils.and(first, second);
                boolean actualResult = actual.test(1, 2);
                assertThat(actualResult).isFalse();
            }
        }

        /**
         * 目标方法：{@link FunctionUtils#and(BiPredicate[], boolean)}。
         */
        @Nested
        @DisplayName("Test method: and(BiPredicate<T, R>[] predicates, boolean defaultValue)")
        class TestAndBiPredicatesWithDefaultValue {
            @Test
            @DisplayName("Predicates are null, defaultValue is true, output is (i1, i2) -> true")
            void givenPredicatesAreNullThenReturnBiPredicateToDefaultValue() {
                BiPredicate<Integer, Integer> actual = FunctionUtils.and((BiPredicate<Integer, Integer>[]) null, true);
                boolean actualResult = actual.test(1, 2);
                assertThat(actualResult).isTrue();
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("Predicates are [null], defaultValue is true, output is (i1, i2) -> true")
            void givenPredicatesWithAllNullThenReturnBiPredicateToDefaultValue() {
                BiPredicate<Integer, Integer>[] predicates = new BiPredicate[1];
                predicates[0] = null;
                BiPredicate<Integer, Integer> actual = FunctionUtils.and(predicates, true);
                boolean actualResult = actual.test(1, 2);
                assertThat(actualResult).isTrue();
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("Predicates are [(i1, i2) -> false], defaultValue is true, output is (i1, i2) -> false")
            void givenPredicatesThenReturnMergedBiPredicate() {
                BiPredicate<Integer, Integer>[] predicates = new BiPredicate[1];
                predicates[0] = (i1, i2) -> false;
                BiPredicate<Integer, Integer> actual = FunctionUtils.and(predicates, true);
                boolean actualResult = actual.test(1, 2);
                assertThat(actualResult).isFalse();
            }
        }

        /**
         * 目标方法：{@link FunctionUtils#and(Predicate, Predicate)}。
         */
        @Nested
        @DisplayName("Test method: and(Predicate<T> first, Predicate<T> second)")
        class TestAnd2Predicate {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is null, second is null, output is null")
            void givenNullAndNullThenReturnNull() {
                Predicate<Integer> actual = FunctionUtils.and(null, (Predicate<Integer>) null);
                assertThat(actual).isNull();
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is null, second is i1 -> true, output is i1 -> true (second)")
            void givenNullAndSecondThenReturnSecond() {
                Predicate<Integer> first = null;
                Predicate<Integer> second = i1 -> true;
                Predicate<Integer> actual = FunctionUtils.and(first, second);
                assertThat(actual).isEqualTo(second);
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is i1 -> true, second is null, output is i1 -> true (first)")
            void givenFirstAndNullThenReturnFirst() {
                Predicate<Integer> first = i1 -> true;
                Predicate<Integer> second = null;
                Predicate<Integer> actual = FunctionUtils.and(first, second);
                assertThat(actual).isEqualTo(first);
            }

            @Test
            @DisplayName("First is i1 -> true, second is i1 -> false, output is i1 -> false (merged)")
            void givenFirstAndSecondThenReturnMerged() {
                Predicate<Integer> first = i1 -> true;
                Predicate<Integer> second = i1 -> false;
                Predicate<Integer> actual = FunctionUtils.and(first, second);
                boolean actualResult = actual.test(1);
                assertThat(actualResult).isFalse();
            }
        }

        /**
         * 目标方法：{@link FunctionUtils#and(Predicate[], boolean)}。
         */
        @Nested
        @DisplayName("Test method: and(Predicate<T>[] predicates, boolean defaultValue)")
        class TestAndPredicatesWithDefaultValue {
            @Test
            @DisplayName("Predicates are null, defaultValue is true, output is i1 -> true")
            void givenPredicatesAreNullThenReturnPredicateToDefaultValue() {
                Predicate<Integer> actual = FunctionUtils.and((Predicate<Integer>[]) null, true);
                boolean actualResult = actual.test(1);
                assertThat(actualResult).isTrue();
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("Predicates are [null], defaultValue is true, output is i1 -> true")
            void givenPredicatesWithAllNullThenReturnPredicateToDefaultValue() {
                Predicate<Integer>[] predicates = new Predicate[1];
                predicates[0] = null;
                Predicate<Integer> actual = FunctionUtils.and(predicates, true);
                boolean actualResult = actual.test(1);
                assertThat(actualResult).isTrue();
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("Predicates are [i1 -> false], defaultValue is true, output is i1 -> false")
            void givenPredicatesThenReturnMergedPredicate() {
                Predicate<Integer>[] predicates = new Predicate[1];
                predicates[0] = i1 -> false;
                Predicate<Integer> actual = FunctionUtils.and(predicates, true);
                boolean actualResult = actual.test(1);
                assertThat(actualResult).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("Test connect")
    class TestConnect {
        /**
         * 目标方法：{@link FunctionUtils#connect(Consumer, Consumer)}。
         */
        @Nested
        @DisplayName("Test method: connect(Consumer<T> first, Consumer<T> second)")
        class TestConnect2Consumer {
            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("First is null, second is not null, output is second")
            void givenNullAndSecondThenReturnSecond() {
                Consumer<Integer> second = (Consumer<Integer>) mock(Consumer.class);
                Consumer<Integer> actual = FunctionUtils.connect(null, second);
                assertThat(actual).isNotNull();
                actual.accept(0);
                verify(second, times(1)).accept(0);
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("First is not null, second is null, output is first")
            void givenFirstAndNullThenReturnFirst() {
                Consumer<Integer> first = (Consumer<Integer>) mock(Consumer.class);
                Consumer<Integer> actual = FunctionUtils.connect(first, null);
                assertThat(actual).isNotNull();
                actual.accept(0);
                verify(first, times(1)).accept(anyInt());
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("First is not null, second is not null, output is connected")
            void givenFirstAndSecondThenReturnConnected() {
                Consumer<Integer> first = (Consumer<Integer>) mock(Consumer.class);
                Consumer<Integer> second = (Consumer<Integer>) mock(Consumer.class);
                when(first.andThen(eq(second))).thenReturn(item -> {
                    first.accept(item);
                    second.accept(item);
                });
                Consumer<Integer> actual = FunctionUtils.connect(first, second);
                assertThat(actual).isNotNull();
                actual.accept(0);
                verify(first, times(1)).accept(0);
                verify(second, times(1)).accept(0);
            }
        }

        /**
         * 目标方法：{@link FunctionUtils#connect(Function[])}。
         */
        @Nested
        @DisplayName("Test method: connect(Function<T, T>... functions)")
        class TestConnectFunctions {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Input is null, output is null")
            void givenFunctionsNullThenReturnNull() {
                Function<Integer, Integer> actual = FunctionUtils.connect((Function<Integer, Integer>[]) null);
                assertThat(actual).isNull();
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("Input is [null], output is null")
            void givenFunctionsWithAllNullThenReturnNull() {
                Function<Integer, Integer>[] functions = new Function[1];
                functions[0] = null;
                Function<Integer, Integer> actual = FunctionUtils.connect(functions);
                assertThat(actual).isNull();
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("Input is [i1 -> i1 + 1, i1 -> i1 + 1], output is i1 -> i1 + 2")
            void givenFunctionsNotNullThenReturnConnectedFunction() {
                Function<Integer, Integer>[] functions = new Function[2];
                functions[0] = i1 -> i1 + 1;
                functions[1] = i1 -> i1 + 1;
                Function<Integer, Integer> actual = FunctionUtils.connect(functions);
                assertThat(actual).isNotNull();
                Integer actualResult = actual.apply(0);
                assertThat(actualResult).isEqualTo(2);
            }
        }

        /**
         * 目标方法：{@link FunctionUtils#connect(Function, Function)}。
         */
        @Nested
        @DisplayName("Test method: connect(Function<T, U> first, Function<U, V> second)")
        class TestConnect2Functions {
            @Test
            @DisplayName("First is i1 -> i1 + 1, second is i1 -> i1 + 1, output is i1 -> i1 + 2")
            void givenFunctionsNotNullThenReturnConnectedFunction() {
                Function<Integer, Integer> first = i1 -> i1 + 1;
                Function<Integer, Integer> second = i1 -> i1 + 1;
                Function<Integer, Integer> actual = FunctionUtils.connect(first, second);
                assertThat(actual).isNotNull();
                Integer actualResult = actual.apply(0);
                assertThat(actualResult).isEqualTo(2);
            }
        }
    }

    @Nested
    @DisplayName("Test or")
    class TestOr {
        /**
         * 目标方法：{@link FunctionUtils#or(Predicate, Predicate)}。
         */
        @Nested
        @DisplayName("Test method: or(Predicate<T> first, Predicate<T> second)")
        class TestOr2Predicate {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is null, second is null, output is null")
            void givenNullAndNullThenReturnNull() {
                Predicate<Integer> actual = FunctionUtils.or(null, null);
                assertThat(actual).isNull();
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is null, second is i1 -> true, output is i1 -> true (second)")
            void givenNullAndSecondThenReturnSecond() {
                Predicate<Integer> first = null;
                Predicate<Integer> second = i1 -> true;
                Predicate<Integer> actual = FunctionUtils.or(first, second);
                assertThat(actual).isEqualTo(second);
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("First is i1 -> true, second is null, output is i1 -> true (first)")
            void givenFirstAndNullThenReturnFirst() {
                Predicate<Integer> first = i1 -> true;
                Predicate<Integer> second = null;
                Predicate<Integer> actual = FunctionUtils.or(first, second);
                assertThat(actual).isEqualTo(first);
            }

            @Test
            @DisplayName("First is i1 -> true, second is i1 -> false, output is i1 -> true (merged)")
            void givenFirstAndSecondThenReturnMerged() {
                Predicate<Integer> first = i1 -> true;
                Predicate<Integer> second = i1 -> false;
                Predicate<Integer> actual = FunctionUtils.or(first, second);
                boolean actualResult = actual.test(1);
                assertThat(actualResult).isTrue();
            }
        }
    }

    /**
     * 目标方法：{@link FunctionUtils#test(Predicate, Object, boolean)}。
     */
    @Nested
    @DisplayName("Test method: test(Predicate<T> predicate, T value, boolean defaultValue)")
    class TestTest {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Predicate is null, default is true, output is true")
        void givenPredicateNullThenReturnDefaultValue() {
            boolean actual = FunctionUtils.test(null, new Object(), true);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Predicate is Objects::nonNull, input is not null, default is false, output is true")
        void givenPredicateNotNullThenReturnTestResult() {
            boolean actual = FunctionUtils.test(Objects::nonNull, new Object(), false);
            assertThat(actual).isTrue();
        }
    }
}
