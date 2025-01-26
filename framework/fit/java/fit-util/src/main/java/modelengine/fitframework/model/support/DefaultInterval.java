/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.support;

import modelengine.fitframework.model.Interval;

/**
 * 为 {@link Interval} 提供默认实现。
 *
 * @param <T> 表示区间中元素的类型的 {@link T}。
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class DefaultInterval<T extends Comparable<T>> implements Interval<T> {
    private final T minimum;
    private final T maximum;
    private final boolean minimumAllowed;
    private final boolean maximumAllowed;

    /**
     * 使用区间的最小值、最大值，以及指定最小值、最大值是否在有效区间的值初始化 {@link DefaultInterval} 类的新实例。
     *
     * @param minimum 表示区间的最小值的 {@link T}。
     * @param maximum 表示区间的最大值的 {@link T}。
     * @param minimumAllowed 若为 {@code true}，则最小值在有效值域；否则最小值不在有效值域。
     * @param maximumAllowed 若为 {@code true}，则最大值在有效值域；否则最大值不在有效值域。
     */
    public DefaultInterval(T minimum, T maximum, boolean minimumAllowed, boolean maximumAllowed) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.minimumAllowed = minimumAllowed;
        this.maximumAllowed = maximumAllowed;
    }

    @Override
    public T getMinimum() {
        return this.minimum;
    }

    @Override
    public T getMaximum() {
        return this.maximum;
    }

    @Override
    public boolean isMinimumAllowed() {
        return this.minimumAllowed;
    }

    @Override
    public boolean isMaximumAllowed() {
        return this.maximumAllowed;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.isMinimumAllowed()) {
            builder.append('[');
        } else {
            builder.append('(');
        }
        builder.append(this.getMinimum()).append(',').append(' ').append(this.getMaximum());
        if (this.isMaximumAllowed()) {
            builder.append(']');
        } else {
            builder.append(')');
        }
        return builder.toString();
    }
}
