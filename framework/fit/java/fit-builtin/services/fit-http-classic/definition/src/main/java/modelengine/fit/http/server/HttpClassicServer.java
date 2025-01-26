/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.HttpResource;
import modelengine.fit.http.websocket.server.WebSocketDispatcher;

/**
 * 表示 Http 服务器。
 *
 * @author 季聿阶
 * @since 2022-07-08
 */
public interface HttpClassicServer extends HttpResource {
    /**
     * 绑定端口号。
     *
     * @param port 表示待绑定的端口号的 {@code int}。
     * @return 表示当前的 Http 服务器的 {@link HttpClassicServer}。
     * @throws IllegalArgumentException 当 {@code port} 小于 1 时。
     */
    HttpClassicServer bind(int port);

    /**
     * 绑定端口号以及标注该端口是否安全。
     *
     * @param port 表示待绑定的端口号的 {@code int}。
     * @param isSecure 表示待绑定的端口号是否安全的 {@code boolean}。
     * @return 表示当前的 Http 服务器的 {@link HttpClassicServer}。
     * @throws IllegalArgumentException 当 {@code port} 小于 1 时。
     */
    HttpClassicServer bind(int port, boolean isSecure);

    /**
     * 启动 Http 服务器。
     *
     * @throws HttpServerStartupException 当启动过程中遇到任何异常时。
     */
    void start() throws HttpServerStartupException;

    /**
     * 判断 Http 服务器是否已经启动。
     *
     * @return 如果已经启动，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isStarted();

    /**
     * 停止 Http 服务器。
     */
    void stop();

    /**
     * 获取 Http 请求的分发器。
     *
     * @return 表示 Http 请求的分发器的 {@link HttpDispatcher}。
     */
    HttpDispatcher httpDispatcher();

    /**
     * 获取 WebSocket 请求的分发器。
     *
     * @return 表示 WebSocket 请求的分发器的 {@link WebSocketDispatcher}。
     */
    WebSocketDispatcher webSocketDispatcher();

    /**
     * 将 Http 响应的消息体发送回去。
     *
     * @param response 表示 Http 响应的消息体的 {@link HttpClassicServerResponse}。
     */
    void send(HttpClassicServerResponse response);
}
