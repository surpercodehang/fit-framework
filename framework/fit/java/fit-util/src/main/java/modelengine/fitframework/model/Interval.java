/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import modelengine.fitframework.model.support.DefaultInterval;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 为区间提供定义。
 *
 * @param <T> 表示区间内元素的类型的 {@link T}。
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public interface Interval<T extends Comparable<T>> {
    /**
     * 获取区间的最小值。
     *
     * @return 表示区间的最小值的 {@link T}。
     */
    T getMinimum();

    /**
     * 获取区间的最大值。
     *
     * @return 表示区间的最大值的 {@link T}。
     */
    T getMaximum();

    /**
     * 获取一个值，该值指示区间的最小值是否为有效值域。
     *
     * @return 若区间最小值在有效值域，则为 {@code true}；否则为 {@code false}。
     */
    boolean isMinimumAllowed();

    /**
     * 获取一个值，该值指示区间的最大值是否在有效值域。
     *
     * @return 若区间最大值在有效值域，则为 {@code true}；否则为 {@code false}。
     */
    boolean isMaximumAllowed();

    /**
     * 检查指定值是否在区间内。
     *
     * @param value 表示待检查的值的 {@link T}。
     * @return 若该值在区间内，则为 {@code true}；否则为 {@code false}。
     */
    default boolean contains(T value) {
        return ObjectUtils.between(value,
                this.getMinimum(),
                this.getMaximum(),
                this.isMinimumAllowed(),
                this.isMaximumAllowed());
    }

    /**
     * 创建一个前闭后闭区间的新实例。
     *
     * @param minimum 表示区间的最小值的 {@link T}。
     * @param maximum 表示区间的最大值的 {@link T}。
     * @param <T> 表示区间元素的类型的 {@link T}。
     * @return 表示区间实例的 {@link Interval}。
     */
    static <T extends Comparable<T>> Interval<T> create(T minimum, T maximum) {
        return create(minimum, maximum, true, true);
    }

    /**
     * 创建一个区间的新实例。
     *
     * @param minimum 表示区间的最小值的 {@link T}。
     * @param maximum 表示区间的最大值的 {@link T}。
     * @param minimumAllowed 若区间的最小值在有效值域内，则为 {@code true}；否则为 {@code false}。
     * @param maximumAllowed 若区间的最大值在有效值域内，则为 {@code true}；否则为 {@code false}。
     * @param <T> 表示区间元素的类型的 {@link T}。
     * @return 表示区间实例的 {@link Interval}。
     */
    static <T extends Comparable<T>> Interval<T> create(T minimum, T maximum, boolean minimumAllowed,
            boolean maximumAllowed) {
        return new DefaultInterval<>(minimum, maximum, minimumAllowed, maximumAllowed);
    }
}
