/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop;

/**
 * 暴露连接点 proceed(..) 方法，以支持 around 的环绕通知的接口。
 *
 * @author 郭龙飞
 * @since 2023-03-08
 */
public interface ProceedingJoinPoint extends JoinPoint {
    /**
     * 继续目标方法的无参调用。
     *
     * @return 目标方法的返回值的 {@link Object}。
     * @throws Throwable 当调用过程中抛出的任何异常时。
     */
    Object proceed() throws Throwable;

    /**
     * 继续目标方法的有参调用。
     *
     * @param args 目标方法的参数的 {@link Object}{@code []}。
     * @return 返回目标方法的返回值的 {@link Object}。
     * @throws Throwable 当调用过程中抛出的任何异常时。
     */
    Object proceed(Object[] args) throws Throwable;
}
