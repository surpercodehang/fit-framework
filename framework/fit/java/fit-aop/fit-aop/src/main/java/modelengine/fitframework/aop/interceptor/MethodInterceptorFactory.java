/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanFactory;

import java.lang.reflect.Method;

/**
 * 表示方法拦截器的工厂。
 *
 * @author 季聿阶
 * @since 2022-05-13
 */
public interface MethodInterceptorFactory {
    /**
     * 判断指定方法是否是一个方法拦截器的定义。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 如果指定方法是一个方法拦截器的定义，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isInterceptMethod(@Nonnull Method method);

    /**
     * 根据指定方法以及其所在的 Bean 的工厂，创建一个方法拦截器。
     *
     * @param factory 表示指定方法所在 Bean 的工厂的 {@link BeanFactory}。
     * @param interceptMethod 表示指定方法的 {@link Method}。
     * @return 表示创建的方法拦截器的 {@link MethodInterceptor}。
     */
    MethodInterceptor create(BeanFactory factory, @Nonnull Method interceptMethod);
}
