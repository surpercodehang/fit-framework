/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser;

import java.util.List;

/**
 * 表达式解析器。
 *
 * @author 郭龙飞
 * @since 2023-03-10
 */
public interface PointcutParser {
    /**
     * 解析入口。
     *
     * @return 解析结果的 {@link ExpressionParser.Result}。
     */
    List<ExpressionParser.Result> parse();
}
