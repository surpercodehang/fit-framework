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

import java.util.Date;

/**
 * {@link ValueConverter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-01-19
 */
public class ValueConverterTest {
    /**
     * 目标方法：{@link ValueConverter#convert(String, Class)}。
     */
    @Nested
    @DisplayName("Test method: convert(String value, Class<?> targetClass)")
    class WhenConvert {
        @Nested
        @DisplayName("Given source is null:")
        class GivenSourceNull {
            @Nested
            @DisplayName("Given supported target class:")
            class GivenSupportedTargetClass {
                @Test
                @DisplayName("Given targetClass is Byte.class, then return null")
                void givenTargetClassIsByteThenReturnNull() {
                    Byte actual = ObjectUtils.cast(ValueConverter.convert(null, Byte.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is byte.class, then return null")
                void givenTargetClassIsPrimitiveByteThenReturnNull() {
                    Byte actual = ObjectUtils.cast(ValueConverter.convert(null, byte.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Short.class, then return null")
                void givenTargetClassIsShortThenReturnNull() {
                    Short actual = ObjectUtils.cast(ValueConverter.convert(null, Short.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is short.class, then return null")
                void givenTargetClassIsPrimitiveShortThenReturnNull() {
                    Short actual = ObjectUtils.cast(ValueConverter.convert(null, short.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Integer.class, then return null")
                void givenTargetClassIsIntegerThenReturnNull() {
                    Integer actual = ObjectUtils.cast(ValueConverter.convert(null, Integer.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is int.class, then return null")
                void givenTargetClassIsIntThenReturnNull() {
                    Integer actual = ObjectUtils.cast(ValueConverter.convert(null, int.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Long.class, then return null")
                void givenTargetClassIsLongThenReturnNull() {
                    Long actual = ObjectUtils.cast(ValueConverter.convert(null, Long.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is long.class, then return null")
                void givenTargetClassIsPrimitiveLongThenReturnNull() {
                    Long actual = ObjectUtils.cast(ValueConverter.convert(null, long.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Float.class, then return null")
                void givenTargetClassIsFloatThenReturnNull() {
                    Float actual = ObjectUtils.cast(ValueConverter.convert(null, Float.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is float.class, then return null")
                void givenTargetClassIsPrimitiveFloatThenReturnNull() {
                    Float actual = ObjectUtils.cast(ValueConverter.convert(null, float.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Double.class, then return null")
                void givenTargetClassIsDoubleThenReturnNull() {
                    Double actual = ObjectUtils.cast(ValueConverter.convert(null, Double.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is double.class, then return null")
                void givenTargetClassIsPrimitiveDoubleThenReturnNull() {
                    Double actual = ObjectUtils.cast(ValueConverter.convert(null, double.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Character.class, then return null")
                void givenTargetClassIsCharacterThenReturnNull() {
                    Character actual = ObjectUtils.cast(ValueConverter.convert(null, Character.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is char.class, then return null")
                void givenTargetClassIsCharThenReturnNull() {
                    Character actual = ObjectUtils.cast(ValueConverter.convert(null, char.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Boolean.class, then return null")
                void givenTargetClassIsBooleanThenReturnNull() {
                    Boolean actual = ObjectUtils.cast(ValueConverter.convert(null, Boolean.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is boolean.class, then return null")
                void givenTargetClassIsPrimitiveBooleanThenReturnNull() {
                    Boolean actual = ObjectUtils.cast(ValueConverter.convert(null, boolean.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Date.class, then return null")
                void givenTargetClassIsDateThenReturnNull() {
                    Date actual = ObjectUtils.cast(ValueConverter.convert(null, Date.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is String.class, then return null")
                void givenTargetClassIsStringThenReturnNull() {
                    String actual = ObjectUtils.cast(ValueConverter.convert(null, String.class));
                    assertThat(actual).isNull();
                }
            }

            @Nested
            @DisplayName("Given not supported target class:")
            class GivenNotSupportedTargetClass {
                @Test
                @DisplayName("Given targetClass is long[].class, then return null")
                void givenTargetClassIsPrimitiveLongArrayThenReturnNull() {
                    Long[] actual = ObjectUtils.cast(ValueConverter.convert(null, long[].class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Object.class, then return null")
                void givenTargetClassIsObjectThenReturnNull() {
                    Object actual = ValueConverter.convert(null, Object.class);
                    assertThat(actual).isNull();
                }
            }
        }

        @Nested
        @DisplayName("Given source is empty:")
        class GivenSourceEmpty {
            @Nested
            @DisplayName("Given supported target class:")
            class GivenSupportedTargetClass {
                @Test
                @DisplayName("Given targetClass is Byte.class, then return null")
                void givenTargetClassIsByteThenReturnNull() {
                    Byte actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Byte.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is byte.class, then return null")
                void givenTargetClassIsPrimitiveByteThenReturnNull() {
                    Byte actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, byte.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Short.class, then return null")
                void givenTargetClassIsShortThenReturnNull() {
                    Short actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Short.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is short.class, then return null")
                void givenTargetClassIsPrimitiveShortThenReturnNull() {
                    Short actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, short.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Integer.class, then return null")
                void givenTargetClassIsIntegerThenReturnNull() {
                    Integer actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Integer.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is int.class, then return null")
                void givenTargetClassIsIntThenReturnNull() {
                    Integer actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, int.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Long.class, then return null")
                void givenTargetClassIsLongThenReturnNull() {
                    Long actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Long.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is long.class, then return null")
                void givenTargetClassIsPrimitiveLongThenReturnNull() {
                    Long actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, long.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Float.class, then return null")
                void givenTargetClassIsFloatThenReturnNull() {
                    Float actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Float.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is float.class, then return null")
                void givenTargetClassIsPrimitiveFloatThenReturnNull() {
                    Float actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, float.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Double.class, then return null")
                void givenTargetClassIsDoubleThenReturnNull() {
                    Double actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Double.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is double.class, then return null")
                void givenTargetClassIsPrimitiveDoubleThenReturnNull() {
                    Double actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, double.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Character.class, then return null")
                void givenTargetClassIsCharacterThenReturnNull() {
                    Character actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Character.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is char.class, then return null")
                void givenTargetClassIsCharThenReturnNull() {
                    Character actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, char.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Boolean.class, then return null")
                void givenTargetClassIsBooleanThenReturnNull() {
                    Boolean actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Boolean.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is boolean.class, then return null")
                void givenTargetClassIsPrimitiveBooleanThenReturnNull() {
                    Boolean actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, boolean.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is Date.class, then return null")
                void givenTargetClassIsDateThenReturnNull() {
                    Date actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, Date.class));
                    assertThat(actual).isNull();
                }

                @Test
                @DisplayName("Given targetClass is String.class, then return ''")
                void givenTargetClassIsStringThenReturnNull() {
                    String actual = ObjectUtils.cast(ValueConverter.convert(StringUtils.EMPTY, String.class));
                    assertThat(actual).isEmpty();
                }
            }

            @Nested
            @DisplayName("Given not supported target class:")
            class GivenNotSupportedTargetClass {
                @Test
                @DisplayName("Given targetClass is long[].class, then throw exception")
                void givenTargetClassIsPrimitiveLongArrayThenThrowException() {
                    ClassCastException exception =
                            catchThrowableOfType(() -> ValueConverter.convert(StringUtils.EMPTY, long[].class),
                                    ClassCastException.class);
                    assertThat(exception).hasMessage(
                            "Not supported class to resolve configuration. [targetClass=long[]]");
                }

                @Test
                @DisplayName("Given targetClass is Object.class, then throw exception")
                void givenTargetClassIsObjectThenThrowException() {
                    ClassCastException exception =
                            catchThrowableOfType(() -> ValueConverter.convert(StringUtils.EMPTY, Object.class),
                                    ClassCastException.class);
                    assertThat(exception).hasMessage(
                            "Not supported class to resolve configuration. [targetClass=Object]");
                }
            }
        }

        @Nested
        @DisplayName("Given source is '0':")
        class GivenSourceNumber {
            private final String source = "0";

            @Test
            @DisplayName("Given targetClass is Byte.class, then return 0")
            void givenTargetClassIsByteThenReturn0() {
                Byte actual = ObjectUtils.cast(ValueConverter.convert(this.source, Byte.class));
                byte comparedValue = 0;
                assertThat(actual).isEqualTo(comparedValue);
            }

            @Test
            @DisplayName("Given targetClass is byte.class, then return 0")
            void givenTargetClassIsPrimitiveByteThenReturn0() {
                Byte actual = ObjectUtils.cast(ValueConverter.convert(this.source, byte.class));
                byte comparedValue = 0;
                assertThat(actual).isEqualTo(comparedValue);
            }

            @Test
            @DisplayName("Given targetClass is Short.class, then return 0")
            void givenTargetClassIsShortThenReturn0() {
                Short actual = ObjectUtils.cast(ValueConverter.convert(this.source, Short.class));
                short comparedValue = 0;
                assertThat(actual).isEqualTo(comparedValue);
            }

            @Test
            @DisplayName("Given targetClass is short.class, then return 0")
            void givenTargetClassIsPrimitiveShortThenReturn0() {
                Short actual = ObjectUtils.cast(ValueConverter.convert(this.source, short.class));
                short comparedValue = 0;
                assertThat(actual).isEqualTo(comparedValue);
            }

            @Test
            @DisplayName("Given targetClass is Integer.class, then return 0")
            void givenTargetClassIsIntegerThenReturn0() {
                Integer actual = ObjectUtils.cast(ValueConverter.convert(this.source, Integer.class));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given targetClass is int.class, then return 0")
            void givenTargetClassIsIntThenReturn0() {
                Integer actual = ObjectUtils.cast(ValueConverter.convert(this.source, int.class));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given targetClass is Long.class, then return 0")
            void givenTargetClassIsLongThenReturn0() {
                Long actual = ObjectUtils.cast(ValueConverter.convert(this.source, Long.class));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given targetClass is long.class, then return 0")
            void givenTargetClassIsPrimitiveLongThenReturn0() {
                Long actual = ObjectUtils.cast(ValueConverter.convert(this.source, long.class));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given targetClass is Float.class, then return 0")
            void givenTargetClassIsFloatThenReturn0() {
                Float actual = ObjectUtils.cast(ValueConverter.convert(this.source, Float.class));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given targetClass is float.class, then return 0")
            void givenTargetClassIsPrimitiveFloatThenReturn0() {
                Float actual = ObjectUtils.cast(ValueConverter.convert(this.source, float.class));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given targetClass is Double.class, then return 0")
            void givenTargetClassIsDoubleThenReturn0() {
                Double actual = ObjectUtils.cast(ValueConverter.convert(this.source, Double.class));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given targetClass is double.class, then return 0")
            void givenTargetClassIsPrimitiveDoubleThenReturn0() {
                Double actual = ObjectUtils.cast(ValueConverter.convert(this.source, double.class));
                assertThat(actual).isEqualTo(0);
            }

            @Test
            @DisplayName("Given targetClass is Character.class, then return '0'")
            void givenTargetClassIsCharacterThenReturn0() {
                Character actual = ObjectUtils.cast(ValueConverter.convert(this.source, Character.class));
                assertThat(actual).isEqualTo('0');
            }

            @Test
            @DisplayName("Given targetClass is char.class, then return '0'")
            void givenTargetClassIsCharThenReturn0() {
                Character actual = ObjectUtils.cast(ValueConverter.convert(this.source, char.class));
                assertThat(actual).isEqualTo('0');
            }

            @Test
            @DisplayName("Given targetClass is Boolean.class, then return null")
            void givenTargetClassIsBooleanThenReturnNull() {
                Boolean actual = ObjectUtils.cast(ValueConverter.convert(this.source, Boolean.class));
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Given targetClass is boolean.class, then return null")
            void givenTargetClassIsPrimitiveBooleanThenReturnNull() {
                Boolean actual = ObjectUtils.cast(ValueConverter.convert(this.source, boolean.class));
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Given targetClass is Date.class, then return null")
            void givenTargetClassIsDateThenReturnNull() {
                Date actual = ObjectUtils.cast(ValueConverter.convert(this.source, Date.class));
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Given targetClass is String.class, then return '0'")
            void givenTargetClassIsStringThenReturn0() {
                String actual = ObjectUtils.cast(ValueConverter.convert(this.source, String.class));
                assertThat(actual).isEqualTo("0");
            }
        }

        @Nested
        @DisplayName("Given source is 'true'")
        class GivenSourceTrue {
            private final String source = "true";

            @Test
            @DisplayName("Given targetClass is Character.class, then return null")
            void givenTargetClassIsCharacterThenReturnNull() {
                Character actual = ObjectUtils.cast(ValueConverter.convert(this.source, Character.class));
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Given targetClass is char.class, then return null")
            void givenTargetClassIsCharThenReturnNull() {
                Character actual = ObjectUtils.cast(ValueConverter.convert(this.source, char.class));
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Given targetClass is Boolean.class, then return true")
            void givenTargetClassIsBooleanThenReturnTrue() {
                Boolean actual = ObjectUtils.cast(ValueConverter.convert(this.source, Boolean.class));
                assertThat(actual).isTrue();
            }

            @Test
            @DisplayName("Given targetClass is boolean.class, then return true")
            void givenTargetClassIsPrimitiveBooleanThenReturnTrue() {
                Boolean actual = ObjectUtils.cast(ValueConverter.convert(this.source, boolean.class));
                assertThat(actual).isTrue();
            }

            @Test
            @DisplayName("Given targetClass is Date.class, then return null")
            void givenTargetClassIsDateThenReturnNull() {
                Date actual = ObjectUtils.cast(ValueConverter.convert(this.source, Date.class));
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Given targetClass is String.class, then return 'true'")
            void givenTargetClassIsStringThenReturnTrue() {
                String actual = ObjectUtils.cast(ValueConverter.convert(this.source, String.class));
                assertThat(actual).isEqualTo("true");
            }
        }

        @Nested
        @DisplayName("Given source is 'FALSE'")
        class GivenSourceFalse {
            private final String source = "FALSE";

            @Test
            @DisplayName("Given targetClass is Boolean.class, then return false")
            void givenTargetClassIsBooleanThenReturnFalse() {
                Boolean actual = ObjectUtils.cast(ValueConverter.convert(this.source, Boolean.class));
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("Given targetClass is boolean.class, then return false")
            void givenTargetClassIsPrimitiveBooleanThenReturnFalse() {
                Boolean actual = ObjectUtils.cast(ValueConverter.convert(this.source, boolean.class));
                assertThat(actual).isFalse();
            }
        }
    }
}
