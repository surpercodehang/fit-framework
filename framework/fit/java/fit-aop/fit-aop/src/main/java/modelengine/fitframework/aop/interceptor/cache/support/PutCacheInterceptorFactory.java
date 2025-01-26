/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache.support;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.cache.KeyGenerator;
import modelengine.fitframework.aop.interceptor.cache.PutCacheInterceptor;
import modelengine.fitframework.cache.annotation.PutCache;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link PutCache} 注解的方法拦截器工厂。
 *
 * @author 季聿阶
 * @since 2022-12-14
 */
public class PutCacheInterceptorFactory extends AbstractCacheInterceptorFactory<PutCache> {
    public PutCacheInterceptorFactory(BeanContainer container) {
        super(container, PutCache.class);
    }

    @Override
    protected List<String> cacheInstanceNames(@Nonnull PutCache annotation) {
        return Stream.of(annotation.name()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    protected String cacheKeyPattern(@Nonnull PutCache annotation) {
        return annotation.key();
    }

    @Override
    protected MethodInterceptor create(BeanContainer container, KeyGenerator keyGenerator, List<String> cacheNames) {
        return new PutCacheInterceptor(container, keyGenerator, cacheNames);
    }
}
