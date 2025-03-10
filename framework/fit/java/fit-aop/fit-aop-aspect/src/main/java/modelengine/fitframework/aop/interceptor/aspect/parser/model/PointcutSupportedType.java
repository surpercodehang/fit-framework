/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.aop.interceptor.aspect.parser.model;

/**
 * 切入点类型。
 *
 * @author 郭龙飞
 * @since 2023-03-08
 */
public enum PointcutSupportedType {
    /** 匹配方法执行的切点。 */
    EXECUTION("execution"),

    /** 匹配类型的切点。 */
    WITHIN("within"),

    /** 匹配带有指定注解的类型的切点。 */
    AT_WITHIN("@within"),

    /** 匹配代理对象的切点。 */
    THIS("this"),

    /** 匹配目标对象的切点。 */
    TARGET("target"),

    /** 匹配带有指定注解的目标对象的切点。 */
    AT_TARGET("@target"),

    /** 匹配方法参数的切点。 */
    ARGS("args"),

    /** 匹配带有指定注解的方法参数的切点。 */
    AT_ARGS("@args"),

    /** 匹配带有指定注解的方法参数名的切点。 */
    AT_PARAMS("@params"),

    /** 匹配引用的切点。 */
    REFERENCE("reference pointcut"),

    /** 匹配带有指定注解的方法的切点。 */
    AT_ANNOTATION("@annotation"),

    /** 逻辑与运算符。 */
    AND("&&"),

    /** 逻辑或运算符。 */
    OR("||"),

    /** 逻辑非运算符。 */
    NOT("!"),

    /** 左括号。 */
    LEFT_BRACKET("("),

    /** 右括号。 */
    RIGHT_BRACKET(")");

    private final String value;

    PointcutSupportedType(String value) {
        this.value = value;
    }

    /**
     * 获取切点类型的值。
     *
     * @return 表示切点类型值的 {@link String}。
     */
    public String getValue() {
        return this.value;
    }
}
