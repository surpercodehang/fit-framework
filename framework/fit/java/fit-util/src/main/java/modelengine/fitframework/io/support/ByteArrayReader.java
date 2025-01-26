/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.support;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.io.ByteReader;

import java.io.IOException;

/**
 * 为 {@link ByteReader} 提供基于字节数组的实现。
 *
 * @author 梁济时
 * @since 2023-03-02
 */
public final class ByteArrayReader implements ByteReader {
    private final byte[] bytes;
    private final int offset;
    private final int length;
    private int position;

    private ByteArrayReader(byte[] bytes, int offset, int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;

        this.position = 0;
    }

    @Override
    public int read() throws IOException {
        if (this.position < this.length) {
            return Byte.toUnsignedInt(this.bytes[this.offset + this.position++]);
        } else {
            return -1;
        }
    }

    /**
     * 创建一个字节读取程序，用以读取指定字节数组中的数据。
     *
     * @param bytes 表示待读取的字节数组。
     * @return 表示用以读取字节数组中数据的读取程序的 {@link ByteArrayReader}。
     * @throws IllegalArgumentException {@code bytes} 为 {@code null}。
     */
    public static ByteArrayReader of(byte[] bytes) {
        return new ByteArrayReader(validateBytes(bytes), 0, bytes.length);
    }

    /**
     * 创建一个字节读取程序，用以读取指定字节数组中的数据。
     *
     * @param bytes 表示待读取的字节数组。
     * @param offset 表示待读取的数据在字节数组中的偏移量的 32 位整数。
     * @param length 表示待读取的数据的字节数的 32 位整数。
     * @return 表示用以读取字节数组中数据的读取程序的 {@link ByteArrayReader}。
     * @throws IllegalArgumentException {@code bytes} 为 {@code null}，或 {@code offset} 或 {@code length} 超出限制。
     */
    public static ByteArrayReader of(byte[] bytes, int offset, int length) {
        validateBytes(bytes);
        between(offset, 0, bytes.length,
                "The offset of bytes to read in array is out of bounds. [offset={0}, bytes.length={1}]",
                offset, bytes.length);
        between(length, 0, bytes.length - offset,
                "The length of bytes to read in array is out of bounds. [length={0}, offset={1}, bytes.length={2}]",
                length, offset, bytes.length);
        return new ByteArrayReader(bytes, offset, length);
    }

    private static byte[] validateBytes(byte[] bytes) {
        return notNull(bytes, "The bytes to read cannot be null.");
    }
}
