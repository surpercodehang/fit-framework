/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.websocket;

import modelengine.fit.client.Request;
import modelengine.fit.client.Response;
import modelengine.fit.http.websocket.Session;

/**
 * 表示流式调用请求的发起器。
 *
 * @author 何天放
 * @since 2024-05-14
 */
public interface WebSocketInvokeRequester {
    /**
     * 发起流式调用。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param request 表示请求的 {@link Request}。
     */
    void request(Session session, Request request);

    /**
     * 等待并获取调用结果。
     *
     * @return 表示调用结果的 {@link Response}。
     * @throws InterruptedException 当当前等待的线程被中断时。
     */
    Response waitAndgetResponse() throws InterruptedException;
}
