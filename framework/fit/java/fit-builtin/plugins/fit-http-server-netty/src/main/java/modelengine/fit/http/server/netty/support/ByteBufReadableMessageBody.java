/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.support;

import io.netty.buffer.ByteBuf;
import modelengine.fit.http.server.netty.NettyReadableMessageBody;
import modelengine.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link modelengine.fit.http.protocol.ReadableMessageBody} 的 {@link ByteBuf} 的实现。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public class ByteBufReadableMessageBody extends NettyReadableMessageBody {
    private final Deque<ByteBuf> byteBuffers = new ConcurrentLinkedDeque<>();
    private final AtomicInteger available = new AtomicInteger();

    @Override
    public int read0() throws IOException {
        if (this.byteBuffers.isEmpty()) {
            return -1;
        }
        ByteBuf buf = this.byteBuffers.peekFirst();
        if (buf.isReadable()) {
            int byteValue = buf.readByte() & 0xFF;
            this.available.decrementAndGet();
            if (!buf.isReadable()) {
                this.byteBuffers.removeFirst().release();
            }
            return byteValue;
        } else {
            this.byteBuffers.removeFirst().release();
            return this.read();
        }
    }

    @Override
    public int read0(@Nonnull byte[] bytes, int off, int len) {
        if (this.byteBuffers.isEmpty()) {
            return 0;
        }
        int totalRead = 0;
        while (totalRead < len && !this.byteBuffers.isEmpty()) {
            ByteBuf buf = this.byteBuffers.peekFirst();
            int toRead = Math.min(len - totalRead, buf.readableBytes());
            buf.readBytes(bytes, off + totalRead, toRead);
            totalRead += toRead;
            this.available.addAndGet(-toRead);
            if (!buf.isReadable()) {
                this.byteBuffers.removeFirst().release();
            }
        }
        return totalRead;
    }

    @Override
    public int available() {
        return this.available.get();
    }

    @Override
    protected int write0(@Nonnull ByteBuf data, boolean isLast) {
        this.byteBuffers.addLast(data.copy());
        int dataSize = data.readableBytes();
        this.available.addAndGet(dataSize);
        return dataSize;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.getLock().lock();
        try {
            while (!this.byteBuffers.isEmpty()) {
                this.byteBuffers.removeFirst().release();
            }
            this.available.set(0);
        } finally {
            this.getLock().unlock();
        }
    }
}
