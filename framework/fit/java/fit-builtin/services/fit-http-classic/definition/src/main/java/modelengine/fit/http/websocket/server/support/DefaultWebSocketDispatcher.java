/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.server.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.RegisterHttpHandlerException;
import modelengine.fit.http.server.dispatch.MappingTree;
import modelengine.fit.http.server.dispatch.support.DefaultMappingTree;
import modelengine.fit.http.websocket.server.WebSocketDispatcher;
import modelengine.fit.http.websocket.server.WebSocketHandler;
import modelengine.fit.http.websocket.server.WebSocketHandlerNotFoundException;
import modelengine.fitframework.util.OptionalUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 {@link WebSocketDispatcher} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-12-07
 */
public class DefaultWebSocketDispatcher implements WebSocketDispatcher {
    private final Map<String, WebSocketHandler> noPathVariableHandlers = new ConcurrentHashMap<>();
    private final MappingTree<WebSocketHandler> pathVariableHandlers = new DefaultMappingTree<>();

    @Override
    public WebSocketHandler dispatch(HttpClassicServerRequest request) {
        return OptionalUtils.get(() -> this.selectFromNoPathVariableHandlers(request))
                .orElse(() -> this.selectFromPathVariableHandlers(request))
                .orElseThrow(() -> {
                    String message =
                            StringUtils.format("No websocket handler for http request. [path={0}]", request.path());
                    return new WebSocketHandlerNotFoundException(message);
                });
    }

    private Optional<WebSocketHandler> selectFromNoPathVariableHandlers(HttpClassicServerRequest request) {
        WebSocketHandler handler = this.noPathVariableHandlers.get(request.path());
        return Optional.ofNullable(handler);
    }

    private Optional<WebSocketHandler> selectFromPathVariableHandlers(HttpClassicServerRequest request) {
        return this.pathVariableHandlers.search(request.path());
    }

    @Override
    public void register(WebSocketHandler handler) {
        notNull(handler, "The websocket handler cannot be null.");
        String pathPattern = MappingTree.convertToMatchedPathPattern(handler.pathPattern());
        notBlank(pathPattern, "The path pattern cannot be blank.");
        WebSocketHandler preHandler;
        if (pathPattern.contains("*")) {
            preHandler = this.pathVariableHandlers.register(pathPattern, handler).orElse(null);
        } else {
            preHandler = this.noPathVariableHandlers.put(pathPattern, handler);
        }
        if (preHandler != null) {
            String message = StringUtils.format("WebSocket handler has been registered. [pattern={0}]", pathPattern);
            throw new RegisterHttpHandlerException(message);
        }
    }

    @Override
    public void unregister(WebSocketHandler handler) {
        notNull(handler, "The websocket handler cannot be null.");
        String pathPattern = MappingTree.convertToMatchedPathPattern(handler.pathPattern());
        notBlank(pathPattern, "The path pattern cannot be blank.");
        if (pathPattern.contains("*")) {
            this.pathVariableHandlers.unregister(pathPattern);
        } else {
            this.noPathVariableHandlers.remove(pathPattern);
        }
    }
}
