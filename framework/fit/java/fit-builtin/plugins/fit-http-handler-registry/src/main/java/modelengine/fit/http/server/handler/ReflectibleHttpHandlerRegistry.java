/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.HttpHandlerGroup;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterSupplier;
import modelengine.fit.http.server.handler.parameter.RequestBeanMetadataResolver;
import modelengine.fit.http.server.handler.parameter.RequestBodyMetadataResolver;
import modelengine.fit.http.server.handler.parameter.RequestParamMetadataResolver;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.annotation.BuiltinSerializer;
import modelengine.fitframework.util.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link HttpHandler} 的注册器。
 *
 * @author 季聿阶
 * @since 2023-01-10
 */
@Component
public class ReflectibleHttpHandlerRegistry extends AbstractHandlerResolver
        implements PluginStartedObserver, PluginStoppingObserver {
    private static final Logger log = Logger.get(ReflectibleHttpHandlerRegistry.class);

    private final BeanContainer currentContainer;
    private final HttpServerFilterSupplier httpServerFilterSupplier;
    private final HttpHandlerResolver handlerResolver;
    private final HttpClassicServer server;
    private final PropertyValueMetadataResolver defaultMetadataResolver;
    private final ExceptionHandlerRegistry exceptionHandlerRegistry;

    /**
     * 创建 Http 请求处理器的注册器。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param handlerResolver 表示 Http 处理器的解析器的 {@link HttpHandlerResolver}。
     * @param server 表示 Http 服务器的 {@link HttpClassicServer}
     * @param exceptionHandlerRegistry 表示异常处理器的注册器的 {@link ExceptionHandlerRegistry}。
     */
    public ReflectibleHttpHandlerRegistry(BeanContainer container, HttpHandlerResolver handlerResolver,
            HttpClassicServer server, ExceptionHandlerRegistry exceptionHandlerRegistry) {
        super(container);
        this.currentContainer = notNull(container, "The bean container cannot be null.");
        this.httpServerFilterSupplier = new DefaultHttpServerFilterSupplier();
        this.handlerResolver = notNull(handlerResolver, "The http handler resolver cannot be null.");
        this.server = notNull(server, "The http classic server cannot be null.");
        this.defaultMetadataResolver = this.metadataResolver(this.currentContainer.runtime().resolverOfAnnotations());
        this.exceptionHandlerRegistry =
                notNull(exceptionHandlerRegistry, "The exception handler registry cannot be null.");
    }

    private PropertyValueMetadataResolver metadataResolver(AnnotationMetadataResolver annotationResolver) {
        RequestBeanMetadataResolver requestBeanMetadataResolver = new RequestBeanMetadataResolver(annotationResolver);
        PropertyValueMetadataResolver resolver =
                PropertyValueMetadataResolver.combine(new RequestBodyMetadataResolver(annotationResolver),
                        new RequestParamMetadataResolver(annotationResolver),
                        requestBeanMetadataResolver);
        requestBeanMetadataResolver.setResolver(resolver);
        return resolver;
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        List<HttpHandlerGroup> groups = this.getMethodAndHttpHandlers(plugin);
        Map<Class<Throwable>, HttpExceptionHandler> exceptionHandlers =
                this.exceptionHandlerRegistry.addExceptionHandlers(plugin);
        Map<Class<Throwable>, HttpExceptionHandler> pluginExceptionHandlers =
                this.filterPluginExceptionHandlers(exceptionHandlers);
        Map<Class<Throwable>, Map<String, HttpExceptionHandler>> globalExceptionHandlers =
                this.exceptionHandlerRegistry.getGlobalExceptionHandlers();
        ObjectSerializer jsonSerializer = this.getCustomJsonSerializer(plugin);
        List<HttpMethodNameResolver> methodNameResolvers = this.getHttpMethodNameResolvers(plugin.container());
        for (HttpHandlerGroup group : groups) {
            this.registerHttpHandlerGroup(group,
                    globalExceptionHandlers,
                    pluginExceptionHandlers,
                    jsonSerializer,
                    methodNameResolvers);
        }
    }

    private Map<Class<Throwable>, HttpExceptionHandler> filterPluginExceptionHandlers(
            Map<Class<Throwable>, HttpExceptionHandler> exceptionHandlers) {
        return exceptionHandlers.entrySet()
                .stream()
                .filter(entry -> entry.getValue().scope() == Scope.PLUGIN)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void registerHttpHandlerGroup(HttpHandlerGroup group,
            Map<Class<Throwable>, Map<String, HttpExceptionHandler>> globalExceptionHandlers,
            Map<Class<Throwable>, HttpExceptionHandler> pluginExceptionHandlers, ObjectSerializer jsonSerializer,
            List<HttpMethodNameResolver> methodNameResolvers) {
        for (Map.Entry<Method, List<HttpHandler>> entry : group.getMethodHandlersMapping().entrySet()) {
            Method method = entry.getKey();
            for (HttpHandler handler : entry.getValue()) {
                if (handler instanceof DefaultReflectibleHttpHandler) {
                    DefaultReflectibleHttpHandler actualHandler = cast(handler);
                    actualHandler.setGlobalExceptionHandler(globalExceptionHandlers);
                    actualHandler.addPluginExceptionHandler(pluginExceptionHandlers);
                    actualHandler.addCustomJsonSerializer(jsonSerializer);
                }
                List<String> methodNames = methodNameResolvers.stream()
                        .map(resolver -> resolver.resolve(method))
                        .reduce(new ArrayList<>(), (allNames, toAddNames) -> {
                            allNames.addAll(toAddNames);
                            return allNames;
                        });
                methodNames.forEach(methodName -> this.server.httpDispatcher().register(methodName, handler));
            }
        }
        this.server.httpDispatcher().registerGroup(group);
        log.info("Register http handler group successfully. [group={}]", group.getName());
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        List<HttpHandlerGroup> groups = this.getMethodAndHttpHandlers(plugin);
        List<HttpMethodNameResolver> methodNameResolvers = this.getHttpMethodNameResolvers(plugin.container());
        for (HttpHandlerGroup group : groups) {
            this.unregisterHttpHandlerGroup(group, methodNameResolvers);
        }
        this.exceptionHandlerRegistry.removeExceptionHandlers(plugin);
    }

    private void unregisterHttpHandlerGroup(HttpHandlerGroup group, List<HttpMethodNameResolver> methodNameResolvers) {
        for (Map.Entry<Method, List<HttpHandler>> entry : group.getMethodHandlersMapping().entrySet()) {
            Method method = entry.getKey();
            for (HttpHandler handler : entry.getValue()) {
                List<String> methodNames = methodNameResolvers.stream()
                        .map(resolver -> resolver.resolve(method))
                        .reduce(new ArrayList<>(), (allNames, toAddNames) -> {
                            allNames.addAll(toAddNames);
                            return allNames;
                        });
                methodNames.forEach(methodName -> this.server.httpDispatcher().unregister(methodName, handler));
            }
        }
        this.server.httpDispatcher().unregisterGroup(group.getName());
        log.info("Unregister http handler group successfully. [group={}]", group.getName());
    }

    private List<HttpHandlerGroup> getMethodAndHttpHandlers(Plugin plugin) {
        BeanContainer container = plugin.container();
        GlobalPathPatternPrefixResolver pathPatternPrefixResolver = getPathPatternPrefixResolver(container);
        List<HttpServerFilter> httpFilters = this.getHttpFilters(container);
        PropertyValueMapperResolver mapperResolver = this.getPropertyValueMapperResolver(container);
        HttpResponseStatusResolver responseStatusResolver = this.getResponseStatusResolver(container);
        return container.factories()
                .stream()
                .map(beanFactory -> this.handlerResolver.resolve(beanFactory,
                        httpFilters,
                        pathPatternPrefixResolver,
                        mapperResolver,
                        this.defaultMetadataResolver,
                        responseStatusResolver))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * 获取全局路径模式的前缀解析器。
     *
     * @param container 表示容器的 {@link BeanContainer}。
     * @return 表示全局路径模式前缀解析器的 {@link GlobalPathPatternPrefixResolver}。
     */
    public static GlobalPathPatternPrefixResolver getPathPatternPrefixResolver(BeanContainer container) {
        FitGlobalPathPatternPrefixResolver defaultResolver = new FitGlobalPathPatternPrefixResolver(container);
        return container.factory(GlobalPathPatternPrefixResolverSupplier.class)
                .map(BeanFactory::<GlobalPathPatternPrefixResolverSupplier>get)
                .map(supplier -> supplier.get(container))
                .map(resolver -> GlobalPathPatternPrefixResolver.combine(defaultResolver, resolver))
                .orElse(defaultResolver);
    }

    private ObjectSerializer getCustomJsonSerializer(Plugin plugin) {
        return plugin.container()
                .factories(ObjectSerializer.class)
                .stream()
                .filter(factory -> !factory.metadata().annotations().isAnnotationPresent(BuiltinSerializer.class))
                .findFirst()
                .map(BeanFactory::<ObjectSerializer>get)
                .orElse(null);
    }

    private List<HttpServerFilter> getHttpFilters(BeanContainer container) {
        List<HttpServerFilter> allFilters = new ArrayList<>();
        List<HttpServerFilter> httpFilters = container.factory(HttpServerFilterSupplier.class)
                .map(BeanFactory::<HttpServerFilterSupplier>get)
                .map(supplier -> supplier.get(container))
                .orElseGet(ArrayList::new);
        allFilters.addAll(httpFilters);
        allFilters.addAll(this.httpServerFilterSupplier.get(container));
        allFilters.sort(HttpServerFilter.PriorityComparator.INSTANCE);
        return allFilters;
    }

    private List<HttpMethodNameResolver> getHttpMethodNameResolvers(BeanContainer container) {
        List<HttpMethodNameResolver> methodNameResolvers = new ArrayList<>();
        methodNameResolvers.add(this::getMethodNames);
        container.factory(HttpMethodNameResolverSupplier.class)
                .map(BeanFactory::<HttpMethodNameResolverSupplier>get)
                .map(supplier -> supplier.get(container))
                .ifPresent(methodNameResolvers::add);
        return methodNameResolvers;
    }

    private List<String> getMethodNames(Method method) {
        Set<HttpRequestMethod> supportedMethod = new HashSet<>();
        supportedMethod.addAll(this.resolveSupportedMethods(method.getDeclaringClass()));
        supportedMethod.addAll(this.resolveSupportedMethods(method));
        return supportedMethod.stream().map(HttpRequestMethod::name).collect(Collectors.toList());
    }

    private Set<HttpRequestMethod> resolveSupportedMethods(AnnotatedElement element) {
        Optional<RequestMapping> opAnnotation =
                AnnotationUtils.getAnnotation(this.currentContainer, element, RequestMapping.class);
        if (opAnnotation.isPresent()) {
            RequestMapping annotation = opAnnotation.get();
            return Stream.of(annotation.method())
                    .map(method -> HttpRequestMethod.from(method.name()))
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }
}
