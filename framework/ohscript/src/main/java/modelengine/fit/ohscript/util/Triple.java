/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

/**
 * 三元组
 *
 * @param <K> 第1个值类型
 * @param <T>第2个值类型
 * @param <V>第3个值类型
 * @since 1.0
 */
public class Triple<K, T, V> {
    private final K f;

    private final T s;

    private final V t;

    /**
     * 构造函数
     *
     * @param f 第1个值
     * @param s 第2个值
     * @param t 第3个值
     */
    public Triple(K f, T s, V t) {
        this.f = f;
        this.s = s;
        this.t = t;
    }

    /**
     * 获取第1个值
     *
     * @return 第1个值
     */
    public K first() {
        return this.f;
    }

    /**
     * 获取第2个值
     *
     * @return 第2个值
     */
    public T second() {
        return this.s;
    }

    /**
     * 获取第3个值
     *
     * @return 第3个值
     */
    public V third() {
        return this.t;
    }
}
