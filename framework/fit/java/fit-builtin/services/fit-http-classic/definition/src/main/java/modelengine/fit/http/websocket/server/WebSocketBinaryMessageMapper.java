/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.server;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.PropertyValueMapper;

import java.util.Map;

/**
 * 表示 WebSocket 中二进制消息的映射器。
 *
 * @author 季聿阶
 * @since 2023-12-11
 */
public class WebSocketBinaryMessageMapper implements PropertyValueMapper {
    /** 表示在自定义上下文中二进制消息的主键。 */
    public static final String KEY = "FIT-WebSocket-Binary-Message";

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        if (context == null) {
            return null;
        }
        Object binaryMessage = context.get(KEY);
        if (binaryMessage instanceof byte[]) {
            return binaryMessage;
        }
        return null;
    }
}
