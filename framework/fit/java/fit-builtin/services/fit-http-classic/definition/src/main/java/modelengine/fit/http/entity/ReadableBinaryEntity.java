/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.support.DefaultReadableBinaryEntity;

import java.io.IOException;
import java.io.InputStream;

/**
 * 表示只读的二进制的消息体数据。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public interface ReadableBinaryEntity extends Entity {
    /**
     * 从 Http 消息体中读取下一个字节。
     *
     * @return 表示读取到的下一个字节的 {@code int}。正常字节范围为 {@code 0 - 255}，没有数据则返回 {@code -1}。
     * @throws IOException 当发生 I/O 异常时。
     */
    int read() throws IOException;

    /**
     * 从 Http 消息体中读取最多 {@code bytes.length} 个字节，存放到 {@code bytes} 数组中。
     *
     * @param bytes 表示读取数据后存放的数组的 {@code byte[]}。
     * @return 表示读取到的数据的字节数的 {@code int}，如果没有可读的任何数据，返回 {@code -1}。
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

    /**
     * 获取 Http 消息体的输入流。
     *
     * @return 表示 Http 消息体的输入流的 {@link InputStream}。
     */
    InputStream getInputStream();

    /**
     * 创建可读的二进制消息体数据。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param in 表示消息体内容的输入流的 {@link InputStream}。
     * @return 表示创建出来的可读的二进制消息体数据的 {@link ReadableBinaryEntity}。
     */
    static ReadableBinaryEntity create(HttpMessage httpMessage, InputStream in) {
        return new DefaultReadableBinaryEntity(httpMessage, in);
    }
}
