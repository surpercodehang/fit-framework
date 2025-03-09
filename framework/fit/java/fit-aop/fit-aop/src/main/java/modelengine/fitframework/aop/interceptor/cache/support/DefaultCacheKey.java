/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.aop.interceptor.cache.support;

import modelengine.fitframework.aop.interceptor.cache.CacheKey;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示 {@link CacheKey} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
public class DefaultCacheKey implements CacheKey {
    /** 表示空的缓存键的单例。 */
    public static final CacheKey EMPTY = new DefaultCacheKey();

    private final Object target;
    private final Method method;
    private final Object[] keys;
    private final int hashcode;

    /**
     * 使用指定的键列表初始化 {@link DefaultCacheKey} 的新实例。
     *
     * @param keys 表示键列表的 {@link Object}{@code []}。
     */
    public DefaultCacheKey(Object... keys) {
        this(null, null, keys);
    }

    /**
     * 使用指定的目标对象、目标方法和键列表初始化 {@link DefaultCacheKey} 的新实例。
     *
     * @param target 表示目标对象的 {@link Object}。
     * @param method 表示目标方法的 {@link Method}。
     * @param keys 表示键列表的 {@link Object}{@code []}。
     */
    public DefaultCacheKey(Object target, Method method, Object... keys) {
        this.target = target;
        this.method = method;
        this.keys = ObjectUtils.getIfNull(keys, () -> new Object[0]);
        this.hashcode = Objects.hash(this.target, this.method, Arrays.deepHashCode(this.keys));
    }

    @Nullable
    @Override
    public Object target() {
        return this.target;
    }

    @Nullable
    @Override
    public Method method() {
        return this.method;
    }

    @Override
    public Object[] params() {
        return this.keys;
    }

    private Object mergeKeys() {
        return this.keys;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (!(another instanceof CacheKey)) {
            return false;
        }
        CacheKey that = ObjectUtils.cast(another);
        return Objects.equals(this.target(), that.target()) && Objects.equals(this.method(), that.target())
                && Arrays.deepEquals(this.params(), that.params());
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }
}
