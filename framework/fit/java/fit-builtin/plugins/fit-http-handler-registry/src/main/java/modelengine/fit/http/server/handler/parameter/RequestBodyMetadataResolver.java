/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fit.http.server.handler.PropertyValueMetadataResolver;
import modelengine.fit.http.server.handler.Source;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * 表示解析带有 {@link RequestBody} 注解的参数的 {@link PropertyValueMetadataResolver}。
 *
 * @author 季聿阶
 * @since 2023-01-12
 */
public class RequestBodyMetadataResolver extends AbstractPropertyValueMetadataResolver {
    public RequestBodyMetadataResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestBody.class;
    }

    @Override
    protected List<PropertyValueMetadata> resolve(PropertyValue propertyValue, AnnotationMetadata annotations) {
        RequestBody body = annotations.getAnnotation(RequestBody.class);
        Property property = annotations.getAnnotation(Property.class);
        PropertyValueMetadata propertyValueMetadata = PropertyValueMetadata.builder()
                .name(body.key())
                .in(Source.BODY)
                .description(property != null ? property.description() : StringUtils.EMPTY)
                .example(property != null ? property.example() : StringUtils.EMPTY)
                .type(propertyValue.getParameterizedType())
                .isRequired(body.required())
                .defaultValue(null)
                .element(propertyValue.getElement().orElse(null))
                .build();
        return Collections.singletonList(propertyValueMetadata);
    }
}
