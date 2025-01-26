/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

/**
 * 为 {@code int} 提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class IntegerUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private IntegerUtils() {}

    /**
     * 检查指定值是否在指定的前闭后闭区间内。
     *
     * @param value 表示待检查的值的 {@code int}。
     * @param min 表示区间最小值的 {@code int}。
     * @param max 表示区间最大值的 {@code int}。
     * @return 若值在有效区间内，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean between(int value, int min, int max) {
        return between(value, min, max, true, true);
    }

    /**
     * 检查指定值是否在有效区间内。
     *
     * @param value 表示待检查的值的 {@code int}。
     * @param minimum 表示区间最小值的 {@code int}。
     * @param maximum 表示区间最大值的 {@code int}。
     * @param includeMin 若为 {@code true}，则最小值在有效区间内；否则最小值不属于有效区间内。
     * @param includeMax 若为 {@code true}，则最大值在有效区间内；否则最大值不属于有效区间内。
     * @return 若在有效区间内，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean between(int value, int minimum, int maximum, boolean includeMin, boolean includeMax) {
        return Integer.compare(value, minimum) + (includeMin ? 1 : 0) > 0
                && Integer.compare(value, maximum) - (includeMax ? 1 : 0) < 0;
    }

    /**
     * 检查指定的两个整型是否相等。
     *
     * @param first 表示待检查的第一个数字的 {@code int}。
     * @param second 表示待检查的第二个数字的 {@code int}。
     * @return 若两个数字相等，返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean equals(int first, int second) {
        return first == second;
    }

    /**
     * 对指定的 {@code int[]} 进行求和。
     *
     * @param values 表示待求和的数组的 {@code int[]}。
     * @return 表示所有整数的和的 {@code int}。
     */
    public static int sum(int... values) {
        int sum = 0;
        if (values != null) {
            for (int value : values) {
                sum += value;
            }
        }
        return sum;
    }
}
