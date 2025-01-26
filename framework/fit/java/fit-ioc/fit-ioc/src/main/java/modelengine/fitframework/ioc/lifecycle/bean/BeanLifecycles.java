/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.lifecycle.bean.support.InterceptedBeanLifecycle;
import modelengine.fitframework.type.TypeMatcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 Bean 声明周期相关的工具类集合。
 *
 * @author 梁济时
 * @since 2022-06-01
 */
public class BeanLifecycles {
    /**
     * 返回应用了拦截程序的生命周期。
     *
     * @param lifecycle 表示待拦截的生命周期的 {@link BeanLifecycle}。
     * @return 表示应用拦截程序后的生命周期的 {@link BeanLifecycle}。
     */
    public static BeanLifecycle intercept(BeanLifecycle lifecycle) {
        notNull(lifecycle, "The lifecycle cannot be null.");
        if (lifecycle instanceof InterceptedBeanLifecycle) {
            return lifecycle;
        } else if (TypeMatcher.match(lifecycle.metadata().type(), BeanLifecycleDependency.class)) {
            return lifecycle;
        } else {
            BeanContainer container = lifecycle.metadata().container();
            List<BeanLifecycleInterceptor> interceptors = container.all(BeanLifecycleInterceptor.class)
                    .stream()
                    .map(BeanFactory::<BeanLifecycleInterceptor>get)
                    .collect(Collectors.toList());
            BeanLifecycle intercepted = lifecycle;
            for (BeanLifecycleInterceptor interceptor : interceptors) {
                if (interceptor.isInterceptionRequired(intercepted.metadata())) {
                    intercepted = new InterceptedBeanLifecycle(intercepted, interceptor);
                }
            }
            return intercepted;
        }
    }
}
