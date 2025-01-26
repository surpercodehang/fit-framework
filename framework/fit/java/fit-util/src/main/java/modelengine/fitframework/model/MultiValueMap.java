/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.model.support.DefaultMultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 表示一个键可以对应多个值的 {@link Map}。
 *
 * @param <K> 表示键的类型的 {@link K}。
 * @param <V> 表示值的类型的 {@link V}。
 * @author 季聿阶
 * @since 2022-08-05
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    /**
     * 获取指定键的第一个值。
     *
     * @param key 表示指定键的 {@link K}。
     * @return 表示指定键的第一个值的 {@link V}。
     */
    @Nullable
    V getFirst(K key);

    /**
     * 添加一个值到一个指定键中。
     *
     * @param key 表示待添加的键的 {@link K}。
     * @param value 表示待添加的值的 {@link V}。
     */
    void add(@Nullable K key, @Nullable V value);

    /**
     * 添加一系列值到一个指定键中。
     *
     * @param key 表示待添加的键的 {@link K}。
     * @param values 表示待添加的一系列值的 {@link List}{@code <? extends }{@link V}{@code >}。
     */
    void addAll(@Nullable K key, @Nullable List<? extends V> values);

    /**
     * 设置一个指定键为指定的单一值。
     *
     * @param key 表示待设置的键的 {@link K}。
     * @param value 表示待设置的值的 {@link V}。
     */
    void set(@Nullable K key, @Nullable V value);

    /**
     * 由一个键与一系列值的映射提供者来创建一个多值映射。
     * <p><b>注意：传参时无法提供编译提示，需要确认映射中的值的类型为 {@link List}。</b></p>
     *
     * @param mapSupplier 表示一个键与一系列值的映射提供者的 {@link Supplier}{@code <}{@link Map}{@code <}{@link K}{@code
     * , ?>>}。
     * @param <K> 表示多值映射的键的类型的 {@link K}。
     * @param <V> 表示多值映射的值的类型的 {@link V}。
     * @return 表示创建的多值映射的 {@link MultiValueMap}{@code <}{@link K}{@code , }{@link V}{@code >}。
     */
    static <K, V> MultiValueMap<K, V> create(Supplier<Map<K, ?>> mapSupplier) {
        return new DefaultMultiValueMap<>(mapSupplier);
    }

    /**
     * 由一个键与一系列值的映射来创建一个多值映射。
     *
     * @param map 表示一个键与一系列值的映射提供者的 {@link Map}{@code <}{@link K}{@code , }{@link List}{@code
     * <}{@link V}{@code >>}。
     * @param <K> 表示多值映射的键的类型的 {@link K}。
     * @param <V> 表示多值映射的值的类型的 {@link V}。
     * @return 表示创建的多值映射的 {@link MultiValueMap}{@code <}{@link K}{@code , }{@link V}{@code >}。
     */
    static <K, V> MultiValueMap<K, V> create(Map<K, List<V>> map) {
        return new DefaultMultiValueMap<>(map);
    }

    /**
     * 创建一个默认的多值映射。
     *
     * @param <K> 表示多值映射的键的类型的 {@link K}。
     * @param <V> 表示多值映射的值的类型的 {@link V}。
     * @return 表示创建的多值映射的 {@link MultiValueMap}{@code <}{@link K}{@code , }{@link V}{@code >}。
     */
    static <K, V> MultiValueMap<K, V> create() {
        return new DefaultMultiValueMap<>();
    }
}
