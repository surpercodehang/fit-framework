/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.client.support;

import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.client.WebSocketClassicListener;

/**
 * 表示 {@link WebSocketClassicListener} 的空实现。
 *
 * @author 季聿阶
 * @since 2024-05-04
 */
public class EmptyWebSocketListener implements WebSocketClassicListener {
    @Override
    public void onOpen(Session session) {}

    @Override
    public void onMessage(Session session, String message) {}

    @Override
    public void onMessage(Session session, byte[] message) {}

    @Override
    public void onClose(Session session, int code, String reason) {}

    @Override
    public void onError(Session session, Throwable cause) {}
}
