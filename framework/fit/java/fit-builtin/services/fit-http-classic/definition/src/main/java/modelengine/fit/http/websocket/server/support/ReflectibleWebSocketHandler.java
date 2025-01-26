/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.server.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.support.ErrorMapper;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.server.WebSocketBinaryMessageMapper;
import modelengine.fit.http.websocket.server.WebSocketTextMessageMapper;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示可反射执行的 WebSocket 处理器。
 *
 * @author 季聿阶
 * @since 2023-12-09
 */
public class ReflectibleWebSocketHandler extends AbstractWebSocketHandler {
    private final Object target;
    private final Method openMethod;
    private final List<PropertyValueMapper> openMappers;
    private final Method messageMethod;
    private final List<PropertyValueMapper> messageMappers;
    private final Method errorMethod;
    private final List<PropertyValueMapper> errorMappers;
    private final Method closeMethod;
    private final List<PropertyValueMapper> closeMappers;

    /**
     * 创建一个新的 WebSocket 处理器信息。
     *
     * @param info 表示处理器信息的 {@link Info}。
     */
    public ReflectibleWebSocketHandler(Info info) {
        super(info);
        this.target = notNull(info.target(), "The specified target of websocket handler cannot be null.");
        this.openMethod = info.openMethod();
        this.openMappers = getIfNull(info.openMethodMappers(), Collections::emptyList);
        this.messageMethod = notNull(info.messageMethod(), "The method on message received cannot be null.");
        this.messageMappers = getIfNull(info.messageMethodMappers(), Collections::emptyList);
        this.errorMethod = info.errorMethod();
        this.errorMappers = getIfNull(info.errorMethodMappers(), Collections::emptyList);
        this.closeMethod = info.closeMethod();
        this.closeMappers = getIfNull(info.closeMethodMappers(), Collections::emptyList);
    }

    @Override
    public void onOpen(Session session) {
        this.handle(session, this.openMappers, this.openMethod, null);
    }

    @Override
    public void onMessage(Session session, String message) {
        Map<String, Object> context =
                MapBuilder.<String, Object>get().put(WebSocketTextMessageMapper.KEY, message).build();
        this.handle(session, this.messageMappers, this.messageMethod, context);
    }

    @Override
    public void onMessage(Session session, byte[] message) {
        Map<String, Object> context =
                MapBuilder.<String, Object>get().put(WebSocketBinaryMessageMapper.KEY, message).build();
        this.handle(session, this.messageMappers, this.messageMethod, context);
    }

    @Override
    public void onClose(Session session) {
        this.handle(session, this.closeMappers, this.closeMethod, null);
    }

    private void handle(Session session, List<PropertyValueMapper> mappers, Method method,
            Map<String, Object> context) {
        if (method == null) {
            return;
        }
        Object[] args;
        try {
            args = mappers.stream()
                    .map(httpMapper -> httpMapper.map(cast(session.getHandshakeMessage()), null, context))
                    .toArray();
        } catch (Throwable e) {
            this.onError(session, e);
            return;
        }
        try {
            ReflectionUtils.invoke(this.target, method, args);
        } catch (MethodInvocationException e) {
            this.onError(session, e.getCause());
        }
    }

    @Override
    public void onError(Session session, Throwable cause) {
        if (this.errorMethod == null) {
            return;
        }
        Map<String, Object> context = MapBuilder.<String, Object>get().put(ErrorMapper.ERROR_KEY, cause).build();
        Object[] args = this.errorMappers.stream()
                .map(httpMapper -> httpMapper.map(cast(session.getHandshakeMessage()), null, context))
                .toArray();
        try {
            ReflectionUtils.invoke(this.target, this.errorMethod, args);
        } catch (MethodInvocationException e) {
            throw new IllegalStateException("Failed to handle websocket error.", e.getCause());
        }
    }
}
