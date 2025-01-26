/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache;

import modelengine.fitframework.aop.interceptor.cache.support.DefaultCacheKey;
import modelengine.fitframework.inspection.Nullable;

import java.lang.reflect.Method;

/**
 * 表示缓存的键。
 * <p><b>注意：所有该接口的实现，必须实现合适的 {@link #equals(Object)} 和 {@link #hashCode()} 方法。</b></p>
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
public interface CacheKey {
    /**
     * 获取形成缓存键的目标对象。
     *
     * @return 表示形成缓存键的目标对象的 {@link Object}。
     */
    @Nullable
    Object target();

    /**
     * 获取形成缓存键的目标方法。
     *
     * @return 表示形成缓存键的目标方法的 {@link Method}。
     */
    @Nullable
    Method method();

    /**
     * 获取形成缓存键的目标方法的参数列表。
     *
     * @return 表示形成缓存键的目标方法的参数列表的 {@link Object}{@code []}。
     */
    Object[] params();

    /**
     * 获取一个空的缓存键。
     *
     * @return 表示空的缓存键的 {@link CacheKey}。
     */
    static CacheKey empty() {
        return DefaultCacheKey.EMPTY;
    }

    /**
     * 将若干个键合并为一个整体的缓存键。
     *
     * @param keys 表示若干键的信息的 {@link Object}{@code []}。
     * @return 表示将若干个键合并为一个整体的缓存键的 {@link CacheKey}。
     */
    static CacheKey combine(Object... keys) {
        return new DefaultCacheKey(keys);
    }

    /**
     * 将目标对象、目标方法以及若干键合并为一个整体的缓存键。
     *
     * @param target 表示目标对象的 {@link Object}。
     * @param method 表示目标方法的 {@link Method}。
     * @param keys 表示若干键的信息的 {@link Object}{@code []}。
     * @return 表示将目标对象、目标方法以及若干键合并为一个整体的缓存键的 {@link CacheKey}。
     */
    static CacheKey all(Object target, Method method, Object... keys) {
        return new DefaultCacheKey(target, method, keys);
    }
}
