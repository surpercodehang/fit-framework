/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Validation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 为 {@link Map} 提供构建器。
 *
 * @param <K> 表示键的类型。
 * @param <V> 表示值的类型。
 * @author 梁济时
 * @since 1.0
 */
public class MapBuilder<K, V> {
    private final Map<K, V> map;

    /**
     * 初始化 {@link MapBuilder} 类的新实例。
     */
    public MapBuilder() {
        this.map = new HashMap<>();
    }

    /**
     * 初始化 {@link MapBuilder} 类的新实例。
     *
     * @param supplier 表示用于提供指定映射类型的 {@link Supplier}{@code <}{@link Map}{@code <K, V>>}。
     */
    public MapBuilder(Supplier<Map<K, V>> supplier) {
        Validation.notNull(supplier, "The supplier to get map cannot be null.");
        this.map = supplier.get();
    }

    /**
     * 向构建器中增加一个键值对。
     *
     * @param key 表示新元素的键的 {@link Object}。
     * @param value 表示新元素的值的 {@link Object}。
     * @return 表示当前的构建器的 {@link MapBuilder}。
     */
    public MapBuilder<K, V> put(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    /**
     * 清空构建器中的所有值。
     *
     * @return 表示当前的构建器的 {@link MapBuilder}。
     */
    public MapBuilder<K, V> clear() {
        this.map.clear();
        return this;
    }

    /**
     * 使用构建器中包含的元素构建一个映射的新实例。
     *
     * @return 表示新构建的映射的实例的 {@link Map}。
     */
    public Map<K, V> build() {
        return this.build(null);
    }

    /**
     * 使用构建器中包含的元素构建一个指定映射的新实例。
     *
     * @param factory 表示用以实例化映射实例的工厂的 {@link Supplier}。若未提供，则将使用 {@link HashMap} 作为默认映射类型。
     * @return 表示新构建的映射的实例的 {@link Map}。
     */
    public Map<K, V> build(Supplier<Map<K, V>> factory) {
        Supplier<Map<K, V>> actualFactory = ObjectUtils.nullIf(factory, HashMap::new);
        Map<K, V> result = actualFactory.get();
        result.putAll(this.map);
        return result;
    }

    /**
     * 获取一个映射构建器的新实例。
     *
     * @return 表示映射构建器的实例的 {@link MapBuilder}。
     */
    public static <K, V> MapBuilder<K, V> get() {
        return new MapBuilder<>();
    }

    /**
     * 获取一个映射构建器的新实例。
     *
     * @param supplier 表示用于提供指定映射类型的 {@link Supplier}{@code <}{@link Map}{@code <K, V>>}。
     * @return 表示映射构建器的实例的 {@link MapBuilder}。
     */
    public static <K, V> MapBuilder<K, V> get(Supplier<Map<K, V>> supplier) {
        return new MapBuilder<>(supplier);
    }
}
