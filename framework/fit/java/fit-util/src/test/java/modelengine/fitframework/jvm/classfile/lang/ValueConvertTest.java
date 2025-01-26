/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.lang;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link ValueConvert} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-09
 */
@DisplayName("测试 ValueConvert 类")
class ValueConvertTest {
    private static final int LOWER_ZERO_NUMBER = -1;

    @Nested
    @DisplayName("测试方法：byteValue")
    class TestByteValue {
        private static final int GREATER_0XFF_NUMBER = 0x1ff;

        @Test
        @DisplayName("当提供 short 参数太小时，返回转换后的 byte 数值")
        void givenShortValueTooSmallThenThrowException() {
            assertThatThrownBy(() -> ValueConvert.byteValue((short) LOWER_ZERO_NUMBER)).isInstanceOf(
                    ValueOverflowException.class);
        }

        @Test
        @DisplayName("当提供 short 参数太大时，返回转换后的 byte 数值")
        void givenShortValueTooLargeThenThrowException() {
            assertThatThrownBy(() -> ValueConvert.byteValue((short) GREATER_0XFF_NUMBER)).isInstanceOf(
                    ValueOverflowException.class);
        }

        @Test
        @DisplayName("当提供 int 参数太小时，返回转换后的 byte 数值")
        void givenIntValueTooSmallThenThrowException() {
            ValueOverflowException cause =
                    catchThrowableOfType(() -> ValueConvert.byteValue(LOWER_ZERO_NUMBER), ValueOverflowException.class);
            assertThat(cause).isNotNull();
        }

        @Test
        @DisplayName("当提供 int 参数太大时，返回转换后的 byte 数值")
        void givenIntValueTooLargeThenThrowException() {
            ValueOverflowException cause = catchThrowableOfType(() -> ValueConvert.byteValue(GREATER_0XFF_NUMBER),
                    ValueOverflowException.class);
            assertThat(cause).isNotNull();
        }

        @Test
        @DisplayName("当提供 long 参数太小时，返回转换后的 byte 数值")
        void givenLongValueTooSmallThenThrowException() {
            assertThatThrownBy(() -> ValueConvert.byteValue((long) LOWER_ZERO_NUMBER)).isInstanceOf(
                    ValueOverflowException.class);
        }

        @Test
        @DisplayName("当提供 long 参数太大时，返回转换后的 byte 数值")
        void givenLongValueTooLargeThenThrowException() {
            assertThatThrownBy(() -> ValueConvert.byteValue((long) GREATER_0XFF_NUMBER)).isInstanceOf(
                    ValueOverflowException.class);
        }

        @Nested
        @DisplayName("测试方法：shortValue")
        class TestShortValue {
            private static final int GREATER_0XFFFF_NUMBER = 0x1ffff;

            @Test
            @DisplayName("当提供 short 参数太小时，返回转换后的 byte 数值")
            void givenShortValueTooSmallThenThrowException() {
                assertThatThrownBy(() -> ValueConvert.shortValue((short) LOWER_ZERO_NUMBER)).isInstanceOf(
                        ValueOverflowException.class);
            }

            @Test
            @DisplayName("当提供 short 参数太大时，返回转换后的 byte 数值")
            void givenShortValueTooLargeThenThrowException() {
                assertThatThrownBy(() -> ValueConvert.shortValue((short) GREATER_0XFFFF_NUMBER)).isInstanceOf(
                        ValueOverflowException.class);
            }

            @Test
            @DisplayName("当提供 int 参数太小时，返回转换后的 byte 数值")
            void givenIntValueTooSmallThenThrowException() {
                ValueOverflowException cause = catchThrowableOfType(() -> ValueConvert.shortValue(LOWER_ZERO_NUMBER),
                        ValueOverflowException.class);
                assertThat(cause).isNotNull();
            }

            @Test
            @DisplayName("当提供 int 参数太大时，返回转换后的 byte 数值")
            void givenIntValueTooLargeThenThrowException() {
                assertThatThrownBy(() -> ValueConvert.shortValue(GREATER_0XFFFF_NUMBER)).isInstanceOf(
                        ValueOverflowException.class);
            }

            @Test
            @DisplayName("当提供 long 参数太小时，返回转换后的 byte 数值")
            void givenLongValueTooSmallThenThrowException() {
                assertThatThrownBy(() -> ValueConvert.shortValue((long) LOWER_ZERO_NUMBER)).isInstanceOf(
                        ValueOverflowException.class);
            }

            @Test
            @DisplayName("当提供 long 参数太大时，返回转换后的 byte 数值")
            void givenLongValueTooLargeThenThrowException() {
                assertThatThrownBy(() -> ValueConvert.shortValue((long) GREATER_0XFFFF_NUMBER)).isInstanceOf(
                        ValueOverflowException.class);
            }
        }

        @Nested
        @DisplayName("测试方法：shortValue")
        class TestIntValue {
            private static final long GREATER_0XFFFFFFFF_NUMBER = 0x1ffffffffL;

            @Test
            @DisplayName("当提供 long 参数太小时，返回转换后的 byte 数值")
            void givenLongValueTooSmallThenThrowException() {
                ValueOverflowException cause = catchThrowableOfType(() -> ValueConvert.intValue(LOWER_ZERO_NUMBER),
                        ValueOverflowException.class);
                assertThat(cause).isNotNull();
            }

            @Test
            @DisplayName("当提供 long 参数太大时，返回转换后的 byte 数值")
            void givenLongValueTooLargeThenThrowException() {
                assertThatThrownBy(() -> ValueConvert.intValue(GREATER_0XFFFFFFFF_NUMBER)).isInstanceOf(
                        ValueOverflowException.class);
            }
        }
    }
}
