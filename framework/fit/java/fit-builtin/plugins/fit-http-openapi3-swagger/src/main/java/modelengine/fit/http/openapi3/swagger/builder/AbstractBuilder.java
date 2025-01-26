/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.builder;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.HttpDispatcher;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.ReflectibleMappingHandler;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;

import java.util.Optional;

/**
 * 表示通用构建器的抽象父类。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public abstract class AbstractBuilder {
    private final BeanContainer container;

    AbstractBuilder(BeanContainer container) {
        this.container = notNull(container, "The bean container cannot be null.");
    }

    /**
     * 获取 Http 的处理转发器。
     *
     * @return 表示 Http 的处理转发器的 {@link Optional}{@code <}{@link HttpDispatcher}{@code >}。
     */
    protected Optional<HttpDispatcher> getHttpDispatcher() {
        return this.container.lookup(HttpClassicServer.class)
                .map(BeanFactory::get)
                .map(HttpClassicServer.class::cast)
                .map(HttpClassicServer::httpDispatcher);
    }

    /**
     * 判断指定的 Http 处理器是否需要忽略。
     *
     * @param handler 表示指定的 Http 处理器的 {@link HttpHandler}。
     * @return 如果指定的 Http 处理器需要忽略，返回 {@code true}，否则，返回 {@code false}。
     */
    protected boolean isHandlerIgnored(HttpHandler handler) {
        if (!(handler instanceof ReflectibleMappingHandler)) {
            return true;
        }
        ReflectibleMappingHandler actualHandler = cast(handler);
        return actualHandler.isDocumentIgnored();
    }

    /**
     * 获取 Bean 容器。
     *
     * @return 表示 Bean 容器的 {@link BeanContainer}。
     */
    protected BeanContainer getContainer() {
        return this.container;
    }
}
