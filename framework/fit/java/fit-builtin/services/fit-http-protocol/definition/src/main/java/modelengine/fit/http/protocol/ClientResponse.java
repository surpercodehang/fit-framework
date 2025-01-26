/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.support.DefaultClientResponse;
import modelengine.fitframework.model.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * 表示客户端的 Http 响应。
 *
 * @author 季聿阶
 * @since 2022-11-25
 */
public interface ClientResponse extends Message<StatusLine, MessageHeaders, ReadableMessageBody> {
    /**
     * 从 Http 消息体中读取下一个字节。
     *
     * @return 表示读取到的下一个字节的 {@code int}。正常字节范围 {@code 0 - 255}，没有数据返回 {@code -1}。
     * @throws IOException 当发生 I/O 异常时。
     */
    int readBody() throws IOException;

    /**
     * 从 Http 消息体中读取最多 {@code bytes.length} 个字节，存放到 {@code bytes} 数组中。
     *
     * @param bytes 表示读取数据后存放的数组的 {@code byte[]}。
     * @return 表示读取到的数据的字节数的 {@code int}，如果没有可读的任何数据，返回 {@code -1}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     */
    default int readBody(byte[] bytes) throws IOException {
        return this.readBody(notNull(bytes, "The bytes to read cannot be null."), 0, bytes.length);
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
    int readBody(byte[] bytes, int off, int len) throws IOException;

    /**
     * 获取 Http 消息体数据的输入流。
     *
     * @return 表示 Http 消息体数据的输入流的 {@link InputStream}。
     */
    InputStream getBodyInputStream();

    /**
     * 根据 Http 响应的状态码、状态信息、消息头和消息内容，创建一个客户端的 Http 响应。
     * <p>注意：{@code responseStream} 的关闭时机，如果调用 {@link #close()} 方法，会将其关闭。</p>
     *
     * @param statusCode 表示 Http 响应的状态码的 {@code int}。
     * @param reasonPhrase 表示 Http 响应的状态信息的 {@link String}。
     * @param headers 表示 Http 响应的消息头的 {@link MultiValueMap}{@code <}{@link String}{@code , }{@link
     * String}{@code >}
     * @param responseStream 表示 Http 响应的消息内容的 {@link InputStream}。
     * @return 表示创建出来的客户端的 Http 响应的 {@link ClientResponse}。
     */
    static ClientResponse create(int statusCode, String reasonPhrase, MultiValueMap<String, String> headers,
            InputStream responseStream) {
        return new DefaultClientResponse(statusCode, reasonPhrase, headers, responseStream);
    }
}
