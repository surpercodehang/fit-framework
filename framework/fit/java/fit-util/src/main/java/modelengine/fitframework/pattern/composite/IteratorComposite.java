/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.composite;

import modelengine.fitframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为迭代器提供组合。
 *
 * @param <T> 表示迭代器中元素的类型。
 * @author 梁济时
 * @since 2022-07-27
 */
public class IteratorComposite<T> implements Iterator<T> {
    private final List<Iterator<T>> iterators;
    private int index;

    public IteratorComposite(List<Iterator<T>> iterators) {
        this.iterators = iterators;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        while (this.index < this.iterators.size()) {
            Iterator<T> iterator = this.iterators.get(this.index);
            if (iterator.hasNext()) {
                return true;
            } else {
                this.index++;
            }
        }
        return false;
    }

    @Override
    public T next() {
        if (this.index < this.iterators.size()) {
            return this.iterators.get(this.index).next();
        } else {
            throw new NoSuchElementException("No more iterators in composite.");
        }
    }

    /**
     * 将多个迭代器组合成为一个迭代器，以顺序迭代所有迭代器中的元素。
     *
     * @param iterators 表示待组合的迭代器的 {@link Iterator}{@code []}。
     * @param <T> 表示迭代器中元素的类型。
     * @return 表示组合后的迭代器的 {@link Iterator}。
     */
    @SafeVarargs
    public static <T> Iterator<T> combine(Iterator<T>... iterators) {
        return combine(ObjectUtils.<Iterator<T>[], Collection<Iterator<T>>>mapIfNotNull(iterators, Arrays::asList));
    }

    /**
     * 将多个迭代器组合成为一个迭代器，以顺序迭代所有迭代器中的元素。
     *
     * @param iterators 表示待组合的迭代器的 {@link Collection}{@code <}{@link Iterator}{@code >}。
     * @param <T> 表示迭代器中元素的类型。
     * @return 表示组合后的迭代器的 {@link Iterator}。
     */
    public static <T> Iterator<T> combine(Collection<Iterator<T>> iterators) {
        List<Iterator<T>> actual = Optional.ofNullable(iterators)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (actual.isEmpty()) {
            return Collections.emptyIterator();
        } else if (actual.size() > 1) {
            return new IteratorComposite<>(actual);
        } else {
            return actual.get(0);
        }
    }
}
