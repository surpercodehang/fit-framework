/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.websocket.server.WebSocketSessionMapper;

/**
 * 表示 WebSocket 的会话。
 * <p>该会话信息应该保存在握手请求的属性中，属性名为 {@link WebSocketSessionMapper#KEY}。</p>
 *
 * @author 季聿阶
 * @since 2023-12-07
 */
public interface Session {
    /**
     * 获取会话的唯一标识。
     *
     * @return 标识会话的唯一标识的 {@link String}。
     */
    String getId();

    /**
     * 获取握手消息的请求。
     * <p>在客户端时，该请求类型为 {@link HttpClassicClientResponse}。</p>
     * <p>在服务端时，该请求类型为 {@link HttpClassicServerRequest}。</p>
     *
     * @return 表示握手消息的请求的 {@link HttpMessage}。
     */
    HttpMessage getHandshakeMessage();

    /**
     * 向会话中以同步的方式发送文本内容。
     *
     * @param text 表示发送的文本内容的 {@link String}。
     */
    void send(String text);

    /**
     * 向会话中以同步的方式发送二进制内容。
     *
     * @param bytes 表示发送的二进制内容的 {@code byte[]}。
     */
    void send(byte[] bytes);

    /**
     * 正常关闭当前会话。
     */
    default void close() {
        this.close(CloseReason.NORMAL_CLOSURE);
    }

    /**
     * 关闭当前会话。
     *
     * @param code 表示关闭的状态码的 {@code int}。
     * @param reason 表示关闭的原因的 {@link String}。
     */
    void close(int code, String reason);

    /**
     * 关闭当前会话。
     *
     * @param closeReason 表示关闭的原因的 {@link CloseReason}。
     */
    void close(CloseReason closeReason);

    /**
     * 获取关闭会话的状态码。
     *
     * @return 表示关闭会话的状态码的 {@code int}。
     */
    int getCloseCode();

    /**
     * 获取关闭会话的原因。
     *
     * @return 表示关闭会话的原因的 {@link String}。
     */
    String getCloseReason();
}
