/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor;

import java.util.List;

/**
 * 表示方法匹配器的集合。
 *
 * @author 季聿阶
 * @since 2022-05-06
 */
public interface MethodMatcherCollection {
    /**
     * 向方法匹配器集合中添加一个指定的方法匹配器。
     *
     * @param matcher 表示待添加的方法匹配器的 {@link MethodMatcher}。
     */
    void add(MethodMatcher matcher);

    /**
     * 获取所有的方法匹配器的 {@link List}{@code <}{@link MethodMatcher}{@code >}。
     *
     * @return 表示所有的方法匹配器的 {@link List}{@code <}{@link MethodMatcher}{@code >}。
     */
    List<MethodMatcher> all();
}
