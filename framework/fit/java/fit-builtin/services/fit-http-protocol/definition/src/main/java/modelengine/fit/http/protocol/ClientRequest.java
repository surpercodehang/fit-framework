/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.io.IOException;

/**
 * 表示客户端的 Http 请求。
 *
 * @author 季聿阶
 * @since 2022-07-05
 */
public interface ClientRequest extends Message<RequestLine, ConfigurableMessageHeaders, WritableMessageBody> {
    /**
     * 向 Http 消息起始行和消息头中写入数据。
     *
     * @throws IOException 当发生 I/O 异常时。
     */
    void writeStartLineAndHeaders() throws IOException;

    /**
     * 向 Http 消息体中写入数据。
     *
     * @param b 表示待写入数据的字节的 {@code int}。正常范围是 {@code 0 - 255}。
     * @throws IOException 当发生 I/O 异常时。
     */
    void writeBody(int b) throws IOException;

    /**
     * 向 Http 消息体中写入数据。
     *
     * @param bytes 表示待写入数据的 {@code byte[]}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     * @see #writeBody(byte[], int, int)
     */
    default void writeBody(byte[] bytes) throws IOException {
        this.writeBody(notNull(bytes, "The bytes to write cannot be null."), 0, bytes.length);
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
    void writeBody(byte[] bytes, int off, int len) throws IOException;

    /**
     * 将所有写入的数据对外发送。
     *
     * @throws IOException 当发生 I/O 异常时。
     */
    void flush() throws IOException;

    /**
     * 读取 Http 响应。
     *
     * @return 表示 Http 响应的 {@link ClientResponse}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws modelengine.fitframework.exception.TimeoutException 当发生超时异常时。
     */
    ClientResponse readResponse() throws IOException;
}
