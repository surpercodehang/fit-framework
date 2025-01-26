/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.io.IOException;

/**
 * 表示只读的 Http 消息体。
 *
 * @author 季聿阶
 * @since 2022-07-11
 */
public interface ReadableMessageBody extends MessageBody {
    /**
     * 从 Http 消息体中读取下一个字节，如果没有任何可读的数据，返回 {@code -1}。
     *
     * @return 表示读取到的字节的 {@code int}。正常范围为 {@code 0 - 255}，没有数据则为 {@code -1}。
     * @throws IOException 当发生 I/O 异常时。
     */
    int read() throws IOException;

    /**
     * 从 Http 消息体中读取最多 {@code bytes.length} 个字节，存放到 {@code bytes} 数组中。
     *
     * @param bytes 表示读取数据后存放的数组的 {@code byte[]}。
     * @return 表示读取到的数据的字节数的 {@code int}，如果没有任何可读的数据，返回 {@code -1}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     */
    default int read(byte[] bytes) throws IOException {
        return this.read(notNull(bytes, "The bytes to read cannot be null."), 0, bytes.length);
    }

    /**
     * 从 Http 消息体中读取最多 {@code len} 个字节，存放到 {@code bytes} 数组中。
     *
     * @param bytes 表示读取数据后存放的数组的 {@code byte[]}。
     * @param off 表示存放数据的偏移量的 {@code int}。
     * @param len 表示读取数据的最大数量的 {@code int}。
     * @return 表示读取到的数据的字节数的 {@code int}，如果没有可读的任何数据，返回 {@code -1}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     * @throws IndexOutOfBoundsException 当 {@code off} 或 {@code len} 为负数时，或 {@code off + len}
     * 超过了 {@code bytes} 的长度时。
     */
    int read(byte[] bytes, int off, int len) throws IOException;
}
