/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.util.support;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * 为 {@link Enumeration} 提供基于 {@link Iterator} 的适配程序。
 *
 * @param <E> 表示待遍历的元素的类型。
 * @author 梁济时
 * @since 2022-09-20
 */
public class IteratorEnumerationAdapter<E, T extends E> implements Enumeration<E> {
    private final Iterator<T> iterator;

    /**
     * 使用指定的迭代器来初始化 {@link IteratorEnumerationAdapter} 的新实例。
     *
     * @param iterator 表示待适配的迭代器的 {@link Iterator}{@code <}{@link T}{@code >}。
     */
    public IteratorEnumerationAdapter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    @Override
    public E nextElement() {
        return this.iterator.next();
    }
}
