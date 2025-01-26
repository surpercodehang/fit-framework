/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.inspection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

/**
 * 为 {@link Validation} 提供单元测试。
 *
 * @author 梁济时
 * @since 2020-07-24
 */
public class ValidationTest {
    private static final String WHITE_SPACE = " \n\t";

    @Nested
    @DisplayName("Test between")
    class TestBetween {
        /**
         * 目标方法：{@link Validation#between(Comparable, Comparable, Comparable, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: between(T actual, T min, T max, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, min: 1, max: 2)")
            void given0LessThanMinThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.between(0, 1, 2, "Error"),
                        IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, min: -1, max: 1)")
            void given0BetweenMinAndMaxThenReturn0() {
                int actual = Validation.between(0, -1, 1, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#between(Comparable, Comparable, Comparable, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: between(T actual, T min, T max, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, min: 1, max: 2)")
            void given0LessThanMinThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.between(0,
                        1,
                        2,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, min: -1, max: 1)")
            void given0BetweenMinAndMaxThenReturn0() {
                int actual = Validation.between(0, -1, 1, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Test equals")
    class TestEquals {
        /**
         * 目标方法：{@link Validation#equals(Object, Object, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: equals(T actual, T expected, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, expected: 1)")
            void given0LessThanMinThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.equals(0, 1, "Error"), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, expected: 0)")
            void given0BetweenMinAndMaxThenReturn0() {
                int actual = Validation.equals(0, 0, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#equals(Object, Object, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: equals(T actual, T expected, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, expected: 1)")
            void given0LessThanMinThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.equals(0, 1, () -> new IllegalArgumentException("Error")),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, expected: 0)")
            void given0BetweenMinAndMaxThenReturn0() {
                int actual = Validation.equals(0, 0, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Test greaterThan")
    class TestGreaterThan {
        /**
         * 目标方法：{@link Validation#greaterThan(int, int, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: greaterThan(int actual, int bound, String error, Object... args)")
        class TestIntWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: 1)")
            void given0AndBound1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.greaterThan(0, 1, "Error"),
                        IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenReturn0() {
                int actual = Validation.greaterThan(0, -1, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#greaterThan(int, int, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: greaterThan(int actual, int bound, Supplier<E> exceptionSupplier)")
        class TestIntWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: 1)")
            void given0AndBound1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.greaterThan(0,
                        1,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenReturn0() {
                int actual = Validation.greaterThan(0, -1, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#greaterThan(long, long, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: greaterThan(long actual, long bound, String error, Object... args)")
        class TestLongWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: 1)")
            void given0AndBound1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.greaterThan(0L, 1L, "Error"),
                        IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenReturn0() {
                long actual = Validation.greaterThan(0L, -1L, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#greaterThan(long, long, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: greaterThan(long actual, long bound, Supplier<E> exceptionSupplier)")
        class TestLongWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: 1)")
            void given0AndBound1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.greaterThan(0L,
                        1L,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenReturn0() {
                long actual = Validation.greaterThan(0L, -1L, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Test greaterThanOrEquals")
    class TestGreaterThanOrEquals {
        /**
         * 目标方法：{@link Validation#greaterThanOrEquals(int, int, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: greaterThanOrEquals(int actual, int bound, String error, Object... args)")
        class TestIntWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: 1)")
            void given0AndBound1ThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.greaterThanOrEquals(0, 1, "Error"),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenReturn0() {
                int actual = Validation.greaterThanOrEquals(0, -1, "Error");
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 0)")
            void given0AndBound0ThenReturn0() {
                int actual = Validation.greaterThanOrEquals(0, 0, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#greaterThanOrEquals(int, int, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: greaterThanOrEquals(int actual, int bound, Supplier<E> exceptionSupplier)")
        class TestIntWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: 1)")
            void given0AndBound1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.greaterThanOrEquals(0,
                        1,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenReturn0() {
                int actual = Validation.greaterThanOrEquals(0, -1, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 0)")
            void given0AndBound0ThenReturn0() {
                int actual = Validation.greaterThanOrEquals(0, 0, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#greaterThanOrEquals(long, long, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: greaterThanOrEquals(long actual, long bound, String error, Object... args)")
        class TestLongWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: 1)")
            void given0AndBound1ThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.greaterThanOrEquals(0L, 1L, "Error"),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenReturn0() {
                long actual = Validation.greaterThanOrEquals(0L, -1L, "Error");
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 0)")
            void given0AndBound0ThenReturn0() {
                long actual = Validation.greaterThanOrEquals(0L, 0L, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#greaterThanOrEquals(int, int, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: greaterThanOrEquals(int actual, int bound, Supplier<E> exceptionSupplier)")
        class TestLongWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: 1)")
            void given0AndBound1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.greaterThanOrEquals(0L,
                        1L,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenReturn0() {
                long actual = Validation.greaterThanOrEquals(0L, -1L, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 0)")
            void given0AndBound0ThenReturn0() {
                long actual = Validation.greaterThanOrEquals(0L, 0L, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Test isFalse")
    class TestIsFalse {
        /**
         * 目标方法：{@link Validation#isFalse(boolean, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: isFalse(boolean actual, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation failed (actual: true)")
            void givenTrueThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.isFalse(true, "Error"), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation passed (actual: false)")
            void givenFalseThenReturnFalse() {
                boolean actual = Validation.isFalse(false, "Error");
                assertThat(actual).isEqualTo(false);
            }
        }

        /**
         * 目标方法：{@link Validation#isFalse(boolean, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: isFalse(boolean actual, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation failed (actual: true)")
            void givenTrueThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.isFalse(true,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation passed (actual: false)")
            void givenFalseThenReturnFalse() {
                boolean actual = Validation.isFalse(false, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(false);
            }
        }
    }

    @Nested
    @DisplayName("Test isInstanceOf")
    class TestIsInstanceOf {
        /**
         * 目标方法：{@link Validation#isInstanceOf(Object, Class, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: isInstanceOf(U actual, Class<T> clazz, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 1, clazz: String.class)")
            void given1AndStringClassThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.isInstanceOf(1, String.class, "Error"),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 'Hello', clazz: String.class)")
            void givenHelloAndStringClassThenReturnHello() {
                String actual = Validation.isInstanceOf("Hello", String.class, "Error");
                assertThat(actual).isEqualTo("Hello");
            }
        }

        /**
         * 目标方法：{@link Validation#isInstanceOf(Object, Class, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: isInstanceOf(U actual, Class<T> clazz, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 1, clazz: String.class)")
            void given1AndStringClassThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.isInstanceOf(1,
                        String.class,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 'Hello', clazz: String.class)")
            void givenHelloAndStringClassThenReturnHello() {
                String actual =
                        Validation.isInstanceOf("Hello", String.class, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo("Hello");
            }
        }
    }

    @Nested
    @DisplayName("Test isTrue")
    class TestIsTrue {
        /**
         * 目标方法：{@link Validation#isTrue(boolean, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: isTrue(boolean actual, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation failed (actual: false)")
            void givenFalseThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.isTrue(false, "Error"), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation passed (actual: true)")
            void givenTrueThenReturnTrue() {
                boolean actual = Validation.isTrue(true, "Error");
                assertThat(actual).isEqualTo(true);
            }
        }

        /**
         * 目标方法：{@link Validation#isTrue(boolean, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: isTrue(boolean actual, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation failed (actual: false)")
            void givenFalseThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.isTrue(false,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation passed (actual: true)")
            void givenTrueThenReturnTrue() {
                boolean actual = Validation.isTrue(true, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(true);
            }
        }
    }

    @Nested
    @DisplayName("Test lessThan")
    class TestLessThan {
        /**
         * 目标方法：{@link Validation#lessThan(int, int, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: lessThan(int actual, int bound, String error, Object... args)")
        class TestIntWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.lessThan(0, -1, "Error"), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 1)")
            void given0AndBound1ThenReturn0() {
                int actual = Validation.lessThan(0, 1, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#lessThan(int, int, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: lessThan(int actual, int bound, Supplier<E> exceptionSupplier)")
        class TestIntWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.lessThan(0,
                        -1,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 1)")
            void given0AndBound1ThenReturn0() {
                int actual = Validation.lessThan(0, 1, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#lessThan(long, long, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: lessThan(long actual, long bound, String error, Object... args)")
        class TestLongWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.lessThan(0L, -1L, "Error"),
                        IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 1)")
            void given0AndBound1ThenReturn0() {
                long actual = Validation.lessThan(0L, 1L, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#lessThan(long, long, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: lessThan(long actual, long bound, Supplier<E> exceptionSupplier)")
        class TestLongWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.lessThan(0L,
                        -1L,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 1)")
            void given0AndBound1ThenReturn0() {
                long actual = Validation.lessThan(0L, 1L, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Test lessThanOrEquals")
    class TestLessThanOrEquals {
        /**
         * 目标方法：{@link Validation#lessThanOrEquals(int, int, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: lessThanOrEquals(int actual, int bound, String error, Object... args)")
        class TestIntWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.lessThanOrEquals(0, -1, "Error"),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 1)")
            void given0AndBound1ThenReturn0() {
                int actual = Validation.lessThanOrEquals(0, 1, "Error");
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 0)")
            void given0AndBound0ThenReturn0() {
                int actual = Validation.lessThanOrEquals(0, 0, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#lessThanOrEquals(int, int, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: lessThanOrEquals(int actual, int bound, Supplier<E> exceptionSupplier)")
        class TestIntWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.lessThanOrEquals(0,
                        -1,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 1)")
            void given0AndBound1ThenReturn0() {
                int actual = Validation.lessThanOrEquals(0, 1, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 0)")
            void given0AndBound0ThenReturn0() {
                int actual = Validation.lessThanOrEquals(0, 0, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#lessThanOrEquals(long, long, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: lessThanOrEquals(long actual, long bound, String error, Object... args)")
        class TestLongWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.lessThanOrEquals(0L, -1L, "Error"),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 1)")
            void given0AndBound1ThenReturn0() {
                long actual = Validation.lessThanOrEquals(0L, 1L, "Error");
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 0)")
            void given0AndBound0ThenReturn0() {
                long actual = Validation.lessThanOrEquals(0L, 0L, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        /**
         * 目标方法：{@link Validation#lessThanOrEquals(int, int, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: lessThanOrEquals(int actual, int bound, Supplier<E> exceptionSupplier)")
        class TestLongWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 0, bound: -1)")
            void given0AndBoundMinus1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.lessThanOrEquals(0L,
                        -1L,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 1)")
            void given0AndBound1ThenReturn0() {
                long actual = Validation.lessThanOrEquals(0L, 1L, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Validation passed (actual: 0, bound: 0)")
            void given0AndBound0ThenReturn0() {
                long actual = Validation.lessThanOrEquals(0L, 0L, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Test notBlank")
    class TestNotBlank {
        /**
         * 目标方法：{@link Validation#notBlank(String, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: notBlank(String actual, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: null)")
            void givenNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.notBlank(null, "Error"), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation failed (actual: '')")
            void givenEmptyThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.notBlank(StringUtils.EMPTY, "Error"),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation failed (actual: ' \n\t')")
            void givenWhiteSpaceThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.notBlank(WHITE_SPACE, "Error"),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 'Hello')")
            void givenHelloThenReturnHello() {
                String actual = Validation.notBlank("Hello", "Error");
                assertThat(actual).isEqualTo("Hello");
            }
        }

        /**
         * 目标方法：{@link Validation#notBlank(String, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: notBlank(String actual, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: null)")
            void givenNullThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.notBlank(null,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation failed (actual: '')")
            void givenEmptyThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.notBlank(StringUtils.EMPTY,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation failed (actual: ' \n\t')")
            void givenWhiteSpaceThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.notBlank(WHITE_SPACE,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 'Hello')")
            void givenHelloThenReturnHello() {
                String actual = Validation.notBlank("Hello", () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo("Hello");
            }
        }
    }

    @Nested
    @DisplayName("测试 notNegative")
    class TestNotNegative {
        @Nested
        @DisplayName("测试方法：notNegative(long value, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: -1)")
            void givenMinus1ThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.notNegative(-1, "Error"), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0)")
            void given0ThenReturn0() {
                long actual = Validation.notNegative(0, "Error");
                assertThat(actual).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("测试方法：notNegative(long value, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: -1)")
            void givenMinus1ThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.notNegative(-1,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 0)")
            void given0ThenReturn0() {
                long actual = Validation.notNegative(0, () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Test notNull")
    class TestNotNull {
        /**
         * 目标方法：{@link Validation#notNull(Object, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: notNull(T actual, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation failed (actual: null)")
            void givenNullThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.notNull(null, "Error"), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 'Hello')")
            void givenHelloThenReturnHello() {
                String actual = Validation.notNull("Hello", "Error");
                assertThat(actual).isEqualTo("Hello");
            }
        }

        /**
         * 目标方法：{@link Validation#notNull(Object, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: notNull(T actual, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Validation failed (actual: null)")
            void givenNullThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.notNull(null,
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 'Hello')")
            void givenHelloThenReturnHello() {
                String actual = Validation.notNull("Hello", () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo("Hello");
            }
        }
    }

    @Nested
    @DisplayName("Test same")
    class TestSame {
        /**
         * 目标方法：{@link Validation#same(Object, Object, String, Object...)}。
         */
        @Nested
        @DisplayName("Test method: same(T actual, T expected, String error, Object... args)")
        class TestWithoutExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 'Hello', expected: 'Hello World')")
            void givenDifferentStringThenThrowException() {
                IllegalArgumentException exception =
                        catchThrowableOfType(() -> Validation.same("Hello", "Hello World", "Error"),
                                IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 'Hello', expected: 'Hello')")
            void givenSameStringThenReturnHello() {
                String actual = Validation.same("Hello", "Hello", "Error");
                assertThat(actual).isEqualTo("Hello");
            }
        }

        /**
         * 目标方法：{@link Validation#same(Object, Object, Supplier)}。
         */
        @Nested
        @DisplayName("Test method: same(T actual, T expected, Supplier<E> exceptionSupplier)")
        class TestWithExceptionHandler {
            @Test
            @DisplayName("Validation failed (actual: 'Hello', expected: 'Hello World')")
            void givenDifferentStringThenThrowException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> Validation.same("Hello",
                        "Hello World",
                        () -> new IllegalArgumentException("Error")), IllegalArgumentException.class);
                assertThat(exception).hasMessage("Error");
            }

            @Test
            @DisplayName("Validation passed (actual: 'Hello', expected: 'Hello')")
            void givenSameStringThenReturnHello() {
                String actual = Validation.same("Hello", "Hello", () -> new IllegalArgumentException("Error"));
                assertThat(actual).isEqualTo("Hello");
            }
        }
    }
}
