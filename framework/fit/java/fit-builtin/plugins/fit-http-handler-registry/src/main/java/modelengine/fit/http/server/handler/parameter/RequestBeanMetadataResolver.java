/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fit.http.server.handler.PropertyValueMetadataResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示解析带有 {@link RequestBean} 注解的参数的 {@link PropertyValueMetadataResolver}。
 *
 * @author 邬涨财
 * @since 2023-11-15
 */
public class RequestBeanMetadataResolver extends AbstractPropertyValueMetadataResolver {
    private PropertyValueMetadataResolver resolver;

    public RequestBeanMetadataResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestBean.class;
    }

    public void setResolver(PropertyValueMetadataResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected List<PropertyValueMetadata> resolve(PropertyValue propertyValue, AnnotationMetadata annotations) {
        Field[] fields = ReflectionUtils.getDeclaredFields(propertyValue.getType());
        return Arrays.stream(fields)
                .map(PropertyValue::createFieldValue)
                .flatMap(fieldValue -> this.resolver.resolve(fieldValue).stream())
                .collect(Collectors.toList());
    }
}
