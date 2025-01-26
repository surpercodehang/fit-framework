/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.support.AbstractMethodInterceptor;
import modelengine.fitframework.cache.Cache;
import modelengine.fitframework.cache.CacheManager;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示缓存方法拦截器的抽象父类。
 *
 * @author 季聿阶
 * @since 2023-04-07
 */
public abstract class AbstractCacheInterceptor extends AbstractMethodInterceptor {
    private final KeyGenerator keyGenerator;
    private final LazyLoader<List<Cache>> instancesLoader;

    AbstractCacheInterceptor(BeanContainer container, KeyGenerator keyGenerator, List<String> cacheNames) {
        notNull(container, "The bean container cannot be null.");
        this.keyGenerator = ObjectUtils.getIfNull(keyGenerator, KeyGenerator::params);
        List<String> actualCacheNames = ObjectUtils.getIfNull(cacheNames, ArrayList::new);
        this.instancesLoader = new LazyLoader<>(() -> container.factory(CacheManager.class)
                .map(BeanFactory::<CacheManager>get)
                .map(manager -> actualCacheNames.stream()
                        .map(manager::getInstance)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList));
    }

    /**
     * 获取键的生成器。
     *
     * @return 表示键的生成器的 {@link KeyGenerator}。
     */
    protected KeyGenerator getKeyGenerator() {
        return this.keyGenerator;
    }

    /**
     * 获取缓存实例列表。
     *
     * @return 表示缓存实例列表的 {@link List}{@code <}{@link Cache}{@code >}。
     */
    protected List<Cache> getCacheInstances() {
        return this.instancesLoader.get();
    }
}
