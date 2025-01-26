/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache;

import modelengine.fitframework.aop.interceptor.cache.support.DefaultKeyGenerator;
import modelengine.fitframework.aop.interceptor.cache.support.SpecifiedParamKeyGenerator;
import modelengine.fitframework.inspection.Nonnull;

import java.lang.reflect.Method;

/**
 * 表示缓存键的生成器。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
@FunctionalInterface
public interface KeyGenerator {
    /**
     * 根据调用对象、调用方法以及调用参数生成缓存键。
     *
     * @param target 表示调用对象的 {@link Object}。
     * @param method 表示调用方法的 {@link Method}。
     * @param params 表示调用参数的 {@link Object}{@code []}。
     * @return 表示生成的缓存键的 {@link CacheKey}。
     */
    CacheKey generate(Object target, @Nonnull Method method, @Nonnull Object... params);

    /**
     * 获取空的缓存键生成器。
     *
     * @return 表示空的缓存键生成器的 {@link KeyGenerator}。
     */
    static KeyGenerator empty() {
        return DefaultKeyGenerator.EMPTY;
    }

    /**
     * 获取仅使用方法参数来生成缓存键的生成器。
     *
     * @return 表示仅使用方法参数来生成缓存键的生成器的 {@link KeyGenerator}。
     */
    static KeyGenerator params() {
        return new DefaultKeyGenerator();
    }

    /**
     * 获取指定序号参数的缓存键生成器。
     *
     * @param index 表示指定参数序号的 {@code int}。
     * @return 表示指定序号参数的缓存键生成器的 {@link KeyGenerator}。
     * @throws IllegalArgumentException 当 {@code index} 小于 {@code 0} 时。
     */
    static KeyGenerator specified(int index) {
        return new SpecifiedParamKeyGenerator(index);
    }

    /**
     * 获取方法中指定名字的参数的缓存键生成器。
     *
     * @param keyPattern 表示方法参数名字的样式的 {@link String}。
     * <p>{@code keyPattern} 必须以 {@code '#'} 开头，后续为参数名。</p>
     * <p><b>注意：该特性需要打开编译参数，即 {@code "-parameters"}。</b></p>
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示方法中指定名字的参数的缓存键生成器的 {@link KeyGenerator}。
     * @throws IllegalArgumentException 当 {@code keyPattern} 为 {@code null} 或空白字符串时。
     * @throws IllegalArgumentException 当 {@code keyPattern} 没有以 {@code '#'} 开头时。
     * @throws IllegalArgumentException 当在指定方法中没有找到指定名字的参数时。
     */
    static KeyGenerator specified(String keyPattern, Method method) {
        return new SpecifiedParamKeyGenerator(keyPattern, method);
    }

    /**
     * 获取指定常量作为缓存键的生成器。
     *
     * @param key 表示指定常量的 {@link String}。
     * @return 表示指定常量作为缓存键的生成器的 {@link KeyGenerator}。
     */
    static KeyGenerator constant(String key) {
        return (target, method, params) -> CacheKey.combine(key);
    }
}
