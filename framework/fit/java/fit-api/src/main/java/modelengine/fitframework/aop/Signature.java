/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop;

/**
 * 表示连接点处的签名接口。
 *
 * @author 郭龙飞
 * @since 2023-03-08
 */
public interface Signature {
    /**
     * 连接点方法的字符串表现形式。
     *
     * @return 返回连接点方法的 {@link String} 表现形式。
     */
    String toString();

    /**
     * 连接点方法的短字符串表现形式。
     *
     * @return 返回连接点方法缩写的 {@link String} 表现形式。
     */
    String toShortString();

    /**
     * 连接点方法的长字符串表现形式。
     *
     * @return 返回连接点方法扩展的 {@link String} 表现形式。
     */
    String toLongString();

    /**
     * 获取连接点方法的方法名。
     *
     * @return 返回连接点方法的 {@link String} 方法名。
     */
    String getName();

    /**
     * 获取连接点方法的修饰符。
     *
     * @return 返回连接点方法的修饰符。
     */
    int getModifiers();

    /**
     * 获取声明类型的类。
     *
     * @return 返回声明类型的 {@link Class}{@code <?>}。
     */
    Class<?> getDeclaringType();

    /**
     * 获取声明类型的名称。
     *
     * @return 返回声明类型的完全限定名称的 {@link String}。
     */
    String getDeclaringTypeName();
}
