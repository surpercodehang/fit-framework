/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.websocket;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.handler.GlobalPathPatternPrefixResolver;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.server.handler.ReflectibleHttpHandlerRegistry;
import modelengine.fit.http.server.handler.parameter.PathVariableMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestBeanMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestCookieMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestHeaderMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestQueryMapperResolver;
import modelengine.fit.http.server.handler.support.ErrorMapperResolver;
import modelengine.fit.http.websocket.server.WebSocketBinaryMessageMapperResolver;
import modelengine.fit.http.websocket.server.WebSocketHandler;
import modelengine.fit.http.websocket.server.WebSocketSessionMapperResolver;
import modelengine.fit.http.websocket.server.WebSocketTextMessageMapperResolver;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 {@link WebSocketHandler} 的注册器。
 *
 * @author 季聿阶
 * @since 2023-12-09
 */
@Component
public class ReflectibleWebSocketHandlerRegistry implements PluginStartedObserver, PluginStoppingObserver {
    private final HttpClassicServer server;
    private final WebSocketHandlerResolver resolver;

    public ReflectibleWebSocketHandlerRegistry(HttpClassicServer server, WebSocketHandlerResolver resolver) {
        this.server = notNull(server, "The http classic server cannot be null.");
        this.resolver = notNull(resolver, "The websocket handler resolver cannot be null.");
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        List<WebSocketHandler> handlers = this.getWebSocketHandlers(plugin);
        handlers.forEach(handler -> this.server.webSocketDispatcher().register(handler));
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        List<WebSocketHandler> handlers = this.getWebSocketHandlers(plugin);
        handlers.forEach(handler -> this.server.webSocketDispatcher().unregister(handler));
    }

    private List<WebSocketHandler> getWebSocketHandlers(Plugin plugin) {
        BeanContainer container = plugin.container();
        PropertyValueMapperResolver mapperResolver = this.mapperResolver(plugin.runtime().resolverOfAnnotations());
        GlobalPathPatternPrefixResolver pathPatternPrefixResolver =
                ReflectibleHttpHandlerRegistry.getPathPatternPrefixResolver(container);
        return container.factories()
                .stream()
                .map(beanFactory -> this.resolver.resolve(beanFactory, pathPatternPrefixResolver, mapperResolver))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private PropertyValueMapperResolver mapperResolver(AnnotationMetadataResolver annotationResolver) {
        return PropertyValueMapperResolver.combine(new WebSocketSessionMapperResolver(),
                new WebSocketTextMessageMapperResolver(annotationResolver),
                new WebSocketBinaryMessageMapperResolver(annotationResolver),
                new RequestCookieMapperResolver(annotationResolver),
                new PathVariableMapperResolver(annotationResolver),
                new RequestHeaderMapperResolver(annotationResolver),
                new RequestQueryMapperResolver(annotationResolver),
                new RequestBeanMapperResolver(annotationResolver),
                new ErrorMapperResolver());
    }
}
