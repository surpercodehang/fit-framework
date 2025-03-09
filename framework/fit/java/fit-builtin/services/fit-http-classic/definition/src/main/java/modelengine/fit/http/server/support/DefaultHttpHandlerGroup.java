/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.HttpHandlerGroup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link HttpHandlerGroup} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-22
 */
public class DefaultHttpHandlerGroup implements HttpHandlerGroup {
    private final String name;
    private final String description;
    private final Map<Method, List<HttpHandler>> handlers = new HashMap<>();

    /**
     * 使用指定的名称和描述初始化 {@link DefaultHttpHandlerGroup} 的新实例。
     *
     * @param name 表示名称的 {@link String}。
     * @param description 表示描述的 {@link String}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    public DefaultHttpHandlerGroup(String name, String description) {
        this.name = notBlank(name, "The group name cannot be blank.");
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<HttpHandler> getHandlers() {
        return Collections.unmodifiableList(this.handlers.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
    }

    @Override
    public Map<Method, List<HttpHandler>> getMethodHandlersMapping() {
        return Collections.unmodifiableMap(this.handlers);
    }

    @Override
    public void addHandler(Method method, HttpHandler handler) {
        if (method != null && handler != null) {
            List<HttpHandler> httpHandlers = this.handlers.computeIfAbsent(method, key -> new ArrayList<>());
            httpHandlers.add(handler);
        }
    }
}
