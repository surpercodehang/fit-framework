/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser;

import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.inspection.Validation;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 表达式解析器。
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public interface ExpressionParser {
    /**
     * 获取待解析表达式的内容。
     * <p>内容格式：解析类型 + 左括号 + 待解析内容 + 右括号</p>
     *
     * @param expression 待解析表达式。
     * @param type 解析关键字类型。
     * @return 表达式括号内的内容。
     */
    static String getParseContent(String expression, String type) {
        Validation.isTrue(expression.startsWith(type), "The expression must start with type.[expression={0}, type={1}]",
                expression, type);
        Validation.isTrue(expression.contains("(") && expression.endsWith(")"),
                "The expression should contains '(' and end with ')'.[expression={0}]", expression);
        return expression.substring(type.length() + 1, expression.length() - 1).trim();
    }

    /**
     * 表达式能否匹配上关键字。
     *
     * @param expression 表达式。
     * @return 返回 true 表示能匹配上，反之，匹配不上。
     */
    boolean couldParse(String expression);

    /**
     * 表达式解析。
     *
     * @param expression 表达式。
     * @return 返回解析结果。
     */
    Result parse(String expression);

    /**
     * 解析结果。
     */
    interface Result {
        /**
         * 检测运行的类是否能匹配上。
         *
         * @param beanClass 运行的类。
         * @return 返回 true 表示能匹配上，反之，匹配不上。
         */
        boolean couldMatch(Class<?> beanClass);

        /**
         * 检测运行的方法是否能匹配上。
         *
         * @param method 运行的方法。
         * @return 返回 true 表示能匹配上，反之，匹配不上。
         */
        boolean match(Method method);

        /**
         * 检测表达式是否是参数绑定。
         *
         * @return 返回 true 表示是，反之，不是。
         */
        boolean isBinding();

        /**
         * 获取表达式类型。
         *
         * @return 返回表达式类型。
         */
        PointcutSupportedType type();

        /**
         * 获取表达式括号内的内容。
         *
         * @return 获取表达式括号内的内容。
         */
        Object content();

        /**
         * 切入点参数名字匹配参数类型。
         *
         * @param expression 表达式。
         * @param bean 运行时的类对象。
         * @param parameters 切点的参数列表。
         * @return 返回 true 表示能匹配上，反之，匹配不上。
         */
        default boolean isClassMatch(String expression, Class<?> bean, PointcutParameter[] parameters) {
            Class<?> aspectClass = null;
            for (PointcutParameter parameter : parameters) {
                if (Objects.equals(parameter.getName(), expression)) {
                    aspectClass = parameter.getType();
                    break;
                }
            }
            Validation.notNull(aspectClass, "Pointcut params name can not be found.[name={0}]", expression);
            return aspectClass.isAssignableFrom(bean);
        }
    }
}
