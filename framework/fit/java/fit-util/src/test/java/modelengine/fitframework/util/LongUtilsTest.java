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
 * {@link LongUtils} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class LongUtilsTest {
    @Nested
    @DisplayName("Test between")
    class TestBetween {
        /** 表示比较的较小值。 */
        private static final long COMPARABLE_SMALL = -100L;

        /** 表示比较的较大值。 */
        private static final long COMPARABLE_BIG = 100L;

        /**
         * 目标方法：{@link LongUtils#between(long, long, long)}。
         */
        @Nested
        @DisplayName("Test method: between(int value, int min, int max)")
        class TestBetweenWithoutInclude {
            @Test
            @DisplayName("Given 100 and range [-100, 100] then return true")
            void givenValueIsMaxThenReturnTrue() {
                boolean actual = LongUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG);
                assertThat(actual).isTrue();
            }
        }

        /**
         * 目标方法：{@link LongUtils#between(long, long, long, boolean, boolean)}。
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
                    boolean actual = LongUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG, true, true);
                    assertThat(actual).isTrue();
                }

                @Test
                @DisplayName("Given 101 and range [-100, 100] then return false")
                void givenValueIsGreaterThanMaxThenReturnFalse() {
                    boolean actual = LongUtils.between(COMPARABLE_BIG + 1, COMPARABLE_SMALL, COMPARABLE_BIG, true,
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
                    boolean actual = LongUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG, true, false);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given 99 and range [-100, 100) then return true")
                void givenValueIsLessThanMaxThenReturnTrue() {
                    boolean actual = LongUtils.between(COMPARABLE_BIG - 1, COMPARABLE_SMALL, COMPARABLE_BIG, true,
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
                    boolean actual = LongUtils.between(COMPARABLE_SMALL, COMPARABLE_SMALL, COMPARABLE_BIG, true, true);
                    assertThat(actual).isTrue();
                }

                @Test
                @DisplayName("Given -101 and range [-100, 100] then return false")
                void givenValueIsLessThanMinThenReturnFalse() {
                    boolean actual = LongUtils.between(COMPARABLE_SMALL - 1, COMPARABLE_SMALL, COMPARABLE_BIG, true,
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
                    boolean actual = LongUtils.between(COMPARABLE_SMALL, COMPARABLE_SMALL, COMPARABLE_BIG, false, true);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given -99 and range (-100, 100] then return true")
                void givenValueIsGreaterThanMinThenReturnTrue() {
                    boolean actual = LongUtils.between(COMPARABLE_SMALL + 1, COMPARABLE_SMALL, COMPARABLE_BIG, false,
                            true);
                    assertThat(actual).isTrue();
                }
            }
        }
    }
}
