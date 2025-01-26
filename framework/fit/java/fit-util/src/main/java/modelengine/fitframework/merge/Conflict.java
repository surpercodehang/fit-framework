/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge;

import modelengine.fitframework.pattern.builder.BuilderFactory;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示冲突上下文。
 *
 * @param <K> 表示冲突键的类型的 {@link K}。
 * @author 季聿阶
 * @since 2022-07-30
 */
public interface Conflict<K> {
    /**
     * 获取冲突的键。
     *
     * @return 表示冲突键的 {@link K}。
     */
    K key();

    /**
     * {@link Conflict} 的构建器。
     *
     * @param <K> 表示冲突键的类型的 {@link K}。
     */
    interface Builder<K> {
        /**
         * 向当前构建器中设置冲突的键。
         *
         * @param key 表示待设置的冲突键的 {@link K}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link K}{@code >}。
         */
        Builder<K> key(K key);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Conflict}{@code <}{@link K}{@code >}。
         */
        Conflict<K> build();
    }

    /**
     * 获取 {@link Conflict} 的构建器。
     *
     * @param <K> 表示冲突键的类型的 {@link K}。
     * @return 表示 {@link Conflict} 的构建器的 {@link Builder}{@code <}{@link K}{@code >}。
     */
    static <K> Builder<K> builder() {
        return builder(null);
    }

    /**
     * 获取 {@link Conflict} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link Conflict}。
     * @param <K> 表示冲突键的类型的 {@link K}。
     * @return 表示 {@link Conflict} 的构建器的 {@link Builder}{@code <}{@link K}{@code >}。
     */
    static <K> Builder<K> builder(Conflict<K> value) {
        return ObjectUtils.cast(BuilderFactory.get(Conflict.class, Builder.class).create(value));
    }
}
