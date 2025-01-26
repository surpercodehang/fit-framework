/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.async;

import modelengine.fitframework.annotation.Asynchronous;
import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.support.DefaultMatchResult;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolvers;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 表示 {@link Asynchronous} 注解定义异步执行方法的方法匹配器。
 *
 * @author 季聿阶
 * @since 2022-11-13
 */
public class AsyncMethodMatcher implements MethodMatcher {
    private final AnnotationMetadataResolver resolver;
    private final String asyncExecutorName;

    /**
     * 创建异步执行方法的方法匹配器。
     *
     * @param asyncExecutorName 表示异步执行方法器名的 {@link String}。
     */
    public AsyncMethodMatcher(String asyncExecutorName) {
        this.resolver = AnnotationMetadataResolvers.create();
        if (StringUtils.isBlank(asyncExecutorName)) {
            this.asyncExecutorName = StringUtils.EMPTY;
        } else {
            this.asyncExecutorName = asyncExecutorName;
        }
    }

    @Override
    public MatchResult match(@Nonnull Method method) {
        boolean matches = this.getAsyncExecutorName(method)
                .filter(name -> Objects.equals(name, this.asyncExecutorName))
                .isPresent();
        return new DefaultMatchResult(matches);
    }

    private Optional<String> getAsyncExecutorName(Method method) {
        AnnotationMetadata annotations = this.resolver.resolve(method);
        if (annotations.isAnnotationPresent(Asynchronous.class)) {
            return Optional.of(annotations.getAnnotation(Asynchronous.class).executor());
        }
        annotations = this.resolver.resolve(method.getDeclaringClass());
        if (annotations.isAnnotationPresent(Asynchronous.class)) {
            return Optional.of(annotations.getAnnotation(Asynchronous.class).executor());
        }
        return Optional.empty();
    }
}
