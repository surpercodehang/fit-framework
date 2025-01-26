/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

/**
 * 为 {@code long} 提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class LongUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private LongUtils() {}

    /**
     * 检查指定值是否在指定的前闭后闭区间内。
     *
     * @param value 表示待检查的值的 {@code long}。
     * @param min 表示区间最小值的 {@code long}。
     * @param max 表示区间最大值的 {@code long}。
     * @return 若值在有效区间内，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean between(long value, long min, long max) {
        return between(value, min, max, true, true);
    }

    /**
     * 检查指定值是否在有效区间内。
     *
     * @param value 表示待检查的值的 {@code long}。
     * @param minimum 表示区间最小值的 {@code long}。
     * @param maximum 表示区间最大值的 {@code long}。
     * @param includeMin 若为 {@code true}，则最小值在有效区间内；否则最小值不属于有效区间内。
     * @param includeMax 若为 {@code true}，则最大值在有效区间内；否则最大值不属于有效区间内。
     * @return 若在有效区间内，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean between(long value, long minimum, long maximum, boolean includeMin, boolean includeMax) {
        return Long.compare(value, minimum) + (includeMin ? 1 : 0) > 0
                && Long.compare(value, maximum) - (includeMax ? 1 : 0) < 0;
    }
}
