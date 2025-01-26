/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.websocket;

import static modelengine.fitframework.inspection.Validation.notNull;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.server.WebSocketSessionMapper;
import modelengine.fit.http.websocket.support.AbstractSession;

/**
 * 表示 {@link Session} 的 Netty 的实现。
 *
 * @author 季聿阶
 * @since 2023-12-07
 */
public class NettyWebSocketSession extends AbstractSession {
    private final ChannelHandlerContext context;
    private final HttpClassicServerRequest request;

    public NettyWebSocketSession(ChannelHandlerContext context, HttpClassicServerRequest request) {
        this.context = notNull(context, "The netty channel context cannot be null.");
        this.request = notNull(request, "The http classic request cannot be null.");
        this.request.attributes().set(WebSocketSessionMapper.KEY, this);
    }

    @Override
    public HttpClassicServerRequest getHandshakeMessage() {
        return this.request;
    }

    @Override
    public void send(String text) {
        this.context.channel().writeAndFlush(new TextWebSocketFrame(text));
    }

    @Override
    public void send(byte[] bytes) {
        this.context.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes)));
    }

    @Override
    protected void close0(int code, String reason) {
        this.context.channel().writeAndFlush(new CloseWebSocketFrame(code, reason));
    }
}
