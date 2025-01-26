/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy.support;

import static modelengine.fitframework.annotation.Order.PRETTY_HIGH;

import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.aop.proxy.AopProxyFactory;
import modelengine.fitframework.aop.proxy.FitProxy;
import modelengine.fitframework.aop.proxy.InterceptSupport;
import modelengine.fitframework.aop.util.ClassLoaderUtils;
import modelengine.fitframework.util.ClassUtils;

import java.lang.reflect.Proxy;

/**
 * 表示通过 JDK 的动态代理技术实现的 {@link AopProxyFactory}。
 *
 * @author 季聿阶
 * @since 2022-05-04
 */
@Order(PRETTY_HIGH)
public class JdkDynamicAopProxyFactory implements AopProxyFactory {
    @Override
    public boolean support(Class<?> targetClass) {
        return targetClass.isInterface() || Proxy.isProxyClass(targetClass) || ClassUtils.isLambda(targetClass);
    }

    @Override
    public Object createProxy(InterceptSupport support) {
        ClassLoader classLoader = ClassLoaderUtils.getCommonChildClassLoader(support.getTargetClass(), FitProxy.class)
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to get common child class loader when create proxy by jdk dynamic."));
        return Proxy.newProxyInstance(classLoader, new Class[] {
                support.getTargetClass(), FitProxy.class
        }, new JdkDynamicProxy(support));
    }
}
