/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.aop.interceptor.support.AbstractMethodInterceptor;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Nullable;

/**
 * 调用被代理对象的方法拦截器。
 *
 * @author 季聿阶
 * @since 2022-05-25
 */
public class ProxiedInterceptor extends AbstractMethodInterceptor {
    private final ProxiedInvoker proxiedInvoker;

    /**
     * 使用被代理对象、被代理对象的方法和调用被代理对象的方法来实例化一个 {@link ProxiedInterceptor}。
     *
     * @param proxiedInvoker 表示调用被代理对象的方法的 {@link ProxiedInvoker}。
     */
    public ProxiedInterceptor(ProxiedInvoker proxiedInvoker) {
        this.proxiedInvoker = notNull(proxiedInvoker, "The proxied invoker cannot be null.");
    }

    @Nullable
    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable {
        return this.proxiedInvoker.invoke(methodJoinPoint.getProxiedInvocation());
    }
}
