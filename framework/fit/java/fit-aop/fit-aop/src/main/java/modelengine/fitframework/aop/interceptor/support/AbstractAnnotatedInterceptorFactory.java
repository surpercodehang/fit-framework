/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.MethodInterceptorFactory;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolvers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 表示通过方法注解来生成方法拦截器的工厂。
 *
 * @author 季聿阶
 * @since 2022-12-14
 */
public abstract class AbstractAnnotatedInterceptorFactory implements MethodInterceptorFactory {
    private final Class<? extends Annotation> annotationClass;

    protected AbstractAnnotatedInterceptorFactory(Class<? extends Annotation> annotationClass) {
        this.annotationClass = notNull(annotationClass, "The annotation class cannot be null.");
    }

    @Override
    public boolean isInterceptMethod(@Nonnull Method method) {
        return this.getAnnotations(method).isAnnotationPresent(this.annotationClass);
    }

    /**
     * 获取指定方法的注解元数据信息。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示指定方法的注解元数据信息的 {@link AnnotationMetadata}。
     */
    protected AnnotationMetadata getAnnotations(@Nonnull Method method) {
        return AnnotationMetadataResolvers.create().resolve(method);
    }
}
