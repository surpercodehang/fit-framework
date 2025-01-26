/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge.map;

import modelengine.fitframework.merge.Conflict;
import modelengine.fitframework.merge.map.support.DefaultMapMerger;
import modelengine.fitframework.pattern.builder.BuilderFactory;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 处理 {@link java.util.Map} 中值的冲突的冲突上下文。
 *
 * @param <K> 表示键值对中冲突点的键的类型的 {@link K}。
 * @param <V> 表示键值对中冲突点的值的类型的 {@link V}。
 * @author 季聿阶
 * @since 2022-07-31
 */
public interface MapConflict<K, V> extends Conflict<K> {
    /**
     * 获取键值对的合并器。
     *
     * @return 表示键值对的合并器的 {@link DefaultMapMerger}{@code <}{@link K}{@code , }{@link V}{@code >}。
     */
    DefaultMapMerger<K, V> merger();

    /**
     * {@link MapConflict} 的构建器。
     *
     * @param <K> 表示键值对中冲突点的键的类型的 {@link K}。
     * @param <V> 表示键值对中冲突点的值的类型的 {@link V}。
     */
    interface Builder<K, V> {
        /**
         * 向当前构建器中设置冲突的键。
         *
         * @param key 表示冲突的键的 {@link K}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link K}{@code , }{@link V}{@code >}。
         */
        Builder<K, V> key(K key);

        /**
         * 向当前构建器中设置键值对的合并器。
         *
         * @param merger 表示键值对的合并器的 {@link DefaultMapMerger}{@code <}{@link K}{@code , }{@link V}{@code >}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link K}{@code , }{@link V}{@code >}。
         */
        Builder<K, V> merger(DefaultMapMerger<K, V> merger);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link MapConflict}{@code <}{@link K}{@code , }{@link V}{@code >}。
         */
        MapConflict<K, V> build();
    }

    /**
     * 获取 {@link MapConflict} 的构建器。
     *
     * @return 表示 {@link MapConflict} 的构建器的 {@link Builder}{@code <}{@link K}{@code , }{@link
     * V}{@code >}。
     */
    static <K, V> Builder<K, V> builder() {
        return builder(null);
    }

    /**
     * 获取 {@link MapConflict} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link MapConflict}。
     * @return 表示 {@link MapConflict} 的构建器的 {@link Builder}{@code <}{@link K}{@code , }{@link
     * V}{@code >}。
     */
    static <K, V> Builder<K, V> builder(MapConflict<K, V> value) {
        return ObjectUtils.cast(BuilderFactory.get(MapConflict.class, Builder.class).create(value));
    }
}
