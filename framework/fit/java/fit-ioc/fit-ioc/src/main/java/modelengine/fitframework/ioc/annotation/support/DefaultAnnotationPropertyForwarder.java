/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.support;

import modelengine.fitframework.annotation.Forward;
import modelengine.fitframework.ioc.annotation.AnnotationProperties;
import modelengine.fitframework.ioc.annotation.AnnotationProperty;
import modelengine.fitframework.ioc.annotation.AnnotationPropertyForward;
import modelengine.fitframework.ioc.annotation.AnnotationPropertyForwarder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 为 {@link AnnotationPropertyForwarder} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-06-15
 */
public class DefaultAnnotationPropertyForwarder implements AnnotationPropertyForwarder {
    @Override
    public Optional<AnnotationPropertyForward> forward(Method propertyMethod) {
        Forward forward = propertyMethod.getAnnotation(Forward.class);
        if (forward == null) {
            return Optional.empty();
        }
        Class<? extends Annotation> targetAnnotation = Optional.of(forward.annotation())
                .filter(annotation -> !Objects.equals(annotation, Annotation.class))
                .orElseGet(() -> ObjectUtils.cast(propertyMethod.getDeclaringClass()));
        String targetProperty = Optional.of(forward.property())
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .orElseGet(propertyMethod::getName);
        AnnotationProperty target = AnnotationProperties.create(targetAnnotation, targetProperty);
        return Optional.of(new DefaultAnnotationPropertyForward(target, forward.converter()));
    }
}
