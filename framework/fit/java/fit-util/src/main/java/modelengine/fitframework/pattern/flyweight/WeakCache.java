/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.flyweight;

import modelengine.fitframework.pattern.flyweight.support.DefaultWeakCache;

import java.util.function.Function;

/**
 * 为享元模式提供物件的弱引用缓存。
 * <p>可通过 {@link WeakCache#create(Function, Function)} 方法创建若引用缓存实例。</p>
 * <p>所提供的 {@link Function} 用以为指定键创建缓存对象实例，该缓存对象可通过索引被检索。</p>
 * <p><b>所被缓存的实例应包含其键的强引用，以避免在缓存对象存在期间，其键被回收。</b></p>
 *
 * @author 梁济时
 * @since 2023-02-09
 */
public interface WeakCache<K, V> {
    /**
     * 获取指定键对应的物件实例。
     *
     * @param key 表示物件的键的 {@link Object}。
     * @return 表示物件实例的 {@link Object}。
     */
    V get(K key);

    /**
     * 使用用以创建物件的方法创建缓存的新实例。
     *
     * @param factory 表示用以通过键创建缓存对象的方法的 {@link Function}。
     * @param indexer 表示用以通过缓存对象实例获取用以检索的键的方法的 {@link Function}。
     * @param <K> 表示物件的键的类型。
     * @param <F> 表示物件的类型。
     * @return 表示新创建的物件缓存的 {@link WeakCache}。
     * @throws IllegalArgumentException {@code factory} 或 {@code indexer} 为 {@code null}。
     */
    static <K, F, V extends F> WeakCache<K, F> create(Function<K, V> factory, Function<V, K> indexer) {
        return new DefaultWeakCache<>(factory, indexer);
    }
}
