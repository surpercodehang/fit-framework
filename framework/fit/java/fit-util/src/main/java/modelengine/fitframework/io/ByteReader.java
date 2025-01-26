/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io;

import modelengine.fitframework.io.support.BufferedInputStreamByteReader;
import modelengine.fitframework.io.support.ByteArrayReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为字节提供读取程序。
 *
 * @author 梁济时
 * @since 2023-03-02
 */
@FunctionalInterface
public interface ByteReader {
    /**
     * 读取下一个字节。
     *
     * @return 若存在剩余的字节，则为表示字节数据的 32 位整数（仅 8 位用以存储数据），否则为 {@code -1}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    int read() throws IOException;

    /**
     * 创建一个字节读取程序，用以读取指定字节数组中的数据。
     *
     * @param bytes 表示待读取的字节数组。
     * @return 表示用以读取字节数组中数据的读取程序的 {@link ByteReader}。
     * @throws IllegalArgumentException {@code bytes} 为 {@code null}。
     */
    static ByteReader fromBytes(byte[] bytes) {
        return ByteArrayReader.of(bytes);
    }

    /**
     * 创建一个字节读取程序，用以读取指定字节数组中的数据。
     *
     * @param bytes 表示待读取的字节数组。
     * @param offset 表示待读取的数据在字节数组中的偏移量的 32 位整数。
     * @param length 表示待读取的数据的字节数的 32 位整数。
     * @return 表示用以读取字节数组中数据的读取程序的 {@link ByteReader}。
     * @throws IllegalArgumentException {@code bytes} 为 {@code null}，或 {@code offset} 或 {@code length} 超出限制。
     */
    static ByteReader fromBytes(byte[] bytes, int offset, int length) {
        return ByteArrayReader.of(bytes, offset, length);
    }

    /**
     * 创建一个字节读取程序，用以读取指定输入流中的数据。
     *
     * @param in 表示待读取的输入流的 {@link InputStream}。
     * @return 表示用以读取输入流中数据的读取程序的 {@link ByteReader}。
     * @throws IllegalArgumentException {@code in} 为 {@code null}。
     */
    static ByteReader fromInputStream(InputStream in) {
        return BufferedInputStreamByteReader.of(in);
    }

    /**
     * 创建一个字节读取程序，用以读取指定输入流中的数据。
     *
     * @param in 表示待读取的输入流的 {@link InputStream}。
     * @param bufferSize 表示缓冲区可容纳的字节数的 32 位整数。
     * @return 表示用以读取输入流中数据的读取程序的 {@link ByteReader}。
     * @throws IllegalArgumentException {@code in} 为 {@code null} 或 {@code bufferSize} 为负数。
     */
    static ByteReader fromInputStream(InputStream in, int bufferSize) {
        return BufferedInputStreamByteReader.of(in, bufferSize);
    }
}
