/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;

/**
 * 解析切点表达式中运算符或 '||' 的解析器。
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class OrParser extends BaseParser {
    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.OR;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new OrResult(content);
    }

    class OrResult extends BaseParser.BaseResult {
        public OrResult(String expression) {
            super(expression, null);
        }
    }
}
