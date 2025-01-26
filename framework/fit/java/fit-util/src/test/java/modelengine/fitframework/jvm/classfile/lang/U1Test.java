/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.lang;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * {@link U1} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-08
 */
@DisplayName("测试 U1 类")
class U1Test {
    private static final int CLASSFILE_INT_VALUE = 10;

    private final U1 u1 = U1.of(CLASSFILE_INT_VALUE);

    @Nested
    @DisplayName("当提供一个 U1 实例")
    class GivenU1 {
        @Test
        @DisplayName("返回 8 位字节数据表现形式")
        void shouldReturnByteValue() {
            final byte value = u1.byteValue();
            assertThat(value).isEqualTo((byte) CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 16 位整数数据表现形式")
        void shouldReturnShortValue() {
            final short value = u1.shortValue();
            assertThat(value).isEqualTo((short) CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 32 位整数数据表现形式")
        void shouldReturnIntValue() {
            final int value = u1.intValue();
            assertThat(value).isEqualTo(CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 64 位整数数据表现形式")
        void shouldReturnLongValue() {
            final long value = u1.longValue();
            assertThat(value).isEqualTo(CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回一个 16 进制字符串")
        void shouldReturnHexString() {
            final String hexString = u1.toHexString();
            assertThat(hexString).isEqualTo(String.format(Locale.ROOT, "%02x", CLASSFILE_INT_VALUE));
        }

        @Test
        @DisplayName("返回一个 toString 后的字符串")
        void shouldReturnString() {
            final String toString = u1.toString();
            assertThat(toString).isEqualTo(String.valueOf(CLASSFILE_INT_VALUE));
        }

        @Nested
        @DisplayName("测试方法：equals(Object obj)")
        class TestEquals {
            @Test
            @DisplayName("调用 equals 和自己比较时，返回 true")
            @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
            void givenWithSelfThenReturnTrue() {
                final boolean equals = u1.equals(u1);
                assertThat(equals).isTrue();
            }

            @Test
            @DisplayName("调用 equals 和其他同类型实例比较时，返回 false")
            void givenSameTypeAndNotSameValueThenReturnFalse() {
                U1 maxU1 = U1.of(Byte.MAX_VALUE);
                final boolean equals = u1.equals(maxU1);
                assertThat(equals).isFalse();
            }

            @Test
            @DisplayName("调用 equals 和其他不同类型实例比较时，返回 false")
            @SuppressWarnings("EqualsBetweenInconvertibleTypes")
            void givenNotSameTypeThenReturnFalse() {
                String otherTypeInstance = "";
                final boolean equals = u1.equals(otherTypeInstance);
                assertThat(equals).isFalse();
            }
        }

        @Test
        @DisplayName("调用 compareTo 比较方法，返回比较后的结果")
        void callCompareMethodThenReturnCompareResult() {
            U1 maxU1 = U1.of(Byte.MAX_VALUE);
            final int compare = u1.compareTo(maxU1);
            assertThat(compare).isNegative();
        }

        @Test
        @DisplayName("调用 write 写入方法，结果写入成功")
        void callWriteMethodThenSuccess() throws IOException {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                u1.write(outputStream);
                final byte[] bytes = outputStream.toByteArray();
                assertThat(bytes).contains(CLASSFILE_INT_VALUE);
            }
        }
    }

    @Nested
    @DisplayName("测试方法：of")
    class TestOf {
        @Test
        @DisplayName("当提供参数为字节时，返回 U1 实例")
        void givenByteNumberThenReturnU1() {
            U1 byteU1 = U1.of((byte) CLASSFILE_INT_VALUE);
            assertThat(byteU1).isEqualTo(u1);
        }

        @Test
        @DisplayName("当提供参数为 short 时，返回 U1 实例")
        void givenShortNumberThenReturnU1() {
            U1 shortU1 = U1.of((short) CLASSFILE_INT_VALUE);
            assertThat(shortU1).isEqualTo(u1);
        }

        @Test
        @DisplayName("当提供参数为 long 时，返回 U1 实例")
        void givenLongNumberThenReturnU1() {
            U1 longU1 = U1.of((long) CLASSFILE_INT_VALUE);
            assertThat(longU1).isEqualTo(u1);
        }
    }
}
