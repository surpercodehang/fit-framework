/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.pattern.flyweight;

import modelengine.fitframework.pattern.flyweight.support.DefaultWeakCache;

import java.util.function.Function;

/**
 * 为享元模式提供弱引用缓存。
 * <p>可通过 {@link WeakCache#create(Function, Function)} 方法创建若引用缓存实例。</p>
 * <p>所提供的 {@link Function} 用以为指定键创建缓存对象实例，该缓存对象可通过索引被检索。</p>
 * <p><b>所被缓存的实例应包含其键的强引用，以避免在缓存对象存在期间，其键被回收。</b></p>
 *
 * @param <K> 表示缓存的键的类型的 {@link K}。
 * @param <V> 表示缓存的值的类型的 {@link V}。
 * @author 梁济时
 * @since 2023-02-09
 */
public interface WeakCache<K, V> {
    /**
     * 获取指定键对应的实例。
     *
     * @param key 表示缓存键的 {@link K}。
     * @return 表示缓存实例的 {@link V}。
     */
    V get(K key);

    /**
     * 使用指定的工厂方法和索引方法来创建缓存的新实例。
     *
     * @param factory 表示用以创建缓存对象的工厂方法的 {@link Function}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param indexer 表示用以获取检索键的方法的 {@link Function}{@code <}{@link V}{@code , }{@link K}{@code >}。
     * @param <K> 表示缓存的键的类型的 {@link K}。
     * @param <F> 表示缓存的值的类型的 {@link F}。
     * @param <V> 表示缓存的具体实现类型，该类型必须是 {@code F} 的子类型。
     * @return 表示新创建的缓存的 {@link WeakCache}{@code <}{@link K}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code factory} 或 {@code indexer} 为 {@code null} 时。
     */
    static <K, F, V extends F> WeakCache<K, F> create(Function<K, V> factory, Function<V, K> indexer) {
        return new DefaultWeakCache<>(factory, indexer);
    }
}
