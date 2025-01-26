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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * {@link U4} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-09
 */
@DisplayName("测试 U4 类")
class U4Test {
    private static final int CLASSFILE_INT_VALUE = 10;

    private final U4 u4 = U4.of(CLASSFILE_INT_VALUE);

    @Nested
    @DisplayName("当提供一个 U4 实例")
    class GivenU4 {
        @Test
        @DisplayName("返回 8 位字节数据表现形式")
        void shouldReturnByteValue() {
            final byte value = u4.byteValue();
            assertThat(value).isEqualTo((byte) CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 16 位整数数据表现形式")
        void shouldReturnShortValue() {
            final short value = u4.shortValue();
            assertThat(value).isEqualTo((short) CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 32 位整数数据表现形式")
        void shouldReturnIntValue() {
            final int value = u4.intValue();
            assertThat(value).isEqualTo(CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 64 位整数数据表现形式")
        void shouldReturnLongValue() {
            final long value = u4.longValue();
            assertThat(value).isEqualTo(CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回一个 16 进制字符串")
        void shouldReturnHexString() {
            final String hexString = u4.toHexString();
            assertThat(hexString).isEqualTo(String.format(Locale.ROOT, "%08x", CLASSFILE_INT_VALUE));
        }

        @Test
        @DisplayName("返回一个 toString 后的字符串")
        void shouldReturnString() {
            final String toString = u4.toString();
            assertThat(toString).isEqualTo(Integer.toUnsignedString(CLASSFILE_INT_VALUE));
        }

        @Test
        @DisplayName("返回 hashcode")
        void shouldReturnHashCode() {
            final int code = u4.hashCode();
            final U4 otherU4 = U4.of(CLASSFILE_INT_VALUE);
            assertThat(code).isEqualTo(otherU4.hashCode());
        }

        @Nested
        @DisplayName("测试方法：equals(Object obj)")
        class TestEquals {
            @Test
            @DisplayName("调用 equals 和自己比较时，返回 true")
            @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
            void givenWithSelfThenReturnTrue() {
                final boolean equals = u4.equals(u4);
                assertThat(equals).isTrue();
            }

            @Test
            @DisplayName("调用 equals 和其他同类型实例比较时，返回 false")
            void givenSameTypeAndNotSameValueThenReturnFalse() {
                U4 maxU4 = U4.of(Byte.MAX_VALUE);
                final boolean equals = u4.equals(maxU4);
                assertThat(equals).isFalse();
            }

            @Test
            @DisplayName("调用 equals 和其他不同类型实例比较时，返回 false")
            @SuppressWarnings("EqualsBetweenInconvertibleTypes")
            void givenNotSameTypeThenReturnFalse() {
                String otherTypeInstance = "";
                final boolean equals = u4.equals(otherTypeInstance);
                assertThat(equals).isFalse();
            }
        }

        @Test
        @DisplayName("调用 compareTo 比较方法，返回比较后的结果")
        void callCompareMethodThenReturnCompareResult() {
            U4 maxU4 = U4.of(Byte.MAX_VALUE);
            final int compare = u4.compareTo(maxU4);
            assertThat(compare).isNegative();
        }

        @Test
        @DisplayName("调用 add 相加方法，返回加法后的结果")
        void callAddMethodThenReturnU4() {
            final U4 add = U4.ONE.add(U4.ONE);
            assertThat(add).isEqualTo(U4.TWO);
        }

        @Test
        @DisplayName("调用 read 读取方法，返回 U4 实例")
        void callReadMethodThenU4() throws IOException {
            byte[] bytes = {0, 0, 0, CLASSFILE_INT_VALUE};
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
                final U4 readU4 = U4.read(inputStream);
                assertThat(readU4).isEqualTo(u4);
            }
        }

        @Test
        @DisplayName("调用 write 写入方法，结果写入成功")
        void callWriteMethodThenSuccess() throws IOException {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                u4.write(outputStream);
                final byte[] bytes = outputStream.toByteArray();
                assertThat(bytes).contains(CLASSFILE_INT_VALUE);
            }
        }
    }

    @Nested
    @DisplayName("测试方法：of")
    class TestOf {
        @Test
        @DisplayName("当提供参数为字节时，返回 U4 实例")
        void givenByteNumberThenReturnU4() {
            U4 byteU4 = U4.of((byte) CLASSFILE_INT_VALUE);
            assertThat(byteU4).isEqualTo(u4);
        }

        @Test
        @DisplayName("当提供参数为 short 时，返回 U4 实例")
        void givenShortNumberThenReturnU4() {
            U4 shortU4 = U4.of((short) CLASSFILE_INT_VALUE);
            assertThat(shortU4).isEqualTo(u4);
        }

        @Test
        @DisplayName("当提供参数为 int 时，返回 U4 实例")
        void givenIntNumberThenReturnU4() {
            U4 maxU4 = U4.of(Integer.MAX_VALUE);
            assertThat(maxU4.intValue()).isEqualTo(Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("当提供参数为 long 时，返回 U4 实例")
        void givenLongNumberThenReturnU4() {
            U4 longU4 = U4.of((long) CLASSFILE_INT_VALUE);
            assertThat(longU4).isEqualTo(u4);
        }
    }
}
