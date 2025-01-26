/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolvers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 表示携带指定注解的匹配器。
 *
 * @author 季聿阶
 * @since 2022-12-14
 */
public class AnnotationMethodMatcher implements MethodMatcher {
    private final AnnotationMetadataResolver resolver;
    private final Class<? extends Annotation> annotationClass;

    public AnnotationMethodMatcher(Class<? extends Annotation> annotationClass) {
        this.resolver = AnnotationMetadataResolvers.create();
        this.annotationClass = notNull(annotationClass, "The annotation class cannot be null.");
    }

    @Override
    public MatchResult match(@Nonnull Method method) {
        AnnotationMetadata annotations = this.resolver.resolve(method);
        return MatchResult.match(annotations.isAnnotationPresent(this.annotationClass));
    }
}
