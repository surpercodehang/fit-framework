/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.cache.instance;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.cache.Cache;
import modelengine.fitframework.util.StringUtils;

import java.util.Optional;

/**
 * 表示 {@link Cache} 的抽象实现。
 *
 * @author 季聿阶
 * @since 2022-12-15
 */
public abstract class AbstractCache implements Cache {
    private final String name;
    private final boolean allowsNullValue;

    protected AbstractCache(String name, boolean allowsNullValue) {
        this.name = notBlank(name, "The cache instance name cannot be blank.");
        this.allowsNullValue = allowsNullValue;
    }

    @Override
    public String name() {
        return this.name;
    }

    /**
     * 判断当前缓存实例是否允许 {@code null} 值。
     *
     * @return 如果允许，则返回 {@code true}，否则，返回 {@code false}。
     */
    protected boolean allowsNullValue() {
        return this.allowsNullValue;
    }

    @Override
    public Object get(Object key) {
        notNull(key, "The cache key cannot be null.");
        return this.fromStoreValue(this.load(key));
    }

    @Override
    public void put(Object key, Object value) {
        notNull(key, "The cache key cannot be null.");
        if (!this.allowsNullValue() && value == null) {
            throw new IllegalStateException(StringUtils.format(
                    "Cache instance is not allowed to store null value. [instance={0}, key={1}]",
                    this.name(),
                    key));
        }
        this.store(key, this.toStoreValue(value));
    }

    private Object toStoreValue(Object value) {
        return value == null ? Optional.empty() : value;
    }

    private Object fromStoreValue(Object storeValue) {
        return storeValue == Optional.empty() ? null : storeValue;
    }

    /**
     * 向当前缓存实例中存储指定的键值对。
     *
     * @param key 表示待存储键值对的键的 {@link Object}。
     * @param value 表示待存储键值对的值的 {@link Object}。
     */
    protected abstract void store(Object key, Object value);

    /**
     * 从当前缓存实例中获取指定键的值。
     *
     * @param key 表示指定键的 {@link Object}。
     * @return 表示从当前缓存实例中获取到的指定键的值的 {@link Object}。
     */
    protected abstract Object load(Object key);
}
