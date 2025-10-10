/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.http.server.netty;

import static modelengine.fitframework.inspection.Validation.notNull;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpVersion;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.QueryCollection;
import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fit.http.protocol.RequestLine;
import modelengine.fit.http.protocol.ServerRequest;
import modelengine.fit.http.protocol.util.HeaderUtils;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * {@link ServerRequest} 的 Netty 实现。
 *
 * @author 季聿阶
 * @since 2022-07-08
 */
public class NettyHttpServerRequest implements ServerRequest, OnHttpContentReceived {
    private static final Logger log = Logger.get(NettyHttpServerRequest.class);

    private static final char QUERY_SEPARATOR = '?';

    private final HttpRequest request;
    private final ChannelHandlerContext ctx;
    private final boolean isSecure;
    private final long largeBodySize;
    private final RequestLine startLine;
    private final MessageHeaders headers;
    private final NettyReadableMessageBody body;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final ReadWriteLock isClosedLock = LockUtils.newReentrantReadWriteLock();
    private final AtomicBoolean isComplete = new AtomicBoolean(false);
    private final AtomicBoolean isFinished = new AtomicBoolean(false);
    private final Lock tryCloseLock = LockUtils.newReentrantLock();
    private volatile Thread executeThread;

    public NettyHttpServerRequest(HttpRequest request, ChannelHandlerContext ctx, boolean isSecure,
            long largeBodySize) {
        this.request = notNull(request, "The netty http request cannot be null.");
        this.ctx = notNull(ctx, "The channel handler context cannot be null.");
        this.isSecure = isSecure;
        this.largeBodySize = largeBodySize;
        this.startLine = this.initStartLine();
        this.headers = this.initHeaders();
        this.body = this.isLargeBody() ? NettyReadableMessageBody.large() : NettyReadableMessageBody.common();
        log.debug("Netty http request initialized. [id={0}, request={1}]", ctx.name(), this.startLine());
    }

    private boolean isLargeBody() {
        if (HeaderUtils.isChunked(this.headers)) {
            return true;
        }
        return HeaderUtils.contentLengthLong(this.headers) > this.largeBodySize;
    }

    private RequestLine initStartLine() {
        HttpRequestMethod method = notNull(HttpRequestMethod.from(this.request.method().name()),
                "The http request method is unsupported. [method={0}]",
                this.request.method().name());
        HttpVersion httpVersion = notNull(HttpVersion.from(this.request.protocolVersion().toString()),
                "The http version is unsupported. [version={0}]",
                this.request.protocolVersion());
        int index = this.request.uri().indexOf(QUERY_SEPARATOR);
        if (index < 0) {
            return RequestLine.create(httpVersion, method, this.request.uri(), QueryCollection.create());
        } else {
            return RequestLine.create(httpVersion,
                    method,
                    this.request.uri().substring(0, index),
                    QueryCollection.create(this.request.uri().substring(index + 1)));
        }
    }

    private MessageHeaders initHeaders() {
        ConfigurableMessageHeaders configurableHeaders = ConfigurableMessageHeaders.create();
        for (String name : this.request.headers().names()) {
            List<String> values = this.request.headers().getAll(name);
            configurableHeaders.set(name, values);
        }
        return configurableHeaders;
    }

    @Override
    public RequestLine startLine() {
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
    public void receiveHttpContent(HttpContent content) throws IOException {
        this.checkIfClosed();
        ByteBuf byteBuf = content.content();
        this.body.write(byteBuf, false);
    }

    @Override
    public void receiveLastHttpContent(LastHttpContent content) throws IOException {
        this.checkIfClosed();
        ByteBuf byteBuf = content.content();
        this.body.write(byteBuf, true);
        LockUtils.synchronize(this.tryCloseLock, () -> this.isComplete.set(true));
    }

    @Override
    public int readBody() throws IOException {
        this.checkIfClosed();
        return this.body.read();
    }

    @Override
    public int readBody(byte[] bytes, int off, int len) throws IOException {
        this.checkIfClosed();
        return this.body.read(bytes, off, len);
    }

    @Override
    public InputStream getBodyInputStream() {
        return this.body;
    }

    @Override
    public boolean isActive() {
        return this.ctx.channel().isActive();
    }

    private void checkIfClosed() throws IOException {
        this.isClosedLock.readLock().lock();
        try {
            if (this.isClosed.get()) {
                throw new IOException("The netty http server request has already been closed.");
            }
        } finally {
            this.isClosedLock.readLock().unlock();
        }
    }

    /**
     * 尝试关闭。
     *
     * @throws IOException 当关闭失败时。
     */
    void tryClose() throws IOException {
        this.tryCloseLock.lock();
        try {
            if (this.isFinished.get() && this.isComplete.get()) {
                this.close();
            }
        } finally {
            this.tryCloseLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.isClosed.get()) {
            return;
        }
        this.isClosedLock.writeLock().lock();
        try {
            if (this.isClosed.get()) {
                return;
            }
            log.debug("Netty http request closed. [id={}]", this.ctx.name());
            this.isClosed.set(true);
            this.body.close();
        } finally {
            this.isClosedLock.writeLock().unlock();
        }
    }

    @Override
    public Address localAddress() {
        SocketAddress socketAddress = this.ctx.channel().localAddress();
        if (!(socketAddress instanceof InetSocketAddress)) {
            return null;
        }
        InetSocketAddress localAddress = ObjectUtils.cast(socketAddress);
        return Address.builder()
                .socketAddress(localAddress)
                .hostAddress(this.getHostAddress(localAddress).orElse(null))
                .port(localAddress.getPort())
                .build();
    }

    @Override
    public Address remoteAddress() {
        SocketAddress socketAddress = this.ctx.channel().remoteAddress();
        if (!(socketAddress instanceof InetSocketAddress)) {
            return null;
        }
        InetSocketAddress remoteAddress = ObjectUtils.cast(socketAddress);
        return Address.builder()
                .socketAddress(remoteAddress)
                .hostAddress(this.getHostAddress(remoteAddress).orElse(null))
                .port(remoteAddress.getPort())
                .build();
    }

    @Override
    public boolean isSecure() {
        return this.isSecure;
    }

    private Optional<String> getHostAddress(InetSocketAddress address) {
        if (address.getAddress() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(address.getAddress().getHostAddress());
    }

    /**
     * 获取 Netty 原始的 Http 请求消息。
     *
     * @return 表示 Netty 原始的 Http 请求消息的 {@link HttpRequest}。
     */
    HttpRequest getNettyRequest() {
        return this.request;
    }

    /**
     * 设置当前请求的执行线程。
     *
     * @param thread 表示当前请求的执行线程的 {@link Thread}。
     * @throws IllegalArgumentException 当 {@code thread} 为 {@code null} 时。
     */
    void setExecuteThread(Thread thread) {
        this.executeThread = notNull(thread, "The execute thread cannot be null.");
    }

    /**
     * 清除当前请求的执行线程。
     */
    void removeExecuteThread() {
        this.executeThread = null;
        LockUtils.synchronize(this.tryCloseLock, () -> this.isFinished.set(true));
    }

    /**
     * 中断当前请求。
     */
    void interruptExecution() {
        if (this.executeThread != null) {
            this.executeThread.interrupt();
        }
    }
}
