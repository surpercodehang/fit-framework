/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.cache.support.CacheableInterceptorFactory;
import modelengine.fitframework.aop.interceptor.cache.support.EvictCacheInterceptorFactory;
import modelengine.fitframework.aop.interceptor.cache.support.PutCacheInterceptorFactory;
import modelengine.fitframework.aop.interceptor.support.AbstractMethodInterceptorResolver;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 解析缓存拦截事件提供者的解析器。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
public class CacheInterceptorResolver extends AbstractMethodInterceptorResolver {
    private final List<CacheInterceptorFactory> methodInterceptorFactories;

    public CacheInterceptorResolver(BeanContainer container) {
        this.methodInterceptorFactories = Arrays.asList(new CacheableInterceptorFactory(container),
                new PutCacheInterceptorFactory(container),
                new EvictCacheInterceptorFactory(container));
    }

    @Override
    public boolean eliminate(BeanMetadata metadata) {
        return false;
    }

    @Override
    public List<MethodInterceptor> resolve(BeanMetadata beanMetadata, Object bean) {
        List<MethodInterceptor> methodInterceptors = this.resolveMethodInterceptors(beanMetadata, bean);
        return this.matchPointcuts(methodInterceptors, bean);
    }

    private List<MethodInterceptor> resolveMethodInterceptors(BeanMetadata beanMetadata, Object bean) {
        List<MethodInterceptor> methodInterceptors = new ArrayList<>();
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            this.methodInterceptorFactories.stream()
                    .filter(factory -> factory.isInterceptMethod(method))
                    .findFirst()
                    .map(factory -> factory.create(this.getBeanFactory(beanMetadata), method))
                    .ifPresent(methodInterceptors::add);
        }
        return methodInterceptors;
    }

    private BeanFactory getBeanFactory(BeanMetadata beanMetadata) {
        return beanMetadata.container().factory(beanMetadata.type()).orElse(null);
    }
}
