/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.util.counter;

/**
 * 用于计数的工具类，保证不会出现数值溢出，计数器数值的值域为 [0, Long.MAX_VALUE]，保证操作的原子性和最终一致性但不保证时序性。
 *
 * @author 何天放
 * @since 2024-02-20
 */
public interface Counter {
    /**
     * 获取计数器的计数值。
     *
     * @return 表示计数值的 {@code long}。
     */
    long getValue();

    /**
     * 订阅计数值发生变化时的观察者。
     *
     * @param observer 表示计数值变化观察者的 {@link CounterValueChangedObserver}。
     */
    void observeValueChanged(CounterValueChangedObserver observer);

    /**
     * 取消订阅计数值发生变化时的观察者。
     *
     * @param observer 表示计数值变化观察者的 {@link CounterValueChangedObserver}。
     */
    void unobserveValueChanged(CounterValueChangedObserver observer);

    /**
     * 计数增加 1。
     *
     * @return 表示计数实际变化值的 {@code long}。
     */
    long increase();

    /**
     * 计数增加一定数值。
     *
     * @param value 表示计数值所增加数值的 {@code long}。
     * @return 表示计数值实际变化数值的 {@code long}。
     * @throws IllegalArgumentException 当 {@code value} 小于等于 {@code 0} 时。
     */
    long increase(long value);

    /**
     * 计数减少 1。
     *
     * @return 表示计数实际变化值的 {@code long}。
     */
    long decrease();

    /**
     * 计数减少一定数值。
     *
     * @param value 表示计数值所减少数值的 {@code long}。
     * @return 表示计数值实际变化数值的 {@code long}。
     * @throws IllegalArgumentException 当 {@code value} 小于等于 {@code 0} 时。
     */
    long decrease(long value);

    /**
     * 创建具有默认实现的计数器。
     *
     * @return 表示所创建计数器的 {@link Counter}。
     */
    static Counter create() {
        return new ThreadSafeCounter(0);
    }

    /**
     * 创建初始值为指定值的具有默认实现的计数器。
     *
     * @param count 表示初始值的 {@code long}。
     * @return 表示所创建计数器的 {@link Counter}。
     */
    static Counter create(long count) {
        return new ThreadSafeCounter(count);
    }
}
