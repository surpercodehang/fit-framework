/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.server.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.HttpResource;
import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.websocket.server.WebSocketHandler;

/**
 * 表示 {@link WebSocketHandler} 的抽象实现。
 *
 * @author 季聿阶
 * @since 2023-12-09
 */
public abstract class AbstractWebSocketHandler implements WebSocketHandler {
    private final HttpClassicServer httpServer;
    private final String pathPattern;

    /**
     * 构造一个新的 {@link AbstractWebSocketHandler} 实例。
     *
     * @param info 表示包含 {@link WebSocketHandler} 信息的 {@link Info}。
     */
    public AbstractWebSocketHandler(Info info) {
        notNull(info, "The websocket handler info cannot be null.");
        this.httpServer = notNull(info.httpServer(), "The http server cannot be null.");
        this.pathPattern = notBlank(info.pathPattern(), "The path pattern cannot be blank.");
    }

    @Override
    public HttpResource httpResource() {
        return this.httpServer;
    }

    @Override
    public String pathPattern() {
        return this.pathPattern;
    }
}
