/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.okhttp.websocket;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.HttpResource;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.client.WebSocketClassicListener;
import modelengine.fit.http.websocket.client.support.EmptyWebSocketListener;
import modelengine.fit.http.websocket.support.AbstractSession;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * 表示 {@link Session} 的 OkHttp 的实现。
 *
 * @author 季聿阶
 * @since 2024-04-29
 */
public class OkHttpWebSocketSession extends AbstractSession {
    private HttpClassicClientResponse<Object> response;
    private final WebSocket webSocket;

    /**
     * 通过 Http 的资源、访问 WebSocket 的地址和 WebSocket 的监听器来初始化 {@link OkHttpWebSocketSession} 的新实例。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param url 表示访问 WebSocket 的地址的 {@link String}。
     * @param listener 表示 WebSocket 的监听器的 {@link WebSocketClassicListener}。
     */
    public OkHttpWebSocketSession(HttpResource httpResource, String url, WebSocketClassicListener listener) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        WebSocketListener actualListener =
                new WebSocketListenerAdapter(notNull(httpResource, "The http resource cannot be null."),
                        getIfNull(listener, EmptyWebSocketListener::new),
                        this);
        this.webSocket = okHttpClient.newWebSocket(request, actualListener);
    }

    /**
     * 设置 Http 的响应。
     * <p>该 Http 响应为升级 Http 协议到 WebSocket 协议的响应报文。</p>
     *
     * @param response 表示待设置的 Http 的响应的 {@link HttpClassicClientResponse}{@code <}{@link Object}{@code >}。
     */
    public void setResponse(HttpClassicClientResponse<Object> response) {
        this.response = response;
    }

    @Override
    public HttpClassicClientResponse<Object> getHandshakeMessage() {
        return this.response;
    }

    @Override
    public void send(String text) {
        this.webSocket.send(text);
    }

    @Override
    public void send(byte[] bytes) {
        this.webSocket.send(ByteString.of(bytes));
    }

    @Override
    protected void close0(int code, String reason) {
        this.webSocket.close(code, reason);
    }
}
