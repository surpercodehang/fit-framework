/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link EmptyInputStream} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-14
 */
@DisplayName("测试 EmptyInputStream 类")
class EmptyInputStreamTest {
    private final EmptyInputStream instance = EmptyInputStream.INSTANCE;

    @Nested
    @DisplayName("测试方法：read()")
    class TestRead {
        @Test
        @DisplayName("当使用无参数读取，应该返回 -1")
        void whenNoParamShouldReturnNegative() {
            assertThat(EmptyInputStreamTest.this.instance.read()).isEqualTo(-1);
        }

        @Test
        @DisplayName("当使用 1 个参数读取，应该返回 0")
        void when1ParamShouldReturnZero() {
            assertThat(EmptyInputStreamTest.this.instance.read(new byte[0])).isEqualTo(0);
        }

        @Test
        @DisplayName("当使用 3 个参数读取，应该返回 0")
        void when3ParamShouldReturnZero() {
            assertThat(EmptyInputStreamTest.this.instance.read(new byte[0], 0, 0)).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("测试方法：skip(long count)")
    void testSkip() {
        assertThat(this.instance.skip(1)).isEqualTo(0);
    }

    @Test
    @DisplayName("测试方法：available()")
    void testAvailable() {
        assertThat(this.instance.available()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试方法：close()")
    void testClose() {
        assertDoesNotThrow(this.instance::close);
    }

    @Test
    @DisplayName("测试方法：mark(int readLimit)")
    void testMark() {
        assertDoesNotThrow(() -> this.instance.mark(0));
    }

    @Test
    @DisplayName("测试方法：reset()")
    void testReset() {
        assertDoesNotThrow(this.instance::reset);
    }

    @Test
    @DisplayName("测试方法：markSupported()")
    void testMarkSupported() {
        assertThat(this.instance.markSupported()).isFalse();
    }

    @Test
    @DisplayName("测试方法：toString()")
    void testToString() {
        final String toString = this.instance.toString();
        final String expect = this.instance.getClass().getSimpleName();
        assertThat(toString).isEqualTo(expect);
    }
}
