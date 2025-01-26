/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser;

/**
 * 参数类型信息。
 *
 * @author 郭龙飞
 * @since 2023-03-08
 */
public interface PointcutParameter {
    /**
     * 获取参数名称。
     *
     * @return 获取参数名称。
     */
    String getName();

    /**
     * 获取参数类型。
     *
     * @return 获取参数类型。
     */
    Class<?> getType();

    /**
     * 获取运行时方法参数值。
     *
     * @return 获取运行时方法参数值。
     */
    Object getBinding();

    /**
     * 设置运行时方法参数值。
     *
     * @param boundValue 设置运行时方法参数值。
     */
    void setBinding(Object boundValue);
}
