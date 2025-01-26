/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.handler.AbstractHandlerResolver;
import modelengine.fit.http.server.handler.ExceptionHandlerRegistry;
import modelengine.fit.http.server.handler.HttpExceptionHandler;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.comparator.ClassComparator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.AnnotationUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.fitframework.value.PropertyValue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link ExceptionHandlerRegistry} 的默认实现。
 *
 * @author 季聿阶
 * @author 邬涨财
 * @since 2022-08-25
 */
@Component
public class DefaultExceptionHandlerRegistry extends AbstractHandlerResolver implements ExceptionHandlerRegistry {
    /**
     * 表示全局异常处理器。
     * <p>其中外层 map 的键为需要处理器需要处理的异常，值为每个插件所对应的全局处理器；内层 map 的键为插件名，值为该插件所含有的全局异常处理器。</p>
     */
    private final Map<Class<Throwable>, Map<String, HttpExceptionHandler>> globalExceptionHandlers =
            new ConcurrentSkipListMap<>(ClassComparator.INSTANCE);

    public DefaultExceptionHandlerRegistry(BeanContainer beanContainer) {
        super(beanContainer);
    }

    @Override
    public Map<Class<Throwable>, HttpExceptionHandler> addExceptionHandlers(Plugin plugin) {
        BeanContainer beanContainer = plugin.container();
        Map<Class<Throwable>, HttpExceptionHandler> exceptionHandlers =
                notNull(beanContainer, "The bean container cannot be null.").factories()
                        .stream()
                        .map(factory -> resolve(beanContainer, factory))
                        .reduce(MapUtils::merge)
                        .orElse(Collections.emptyMap());
        exceptionHandlers.entrySet()
                .stream()
                .filter(entry -> entry.getValue().scope() == Scope.GLOBAL)
                .forEach(entry -> {
                    Map<String, HttpExceptionHandler> map =
                            this.globalExceptionHandlers.computeIfAbsent(entry.getKey(), key -> new HashMap<>());
                    map.put(plugin.metadata().name(), entry.getValue());
                });
        return exceptionHandlers;
    }

    @Override
    public Map<Class<Throwable>, Map<String, HttpExceptionHandler>> getGlobalExceptionHandlers() {
        return Collections.unmodifiableMap(this.globalExceptionHandlers);
    }

    @Override
    public void removeExceptionHandlers(Plugin plugin) {
        this.globalExceptionHandlers.forEach((throwable, handlers) -> {
            String pluginName = plugin.metadata().name();
            if (handlers.containsKey(pluginName)) {
                this.globalExceptionHandlers.get(throwable).remove(pluginName);
            }
        });
    }

    private Map<Class<Throwable>, HttpExceptionHandler> resolve(BeanContainer beanContainer, BeanFactory factory) {
        Map<Class<Throwable>, HttpExceptionHandler> exceptionHandlers = new HashMap<>();
        Method[] methods = TypeUtils.toClass(factory.metadata().type()).getDeclaredMethods();
        for (Method method : methods) {
            Map<Class<Throwable>, HttpExceptionHandler> resolved = this.resolveMethod(beanContainer, factory, method);
            exceptionHandlers = MapUtils.merge(exceptionHandlers, resolved);
        }
        return exceptionHandlers;
    }

    private Map<Class<Throwable>, HttpExceptionHandler> resolveMethod(BeanContainer beanContainer, BeanFactory factory,
            Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            return Collections.emptyMap();
        }
        Optional<ExceptionHandler> opAnnotation =
                AnnotationUtils.getAnnotation(beanContainer, method, ExceptionHandler.class);
        if (!opAnnotation.isPresent()) {
            return Collections.emptyMap();
        }
        HttpExceptionHandler handler = new DefaultHttpExceptionHandler(factory.get(),
                method,
                this.getStatusCode(beanContainer, method),
                this.resolveMappers(beanContainer, method),
                opAnnotation.get().scope());
        MapBuilder<Class<Throwable>, HttpExceptionHandler> mapBuilder = MapBuilder.get();
        for (Class<? extends Throwable> clazz : opAnnotation.get().value()) {
            mapBuilder.put(ObjectUtils.cast(clazz), handler);
        }
        return mapBuilder.build();
    }

    private List<PropertyValueMapper> resolveMappers(BeanContainer beanContainer, Method method) {
        return Stream.of(method.getParameters())
                .map(PropertyValue::createParameterValue)
                .map(propertyValue -> this.getPropertyValueMapperResolver(beanContainer).resolve(propertyValue))
                .map(optional -> optional.orElse(PropertyValueMapper.empty()))
                .collect(Collectors.toList());
    }

    private int getStatusCode(BeanContainer beanContainer, Method method) {
        return this.getResponseStatusResolver(beanContainer).resolve(method).orElse(HttpResponseStatus.OK).statusCode();
    }
}