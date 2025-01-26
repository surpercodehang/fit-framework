/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.proxy.InterceptSupport;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link InterceptSupport} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-05-18
 */
public class DefaultInterceptSupport implements InterceptSupport {
    private final Class<?> targetClass;
    private final Supplier<Object> targetSupplier;
    private final List<MethodInterceptor> methodInterceptors;

    /**
     * 使用被代理对象的类型、被代理对象和适配被代理对象的方法拦截器列表实例化 {@link DefaultInterceptSupport}。
     *
     * @param targetClass 表示被代理对象的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param targetSupplier 表示被代理对象的提供者的 {@link Supplier}{@code <}{@link Object}{@code >}。
     * @param methodInterceptors 表示指定方法拦截器列表的 {@link List}{@code <}{@link MethodInterceptor}{@code >}。
     */
    public DefaultInterceptSupport(Class<?> targetClass, Supplier<Object> targetSupplier,
            List<MethodInterceptor> methodInterceptors) {
        this.targetClass = notNull(targetClass, "The target class cannot be null.");
        this.targetSupplier = notNull(targetSupplier, "The target supplier cannot be null.");
        this.methodInterceptors = notNull(methodInterceptors, "The method interceptors cannot be null.");
    }

    @Override
    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    @Override
    public Object getTarget() {
        return this.targetSupplier.get();
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors() {
        return Collections.unmodifiableList(this.methodInterceptors);
    }
}
