/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.support;

import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.OrderResolver;

import java.util.Comparator;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * {@link MethodInterceptor} 的排序。
 *
 * @author 詹高扬
 * @since 2022-07-27
 */
public class MethodInterceptorComparator implements Comparator<MethodInterceptor> {
    private final Class<?>[] methodInterceptorPriority = {
            AroundInterceptor.class, BeforeInterceptor.class, AfterInterceptor.class, AfterReturningInterceptor.class,
            AfterThrowingInterceptor.class
    };
    private final int minTypeOrder = this.methodInterceptorPriority.length;
    private final OrderResolver orderResolver;

    /**
     * 构建 {@link MethodInterceptorComparator} 的新实例。
     * <p>通过 SPI 机制加载所有的 {@link OrderResolver}，然后将其通过 {@link OrderResolver#combine(OrderResolver,
     * OrderResolver)} 组合成一个单独的 {@link OrderResolver}。</p>
     */
    public MethodInterceptorComparator() {
        OrderResolver actualResolver = null;
        ServiceLoader<OrderResolver> loader = ServiceLoader.load(OrderResolver.class, this.getClass().getClassLoader());
        for (OrderResolver resolver : loader) {
            actualResolver = OrderResolver.combine(actualResolver, resolver);
        }
        this.orderResolver = actualResolver;
    }

    @Override
    public int compare(MethodInterceptor methodInterceptor1, MethodInterceptor methodInterceptor2) {
        int primaryOrder1 = this.getPrimaryOrder(methodInterceptor1);
        int primaryOrder2 = this.getPrimaryOrder(methodInterceptor2);
        if (primaryOrder1 == primaryOrder2) {
            int secondaryOrder1 = this.getSecondaryOrder(methodInterceptor1);
            int secondaryOrder2 = this.getSecondaryOrder(methodInterceptor2);
            return Integer.compare(secondaryOrder1, secondaryOrder2);
        }
        return Integer.compare(primaryOrder1, primaryOrder2);
    }

    private int getPrimaryOrder(MethodInterceptor methodInterceptor) {
        if (methodInterceptor == null) {
            return this.minTypeOrder;
        }
        for (int i = 0; i < this.methodInterceptorPriority.length; i++) {
            if (this.methodInterceptorPriority[i].isInstance(methodInterceptor)) {
                return i;
            }
        }
        return this.minTypeOrder;
    }

    private int getSecondaryOrder(MethodInterceptor methodInterceptor) {
        return Optional.ofNullable(this.orderResolver)
                .map(resolver -> resolver.resolve(methodInterceptor))
                .filter(OrderResolver.Result::success)
                .map(OrderResolver.Result::order)
                .orElse(Order.MEDIUM);
    }
}
