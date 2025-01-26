/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty;

import static modelengine.fitframework.inspection.Validation.notNull;

import io.netty.buffer.ByteBuf;
import modelengine.fit.http.protocol.support.AbstractReadableMessageBody;
import modelengine.fit.http.server.netty.support.CompositeByteBufReadableMessageBody;
import modelengine.fit.http.server.netty.support.FileChannelReadableMessageBody;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.ThreadUtils;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 表示 Netty 服务器的请求消息体。
 *
 * @author 季聿阶
 * @since 2023-09-30
 */
public abstract class NettyReadableMessageBody extends AbstractReadableMessageBody
        implements NettyReadableMessageBodyBuffer {
    private volatile boolean closed = false;
    private volatile boolean writingFinished = false;
    private final Lock lock = LockUtils.newReentrantLock();
    private final Condition condition = this.lock.newCondition();

    @Override
    public int read() throws IOException {
        this.checkIfClosed();
        while (true) {
            this.lock.lock();
            int read = this.tryRead();
            if (read != Integer.MIN_VALUE) {
                return read;
            }
            ThreadUtils.sleep(0);
        }
    }

    /**
     * 尝试读取下一个字节。
     *
     * @return 当返回 {@code -1 - 255} 表示正常读取范围，当返回 {@link Integer#MIN_VALUE} 表示未读取到。
     * @throws IOException 当发生 I/O 异常时。
     */
    private int tryRead() throws IOException {
        try {
            int read = this.read0();
            if (read != -1) {
                return read;
            }
            if (this.writingFinished) {
                return -1;
            }
            try {
                this.condition.await();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        } finally {
            this.lock.unlock();
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 从 Http 消息体中读取下一个字节。
     *
     * @return 表示读取到的下一个字节的 {@code int}。正常的范围是 {@code 0 - 255}，但没有数据时，返回 {@code -1}。
     * @throws IOException 当发生 I/O 异常时。
     */
    protected abstract int read0() throws IOException;

    @Override
    public int read(@Nonnull byte[] bytes, int off, int len) throws IOException {
        this.checkIfClosed();
        this.validate(bytes, off, len);
        if (len == 0) {
            return 0;
        }
        int read;
        this.lock.lock();
        try {
            read = this.read0(bytes, off, len);
            if (read == 0) {
                read = this.writingFinished ? -1 : 0;
            }
            while (read == 0) {
                try {
                    this.condition.await();
                    read = this.read0(bytes, off, len);
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            }
        } finally {
            this.lock.unlock();
        }
        return read;
    }

    /**
     * 从 Http 消息体中读取最多 {@code len} 个字节，存放到 {@code bytes} 数组中。
     *
     * @param bytes 表示读取数据后存放的数组的 {@code byte[]}。
     * @param off 表示存放数据的偏移量的 {@code int}。
     * @param len 表示读取数据的最大数量的 {@code int}。
     * @return 表示读取到的数据的字节数的 {@code int}，如果没有可读的任何数据，返回 {@code 0}。
     * @throws IOException 当发生 I/O 异常时。
     */
    protected abstract int read0(@Nonnull byte[] bytes, int off, int len) throws IOException;

    @Override
    public void write(ByteBuf data, boolean isLast) throws IOException {
        this.checkIfClosed();
        notNull(data, "The byte buffer to write cannot be null.");
        this.lock.lock();
        try {
            int write = this.write0(data, isLast);
            if (isLast) {
                this.writingFinished = true;
                this.condition.signal();
            } else if (write > 0) {
                this.condition.signal();
            }
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * 向当前可读消息体中写入数据，供后续读取。
     *
     * @param data 表示待写入数据的 {@link ByteBuf}。
     * @param isLast 表示待写入数据是否为最后一块数据的标志的 {@code boolean}。
     * @return 表示写入的数据量的 {@code int}。
     * @throws IOException 当发生 I/O 异常时。
     */
    protected abstract int write0(@Nonnull ByteBuf data, boolean isLast) throws IOException;

    private void checkIfClosed() throws IOException {
        if (this.closed) {
            throw new IOException("The netty readable message body has already been closed.");
        }
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
    }

    /**
     * 获取并发控制的锁。
     *
     * @return 表示并发控制的锁的 {@link Lock}。
     */
    protected Lock getLock() {
        return this.lock;
    }

    /**
     * 创建一个通用的 {@link NettyReadableMessageBody}。
     *
     * @return 表示创建出来的通用的 {@link NettyReadableMessageBody}。
     */
    public static NettyReadableMessageBody common() {
        return new CompositeByteBufReadableMessageBody();
    }

    /**
     * 创建一个存储大文件的 {@link NettyReadableMessageBody}。
     *
     * @return 表示创建出来的存储大文件的 {@link NettyReadableMessageBody}。
     */
    public static NettyReadableMessageBody large() {
        return new FileChannelReadableMessageBody();
    }
}
