/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.HttpHandlerGroup;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.ReflectibleMappingHandler;
import modelengine.fit.http.server.handler.DefaultReflectibleHttpHandler;
import modelengine.fit.http.server.handler.GlobalPathPatternPrefixResolver;
import modelengine.fit.http.server.handler.HttpHandlerResolver;
import modelengine.fit.http.server.handler.HttpResponseStatusResolver;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fit.http.server.handler.PropertyValueMetadataResolver;
import modelengine.fit.http.server.handler.exception.RequestParamFetchException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.pattern.builder.BuilderFactory;
import modelengine.fitframework.util.AnnotationUtils;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link HttpHandlerResolver} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-01-10
 */
@Component
public class DefaultHttpHandlerResolver implements HttpHandlerResolver {
    private final HttpClassicServer httpServer;
    private final BeanContainer container;

    public DefaultHttpHandlerResolver(HttpClassicServer httpServer, BeanContainer container) {
        this.httpServer = notNull(httpServer, "The http server cannot be null.");
        this.container = notNull(container, "The bean container cannot be null.");
    }

    @Override
    public Optional<HttpHandlerGroup> resolve(BeanFactory candidate, List<HttpServerFilter> preFilters,
            GlobalPathPatternPrefixResolver pathPatternPrefixResolver, PropertyValueMapperResolver mapperResolver,
            PropertyValueMetadataResolver metadataResolver, HttpResponseStatusResolver responseStatusResolver)
            throws RequestParamFetchException {
        String globalPrefix = pathPatternPrefixResolver.resolve().orElse(StringUtils.EMPTY);
        List<String> pathPatternPrefixes = this.resolvePathPatternPrefixes(candidate)
                .stream()
                .map(prefix -> globalPrefix + prefix)
                .collect(Collectors.toList());
        Method[] methods = TypeUtils.toClass(candidate.metadata().type()).getDeclaredMethods();
        Map<Method, List<HttpHandler>> handlers = new HashMap<>();
        for (Method method : methods) {
            if (!this.canResolve(method)) {
                continue;
            }
            Resolver resolver = new Resolver(ResolveParam.builder()
                    .candidate(candidate)
                    .method(method)
                    .preFilters(preFilters)
                    .mapperResolver(mapperResolver)
                    .metadataResolver(metadataResolver)
                    .responseStatusResolver(responseStatusResolver)
                    .pathPatternPrefixes(pathPatternPrefixes)
                    .build());
            List<HttpHandler> httpHandlers = resolver.resolveMethod();
            handlers.put(method, httpHandlers);
        }
        if (MapUtils.isEmpty(handlers)) {
            return Optional.empty();
        }
        String groupName = this.resolveGroupName(candidate);
        HttpHandlerGroup group = HttpHandlerGroup.create(groupName, this.resolveGroupDescription(candidate));
        for (Map.Entry<Method, List<HttpHandler>> entry : handlers.entrySet()) {
            for (HttpHandler handler : entry.getValue()) {
                if (handler instanceof ReflectibleMappingHandler) {
                    ReflectibleMappingHandler actualHandler = cast(handler);
                    actualHandler.group(groupName);
                }
                group.addHandler(entry.getKey(), handler);
            }
        }
        return Optional.of(group);
    }

