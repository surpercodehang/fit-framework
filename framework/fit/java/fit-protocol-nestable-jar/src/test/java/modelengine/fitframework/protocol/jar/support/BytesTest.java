/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("测试 Bytes 工具类")
final class BytesTest {
    @Test
    @DisplayName("从字节序中读取正确的 16 位无符号数")
    void should_return_u2_from_bytes() {
        byte[] bytes = new byte[] {0x00, (byte) 0xf0, (byte) 0xf1, 0x00};
        int value = Bytes.u2(bytes, 1);
        assertEquals(0xf1f0, value);
    }

    @Test
    @DisplayName("从字节序中读取正确的 32 位有符号数")
    void should_return_s4_from_bytes() {
        byte[] bytes = new byte[] {0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x00};
        int value = Bytes.s4(bytes, 1);
        assertEquals(-1, value);
    }

    @Test
    @DisplayName("从字节序中读取正确的 32 位无符号数")
    void should_return_u4_from_bytes() {
        byte[] bytes = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x00};
        long value = Bytes.u4(bytes, 1);
        assertEquals(0x04030201L, value);
    }

    @Test
    @DisplayName("从字节序中读取正确的 64 位有符号数")
    void should_return_s8_from_bytes() {
        byte[] bytes = new byte[] {0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x00};
        long value = Bytes.s8(bytes, 1);
        assertEquals(-1L, value);
    }

    @Test
    @DisplayName("返回字节序的 16 进制字符串表现形式")
    void should_return_hex_string_of_bytes() {
        byte[] bytes = new byte[] {0x0a, 0x1b, 0x2c, 0x3d, 0x4e, 0x5f};
        String hex = Bytes.hex(bytes);
        assertEquals("0a1b2c3d4e5f", hex);
    }
}
