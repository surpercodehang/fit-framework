/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.model;

/**
 * 切入点类型。
 *
 * @author 郭龙飞
 * @since 2023-03-08
 */
public enum PointcutSupportedType {
    EXECUTION("execution"),
    WITHIN("within"),
    AT_WITHIN("@within"),
    THIS("this"),
    AT_TARGET("@target"),
    TARGET("target"),
    ARGS("args"),
    AT_ARGS("@args"),
    AT_PARAMS("@params"),
    REFERENCE("reference pointcut"),
    AT_ANNOTATION("@annotation"),
    AND("&&"),
    OR("||"),
    NOT("!"),
    LEFT_BRACKET("("),
    RIGHT_BRACKET(")");

    private final String value;

    PointcutSupportedType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
