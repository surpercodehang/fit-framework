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
 * 表示 WebSocket 中会话的映射器。
 *
 * @author 季聿阶
 * @since 2023-12-10
 */
public class WebSocketSessionMapper implements PropertyValueMapper {
    /** 表示存储在 WebSocket 握手请求中的会话主键。 */
    public static final String KEY = "FIT-WebSocket-Session";

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        return request.attributes().get(KEY).orElse(null);
    }
}
