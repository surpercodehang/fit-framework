/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.composite.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.pattern.composite.ConsumerComposite;
import modelengine.fitframework.util.LockUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;

/**
 * 为 {@link ConsumerComposite} 提供线程安全的装饰器。
 *
 * @author 梁济时
 * @since 2020-10-15
 */
public class ConsumerCompositeConcurrentDecorator<T> implements ConsumerComposite<T> {
    private final ConsumerComposite<T> decorated;
    private final ReadWriteLock lock;

    /**
     * 使用被装饰的消费者组合初始化 {@link ConsumerCompositeConcurrentDecorator} 类的新实例。
     *
     * @param decorated 表示被装饰的消费者组合的 {@link ConsumerComposite}。
     * @throws IllegalArgumentException {@code decorated} 为 {@code null}。
     */
    public ConsumerCompositeConcurrentDecorator(ConsumerComposite<T> decorated) {
        this.decorated = Validation.notNull(decorated, "The decorated consumer composite cannot be null.");
        this.lock = LockUtils.newReentrantReadWriteLock();
    }

    @Override
    public void add(Consumer<T> consumer) {
        LockUtils.synchronize(this.lock.writeLock(), () -> this.decorated.add(consumer));
    }

    @Override
    public void addAll(Collection<Consumer<T>> consumers) {
        LockUtils.synchronize(this.lock.writeLock(), () -> this.decorated.addAll(consumers));
    }

    @Override
    public void remove(Consumer<T> consumer) {
        LockUtils.synchronize(this.lock.writeLock(), () -> this.decorated.remove(consumer));
    }

    @Override
    public void removeAll(Collection<Consumer<T>> consumers) {
        LockUtils.synchronize(this.lock.writeLock(), () -> this.decorated.removeAll(consumers));
    }

    @Override
    public List<Consumer<T>> getConsumers() {
        return LockUtils.synchronize(this.lock.readLock(), this.decorated::getConsumers);
    }

    @Override
    public void accept(T object) {
        LockUtils.synchronize(this.lock.readLock(), () -> new ArrayList<>(this.decorated.getConsumers()))
                .forEach(consumer -> consumer.accept(object));
    }
}
