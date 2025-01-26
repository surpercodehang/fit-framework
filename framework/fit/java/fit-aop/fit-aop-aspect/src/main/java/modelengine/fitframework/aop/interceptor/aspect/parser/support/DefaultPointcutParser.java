/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import static modelengine.fitframework.inspection.Validation.isFalse;
import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.aop.interceptor.aspect.parser.ExpressionParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.aop.interceptor.aspect.util.ExpressionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link PointcutParser} 默认实现。
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class DefaultPointcutParser implements PointcutParser {
    private final List<String> expressions;
    private final Class<?> aspectClass;
    private final PointcutParameter[] parameters;
    private final List<ExpressionParser> expressionParsers;

    public DefaultPointcutParser(String pointcut, Class<?> aspectClass, PointcutParameter[] parameters) {
        this.expressions = ExpressionUtils.expressionSplit(pointcut);
        this.aspectClass = aspectClass;
        this.parameters = parameters;
        this.expressionParsers = this.getParserList(aspectClass, parameters);
    }

    @Override
    public List<ExpressionParser.Result> parse() {
        List<ExpressionParser.Result> results = new ArrayList<>();
        for (String expression : this.expressions) {
            isFalse(expression.startsWith("(") && expression.endsWith(")"),
                    "UnSupported '(' ')' operators. [expression={0}]",
                    expression);
            ExpressionParser expressionParser = this.expressionParsers.stream()
                    .filter(parser -> parser.couldParse(expression))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(StringUtils.format(
                            "The expression value format error. [expression={0}]",
                            expression)));
            ExpressionParser.Result result = expressionParser.parse(expression);
            if (result.type() == PointcutSupportedType.REFERENCE) {
                results.add(LeftBracketParser.getResult());
                String pointcut = ObjectUtils.cast(result.content());
                notBlank(pointcut, "The expression value cannot be blank. [pointcut={0}]", expression);
                DefaultPointcutParser pointcutParser =
                        new DefaultPointcutParser(pointcut, this.aspectClass, this.parameters);
                List<ExpressionParser.Result> resultList = pointcutParser.parse();
                results.addAll(resultList);
                results.add(RightBracketParser.getResult());
            } else {
                results.add(result);
            }
        }
        return results;
    }

    private List<ExpressionParser> getParserList(Class<?> aspectClass, PointcutParameter[] parameters) {
        return PointcutParserFactory.create(aspectClass, parameters).build();
    }
}
