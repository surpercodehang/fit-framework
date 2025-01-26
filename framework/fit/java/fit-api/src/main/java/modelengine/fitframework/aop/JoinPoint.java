/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop;

import java.lang.reflect.Method;

/**
 * 提供对连接点可用状态和关于它的静态信息接口。
 *
 * @author 白鹏坤
 * @since 2023-03-23
 */
public interface JoinPoint {
    /**
     * 连接点类型之一。
     */
    String METHOD_EXECUTION = "method-execution";

    /**
     * 连接点的字符串表现形式。
     *
     * @return 返回连接点的 {@link String} 表现形式。
     */
    String toString();

    /**
     * 连接点的短字符串表现形式。
     *
     * @return 返回连接点缩写的 {@link String} 表现形式。
     */
    String toShortString();

    /**
     * 连接点的长字符串表现形式。
     *
     * @return 返回连接点扩展的 {@link String} 表现形式。
     */
    String toLongString();

    /**
     * 获取当前正在执行的代理对象。
     *
     * @return 返回当前正在执行的代理对象 {@link Object}。
     */
    Object getThis();

    /**
     * 获取增强处理的目标对象。
     *
     * @return 返回被织入增强处理的目标对象 {@link Object}。
     */
    Object getTarget();

    /**
     * 获取目标方法的参数数组。
     *
     * @return 返回目标方法的参数数组 {@link Object}{@code []}。
     */
    Object[] getArgs();

    /**
     * 获取目标方法。
     *
     * @return 返回目标方法的 {@link Method}。
     */
    Method getMethod();

    /**
     * 获取目标方法的签名。
     *
     * @return 返回目标方法的签名 {@link Signature}。
     */
    Signature getSignature();

    /**
     * 获取连接点类型。
     *
     * @return 表示连接点类型的 {@link String}。
     */
    String getKind();

    /**
     * 获取连接点的静态部分。
     *
     * @return 连接点的静态部分。
     */
    StaticPart getStaticPart();

    /**
     * 连接点的静态部分。
     */
    interface StaticPart {
        /**
         * 获取目标方法的签名。
         *
         * @return 返回目标方法的签名 {@link Signature}。
         */
        Signature getSignature();

        /**
         * 获取连接点类型。
         *
         * @return 表示连接点类型的 {@link String}。
         */
        String getKind();

        /**
         * 获取连接点 Id。
         *
         * @return 表示连接点 id 的整型数字。
         */
        int getId();

        /**
         * 连接点的字符串表现形式。
         *
         * @return 返回连接点的 {@link String} 表现形式。
         */
        String toString();

        /**
         * 连接点的短字符串表现形式。
         *
         * @return 返回连接点缩写的 {@link String} 表现形式。
         */
        String toShortString();

        /**
         * 连接点的长字符串表现形式。
         *
         * @return 返回连接点扩展的 {@link String} 表现形式。
         */
        String toLongString();
    }
}
