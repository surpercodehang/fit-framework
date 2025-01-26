/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge.map.support;

import modelengine.fitframework.merge.ConflictException;
import modelengine.fitframework.merge.ConflictResolver;
import modelengine.fitframework.merge.ConflictResolverCollection;
import modelengine.fitframework.merge.map.MapConflict;
import modelengine.fitframework.merge.map.MapMerger;
import modelengine.fitframework.merge.support.AbstractMerger;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 处理 {@link Map} 的合并器。
 *
 * @param <K> 表示 {@link Map} 中的键的类型的 {@link K}。
 * @param <V> 表示 {@link Map} 中的值的类型的 {@link V}。
 * @author 季聿阶
 * @since 2022-07-30
 */
public class DefaultMapMerger<K, V> extends AbstractMerger<Map<K, V>> implements MapMerger<K, V> {
    private final Deque<Object> location;

    /**
     * 通过冲突处理器的集合来实例化 {@link DefaultMapMerger}。
     *
     * @param conflictResolvers 表示冲突处理器的集合的 {@link ConflictResolverCollection}。
     */
    public DefaultMapMerger(ConflictResolverCollection conflictResolvers) {
        super(conflictResolvers);
        this.location = new LinkedList<>();
    }

    @Override
    public List<Object> location() {
        return Collections.unmodifiableList(ObjectUtils.cast(this.location));
    }

    @Override
    public Map<K, V> merge(Map<K, V> m1, Map<K, V> m2) {
        boolean isM1Empty = MapUtils.isEmpty(m1);
        boolean isM2Empty = MapUtils.isEmpty(m2);
        if (isM1Empty && isM2Empty) {
            return Collections.emptyMap();
        } else if (isM1Empty) {
            return m2;
        } else if (isM2Empty) {
            return m1;
        } else {
            return this.mergeNonEmptyMaps(m1, m2);
        }
    }

    private Map<K, V> mergeNonEmptyMaps(Map<K, V> m1, Map<K, V> m2) {
        Map<K, V> mergedMap = new HashMap<>();
        Set<K> keys = new HashSet<>();
        keys.addAll(m1.keySet());
        keys.addAll(m2.keySet());
        for (K key : keys) {
            V v1 = m1.get(key);
            V v2 = m2.get(key);
            if (v1 == null && v2 == null) {
                continue;
            }
            if (v1 == null) {
                mergedMap.put(key, v2);
            } else if (v2 == null) {
                mergedMap.put(key, v1);
            } else {
                mergedMap.put(key, this.resolveConflictValues(key, v1, v2));
            }
        }
        return mergedMap;
    }

    private V resolveConflictValues(K key, V v1, V v2) {
        Class<V> c1 = ObjectUtils.cast(v1.getClass());
        Class<V> c2 = ObjectUtils.cast(v2.getClass());
        if (c1 != c2) {
            throw new ConflictException(StringUtils.format(
                    "Conflict in merge map process: the class of conflict values is not the same. "
                            + "[key={0}, v1Class={1}, v2Class={2}]", this.key(), c1, c2));
        }
        this.location.addLast(key);
        ConflictResolver.Result<V> ret;
        try {
            ret = this.conflictResolvers()
                    .get(c1)
                    .resolve(v1, v2, MapConflict.builder().key(key).merger(ObjectUtils.cast(this)).build());
            if (!ret.resolved()) {
                if (ret.cause() != null) {
                    throw ret.cause();
                }
                throw new ConflictException(
                        StringUtils.format("Conflict in merge map process. [key={0}, v1={1}, v2={2}]", this.key(), v1,
                                v2));
            }
        } finally {
            this.location.removeLast();
        }
        return ret.result();
    }

    private String key() {
        return this.location().stream().map(Object::toString).collect(Collectors.joining("."));
    }
}
