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
 * {@link CharacterUtils} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class CharacterUtilsTest {
    @Nested
    @DisplayName("Test between")
    class TestBetween {
        /** 表示比较的较小值。 */
        private static final char COMPARABLE_SMALL = '1';

        /** 表示比较的较大值。 */
        private static final char COMPARABLE_BIG = '3';

        /**
         * 目标方法：{@link CharacterUtils#between(char, char, char)}。
         */
        @Nested
        @DisplayName("Test method: between(char value, char min, char max)")
        class TestBetweenWithoutInclude {
            @Test
            @DisplayName("Given '3' and range ['1', '3'] then return true")
            void givenValueIsMaxThenReturnTrue() {
                boolean actual = CharacterUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG);
                assertThat(actual).isTrue();
            }
        }

        /**
         * 目标方法：{@link CharacterUtils#between(char, char, char, boolean, boolean)}。
         */
        @Nested
        @DisplayName("Test method: between(T value, T min, T max, boolean includeMin, boolean includeMax)")
        class TestBetweenWithInclude {
            @Nested
            @DisplayName("Given include max")
            class GivenIncludeMax {
                @Test
                @DisplayName("Given '3' and range ['1', '3'] then return true")
                void givenValueIsMaxThenReturnTrue() {
                    boolean actual = CharacterUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG, true,
                            true);
                    assertThat(actual).isTrue();
                }

                @Test
                @DisplayName("Given '4' and range ['1', '3'] then return false")
                void givenValueIsGreaterThanMaxThenReturnFalse() {
                    boolean actual = CharacterUtils.between((char) ((int) COMPARABLE_BIG + 1), COMPARABLE_SMALL,
                            COMPARABLE_BIG, true, true);
                    assertThat(actual).isFalse();
                }
            }

            @Nested
            @DisplayName("Given exclude max")
            class GivenExcludeMax {
                @Test
                @DisplayName("Given '3' and range ['1', '3') then return false")
                void givenValueIsMaxThenReturnFalse() {
                    boolean actual = CharacterUtils.between(COMPARABLE_BIG, COMPARABLE_SMALL, COMPARABLE_BIG, true,
                            false);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given '2' and range ['1', '3') then return true")
                void givenValueIsLessThanMaxThenReturnTrue() {
                    boolean actual = CharacterUtils.between((char) ((int) COMPARABLE_BIG - 1), COMPARABLE_SMALL,
                            COMPARABLE_BIG, true, false);
                    assertThat(actual).isTrue();
                }
            }

            @Nested
            @DisplayName("Given include min")
            class GivenIncludeMin {
                @Test
                @DisplayName("Given '1' and range ['1', '3'] then return true")
                void givenValueIsMinThenReturnTrue() {
                    boolean actual = CharacterUtils.between(COMPARABLE_SMALL, COMPARABLE_SMALL, COMPARABLE_BIG, true,
                            true);
                    assertThat(actual).isTrue();
                }

                @Test
                @DisplayName("Given '0' and range ['1', '3'] then return false")
                void givenValueIsLessThanMinThenReturnFalse() {
                    boolean actual = CharacterUtils.between((char) ((int) COMPARABLE_SMALL - 1), COMPARABLE_SMALL,
                            COMPARABLE_BIG, true, true);
                    assertThat(actual).isFalse();
                }
            }

            @Nested
            @DisplayName("Given exclude min")
            class GivenExcludeMin {
                @Test
                @DisplayName("Given '1' and range ('1', '3'] then return false")
                void givenValueIsMinThenReturnFalse() {
                    boolean actual = CharacterUtils.between(COMPARABLE_SMALL, COMPARABLE_SMALL, COMPARABLE_BIG, false,
                            true);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given '2' and range ('1', '3'] then return true")
                void givenValueIsGreaterThanMinThenReturnTrue() {
                    boolean actual = CharacterUtils.between((char) ((int) COMPARABLE_SMALL + 1), COMPARABLE_SMALL,
                            COMPARABLE_BIG, false, true);
                    assertThat(actual).isTrue();
                }
            }
        }
    }
}
