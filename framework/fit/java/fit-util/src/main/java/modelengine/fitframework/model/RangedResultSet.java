/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import modelengine.fitframework.model.support.DefaultRangedResultSet;

import java.util.List;

/**
 * 为被限定的结果集提供定义。
 *
 * @author 梁济时
 * @since 2020-07-24
 */
public interface RangedResultSet<T> {
    /**
     * 获取限定范围内的结果集。
     *
     * @return 表示限定范围内的结果集的 {@link List}。
     */
    List<T> getResults();

    /**
     * 获取限定结果。
     *
     * @return 表示限定结果的 {@link RangeResult}。
     */
    RangeResult getRange();

    /**
     * 使用被限定的结果集、偏移量、限定长度和结果总数量实例化被限定结果集的默认实现。
     *
     * @param results 表示被限定的结果集的 {@link List}。
     * @param offset 表示便宜量的32位整数。
     * @param limit 表示限定长度的32位整数。
     * @param total 表示结果总数量的32位整数。
     * @param <T> 表示结果集中数据的类型。
     * @return 表示具备指定结果集和限定结果的限定结果集的默认实现的 {@link RangedResultSet}。
     */
    static <T> RangedResultSet<T> create(List<T> results, int offset, int limit, int total) {
        return create(results, RangeResult.create(offset, limit, total));
    }

    /**
     * 使用被限定的结果集、原始范围和结果总数量实例化被限定结果集的默认实现。
     *
     * @param results 表示被限定的结果集的 {@link List}。
     * @param range 表示原始范围的 {@link Range}。
     * @param total 表示结果总数量的32位整数。
     * @param <T> 表示结果集中数据的类型。
     * @return 表示具备指定结果集和限定结果的限定结果集的默认实现的 {@link RangedResultSet}。
     */
    static <T> RangedResultSet<T> create(List<T> results, Range range, int total) {
        return create(results, RangeResult.create(range, total));
    }

    /**
     * 使用被限定的结果集和限定结果实例化被限定结果集的默认实现。
     *
     * @param results 表示被限定的结果集的 {@link List}。
     * @param range 表示限定结果的 {@link RangeResult}。
     * @param <T> 表示结果集中数据的类型。
     * @return 表示具备指定结果集和限定结果的限定结果集的默认实现的 {@link RangedResultSet}。
     */
    static <T> RangedResultSet<T> create(List<T> results, RangeResult range) {
        return new DefaultRangedResultSet<>(results, range);
    }
}
