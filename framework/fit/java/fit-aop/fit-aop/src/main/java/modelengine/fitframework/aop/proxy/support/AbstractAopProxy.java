/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy.support;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.aop.interceptor.support.DefaultMethodInvocation;
import modelengine.fitframework.aop.interceptor.support.DefaultMethodJoinPoint;
import modelengine.fitframework.aop.proxy.FitProxy;
import modelengine.fitframework.aop.proxy.InterceptSupport;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LazyLoader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AOP 调用的核心抽象代理。
 *
 * @author 季聿阶
 * @since 2022-05-25
 */
public abstract class AbstractAopProxy implements FitProxy {
    private final LazyLoader<Object> targetSupplier;
    private final List<MethodInterceptor> methodInterceptors;
    private final Class<?> targetClass;
    private final Method interceptMethod;
    private final Method getActualClassMethod;

    /**
     * 使用拦截支持信息实例化 {@link AbstractAopProxy}。
     *
     * @param support 表示拦截支持信息的 {@link InterceptSupport}。
     */
    protected AbstractAopProxy(InterceptSupport support) {
        this.targetSupplier = new LazyLoader<>(support::getTarget);
        this.methodInterceptors = support.getMethodInterceptors();
        this.targetClass = support.getTargetClass();
        try {
            this.interceptMethod = MethodInterceptor.class.getDeclaredMethod("intercept", MethodJoinPoint.class);
            this.getActualClassMethod = FitProxy.class.getDeclaredMethod("$fit$getActualClass");
        } catch (NoSuchMethodException e) {
            // 必然存在指定方法，因此该分支不会走到。
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Class<?> $fit$getActualClass() {
        return Optional.ofNullable(this.getTarget()).map(Object::getClass).orElse(null);
    }

    /**
     * AOP 调用核心逻辑。
     * <p>调用过程如下：
     * <ol>
     *     <li>根据调用方法，过滤被代理对象的所有方法拦截器，如果没有合适的方法拦截器，则直接调用被代理对象。</li>
     *     <li>在过滤后的方法拦截器列表最后，添加一个 {@link ProxiedInterceptor}，用于调用被代理对象。</li>
     *     <li>从最后一个方法拦截器开始，依次向前构造当前方法拦截器的参数。</li>
     *     <li>构造完所有方法拦截器的参数后，调用第一个方法拦截器。</li>
     * </ol>
     * </p>
     *
     * @param proxy 表示代理对象的 {@link Object}。
     * @param method 表示调用方法的 {@link Method}。
     * @param args 表示调用参数的 {@link Object}{@code []}。
     * @param proxiedInvoker 表示调用被代理对象的方法的 {@link ProxiedInvoker}。
     * @return 表示调用后返回值的 {@link Object}。
     * @throws Throwable 当调用过程发生异常时。
     */
    protected Object invoke(Object proxy, Method method, Object[] args, ProxiedInvoker proxiedInvoker)
            throws Throwable {
        if (Objects.equals(method.getName(), this.getActualClassMethod.getName())) {
            return this.$fit$getActualClass();
        }
        List<MethodInterceptor> actualMethodInterceptors = this.filterMethodInterceptors(method);
        if (CollectionUtils.isEmpty(actualMethodInterceptors)) {
            return proxiedInvoker.invoke(new DefaultMethodInvocation(this.getTarget(), method, args));
        }
        MethodJoinPoint joinPoint = this.getJoinPoint(proxy, method, args, proxiedInvoker, actualMethodInterceptors);
        return actualMethodInterceptors.get(0).intercept(joinPoint);
    }

    private List<MethodInterceptor> filterMethodInterceptors(Method method) {
        return this.methodInterceptors.stream()
                .filter(Objects::nonNull)
                .filter(methodInterceptor -> methodInterceptor.getPointCut().methods().contains(method))
                .collect(Collectors.toList());
    }

    private MethodJoinPoint getJoinPoint(Object proxy, Method method, Object[] args, ProxiedInvoker proxiedInvoker,
            List<MethodInterceptor> actualMethodInterceptors) {
        MethodInvocation proxiedInvocation = new DefaultMethodInvocation(this.getTarget(), method, args);
        MethodInvocation proxyInvocation = new DefaultMethodInvocation(proxy, method, args);
        MethodJoinPoint lastJoinPoint =
                new DefaultMethodJoinPoint(proxiedInvocation, proxiedInvocation, proxyInvocation);
        MethodInterceptor lastInterceptor = new ProxiedInterceptor(proxiedInvoker);
        MethodInvocation nextInvocation =
                new DefaultMethodInvocation(lastInterceptor, this.interceptMethod, new Object[] {lastJoinPoint});
        lastJoinPoint = new DefaultMethodJoinPoint(nextInvocation, proxiedInvocation, proxyInvocation);
        for (int i = actualMethodInterceptors.size() - 1; i >= 1; i--) {
            MethodInterceptor currentInterceptor = actualMethodInterceptors.get(i);
            Object[] currentArgs = new Object[] {lastJoinPoint};
            nextInvocation = new DefaultMethodInvocation(currentInterceptor, this.interceptMethod, currentArgs);
            lastJoinPoint = new DefaultMethodJoinPoint(nextInvocation, proxiedInvocation, proxyInvocation);
        }
        return lastJoinPoint;
    }

    @Nullable
    private Object getTarget() {
        return this.targetSupplier.get();
    }

    /**
     * 获取代理对象的定义类型。
     *
     * @return 表示代理对象定义的类型的 {@link Class}{@code <?>}。
     */
    protected Class<?> getTargetClass() {
        return this.targetClass;
    }
}
