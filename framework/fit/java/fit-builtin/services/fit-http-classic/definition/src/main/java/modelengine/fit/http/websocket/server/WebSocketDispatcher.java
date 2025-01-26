/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.server;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.websocket.server.support.DefaultWebSocketDispatcher;

/**
 * 表示 WebSocket 消息的分发器。
 *
 * @author 季聿阶
 * @since 2023-12-07
 */
public interface WebSocketDispatcher {
    /**
     * 将 WebSocket 的握手消息进行分发，转到对应的处理器上。
     *
     * @param request 表示 WebSocket 的握手消息的 {@link HttpClassicServerRequest}。
     * @return 表示分发到的 WebSocket 的处理器的 {@link WebSocketHandler}。
     */
    WebSocketHandler dispatch(HttpClassicServerRequest request);

    /**
     * 注册一个 WebSocket 的处理器。
     *
     * @param handler 表示待注册的 WebSocket 的处理器的 {@link WebSocketHandler}。
     */
    void register(WebSocketHandler handler);

    /**
     * 取消注册一个 WebSocket 的处理器。
     *
     * @param handler 表示待取消注册的 WebSocket 的处理器的 {@link WebSocketHandler}。
     */
    void unregister(WebSocketHandler handler);

    /**
     * 创建一个新的 WebSocket 消息的分发器。
     *
     * @return 表示创建出来的新的 WebSocket 消息的分发器的 {@link WebSocketDispatcher}。
     */
    static WebSocketDispatcher create() {
        return new DefaultWebSocketDispatcher();
    }
}
