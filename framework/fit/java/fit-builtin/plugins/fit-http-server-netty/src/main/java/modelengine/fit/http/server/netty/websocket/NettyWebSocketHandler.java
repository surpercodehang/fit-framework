/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.websocket;

import static modelengine.fitframework.inspection.Validation.notNull;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.websocket.CloseReason;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.server.WebSocketHandler;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolExecutor;

import java.util.concurrent.TimeUnit;

/**
 * 表示 Netty 用于处理 WebSocket 消息的处理器。
 *
 * @author 季聿阶
 * @since 2023-12-07
 */
public class NettyWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Logger log = Logger.get(NettyWebSocketHandler.class);

    private final WebSocketHandler handler;
    private final Session session;
    private final ThreadPoolExecutor executor;

    public NettyWebSocketHandler(ChannelHandlerContext ctx, WebSocketHandler handler, HttpClassicServerRequest request,
            boolean isGracefulExit) {
        this.handler = notNull(handler, "The websocket handler cannot be null.");
        this.session = new NettyWebSocketSession(ctx, request);
        this.executor = ThreadPoolExecutor.custom()
                .threadPoolName("netty-websocket-handler-" + ctx.name())
                .awaitTermination(3, TimeUnit.SECONDS)
                .isImmediateShutdown(!isGracefulExit)
                .corePoolSize(1)
                .maximumPoolSize(1)
                .keepAliveTime(60, TimeUnit.SECONDS)
                .workQueueCapacity(Integer.MAX_VALUE)
                .isDaemonThread(!isGracefulExit)
                .exceptionHandler((thread, cause) -> this.exceptionCaught(ctx, cause))
                .rejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy())
                .build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            this.handler.onError(this.session, cause);
        } catch (Throwable e) {
            cause.addSuppressed(e);
            this.session.close(CloseReason.UNEXPECTED_CONDITION);
        }
        log.error("Failed to handle netty websocket.", cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame frame = (TextWebSocketFrame) msg;
            String text = frame.text();
            this.executor.execute(Task.builder()
                    .runnable(() -> this.handler.onMessage(this.session, text))
                    .buildDisposable());
            return;
        }
        if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = frame.content();
            byte[] binMsg = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(binMsg);
            this.executor.execute(Task.builder().runnable(() -> {
                this.handler.onMessage(this.session, binMsg);
            }).buildDisposable());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            this.executor.execute(Task.builder().runnable(() -> this.handler.onOpen(this.session)).buildDisposable());
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.executor.execute(Task.builder().runnable(() -> this.handler.onClose(this.session)).buildDisposable());
        super.channelInactive(ctx);
    }
}
