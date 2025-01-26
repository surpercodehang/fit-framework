/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.composite;

import modelengine.fitframework.pattern.composite.support.ConsumerCompositeConcurrentDecorator;
import modelengine.fitframework.pattern.composite.support.DefaultConsumerComposite;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 为 {@link Consumer} 提供组合模式实现。
 *
 * @param <T> 表示消费对象的类型的 {@link T}。
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-10-15
 */
public interface ConsumerComposite<T> extends Consumer<T> {
    /**
     * 创建一个消费者组合的默认实例。
     *
     * @param <T> 表示待消费对象的类型的 {@link T}。
     * @return 表示新实例化的 {@link ConsumerComposite}{@code <}{@link T}{@code >}。
     */
    static <T> ConsumerComposite<T> createDefault() {
        return new DefaultConsumerComposite<>();
    }

    /**
     * 创建一个线程安全的消费者组合实例。
     *
     * @param <T> 表示待消费对象的类型的 {@link T}。
     * @return 表示新实例化的 {@link ConsumerComposite}{@code <}{@link T}{@code >}。
     */
    static <T> ConsumerComposite<T> createConcurrent() {
        ConsumerComposite<T> defaultComposite = createDefault();
        return defaultComposite.concurrent();
    }

    @Override
    default void accept(T object) {
        this.getConsumers().stream().filter(Objects::nonNull).forEach(consumer -> consumer.accept(object));
    }

    /**
     * 返回一个当前组合的线程安全的装饰器。
     *
     * @return 表示线程安全的装饰器的 {@link ConsumerComposite}{@code <}{@link T}{@code >}。
     */
    default ConsumerComposite<T> concurrent() {
        return new ConsumerCompositeConcurrentDecorator<>(this);
    }

    /**
     * 获取组合中包含的所有子项。
     *
     * @return 表示包含的所有子项的 {@link List}{@code <}{@link Consumer}{@code <}{@link T}{@code >>}。
     */
    List<Consumer<T>> getConsumers();

    /**
     * 向组合中增加一个子项。
     *
     * @param consumer 表示待添加到组合中的子项的 {@link Consumer}{@code <}{@link T}{@code >}。
     */
    void add(Consumer<T> consumer);

    /**
     * 向组合中增加一系列的子项。
     *
     * @param consumers 表示待添加到组合中的所有子项的 {@link Collection}{@code <}{@link Consumer}{@code <}{@link T}{@code >>}。
     * @throws IllegalArgumentException 当 {@code consumers} 为 {@code null} 时。
     */
    void addAll(Collection<Consumer<T>> consumers);

    /**
     * 从组合中移除一个子项。
     *
     * @param consumer 表示待从组合中移除的子项的 {@link Consumer}{@code <}{@link T}{@code >}。
     */
    void remove(Consumer<T> consumer);

    /**
     * 从组合中移除一系列子项。
     *
     * @param consumers 表示待从组合中移除的所有子项的 {@link Collection}{@code <}{@link Consumer}{@code <}{@link T}{@code >>}。
     * @throws IllegalArgumentException 当 {@code consumers} 为 {@code null} 时。
     */
    void removeAll(Collection<Consumer<T>> consumers);
}
