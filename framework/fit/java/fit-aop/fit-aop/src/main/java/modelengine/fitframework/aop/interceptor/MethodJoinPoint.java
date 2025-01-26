/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Nullable;

/**
 * 表示运行时的方法连接点。
 *
 * @author 季聿阶
 * @since 2022-05-05
 */
public interface MethodJoinPoint {
    /**
     * 继续执行下一个方法的逻辑。
     *
     * @return 表示继续执行后的返回值的 {@link Object}。
     * @throws Throwable 当抛出的异常为原方法所定义的异常类型或者是 {@link RuntimeException} 的子类时，需要直接抛出，
     * 否则，需要转换为 {@link AspectException} 抛出。
     */
    @Nullable
    Object proceed() throws Throwable;

    /**
     * 以指定参数调用最终被代理的方法，继续执行下一个方法的逻辑。
     *
     * @param args 表示指定参数的 {@link Object}{@code []}。
     * @return 表示继续执行后的返回值的 {@link Object}。
     * @throws Throwable 当抛出的异常为原方法所定义的异常类型或者是 {@link RuntimeException} 的子类时，需要直接抛出，
     * 否则，需要转换为 {@link AspectException} 抛出。
     */
    @Nullable
    Object proceed(@Nonnull Object[] args) throws Throwable;

    /**
     * 获取下一个方法调用。
     *
     * @return 表示下一个方法调用的 {@link MethodInvocation}。
     */
    @Nonnull
    MethodInvocation getNextInvocation();

    /**
     * 获取被代理的方法调用。
     *
     * @return 表示获取被代理的方法调用的 {@link MethodInvocation}。
     */
    @Nonnull
    MethodInvocation getProxiedInvocation();

    /**
     * 获取代理的方法调用。
     *
     * @return 表示获取代理的方法调用的 {@link MethodInvocation}。
     */
    @Nonnull
    MethodInvocation getProxyInvocation();
}
