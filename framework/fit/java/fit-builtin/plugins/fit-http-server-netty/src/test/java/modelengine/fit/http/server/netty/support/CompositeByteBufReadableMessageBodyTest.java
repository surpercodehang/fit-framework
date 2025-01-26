/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.support;

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 表示 {@link CompositeByteBufReadableMessageBody} 的单元测试。
 *
 * @author 王成
 * @since 2024-02-23
 */
@DisplayName("测试 HttpClassicRequestAssembler 类")
class CompositeByteBufReadableMessageBodyTest {
    @Test
    @DisplayName("测试读取空 CompositeByteBuf 的行为")
    void testReadOnEmptyBody() throws IOException {
        byte[] actual = new byte[20];
        CompositeByteBufReadableMessageBody body = new CompositeByteBufReadableMessageBody();
        assertThat(body.read0()).isEqualTo(-1);
        assertThat(body.read0(actual, 0, 20)).isEqualTo(0);
    }

    @Test
    @DisplayName("测试 CompositeByteBuf 所管理的 ByteBuf 的引用计数")
    void testReadRefCount() throws IOException {
        byte[] bytes = new byte[] {44, 32, 67, 104, 101, 110, 103};
        ByteBuf actual = Unpooled.wrappedBuffer(bytes);
        CompositeByteBufReadableMessageBody body = new CompositeByteBufReadableMessageBody();

        assertThat(actual.refCnt()).isEqualTo(1);
        body.write(actual, true);
        assertThat(actual.refCnt()).isEqualTo(2);
        body.close();
        assertThat(actual.refCnt()).isEqualTo(1);
    }

    @Test
    @DisplayName("测试从 CompositeByteBuf 批量读取数据")
    void testReadBytes() throws IOException {
        byte[] bytes = new byte[] {104};
        byte[] actual = new byte[20];
        ByteBuf actualBuf = Unpooled.wrappedBuffer(bytes);
        CompositeByteBufReadableMessageBody body = new CompositeByteBufReadableMessageBody();
        body.write(actualBuf, true);
        assertThat(body.read0(actual, 0, 1)).isEqualTo(1);
        assertThat(actual[0]).isEqualTo((byte) 104);
        assertThat(body.read0(actual, 0, 1)).isEqualTo(0);
    }

    @Test
    @DisplayName("测试从 CompositeByteBuf 读取一个字节的数据")
    void testReadOneByte() throws IOException {
        byte[] bytes = new byte[] {104};
        ByteBuf actual = Unpooled.wrappedBuffer(bytes);
        CompositeByteBufReadableMessageBody body = new CompositeByteBufReadableMessageBody();
        assertThat(body.available()).isEqualTo(0);
        body.write(actual, true);
        assertThat(body.available()).isEqualTo(1);
        assertThat(body.read0()).isEqualTo(104);
        assertThat(body.read0()).isEqualTo(-1);
        assertThat(body.available()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试对 CompositeByteBuf 进行复杂写入/读取操作")
    void testRead0() throws IOException {
        // "hello world"的Byte表示
        byte[] bytes1 = new byte[] {104};
        byte[] bytes2 = new byte[] {101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
        // ", Cheng"的Byte表示
        byte[] bytes3 = new byte[] {44, 32, 67, 104, 101, 110, 103};
        byte[] actual = new byte[18];

        ByteBuf buf1 = Unpooled.wrappedBuffer(bytes1);
        ByteBuf buf2 = Unpooled.wrappedBuffer(bytes2);
        ByteBuf buf3 = Unpooled.wrappedBuffer(bytes3);

        CompositeByteBufReadableMessageBody body = new CompositeByteBufReadableMessageBody();
        body.write(buf1, false);
        body.write(buf2, false);
        assertThat(body.available()).isEqualTo(11);
        assertThat(body.read0(actual, 0, 11)).isEqualTo(11);
        body.write(buf3, true);
        assertThat(body.available()).isEqualTo(7);
        assertThat(body.read0(actual, 11, 7)).isEqualTo(7);
        assertThat(new String(actual, StandardCharsets.UTF_8)).isEqualTo("hello world, Cheng");
    }
}