    private String resolveGroupName(BeanFactory candidate) {
        return this.getAnnotation(TypeUtils.toClass(candidate.metadata().type()), RequestMapping.class)
                .map(RequestMapping::group)
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> candidate.metadata().type().getTypeName());
    }

    private String resolveGroupDescription(BeanFactory candidate) {
        return this.getAnnotation(TypeUtils.toClass(candidate.metadata().type()), RequestMapping.class)
                .map(RequestMapping::description)
                .orElse(StringUtils.EMPTY);
    }

    /**
     * 表示具体的解析器。
     */
    private class Resolver {
        private final BeanFactory candidate;
        private final Method method;
        private final List<HttpServerFilter> preFilters;
        private final PropertyValueMapperResolver mapperResolver;
        private final PropertyValueMetadataResolver metadataResolver;
        private final HttpResponseStatusResolver responseStatusResolver;
        private final List<String> pathPatternPrefixes;

        /**
         * 创建具体的解析器。
         *
         * @param param 表示解析参数的 {@link ResolveParam}。
         */
        public Resolver(ResolveParam param) {
            notNull(param, "The resolve param cannot be null.");
            this.candidate = notNull(param.candidate(), "The candidate bean factory cannot be null.");
            this.method = notNull(param.method(), "The method cannot be null.");
            this.preFilters = notNull(param.preFilters(), "The pre filters cannot be null.");
            this.mapperResolver = notNull(param.mapperResolver(), "The mapper resolver cannot be null.");
            this.metadataResolver = notNull(param.metadataResolver(), "The metadata resolver cannot be null.");
            this.responseStatusResolver =
                    notNull(param.responseStatusResolver(), "The response status resolver cannot be null.");
            this.pathPatternPrefixes =
                    notNull(param.pathPatternPrefixes(), "The path pattern prefixes cannot be null.");
        }

        private List<HttpHandler> resolveMethod() {
            List<String> pathPatternSuffixes = DefaultHttpHandlerResolver.this.resolvePathPatternSuffixes(this.method);
            List<PropertyValueMapper> httpMappers = this.resolveMappers(pathPatternSuffixes);
            List<PropertyValueMetadata> propertyValueMetadata = this.resolveMetadata();
            List<HttpHandler> handlers = new ArrayList<>();
            int statusCode = this.resolveStatusCode();
            boolean isDocumentIgnored = DefaultHttpHandlerResolver.this.isDocumentIgnored(this.candidate, this.method);
            for (String pathPatternPrefix : this.pathPatternPrefixes) {
                for (String pathPatternSuffix : pathPatternSuffixes) {
                    String pathPattern = pathPatternPrefix + pathPatternSuffix;
                    HttpHandler.StaticInfo staticInfo = HttpHandler.StaticInfo.builder()
                            .pathPattern(pathPattern)
                            .statusCode(statusCode)
                            .propertyValueMetadata(propertyValueMetadata)
                            .isDocumentIgnored(isDocumentIgnored)
                            .summary(DefaultHttpHandlerResolver.this.resolveSummary(this.method))
                            .description(DefaultHttpHandlerResolver.this.resolveDescription(this.method))
                            .returnDescription(DefaultHttpHandlerResolver.this.resolveReturnDescription(this.method))
                            .build();
                    HttpHandler.ExecutionInfo executionInfo = HttpHandler.ExecutionInfo.builder()
                            .httpServer(DefaultHttpHandlerResolver.this.httpServer)
                            .preFilters(this.preFilters)
                            .httpMappers(httpMappers)
                            .target(this.candidate.get())
                            .method(this.method)
                            .build();
                    handlers.add(this.resolve(staticInfo, executionInfo));
                }
            }
            return handlers;
        }

        private DefaultReflectibleHttpHandler resolve(HttpHandler.StaticInfo staticInfo,
                HttpHandler.ExecutionInfo executionInfo) {
            return new DefaultReflectibleHttpHandler(staticInfo, executionInfo);
        }

        private int resolveStatusCode() {
            return this.responseStatusResolver.resolve(this.method).orElse(HttpResponseStatus.OK).statusCode();
        }

        private List<PropertyValueMapper> resolveMappers(List<String> pathPatternSuffixes) {
            try {
                return Stream.of(this.method.getParameters())
                        .map(PropertyValue::createParameterValue)
                        .map(this.mapperResolver::resolve)
                        .map(optional -> optional.orElse(PropertyValueMapper.empty()))
                        .collect(Collectors.toList());
            } catch (RequestParamFetchException e) {
                throw new RequestParamFetchException(StringUtils.format(
                        "Invalid request parameter. [method={0}, prefixPath={1}, suffixPath={2}]",
                        ReflectionUtils.toString(method),
                        this.pathPatternPrefixes,
                        pathPatternSuffixes), e);
            }
        }

        private List<PropertyValueMetadata> resolveMetadata() {
            return Stream.of(this.method.getParameters())
                    .map(PropertyValue::createParameterValue)
                    .flatMap(propertyValue -> this.metadataResolver.resolve(propertyValue).stream())
                    .collect(Collectors.toList());
        }
    }

    private boolean canResolve(Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        return this.getAnnotation(method, RequestMapping.class).isPresent();
    }

    private List<String> resolvePathPatternPrefixes(BeanFactory candidate) {
        return this.resolvePathPattern(TypeUtils.toClass(candidate.metadata().type()));
    }

    private List<String> resolvePathPatternSuffixes(Method method) {
        return this.resolvePathPattern(method);
    }

    private List<String> resolvePathPattern(AnnotatedElement element) {
        Optional<RequestMapping> opAnnotation = this.getAnnotation(element, RequestMapping.class);
        if (opAnnotation.isPresent()) {
            RequestMapping annotation = opAnnotation.get();
            List<String> pathPatterns =
                    Stream.of(annotation.path()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(pathPatterns)) {
                pathPatterns.add(StringUtils.EMPTY);
            }
            return pathPatterns;
        } else {
            return Collections.singletonList(StringUtils.EMPTY);
        }
    }

    private boolean isDocumentIgnored(BeanFactory candidate, Method method) {
        boolean isIgnored = this.isDocumentIgnored(method);
        if (isIgnored) {
            return true;
        }
        return this.isDocumentIgnored(TypeUtils.toClass(candidate.metadata().type()));
    }

    private boolean isDocumentIgnored(AnnotatedElement element) {
        return this.getAnnotation(element, DocumentIgnored.class).isPresent();
    }

    private String resolveSummary(AnnotatedElement element) {
        return this.getAnnotation(element, RequestMapping.class).map(RequestMapping::summary).orElse(StringUtils.EMPTY);
    }

    private String resolveDescription(AnnotatedElement element) {
        return this.getAnnotation(element, RequestMapping.class)
                .map(RequestMapping::description)
                .orElse(StringUtils.EMPTY);
    }

    private String resolveReturnDescription(AnnotatedElement element) {
        return this.getAnnotation(element, RequestMapping.class)
                .map(RequestMapping::returnDescription)
                .orElse(StringUtils.EMPTY);
    }

    private <T extends Annotation> Optional<T> getAnnotation(AnnotatedElement element, Class<T> annotationClass) {
        return AnnotationUtils.getAnnotation(this.container, element, annotationClass);
    }

    /**
     * 表示解析参数。
     */
    public interface ResolveParam {
        /**
         * 获取候选 Bean 的工厂。
         *
         * @return 表示候选 Bean 的工厂的 {@link BeanFactory}。
         */
        BeanFactory candidate();

        /**
         * 获取待解析的方法。
         *
         * @return 表示待解析的方法的 {@link Method}。
         */
        Method method();

        /**
         * 获取前置的过滤器列表。
         *
         * @return 表示前置的过滤器列表的 {@link List}{@code <}{@link HttpServerFilter}{@code >}。
         */
        List<HttpServerFilter> preFilters();

        /**
         * 获取属性值映射解析器。
         *
         * @return 表示属性值映射解析器的 {@link PropertyValueMapperResolver}。
         */
        PropertyValueMapperResolver mapperResolver();

        /**
         * 获取属性值元数据解析器。
         *
         * @return 表示属性值元数据解析器的 {@link PropertyValueMetadataResolver}。
         */
        PropertyValueMetadataResolver metadataResolver();

        /**
         * 获取响应状态码解析器。
         *
         * @return 表示响应状态码解析器的 {@link HttpResponseStatusResolver}。
         */
        HttpResponseStatusResolver responseStatusResolver();

        /**
         * 获取前置路径样式列表。
         *
         * @return 表示前置路径样式列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> pathPatternPrefixes();

        /**
         * {@link ResolveParam} 的构建器。
         */
        interface Builder {
            /**
             * 向当前构建器中设置候选的 Bean 工厂。
             *
             * @param candidate 表示待设置的候选的 Bean 工厂的 {@link BeanFactory}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder candidate(BeanFactory candidate);

            /**
             * 向当前构建器中设置方法。
             *
             * @param method 表示待设置的方法的 {@link Method}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder method(Method method);

            /**
             * 向当前构建器中设置前置过滤器列表。
             *
             * @param preFilters 表示待设置的前置过滤器列表的 {@link List}{@code <}{@link HttpServerFilter}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder preFilters(List<HttpServerFilter> preFilters);

            /**
             * 向当前构建器中设置属性值映射解析器。
             *
             * @param preFilters 表示待设置的属性值映射解析器的 {@link PropertyValueMapperResolver}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder mapperResolver(PropertyValueMapperResolver preFilters);

            /**
             * 向当前构建器中设置属性值元数据解析器。
             *
             * @param metadataResolver 表示待设置的属性值元数据解析器的 {@link PropertyValueMetadataResolver}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder metadataResolver(PropertyValueMetadataResolver metadataResolver);

            /**
             * 向当前构建器中设置响应状态码解析器。
             *
             * @param responseStatusResolver 表示待设置的响应状态码解析器的 {@link HttpResponseStatusResolver}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder responseStatusResolver(HttpResponseStatusResolver responseStatusResolver);

            /**
             * 向当前构建器中设置前置路径样式列表。
             *
             * @param pathPatternPrefixes 表示待设置的前置路径样式列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder pathPatternPrefixes(List<String> pathPatternPrefixes);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link ResolveParam}。
             */
            ResolveParam build();
        }

        /**
         * 获取 {@link ResolveParam} 的构建器。
         *
         * @return 表示 {@link ResolveParam} 的构建器的 {@link Builder}。
         */
        static Builder builder() {
            return builder(null);
        }

        /**
         * 获取 {@link ResolveParam} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link ResolveParam}。
         * @return 表示 {@link ResolveParam} 的构建器的 {@link Builder}。
         */
        static Builder builder(ResolveParam value) {
            return BuilderFactory.get(ResolveParam.class, Builder.class).create(value);
        }
    }
}
