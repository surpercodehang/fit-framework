/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fit.http.server.handler.support.TypeTransformationPropertyValueMapper;
import modelengine.fit.http.server.handler.support.UniqueSourcePropertyValueMapper;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * 表示解析带有 {@link RequestParam} 注解的参数的 {@link PropertyValueMapperResolver}。
 *
 * @author 季聿阶
 * @since 2022-08-29
 */
public abstract class AbstractRequestParamMapperResolver extends AbstractPropertyValueMapperResolver {
    /**
     * 通过注解解析器来实例化 {@link AbstractRequestParamMapperResolver}。
     *
     * @param annotationResolver 表示注解解析器的 {@link AnnotationMetadataResolver}。
     * @throws IllegalArgumentException 当 {@code annotationResolver} 为 {@code null} 时。
     */
    public AbstractRequestParamMapperResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestParam.class;
    }

    @Override
    protected Optional<PropertyValueMapper> resolve(PropertyValue propertyValue, AnnotationMetadata annotations) {
        boolean isArray = this.isArray(propertyValue);
        RequestParam requestParam = annotations.getAnnotation(RequestParam.class);
        SourceFetcher sourceFetcher = this.createSourceFetcher(requestParam);
        PropertyValueMapper mapper = new UniqueSourcePropertyValueMapper(sourceFetcher, isArray);
        TypeTransformationPropertyValueMapper typeTransformationHttpMapper =
                new TypeTransformationPropertyValueMapper(mapper, propertyValue.getParameterizedType());
        return Optional.of(typeTransformationHttpMapper);
    }

    /**
     * 判断当前的值是否为一个数组。
     *
     * @param propertyValue 表示当前的值得 {@link PropertyValue}。
     * @return 如果当前值是数组，则返回 {@code true}，否则，返回 {@code false}。
     */
    protected boolean isArray(PropertyValue propertyValue) {
        return List.class.isAssignableFrom(propertyValue.getType());
    }

    /**
     * 创建一个数据来源的获取器。
     *
     * @param requestParam 表示数据参数上的注解的 {@link RequestParam}。
     * @return 表示创建出来的数据来源的获取器的 {@link SourceFetcher}。
     */
    protected abstract SourceFetcher createSourceFetcher(RequestParam requestParam);
}