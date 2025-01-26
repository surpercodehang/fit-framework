/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.websocket.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.handler.GlobalPathPatternPrefixResolver;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.server.handler.websocket.WebSocketHandlerResolver;
import modelengine.fit.http.websocket.annotation.OnClose;
import modelengine.fit.http.websocket.annotation.OnError;
import modelengine.fit.http.websocket.annotation.OnMessage;
import modelengine.fit.http.websocket.annotation.OnOpen;
import modelengine.fit.http.websocket.annotation.WebSocketEndpoint;
import modelengine.fit.http.websocket.server.WebSocketHandler;
import modelengine.fit.http.websocket.server.support.ReflectibleWebSocketHandler;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.fitframework.value.PropertyValue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link WebSocketHandlerResolver} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-12-09
 */
@Component
public class DefaultWebSocketHandlerResolver implements WebSocketHandlerResolver {
    private final HttpClassicServer server;

    public DefaultWebSocketHandlerResolver(HttpClassicServer server) {
        this.server = notNull(server, "The http classic server cannot be null.");
    }

    @Override
    public Optional<WebSocketHandler> resolve(BeanFactory candidate,
            GlobalPathPatternPrefixResolver pathPatternPrefixResolver, PropertyValueMapperResolver mapperResolver) {
        boolean isWebSocketEndPoint = candidate.metadata().annotations().isAnnotationPresent(WebSocketEndpoint.class);
        if (!isWebSocketEndPoint) {
            return Optional.empty();
        }
        String globalPrefix = pathPatternPrefixResolver.resolve().orElse(StringUtils.EMPTY);
        WebSocketEndpoint endpoint = candidate.metadata().annotations().getAnnotation(WebSocketEndpoint.class);
        String path = globalPrefix + endpoint.path();
        WebSocketHandler.Info.Builder infoBuilder =
                WebSocketHandler.Info.custom().httpServer(this.server).pathPattern(path).target(candidate.get());
        Method[] methods = TypeUtils.toClass(candidate.metadata().type()).getDeclaredMethods();
        AnnotationMetadataResolver annotationResolver = candidate.metadata().runtime().resolverOfAnnotations();
        for (Method method : methods) {
            AnnotationMetadata annotations = annotationResolver.resolve(method);
            if (annotations.isAnnotationPresent(OnOpen.class)) {
                infoBuilder.openMethod(method).openMethodMappers(this.resolveMappers(method, mapperResolver));
            } else if (annotations.isAnnotationPresent(OnMessage.class)) {
                infoBuilder.messageMethod(method).messageMethodMappers(this.resolveMappers(method, mapperResolver));
            } else if (annotations.isAnnotationPresent(OnError.class)) {
                infoBuilder.errorMethod(method).errorMethodMappers(this.resolveMappers(method, mapperResolver));
            } else if (annotations.isAnnotationPresent(OnClose.class)) {
                infoBuilder.closeMethod(method).closeMethodMappers(this.resolveMappers(method, mapperResolver));
            }
        }
        return Optional.of(new ReflectibleWebSocketHandler(infoBuilder.build()));
    }

    private List<PropertyValueMapper> resolveMappers(Method method, PropertyValueMapperResolver mapperResolver) {
        return Stream.of(method.getParameters())
                .map(PropertyValue::createParameterValue)
                .map(mapperResolver::resolve)
                .map(optional -> optional.orElse(PropertyValueMapper.empty()))
                .collect(Collectors.toList());
    }
}
