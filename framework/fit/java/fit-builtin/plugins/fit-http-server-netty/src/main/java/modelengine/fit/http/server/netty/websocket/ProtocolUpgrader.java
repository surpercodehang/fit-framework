/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.websocket;

import static modelengine.fit.http.HttpClassicRequestAttribute.PATH_PATTERN;
import static modelengine.fit.http.protocol.MessageHeaderValues.WEBSOCKET;
import static modelengine.fitframework.inspection.Validation.notNull;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fit.http.protocol.MessageHeaderValues;
import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.netty.NettyHttpServerRequest;
import modelengine.fit.http.server.support.DefaultHttpClassicServerRequest;
import modelengine.fit.http.websocket.server.WebSocketHandler;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示 Http 协议的升级处理器。
 *
 * @author 季聿阶
 * @since 2023-12-07
 */
@ChannelHandler.Sharable
public class ProtocolUpgrader extends ChannelInboundHandlerAdapter {
    private static final int MAX_CONTENT_LENGTH = 8192;

    private final HttpClassicServer server;
    private final boolean secure;
    private final long largeBodySize;
    private final boolean isGracefulExit;

    public ProtocolUpgrader(HttpClassicServer server, boolean secure, long largeBodySize, boolean isGracefulExit) {
        this.server = notNull(server, "The http server cannot be null.");
        this.secure = secure;
        this.largeBodySize = largeBodySize;
        this.isGracefulExit = isGracefulExit;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            if (isWebSocketUpgrade(req)) {
                String path = req.uri();
                NettyHttpServerRequest serverRequest =
                        new NettyHttpServerRequest(req, ctx, this.secure, this.largeBodySize);
                HttpClassicServerRequest classicRequest =
                        new DefaultHttpClassicServerRequest(this.server, serverRequest);
                WebSocketHandler handler = this.server.webSocketDispatcher().dispatch(classicRequest);
                classicRequest.attributes().set(PATH_PATTERN.key(), handler.pathPattern());
                // 先移除 HttpClassicRequestAssembler，确保 Http 请求的处理器被去除
                ctx.pipeline().removeLast();
                // 再动态添加 WebSocket 的处理器
                ctx.pipeline().addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                ctx.pipeline().addLast(new WebSocketServerProtocolHandler(path));
                ctx.pipeline().addLast(new NettyWebSocketHandler(ctx, handler, classicRequest, this.isGracefulExit));
                // 最后将当前处理器移除，因为一个通道只会发生一次 Http 协议升级
                ctx.pipeline().remove(this);
            }
        }
        super.channelRead(ctx, msg);
    }

    private static boolean isWebSocketUpgrade(HttpRequest req) {
        return req.method() == HttpMethod.GET && StringUtils.equalsIgnoreCase(req.headers()
                .get(MessageHeaderNames.CONNECTION), MessageHeaderValues.UPGRADE)
                && StringUtils.equalsIgnoreCase(req.headers().get(MessageHeaderNames.UPGRADE), WEBSOCKET);
    }
}
