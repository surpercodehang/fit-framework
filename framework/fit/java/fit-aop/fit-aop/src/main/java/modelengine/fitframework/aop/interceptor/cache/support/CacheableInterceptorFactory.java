/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.aop.interceptor.cache.support;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.cache.CacheableInterceptor;
import modelengine.fitframework.aop.interceptor.cache.KeyGenerator;
import modelengine.fitframework.cache.annotation.Cacheable;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link Cacheable} 注解的方法拦截器工厂。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
public class CacheableInterceptorFactory extends AbstractCacheInterceptorFactory<Cacheable> {
    /**
     * 使用指定的容器初始化 {@link CacheableInterceptorFactory} 的新实例。
     *
     * @param container 表示容器的 {@link BeanContainer}。
     * @throws IllegalArgumentException 当 {@code container} 为 {@code null} 时。
     */
    public CacheableInterceptorFactory(BeanContainer container) {
        super(container, Cacheable.class);
    }

    @Override
    protected List<String> cacheInstanceNames(@Nonnull Cacheable annotation) {
        return Stream.of(annotation.name()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    protected String cacheKeyPattern(@Nonnull Cacheable annotation) {
        return annotation.key();
    }

    @Override
    protected MethodInterceptor create(BeanContainer container, KeyGenerator keyGenerator, List<String> cacheNames) {
        return new CacheableInterceptor(container, keyGenerator, cacheNames);
    }
}
