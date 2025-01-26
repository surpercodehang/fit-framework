/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import modelengine.fitframework.model.support.DefaultRange;

/**
 * 为范围提供定义。
 *
 * @author 梁济时
 * @since 2020-07-24
 */
public interface Range {
    /**
     * 获取范围的偏移量。
     *
     * @return 表示偏移量的32位整数。
     */
    int getOffset();

    /**
     * 获取范围的限定长度。
     *
     * @return 表示限定长度的32位整数。
     */
    int getLimit();

    /**
     * 使用偏移量和限定长度实例化一个范围的默认实现。
     *
     * @param offset 表示偏移量的32位整数。
     * @param limit 表示限定长度的32位整数。
     * @return 表示具备指定偏移量和限定长度的范围的默认实现的 {@link Range}。
     */
    static Range create(int offset, int limit) {
        return new DefaultRange(offset, limit);
    }
}
