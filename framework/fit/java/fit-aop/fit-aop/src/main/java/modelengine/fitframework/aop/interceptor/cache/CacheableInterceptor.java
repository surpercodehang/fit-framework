/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache;

import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.cache.Cache;
import modelengine.fitframework.cache.annotation.Cacheable;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.ioc.BeanContainer;

import java.util.List;

/**
 * 表示 {@link Cacheable} 的方法拦截器。
 *
 * @author 季聿阶
 * @since 2022-12-12
 */
public class CacheableInterceptor extends AbstractCacheInterceptor {
    public CacheableInterceptor(BeanContainer container, KeyGenerator keyGenerator, List<String> cacheNames) {
        super(container, keyGenerator, cacheNames);
    }

    @Nullable
    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable {
        MethodInvocation invocation = methodJoinPoint.getProxiedInvocation();
        CacheKey key = this.getKeyGenerator()
                .generate(invocation.getTarget(), invocation.getMethod(), invocation.getArguments());
        for (Cache instance : this.getCacheInstances()) {
            if (instance.contains(key)) {
                return instance.get(key);
            }
        }
        Object result = methodJoinPoint.proceed();
        this.getCacheInstances().forEach(instance -> instance.put(key, result));
        return result;
    }
}
