/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

/**
 * 为字符提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class CharacterUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private CharacterUtils() {}

    /**
     * 检查指定值是否在指定的前闭后闭区间内。
     *
     * @param value 表示待检查的值的 {@code char}。
     * @param min 表示区间最小值的 {@code char}。
     * @param max 表示区间最大值的 {@code char}。
     * @return 若值在有效区间内，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean between(char value, char min, char max) {
        return between(value, min, max, true, true);
    }

    /**
     * 检查指定值是否在有效区间内。
     *
     * @param value 表示待检查的值的 {@code char}。
     * @param minimum 表示区间最小值的 {@code char}。
     * @param maximum 表示区间最大值的 {@code char}。
     * @param includeMin 若为 {@code true}，则最小值在有效区间内；否则最小值不属于有效区间内。
     * @param includeMax 若为 {@code true}，则最大值在有效区间内；否则最大值不属于有效区间内。
     * @return 若在有效区间内，则为 {@code true}；否则为 {@code false}。
     */
    public static boolean between(char value, char minimum, char maximum, boolean includeMin, boolean includeMax) {
        return Character.compare(value, minimum) + (includeMin ? 1 : 0) > 0
                && Character.compare(value, maximum) - (includeMax ? 1 : 0) < 0;
    }
}
