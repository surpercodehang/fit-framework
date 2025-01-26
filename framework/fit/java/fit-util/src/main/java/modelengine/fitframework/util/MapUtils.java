/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.merge.ConflictResolutionPolicy;
import modelengine.fitframework.merge.ConflictResolver;
import modelengine.fitframework.merge.ConflictResolverCollection;
import modelengine.fitframework.merge.map.MapConflict;
import modelengine.fitframework.merge.map.MapConflictResolver;
import modelengine.fitframework.merge.map.MapMerger;
import modelengine.fitframework.merge.map.support.DefaultMapMerger;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 为映射提供工具方法。
 *
 * @author 季聿阶
 * @since 2020-09-17
 */
public class MapUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private MapUtils() {}

    /**
     * 返回指定映射的大小。
     *
     * @param map 表示待检查的映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param <K> 表示映射中键的类型的 {@link K}。
     * @param <V> 表示映射中值的类型的 {@link V}。
     * @return 若映射为 {@code null} 或是一个空映射，则为 {@code 0}；否则为映射实际的大小的 {@code int}。
     */
    public static <K, V> int count(Map<K, V> map) {
        return map == null ? 0 : map.size();
    }

    /**
     * 使用指定连接符对指定映射类型的键值进行扁平化处理。
     *
     * @param map 表示需要扁平化的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param connector 表示连接符的 {@code char}。
     * @return 表示扁平化处理后的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public static Map<String, String> flat(Map<String, Object> map, char connector) {
        return flat(map, Character.toString(connector));
    }

    /**
     * 使用指定连接符对指定映射类型的键值进行扁平化处理。
     * <p>例如：
     * <pre>
     * 输入映射类型为：
     * {
     *      k1 : {
     *          k2 : {
     *              k4 : v4,
     *              k5 : v5
     *          },
     *          k3: {
     *              k6 : v6
     *          }
     *      }
     * }
     * </pre>
     * 输入的连接符为 '.', 则输出的映射类型为:
     * <pre>
     * 输入 map 为：
     * {
     *      k1.k2.k4 : v4,
     *      k1.k2.k5 : v5,
     *      k1.k3.k6 : v6
     * }
     * </pre>
     *
     * @param map 表示需要扁平化的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param connector 表示连接符的 {@link String}。
     * @return 表示扁平化处理后的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public static Map<String, String> flat(Map<String, Object> map, String connector) {
        Map<String, String> flattenedMap = new HashMap<>();
        if (map != null) {
            flat(map, flattenedMap, new LinkedList<>(), connector);
        }
        return flattenedMap;
    }

    private static void flat(Map<String, Object> map, Map<String, String> flattenedMap, Deque<String> keys,
            String connector) {
        for (Map.Entry<String, Object> e : map.entrySet()) {
            keys.addLast(e.getKey());
            Object value = e.getValue();
            if (value instanceof Map) {
                Map<String, Object> nextMap = ObjectUtils.<Map<?, ?>>cast(value)
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
                flat(nextMap, flattenedMap, keys, connector);
            } else {
                flattenedMap.put(String.join(connector, keys), String.valueOf(value));
            }
            keys.removeLast();
        }
    }

    /**
     * 当指定映射为 {@code null} 或是一个空映射时，使用指定的 {@link Supplier}{@code <}{@link Map}{@code <}{@link K}{@code ,
     * }{@link V}{@code >>} 获取映射，否则继续使用指定映射。
     *
     * @param map 表示指定映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param factory 表示默认映射提供器的 {@link Supplier}{@code <}{@link Map}{@code <}{@link K}{@code , }{@link V}{@code
     * >>}。
     * @param <K> 表示映射中键的类型的 {@link K}。
     * @param <V> 表示映射中值的类型的 {@link V}。
     * @return 若映射为 {@code null} 或是一个空映射，则为 {@link Supplier#get()}；否则为 {@code map}。
     */
    public static <K, V> Map<K, V> getIfEmpty(Map<K, V> map, Supplier<Map<K, V>> factory) {
        Validation.notNull(factory, "The factory to create default map cannot be null.");
        return isEmpty(map) ? factory.get() : map;
    }

    /**
     * 检查指定映射是否为 {@code null} 或是一个空映射。
     *
     * @param map 表示待检查的映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param <K> 表示映射中键的类型的 {@link K}。
     * @param <V> 表示映射中值的类型的 {@link V}。
     * @return 若映射为 {@code null} 或是一个空映射，则为 {@code true}；否则为 {@code false}。
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 检查指定映射是否不为 {@code null} 或不是一个空映射。
     *
     * @param map 表示待检查的映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param <K> 表示映射中键的类型的 {@link K}。
     * @param <V> 表示映射中值的类型的 {@link V}。
     * @return 若映射为 {@code null} 或是一个空映射，则为 {@code false}；否则为 {@code true}。
     */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    /**
     * 将两个映射合并成一个新的映射。
     * <p>冲突解决策略是 {@link ConflictResolutionPolicy#ABORT}。</p>
     *
     * @param first 表示待合并的第一个映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param second 表示待合并的第二个映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param <K> 表示待合并映射的键的类型的 {@link K}。
     * @param <V> 表示待合并映射中值的类型的 {@link V}。
     * @return 表示合并后的映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @throws modelengine.fitframework.merge.ConflictException 当合并过程中发生异常时。
     */
    public static <K, V> Map<K, V> merge(Map<K, V> first, Map<K, V> second) {
        return merge(first, second, ConflictResolutionPolicy.ABORT);
    }

    /**
     * 将两个映射按照指定的冲突解决策略合并成一个新的映射。
     *
     * @param first 表示待合并的第一个映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param second 表示待合并的第二个映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param defaultPolicy 表示指定的冲突解决策略的 {@link ConflictResolutionPolicy}。
     * @param <K> 表示待合并映射的键的类型的 {@link K}。
     * @param <V> 表示待合并映射中值的类型的 {@link V}。
     * @return 表示合并后的映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @throws modelengine.fitframework.merge.ConflictException 当合并过程中发生异常时。
     */
    public static <K, V> Map<K, V> merge(Map<K, V> first, Map<K, V> second, ConflictResolutionPolicy defaultPolicy) {
        ConflictResolver<K, V, MapConflict<K, V>> defaultResolver = ConflictResolver.resolver(defaultPolicy);
        ConflictResolver<K, V, MapConflict<K, V>> resolver = ObjectUtils.cast(new MapConflictResolver<>());
        ConflictResolverCollection conflictResolvers = ConflictResolverCollection.create();
        conflictResolvers.add(ObjectUtils.cast(Map.class), resolver);
        conflictResolvers.add(ObjectUtils.cast(defaultResolver));
        return merge(first, second, conflictResolvers);
    }

    /**
     * 将两个映射按照指定的冲突处理器的集合合并成一个新的映射。
     *
     * @param first 表示待合并的第一个映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param second 表示待合并的第二个映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @param conflictResolvers 表示指定的冲突处理器集合的 {@link ConflictResolverCollection}。
     * @param <K> 表示待合并映射的键的类型的 {@link K}。
     * @param <V> 表示待合并映射中值的类型的 {@link V}。
     * @return 表示合并后的映射的 {@link Map}{@code <}{@link K}{@code , }{@link V}{@code >}。
     * @throws modelengine.fitframework.merge.ConflictException 当合并过程中发生异常时。
     */
    public static <K, V> Map<K, V> merge(Map<K, V> first, Map<K, V> second,
            ConflictResolverCollection conflictResolvers) {
        MapMerger<K, V> merger = new DefaultMapMerger<>(conflictResolvers);
        return merger.merge(first, second);
    }
}
