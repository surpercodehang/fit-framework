/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.model.support.DefaultRangeResult;

/**
 * 为范围结果提供定义。
 *
 * @author 梁济时
 * @since 2020-07-24
 */
public interface RangeResult extends Range {
    /**
     * 获取范围所限定的整体结果集中结果的数量。
     *
     * @return 表示结果数量的32位整数。
     */
    int getTotal();

    /**
     * 使用范围及结果总数量实例化一个范围结果的默认实现。
     *
     * @param range 表示原始范围的 {@link Range}。
     * @param total 表示结果总数量的32位整数。
     * @return 表示具备指定原始范围信息及结果总数量的结果范围的默认实现的 {@link RangeResult}。
     */
    static RangeResult create(Range range, int total) {
        Validation.notNull(range, "The range for result cannot be null.");
        return create(range.getOffset(), range.getLimit(), total);
    }

    /**
     * 使用偏移量、限定长度和结果总数量实例化一个范围结果的默认实现。
     *
     * @param offset 表示便宜量的32位整数。
     * @param limit 表示限定长度的32位整数。
     * @param total 表示结果总数量的32位整数。
     * @return 表示具备指定偏移量、限定长度和结果总数量的范围结果的默认实现的 {@link RangeResult}。
     */
    static RangeResult create(int offset, int limit, int total) {
        return new DefaultRangeResult(offset, limit, total);
    }
}
