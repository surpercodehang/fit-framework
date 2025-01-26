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
 * {@link U2} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-09
 */
@DisplayName("测试 U2 类")
class U2Test {
    private static final int CLASSFILE_INT_VALUE = 10;

    private final U2 u2 = U2.of(CLASSFILE_INT_VALUE);

    @Nested
    @DisplayName("当提供一个 U2 实例")
    class GivenU2 {
        @Test
        @DisplayName("返回 8 位字节数据表现形式")
        void shouldReturnByteValue() {
            final byte value = u2.byteValue();
            assertThat(value).isEqualTo((byte) CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 16 位整数数据表现形式")
        void shouldReturnShortValue() {
            final short value = u2.shortValue();
            assertThat(value).isEqualTo((short) CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 32 位整数数据表现形式")
        void shouldReturnIntValue() {
            final int value = u2.intValue();
            assertThat(value).isEqualTo(CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回 64 位整数数据表现形式")
        void shouldReturnLongValue() {
            final long value = u2.longValue();
            assertThat(value).isEqualTo(CLASSFILE_INT_VALUE);
        }

        @Test
        @DisplayName("返回一个 16 进制字符串")
        void shouldReturnHexString() {
            final String hexString = u2.toHexString();
            assertThat(hexString).isEqualTo(String.format(Locale.ROOT, "%04x", CLASSFILE_INT_VALUE));
        }

        @Test
        @DisplayName("返回一个 toString 后的字符串")
        void shouldReturnString() {
            final String toString = u2.toString();
            assertThat(toString).isEqualTo(String.valueOf(CLASSFILE_INT_VALUE));
        }

        @Test
        @DisplayName("返回 hashcode")
        void shouldReturnHashCode() {
            final int code = u2.hashCode();
            final U2 otherU2 = U2.of(CLASSFILE_INT_VALUE);
            assertThat(code).isEqualTo(otherU2.hashCode());
        }

        @Nested
        @DisplayName("测试方法：equals(Object obj)")
        class TestEquals {
            @Test
            @DisplayName("调用 equals 和自己比较时，返回 true")
            @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
            void givenWithSelfThenReturnTrue() {
                final boolean equals = u2.equals(u2);
                assertThat(equals).isTrue();
            }

            @Test
            @DisplayName("调用 equals 和其他同类型实例比较时，返回 false")
            void givenSameTypeAndNotSameValueThenReturnFalse() {
                U2 maxU2 = U2.of(Byte.MAX_VALUE);
                final boolean equals = u2.equals(maxU2);
                assertThat(equals).isFalse();
            }

            @Test
            @DisplayName("调用 equals 和其他不同类型实例比较时，返回 false")
            @SuppressWarnings("EqualsBetweenInconvertibleTypes")
            void givenNotSameTypeThenReturnFalse() {
                String otherTypeInstance = "";
                final boolean equals = u2.equals(otherTypeInstance);
                assertThat(equals).isFalse();
            }
        }

        @Test
        @DisplayName("调用 compareTo 比较方法，返回比较后的结果")
        void callCompareMethodThenReturnCompareResult() {
            U2 maxU2 = U2.of(Byte.MAX_VALUE);
            final int compare = u2.compareTo(maxU2);
            assertThat(compare).isNegative();
        }

        @Test
        @DisplayName("调用 add 相加方法，返回加法后的结果")
        void callAddMethodThenReturnU2() {
            final U2 add = U2.ONE.add(U2.ONE);
            assertThat(add).isEqualTo(U2.TWO);
        }

        @Test
        @DisplayName("调用 and 相与方法，返回与后的结果")
        void callAndMethodThenReturnU2() {
            final U2 add = U2.ONE.and(U2.ONE);
            assertThat(add).isEqualTo(U2.ONE);
        }

        @Test
        @DisplayName("调用 read 读取方法，返回 U2 实例")
        void callReadMethodThenU2() throws IOException {
            byte[] bytes = {0, CLASSFILE_INT_VALUE};
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
                final U2 readU2 = U2.read(inputStream);
                assertThat(readU2).isEqualTo(u2);
            }
        }

        @Test
        @DisplayName("调用 write 写入方法，结果写入成功")
        void callWriteMethodThenSuccess() throws IOException {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                u2.write(outputStream);
                final byte[] bytes = outputStream.toByteArray();
                assertThat(bytes).contains(CLASSFILE_INT_VALUE);
            }
        }
    }

    @Nested
    @DisplayName("测试方法：of")
    class TestOf {
        @Test
        @DisplayName("当提供参数为字节时，返回 U2 实例")
        void givenByteNumberThenReturnU2() {
            U2 byteU2 = U2.of((byte) CLASSFILE_INT_VALUE);
            assertThat(byteU2).isEqualTo(u2);
        }

        @Test
        @DisplayName("当提供参数为 short 时，返回 U2 实例")
        void givenShortNumberThenReturnU2() {
            U2 shortU2 = U2.of((short) CLASSFILE_INT_VALUE);
            assertThat(shortU2).isEqualTo(u2);
        }

        @Test
        @DisplayName("当提供参数为 long 时，返回 U2 实例")
        void givenLongNumberThenReturnU2() {
            U2 longU2 = U2.of((long) CLASSFILE_INT_VALUE);
            assertThat(longU2).isEqualTo(u2);
        }
    }
}
