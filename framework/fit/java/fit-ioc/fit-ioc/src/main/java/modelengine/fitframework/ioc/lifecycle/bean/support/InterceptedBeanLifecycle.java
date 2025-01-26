/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycleInterceptor;

/**
 * 为 {@link BeanLifecycle} 提供基于拦截程序的实现。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class InterceptedBeanLifecycle implements BeanLifecycle {
    private final BeanLifecycle lifecycle;
    private final BeanLifecycleInterceptor interceptor;

    /**
     * 使用拦截程序和被拦截的Bean生命周期初始化 {@link InterceptedBeanLifecycle} 类的新实例。
     *
     * @param lifecycle 表示被拦截的Bean生命周期的 {@link BeanLifecycle}。
     * @param interceptor 表示拦截程序的 {@link BeanLifecycleInterceptor}。
     * @throws IllegalArgumentException {@code interceptor} 或 {@code lifecycle} 为 {@code null}。
     */
    public InterceptedBeanLifecycle(BeanLifecycle lifecycle, BeanLifecycleInterceptor interceptor) {
        this.interceptor = Validation.notNull(interceptor, "The bean lifecycle interceptor cannot be null.");
        this.lifecycle = Validation.notNull(lifecycle, "The intercepted bean lifecycle cannot be null.");
    }

    @Override
    public BeanMetadata metadata() {
        return this.lifecycle.metadata();
    }

    @Override
    public Object create(Object[] arguments) {
        return this.interceptor.create(this.lifecycle, arguments);
    }

    @Override
    public Object decorate(Object bean) {
        return this.interceptor.decorate(this.lifecycle, bean);
    }

    @Override
    public void inject(Object bean) {
        this.interceptor.inject(this.lifecycle, bean);
    }

    @Override
    public void initialize(Object bean) {
        this.interceptor.initialize(this.lifecycle, bean);
    }

    @Override
    public void destroy(Object bean) {
        this.interceptor.destroy(this.lifecycle, bean);
    }
}
