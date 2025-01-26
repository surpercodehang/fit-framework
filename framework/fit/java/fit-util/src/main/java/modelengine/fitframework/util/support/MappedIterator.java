/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import modelengine.fitframework.inspection.Validation;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 为迭代器提供用以映射其迭代值提供封装。
 *
 * @param <F> 表示迭代的原始值的类型。
 * @param <T> 表示映射后的值的类型。
 * @author 梁济时
 * @since 2021-11-05
 */
public class MappedIterator<F, T> implements Iterator<T> {
    private final Iterator<F> origin;
    private final Function<F, T> mapper;

    /**
     * 使用原始的迭代器和用以映射值的方法初始化 {@link MappedIterator} 类的新实例。
     *
     * @param origin 表示原始的迭代器的 {@link Iterator}。
     * @param mapper 表示用以映射迭代的值的 {@link Function}。
     * @throws IllegalArgumentException {@code iterator} 或 {@code mapper} 为 {@code null}。
     */
    public MappedIterator(Iterator<F> origin, Function<F, T> mapper) {
        this.origin = Validation.notNull(origin, "The origin iterator to map cannot be null.");
        this.mapper = Validation.notNull(mapper, "The mapper to map iterating values cannot be null.");
    }

    @Override
    public boolean hasNext() {
        return this.origin.hasNext();
    }

    @Override
    public T next() {
        return this.mapper.apply(this.origin.next());
    }
}
