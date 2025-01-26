/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 表示只写的二进制的消息体数据。
 *
 * @author 季聿阶
 * @since 2023-01-28
 */
public interface WritableBinaryEntity extends Entity {
    /**
     * 向 Http 消息体中写入数据。
     *
     * @param bytes 表示待写入数据的 {@code byte[]}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     */
    default void write(byte[] bytes) throws IOException {
        this.write(notNull(bytes, "The bytes to write cannot be null."), 0, bytes.length);
    }

    /**
     * 向 Http 消息体中写入数据。
     *
     * @param bytes 表示待写入数据所在数组的 {@code byte[]}。
     * @param off 表示待写入数据的偏移量的 {@code int}。
     * @param len 表示待写入数据的数量的 {@code int}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     * @throws IndexOutOfBoundsException 当 {@code off} 或 {@code len} 为负数时，或 {@code off + len}
     * 超过了 {@code bytes} 的长度时。
     */
    void write(byte[] bytes, int off, int len) throws IOException;

    /**
     * 强制已经写入的数据执行写出，也就是说将之前写入到缓冲区的数据全部对外输出。
     *
     * @throws IOException 当发生 I/O 异常时。
     */
    void flush() throws IOException;

    /**
     * 获取 Http 消息体的输出流。
     *
     * @return 表示 Http 消息体的输出流的 {@link OutputStream}。
     */
    OutputStream getOutputStream();
}
