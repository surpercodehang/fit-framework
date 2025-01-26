/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.Set;

/**
 * 表示标签集合。
 *
 * @author 季聿阶
 * @since 2023-03-27
 */
public interface Tags {
    /**
     * 获取所有的标签。
     *
     * @return 表示所有标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> all();

    /**
     * 判断标签集合中是否包含指定的标签。
     *
     * @param tag 表示指定标签的 {@link String}。
     * @return 表示判断结果的 {@code boolean}。
     */
    boolean contains(String tag);
}
