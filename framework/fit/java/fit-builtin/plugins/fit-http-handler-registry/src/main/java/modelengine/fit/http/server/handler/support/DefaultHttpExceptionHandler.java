/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.HttpExceptionHandler;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link HttpExceptionHandler} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-08-25
 */
public class DefaultHttpExceptionHandler implements HttpExceptionHandler {
    private final Object target;
    private final Method method;
    private final int statusCode;
    private final List<PropertyValueMapper> propertyValueMappers;
    private final Scope scope;

    public DefaultHttpExceptionHandler(Object target, Method method, int statusCode,
            List<PropertyValueMapper> propertyValueMappers, Scope scope) {
        this.target = notNull(target, "The bean of exception handler cannot be null.");
        this.method = notNull(method, "The method of exception handler cannot be null.");
        this.statusCode = statusCode;
        this.propertyValueMappers = ObjectUtils.getIfNull(propertyValueMappers, Collections::emptyList);
        this.scope = scope;
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public Scope scope() {
        return this.scope;
    }

    @Override
    public Object handle(HttpClassicServerRequest request, HttpClassicServerResponse response, Throwable cause) {
        Map<String, Object> context = MapBuilder.<String, Object>get().put(ErrorMapper.ERROR_KEY, cause).build();
        Object[] args = this.propertyValueMappers.stream()
                .map(httpMapper -> httpMapper.map(request, response, context))
                .toArray();
        return ReflectionUtils.invoke(this.target, this.method, args);
    }
}
