/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.support;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import modelengine.fit.http.server.netty.NettyReadableMessageBody;
import modelengine.fitframework.inspection.Nonnull;

import java.io.IOException;

/**
 * {@link modelengine.fit.http.protocol.ReadableMessageBody} 的 {@link CompositeByteBuf} 的实现。
 *
 * @author 王成
 * @since 2024-02-17
 */
public class CompositeByteBufReadableMessageBody extends NettyReadableMessageBody {
    private final CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();

    @Override
    public int read0() throws IOException {
        if (this.compositeByteBuf.readableBytes() == 0) {
            return -1;
        }
        return this.compositeByteBuf.readByte() & 0xFF;
    }

    @Override
    public int read0(@Nonnull byte[] bytes, int off, int len) {
        if (this.compositeByteBuf.readableBytes() == 0 || len == 0) {
            return 0;
        }
        int toRead = Math.min(len, compositeByteBuf.readableBytes());
        this.compositeByteBuf.readBytes(bytes, off, toRead);
        return toRead;
    }

    @Override
    public int available() {
        if (this.compositeByteBuf.refCnt() > 0) {
            return this.compositeByteBuf.readableBytes();
        }
        return 0;
    }

    @Override
    protected int write0(@Nonnull ByteBuf data, boolean isLast) {
        this.compositeByteBuf.addComponent(true, data.retain());
        return data.readableBytes();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (this.compositeByteBuf.refCnt() > 0) {
            this.compositeByteBuf.release();
        }
    }
}