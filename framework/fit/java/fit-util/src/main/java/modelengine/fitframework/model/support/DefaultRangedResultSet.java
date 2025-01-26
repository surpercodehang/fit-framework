/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.support;

import modelengine.fitframework.model.RangeResult;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.util.StringUtils;

import java.util.List;

/**
 * 为 {@link RangedResultSet} 提供默认实现。
 *
 * @param <T> 表示结果集中数据的类型。
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class DefaultRangedResultSet<T> implements RangedResultSet<T> {
    private final List<T> results;
    private final RangeResult range;

    /**
     * 使用被限定的结果集和限定结果初始化 {@link DefaultRangedResultSet} 类的新实例。
     *
     * @param results 表示被限定的结果集的 {@link List}。
     * @param range 表示限定结果的 {@link RangeResult}。
     */
    public DefaultRangedResultSet(List<T> results, RangeResult range) {
        this.results = results;
        this.range = range;
    }

    @Override
    public List<T> getResults() {
        return this.results;
    }

    @Override
    public RangeResult getRange() {
        return this.range;
    }

    @Override
    public String toString() {
        return StringUtils.format("[range={0}, results={1}]", this.getRange(), this.getResults());
    }
}
