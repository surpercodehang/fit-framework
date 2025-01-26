/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fit.http.server.handler.PropertyValueMetadataResolver;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * 表示解析带有 {@link RequestParam} 注解的参数的 {@link PropertyValueMetadataResolver}。
 *
 * @author 季聿阶
 * @since 2023-01-12
 */
public class RequestParamMetadataResolver extends AbstractPropertyValueMetadataResolver {
    public RequestParamMetadataResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestParam.class;
    }

    @Override
    protected List<PropertyValueMetadata> resolve(PropertyValue propertyValue, AnnotationMetadata annotations) {
        RequestParam param = annotations.getAnnotation(RequestParam.class);
        Property property = annotations.getAnnotation(Property.class);
        PropertyValueMetadata propertyValueMetadata = PropertyValueMetadata.builder()
                .name(StringUtils.blankIf(param.name(), propertyValue.getName()))
                .in(param.in())
                .description(property != null ? property.description() : StringUtils.EMPTY)
                .example(property != null ? property.example() : StringUtils.EMPTY)
                .type(propertyValue.getParameterizedType())
                .isRequired(param.required())
                .defaultValue(param.defaultValue())
                .element(propertyValue.getElement().orElse(null))
                .build();
        return Collections.singletonList(propertyValueMetadata);
    }
}
