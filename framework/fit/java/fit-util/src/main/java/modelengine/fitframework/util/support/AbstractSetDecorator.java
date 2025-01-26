/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 为 {@link Set} 提供空的装饰器。
 *
 * @param <T> 表示集合中元素的类型。
 * @author 梁济时
 * @since 2021-11-05
 */
public abstract class AbstractSetDecorator<T> implements Set<T> {
    private final Set<T> decorated;

    /**
     * 使用被装饰的集合初始化 {@link AbstractSetDecorator} 类的新实例。
     *
     * @param decorated 表示被装饰的集合的 {@link Set}。
     * @throws IllegalArgumentException {@code decorated} 为 {@code null}。
     */
    public AbstractSetDecorator(Set<T> decorated) {
        this.decorated = Validation.notNull(decorated, "The decorated set cannot be null.");
    }

    @Override
    public int size() {
        return this.decorated.size();
    }

    @Override
    public boolean isEmpty() {
        return this.decorated.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        return this.decorated.contains(object);
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return this.decorated.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.decorated.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        return this.decorated.toArray(array);
    }

    @Override
    public boolean add(T element) {
        return this.decorated.add(element);
    }

    @Override
    public boolean remove(Object object) {
        return this.decorated.remove(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.decorated.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return this.decorated.addAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return this.decorated.retainAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return this.decorated.removeAll(collection);
    }

    @Override
    public void clear() {
        this.decorated.clear();
    }
}
