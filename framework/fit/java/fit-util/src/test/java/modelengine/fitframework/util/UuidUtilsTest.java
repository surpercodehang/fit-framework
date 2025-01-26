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
 * {@link UuidUtils} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class UuidUtilsTest {
    @Nested
    @DisplayName("Test method: isUuidCharacter(char ch)")
    class TestIsUuidCharacter {
        @Test
        @DisplayName("Given ch is '0' then return true")
        void givenNumberThenReturnTrue() {
            boolean actual = UuidUtils.isUuidCharacter('0');
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Given ch is 'a' then return true")
        void givenLowerCaseThenReturnTrue() {
            boolean actual = UuidUtils.isUuidCharacter('a');
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Given ch is 'A' then return true")
        void givenUpperCaseThenReturnTrue() {
            boolean actual = UuidUtils.isUuidCharacter('A');
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Given ch is '-' then return true")
        void givenSymbolThenReturnFalse() {
            boolean actual = UuidUtils.isUuidCharacter('-');
            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("Test method: isUuidCharacterSequence(CharSequence chars, int from, int to)")
    class TestIsUuidCharacterSequence {
        @Test
        @DisplayName("Given 'aA0' then return true")
        void givenAllUuidCharThenReturnTrue() {
            boolean actual = UuidUtils.isUuidCharacterSequence("aA0", 0, 3);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Given 'aA0-' then return false")
        void givenNotAllUuidCharThenReturnFalse() {
            boolean actual = UuidUtils.isUuidCharacterSequence("aA0-", 0, 4);
            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("Test isUuidString")
    class TestIsUuidString {
        @Nested
        @DisplayName("Test method: isUuidString(String uuid)")
        class TestIsUuidStringNotIgnoreSeparator {
            private static final String VALID_UUID_STRING = "28a6e53d-da46-44dc-b10d-c46acbc1b647";
            private static final String LENGTH_INVALID_UUID_STRING = "28a6e53d-da46-44dc-b10d-c46acbc1b647a";
            private static final String CHARACTER_INVALID_UUID_STRING = "28a6e53d-da46-44dc-b10d-c46acbc1b64z";
            private static final String FORMAT_INVALID_UUID_STRING = "28a6e53-dda46-44dc-b10d-c46acbc1b647";

            @Test
            @DisplayName("Given valid uuid with length 36 then return true")
            void givenValidUuid36ThenReturnTrue() {
                boolean actual = UuidUtils.isUuidString(VALID_UUID_STRING);
                assertThat(actual).isTrue();
            }

            @Test
            @DisplayName("Given uuid with length 37 then return false")
            void givenUuid37ThenReturnFalse() {
                boolean actual = UuidUtils.isUuidString(LENGTH_INVALID_UUID_STRING);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("Given uuid with invalid char then return false")
            void givenUuidWithInvalidCharThenReturnFalse() {
                boolean actual = UuidUtils.isUuidString(CHARACTER_INVALID_UUID_STRING);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("Given uuid with invalid format then return false")
            void givenUuidWithInvalidFormatThenReturnFalse() {
                boolean actual = UuidUtils.isUuidString(FORMAT_INVALID_UUID_STRING);
                assertThat(actual).isFalse();
            }
        }

        @Nested
        @DisplayName("Test method: isUuidString(String uuid, boolean ignoreSeparator)")
        class TestIsUuidStringWithIgnoreParam {
            @Nested
            @DisplayName("Given ignoreSeparator is true")
            class TestIsUuidStringWithIgnoreTrue {
                private static final String VALID_UUID_STRING = "28a6e53dda4644dcb10dc46acbc1b647";
                private static final String LENGTH_INVALID_UUID_STRING = "28a6e53dda4644dcb10dc46acbc1b647a";

                @Test
                @DisplayName("Given uuid is null then return false")
                void givenNullThenReturnFalse() {
                    boolean actual = UuidUtils.isUuidString(null, true);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given uuid is length 33 then return false")
                void givenUuidWithLength33ThenReturnFalse() {
                    boolean actual = UuidUtils.isUuidString(LENGTH_INVALID_UUID_STRING, true);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given valid uuid with length 32 then return true")
                void givenValidUuidWithLength32ThenReturnTrue() {
                    boolean actual = UuidUtils.isUuidString(VALID_UUID_STRING, true);
                    assertThat(actual).isTrue();
                }
            }

            @Nested
            @DisplayName("Given ignoreSeparator is false")
            class TestIsUuidStringWithIgnoreFalse {
                private static final String CHARACTER_INVALID_UUID_STRING = "28a6e53d+da46-44dc-b10d-c46acbc1b647";

                @Test
                @DisplayName("Given uuid is null then return false")
                void givenNullThenReturnFalse() {
                    boolean actual = UuidUtils.isUuidString(null, false);
                    assertThat(actual).isFalse();
                }

                @Test
                @DisplayName("Given uuid with invalid separator then return false")
                void givenUuidWithInvalidSeparatorThenReturnFalse() {
                    boolean actual = UuidUtils.isUuidString(CHARACTER_INVALID_UUID_STRING, false);
                    assertThat(actual).isFalse();
                }
            }
        }
    }

    @Nested
    @DisplayName("Test method: randomUuidString()")
    class TestRandomUuidString {
        @Test
        @DisplayName("Then return correct uuid")
        void returnCorrectUuid() {
            String actual = UuidUtils.randomUuidString();
            boolean actualRet = UuidUtils.isUuidString(actual);
            assertThat(actualRet).isTrue();
        }
    }
}
