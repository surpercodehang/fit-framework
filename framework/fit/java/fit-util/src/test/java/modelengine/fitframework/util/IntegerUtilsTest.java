/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link IntegerUtils} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class IntegerUtilsTest {
    @Nested
    @DisplayName("Test between")
    class TestBetween {
        /** 表示比较的较小值。 */
        private static final int COMPARABLE_SMALL = -100;

        /** 表示比较的较大值。 */
        private static final int COMPARABLE_BIG = 100;

        /**
         * 目标方法：{@link IntegerUtils#between(int, int, int)}。
         */
        @Nested
        @DisplayName("Test method: between(int value, int min, int max)")
        class TestBetweenWithoutInclude {
            @Test
            @DisplayName("Given 100 and range [-100, 100] then return true")
            void givenValueIsMaxThenReturnTrue() {
                boolean actual = IntegerUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG);
                assertThat(actual).isTrue();
            }
        }

        /**
         * 目标方法：{@link IntegerUtils#between(int, int, int, boolean, boolean)}。
         */
        @Nested
        @DisplayName("Test method: between(T value, T min, T max, boolean includeMin, boolean includeMax)")
        class TestBetweenWithInclude {
            @Nested
            @DisplayName("Given include max")
            class GivenIncludeMax {
                @Test
                @DisplayName("Given 100 and range [-100, 100] then return true")
                void givenValueIsMaxThenReturnTrue() {
                    boolean actual = IntegerUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG, true, true);
                    assertThat(actual).isTrue();
                }

                @Test
                @DisplayName("Given 101 and range [-100, 100] then return false")
                void givenValueIsGreaterThanMaxThenReturnFalse() {
                    boolean actual = IntegerUtils.between(COMPARABLE_BIG + 1, COMPARABLE_SMALL, COMPARABLE_BIG, true,
                            true);
                    assertThat(actual).isFalse();
                }
            }

            @Nested
            @DisplayName("Given exclude max")
            class GivenExcludeMax {
                @Test
                @DisplayName("Given 100 and range [-100, 100) then return false")
                void givenValueIsMaxThenReturnFalse() {
                    boolean actual = IntegerUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG, true,
                            false);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given 99 and range [-100, 100) then return true")
                void givenValueIsLessThanMaxThenReturnTrue() {
                    boolean actual = IntegerUtils.between(COMPARABLE_BIG - 1, COMPARABLE_SMALL, COMPARABLE_BIG, true,
                            false);
                    assertThat(actual).isTrue();
                }
            }

            @Nested
            @DisplayName("Given include min")
            class GivenIncludeMin {
                @Test
                @DisplayName("Given -100 and range [-100, 100] then return true")
                void givenValueIsMinThenReturnTrue() {
                    boolean actual = IntegerUtils.between(COMPARABLE_SMALL, COMPARABLE_SMALL, COMPARABLE_BIG, true,
                            true);
                    assertThat(actual).isTrue();
                }

                @Test
                @DisplayName("Given -101 and range [-100, 100] then return false")
                void givenValueIsLessThanMinThenReturnFalse() {
                    boolean actual = IntegerUtils.between(COMPARABLE_SMALL - 1, COMPARABLE_SMALL, COMPARABLE_BIG, true,
                            true);
                    assertThat(actual).isFalse();
                }
            }

            @Nested
            @DisplayName("Given exclude min")
            class GivenExcludeMin {
                @Test
                @DisplayName("Given -100 and range (-100, 100] then return false")
                void givenValueIsMinThenReturnFalse() {
                    boolean actual = IntegerUtils.between(COMPARABLE_SMALL, COMPARABLE_SMALL, COMPARABLE_BIG, false,
                            true);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given -99 and range (-100, 100] then return true")
                void givenValueIsGreaterThanMinThenReturnTrue() {
                    boolean actual = IntegerUtils.between(COMPARABLE_SMALL + 1, COMPARABLE_SMALL, COMPARABLE_BIG, false,
                            true);
                    assertThat(actual).isTrue();
                }
            }
        }
    }

    @Nested
    @DisplayName("验证方法: equals(int first, int second)")
    class TestEquals {
        @Test
        @DisplayName("当两个数字相等时，返回 true")
        void given2SameIntThenReturnTrue() {
            boolean actual = IntegerUtils.equals(0, 0);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("当两个数字不相等时，返回 false")
        void given2DifferentIntThenReturnFalse() {
            boolean actual = IntegerUtils.equals(0, 1);
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link IntegerUtils#sum(int...)}。
     */
    @Nested
    @DisplayName("Test method: sum(int... values)")
    class TestSum {
        @Test
        @DisplayName("Given values is empty then return 0")
        void givenValuesEmptyThenReturn0() {
            int actual = IntegerUtils.sum();
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("Given values is null then return 0")
        void givenValuesNullThenReturn0() {
            int actual = IntegerUtils.sum((int[]) null);
            assertThat(actual).isEqualTo(0);
        }

        @Test
        @DisplayName("Given values is [1, 2, 3, 4] then return their 10")
        void givenValuesNotEmptyThenReturnTheirSum() {
            int actual = IntegerUtils.sum(1, 2, 3, 4);
            assertThat(actual).isEqualTo(10);
        }
    }
}
