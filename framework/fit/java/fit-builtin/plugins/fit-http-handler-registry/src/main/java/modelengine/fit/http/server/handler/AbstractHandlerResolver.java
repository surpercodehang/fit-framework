/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.server.handler.parameter.PathVariableMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestBeanMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestBodyMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestCookieMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestFormMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestHeaderMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestParamMapperResolver;
import modelengine.fit.http.server.handler.parameter.RequestQueryMapperResolver;
import modelengine.fit.http.server.handler.support.ErrorMapperResolver;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;

/**
 * 表示解析请求处理器的抽象基类。
 *
 * @author 邬涨财
 * @since 2023-11-29
 */
public abstract class AbstractHandlerResolver {
    private final PropertyValueMapperResolver defaultMapperResolver;
    private final HttpResponseStatusResolver defaultResponseStatusResolver;

    /**
     * 创建解析请求处理器。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     */
    public AbstractHandlerResolver(BeanContainer container) {
        Validation.notNull(container, "The bean container can not be null.");
        AnnotationMetadataResolver annotationMetadataResolver = container.runtime().resolverOfAnnotations();
        this.defaultMapperResolver = this.mapperResolver(annotationMetadataResolver);
        this.defaultResponseStatusResolver = new FitHttpResponseStatusResolver(container);
    }

    private PropertyValueMapperResolver mapperResolver(AnnotationMetadataResolver annotationResolver) {
        return PropertyValueMapperResolver.combine(new RequestCookieMapperResolver(annotationResolver),
                new PathVariableMapperResolver(annotationResolver),
                new RequestBodyMapperResolver(annotationResolver),
                new RequestHeaderMapperResolver(annotationResolver),
                new RequestParamMapperResolver(annotationResolver),
                new RequestQueryMapperResolver(annotationResolver),
                new RequestFormMapperResolver(annotationResolver),
                new RequestBeanMapperResolver(annotationResolver),
                new ErrorMapperResolver(),
                PropertyValueMapperResolver.defaultResolver());
    }

    /**
     * 获取 Http 值解析器。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @return 表示获取到的 Http 值解析器的 {@link PropertyValueMapperResolver}。
     */
    protected PropertyValueMapperResolver getPropertyValueMapperResolver(BeanContainer container) {
        return container.factory(PropertyValueMapperResolverSupplier.class)
                .map(BeanFactory::<PropertyValueMapperResolverSupplier>get)
                .map(propertyValueMapperResolverSupplier -> propertyValueMapperResolverSupplier.get(container))
                .map(resolver -> PropertyValueMapperResolver.combine(resolver, this.defaultMapperResolver))
                .orElse(this.defaultMapperResolver);
    }

    /**
     * 获取 Http 响应状态的解析器。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @return 表示获取到的 Http 响应状态的解析器的 {@link HttpResponseStatusResolver}。
     */
    protected HttpResponseStatusResolver getResponseStatusResolver(BeanContainer container) {
        return container.factory(HttpResponseStatusResolverSupplier.class)
                .map(BeanFactory::<HttpResponseStatusResolverSupplier>get)
                .map(supplier -> supplier.get(container))
                .map(resolver -> HttpResponseStatusResolver.combine(this.defaultResponseStatusResolver, resolver))
                .orElse(this.defaultResponseStatusResolver);
    }
}
