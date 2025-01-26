/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.util.CollectionUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * {@link MultiValueMap} 的抽象实现。
 *
 * @param <K> 表示键的类型的 {@link K}。
 * @param <V> 表示值的类型的 {@link V}。
 * @author 季聿阶
 * @since 2022-08-05
 */
public class DefaultMultiValueMap<K, V> extends AbstractMap<K, List<V>> implements MultiValueMap<K, V> {
    private final Map<K, List<V>> innerMap;

    /**
     * 直接实例化 {@link DefaultMultiValueMap}。
     * <p>使用 {@link HashMap} 作为映射提供者。</p>
     */
    public DefaultMultiValueMap() {
        this((Map<K, List<V>>) null);
    }

    /**
     * 通过一个键与一系列值的映射提供者来实例化 {@link DefaultMultiValueMap}。
     * <p>当 {@code mapSupplier} 为 {@code null} 时，使用 {@link HashMap} 作为默认映射提供者。</p>
     *
     * @param mapSupplier 表示一个键与一系列值的映射提供者的 {@link Supplier}{@code <}{@link Map}{@code <}{@link K}{@code
     * , ?>>}。
     */
    public DefaultMultiValueMap(Supplier<Map<K, ?>> mapSupplier) {
        this.innerMap = cast(Optional.ofNullable(mapSupplier).map(Supplier::get).orElseGet(HashMap::new));
    }

    /**
     * 通过一个键与一系列值的映射来实例化 {@link DefaultMultiValueMap}。
     * <p>当 {@code map} 为 {@code null} 时，使用 {@link HashMap} 作为默认映射提供者。</p>
     *
     * @param map 表示一个键与一系列值的映射提供者的 {@link Map}{@code <}{@link K}{@code , }{@link List}{@code
     * <}{@link V}{@code >>}。
     */
    public DefaultMultiValueMap(Map<K, List<V>> map) {
        this.innerMap = Optional.ofNullable(map).orElseGet(HashMap::new);
    }

    @Nullable
    @Override
    public V getFirst(@Nullable K key) {
        List<V> values = this.innerMap.get(key);
        return CollectionUtils.isNotEmpty(values) ? values.get(0) : null;
    }

    @Override
    public void add(@Nullable K key, @Nullable V value) {
        List<V> currentValues = this.innerMap.computeIfAbsent(key, newKey -> new ArrayList<>(1));
        currentValues.add(value);
    }

    @Override
    public void addAll(@Nullable K key, @Nullable List<? extends V> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        List<V> currentValues = this.innerMap.computeIfAbsent(key, newKey -> new ArrayList<>(values.size()));
        currentValues.addAll(values);
    }

    @Override
    public void set(@Nullable K key, @Nullable V value) {
        List<V> values = new ArrayList<>(1);
        values.add(value);
        this.innerMap.put(key, values);
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return this.innerMap.entrySet();
    }

    @Override
    public List<V> put(K key, List<V> value) {
        return this.innerMap.put(key, value);
    }
}
