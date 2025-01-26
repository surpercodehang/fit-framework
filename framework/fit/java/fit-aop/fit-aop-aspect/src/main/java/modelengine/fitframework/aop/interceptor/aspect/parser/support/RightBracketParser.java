/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;

/**
 * 解析切点表达式中运算符 ')' 的解析器。用于 pointcut 解析时自动添加，解决多个 pointcut 运算优先级问题。
 *
 * @author 白鹏坤
 * @since 2023-03-31
 */
public class RightBracketParser extends BaseParser {
    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.RIGHT_BRACKET;
    }

    @Override
    public Result createConcreteParser(String content) {
        return new RightBracketResult(content);
    }

    class RightBracketResult extends BaseParser.BaseResult {
        public RightBracketResult(String expression) {
            super(expression, null);
        }
    }

    /**
     * 创建解析结果实例。
     *
     * @return 解析结果实例的 {@link Result}。
     */
    public static Result getResult() {
        return new RightBracketParser().createConcreteParser(PointcutSupportedType.RIGHT_BRACKET.getValue());
    }
}
