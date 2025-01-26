/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.support;

import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ReflectionUtils;

/**
 * {@link MethodJoinPoint} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-05-11
 */
public class DefaultMethodJoinPoint implements MethodJoinPoint {
    private final MethodInvocation nextInvocation;
    private final MethodInvocation proxiedInvocation;
    private final MethodInvocation proxyInvocation;

    /**
     * 使用下一个方法调用和被代理的方法调用实例化 {@link DefaultMethodJoinPoint}。
     *
     * @param nextInvocation 表示下一个方法调用的 {@link MethodInvocation}。
     * @param proxiedInvocation 表示被代理的方法调用的 {@link MethodInvocation}。
     * @param proxyInvocation 表示代理的方法调用的 {@link MethodInvocation}。
     */
    public DefaultMethodJoinPoint(MethodInvocation nextInvocation, MethodInvocation proxiedInvocation,
            MethodInvocation proxyInvocation) {
        this.nextInvocation = Validation.notNull(nextInvocation, "The next method invocation cannot be null.");
        this.proxiedInvocation = Validation.notNull(proxiedInvocation, "The proxied method invocation cannot be null.");
        this.proxyInvocation = Validation.notNull(proxyInvocation, "The proxy method invocation cannot be null.");
    }

    @Nullable
    @Override
    public Object proceed() throws Throwable {
        try {
            return ReflectionUtils.invoke(this.nextInvocation.getTarget(),
                    this.nextInvocation.getMethod(),
                    this.nextInvocation.getArguments());
        } catch (MethodInvocationException e) {
            throw e.getCause();
        }
    }

    @Nullable
    @Override
    public Object proceed(@Nonnull Object[] args) throws Throwable {
        this.proxiedInvocation.setArguments(args);
        return this.proceed();
    }

    @Nonnull
    @Override
    public MethodInvocation getNextInvocation() {
        return this.nextInvocation;
    }

    @Nonnull
    @Override
    public MethodInvocation getProxiedInvocation() {
        return this.proxiedInvocation;
    }

    @Nonnull
    @Override
    public MethodInvocation getProxyInvocation() {
        return this.proxyInvocation;
    }
}
