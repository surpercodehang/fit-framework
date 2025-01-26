/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import modelengine.fit.http.protocol.ClientResponse;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.ConfigurableStatusLine;
import modelengine.fit.http.protocol.HttpVersion;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fit.http.protocol.StatusLine;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.util.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link ClientResponse} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-25
 */
public class DefaultClientResponse implements ClientResponse {
    private final StatusLine startLine;
    private final ConfigurableMessageHeaders headers;
    private final ReadableMessageBody body;
    private final InputStream inputStream;
    private final boolean shouldCloseStream;
    private volatile boolean isClosed;

    /**
     * 使用 Http 响应状态码、响应状态信息、响应消息头和响应消息体的输入流 {@link DefaultClientResponse} 的新实例。
     *
     * @param statusCode 表示 Http 响应状态码的 {@code int}。
     * @param reasonPhrase 表示 Http 响应状态信息的 {@link String}。
     * @param headers 表示 Http 响应消息头的 {@link MultiValueMap}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @param responseStream 表示 Http 响应消息体的输入流的 {@link InputStream}。
     */
    public DefaultClientResponse(int statusCode, String reasonPhrase, MultiValueMap<String, String> headers,
            InputStream responseStream) {
        this(statusCode, reasonPhrase, headers, responseStream, true);
    }

    /**
     * 使用 Http 响应状态码、响应状态信息、响应消息头、响应消息体的输入流和是否在资源释放时关闭输入流的标志来初始化
     * {@link DefaultClientResponse} 的新实例。
     *
     * @param statusCode 表示 Http 响应状态码的 {@code int}。
     * @param reasonPhrase 表示 Http 响应状态信息的 {@link String}。
     * @param headers 表示 Http 响应消息头的 {@link MultiValueMap}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @param responseStream 表示 Http 响应消息体的输入流的 {@link InputStream}。
     * @param shouldCloseResponseStream 表示资源释放时是否关闭 {@code responseStream} 的标志的 {@code boolean}。
     */
    public DefaultClientResponse(int statusCode, String reasonPhrase, MultiValueMap<String, String> headers,
            InputStream responseStream, boolean shouldCloseResponseStream) {
        this.startLine = ConfigurableStatusLine.create(HttpVersion.HTTP_1_1, statusCode, reasonPhrase);
        this.headers = ConfigurableMessageHeaders.create();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            this.headers.set(entry.getKey(), entry.getValue());
        }
        this.inputStream = ObjectUtils.getIfNull(responseStream, () -> new ByteArrayInputStream(new byte[0]));
        this.body = new ClientResponseBody(this);
        this.shouldCloseStream = shouldCloseResponseStream;
    }

    @Override
    public StatusLine startLine() {
        return this.startLine;
    }

    @Override
    public MessageHeaders headers() {
        return this.headers;
    }

    @Override
    public ReadableMessageBody body() {
        return this.body;
    }

    @Override
    public void close() throws IOException {
        this.isClosed = true;
        if (this.shouldCloseStream) {
            this.inputStream.close();
        }
        this.body.close();
    }

    @Override
    public int readBody() throws IOException {
        this.checkIfClosed();
        return this.inputStream.read();
    }

    @Override
    public int readBody(byte[] bytes, int off, int len) throws IOException {
        this.checkIfClosed();
        return this.inputStream.read(bytes, off, len);
    }

    @Override
    public InputStream getBodyInputStream() {
        return this.inputStream;
    }

    private void checkIfClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException("The client response has already been closed.");
        }
    }
}
