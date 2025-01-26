/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.support;

import static modelengine.fitframework.inspection.Validation.notNegative;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.io.ByteReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为 {@link ByteReader} 提供基于输入流的实现。
 *
 * @author 梁济时
 * @since 2023-03-02
 */
public class BufferedInputStreamByteReader implements ByteReader {
    private static final int DEFAULT_BUFFER_SIZE = 256;

    private final InputStream in;
    private final byte[] buffer;
    private int position;
    private int length;

    private BufferedInputStreamByteReader(InputStream in, int bufferSize) {
        this.in = notNull(in, "The input stream to read bytes cannot be null.");
        this.buffer = new byte[notNegative(bufferSize, "The size of buffer to read bytes cannot be negative.")];
        this.position = 0;
        this.length = 0;
    }

    @Override
    public int read() throws IOException {
        if (this.position == this.length) {
            this.fillNext();
        }
        if (this.length < 0) {
            return -1;
        }
        return Byte.toUnsignedInt(this.buffer[this.position++]);
    }

    private void fillNext() throws IOException {
        do {
            this.length = this.in.read(this.buffer, 0, this.buffer.length);
        } while (this.length == 0);
        this.position = 0;
    }

    /**
     * 创建一个字节读取程序，用以读取指定输入流中的数据。
     *
     * @param in 表示待读取的输入流的 {@link InputStream}。
     * @return 表示用以读取输入流中数据的读取程序的 {@link BufferedInputStreamByteReader}。
     * @throws IllegalArgumentException {@code in} 为 {@code null}。
     */
    public static BufferedInputStreamByteReader of(InputStream in) {
        return new BufferedInputStreamByteReader(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 创建一个字节读取程序，用以读取指定输入流中的数据。
     *
     * @param in 表示待读取的输入流的 {@link InputStream}。
     * @param bufferSize 表示缓冲区可容纳的字节数的 32 位整数。
     * @return 表示用以读取输入流中数据的读取程序的 {@link BufferedInputStreamByteReader}。
     * @throws IllegalArgumentException {@code in} 为 {@code null} 或 {@code bufferSize} 为负数。
     */
    public static BufferedInputStreamByteReader of(InputStream in, int bufferSize) {
        return new BufferedInputStreamByteReader(in, bufferSize);
    }
}
