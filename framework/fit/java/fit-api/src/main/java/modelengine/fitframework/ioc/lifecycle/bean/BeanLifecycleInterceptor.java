/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean;

import modelengine.fitframework.ioc.BeanMetadata;

/**
 * 为 Bean 的生命周期提供拦截程序。
 *
 * @author 梁济时
 * @since 2022-04-26
 */
public interface BeanLifecycleInterceptor extends BeanLifecycleDependency {
    /**
     * 检查指定的 Bean 是否需要被拦截。
     *
     * @param metadata 表示待检查的 Bean 的元数据的 {@link BeanMetadata}。
     * @return 若需要被拦截，则为 {@code true}；否则为 {@code false}。
     */
    boolean isInterceptionRequired(BeanMetadata metadata);

    /**
     * 创建 Bean 实例。
     *
     * @param lifecycle 表示待拦截的 Bean 生命周期的 {@link BeanLifecycle}。
     * @param arguments 表示 Bean 的初始化参数的 {@link Object}{@code []}。
     * @return 表示新创建的 Bean 的实例的 {@link Object}。
     */
    default Object create(BeanLifecycle lifecycle, Object[] arguments) {
        return lifecycle.create(arguments);
    }

    /**
     * 装饰 Bean。
     *
     * @param lifecycle 表示待拦截的 Bean 的生命周期的 {@link BeanLifecycle}。
     * @param bean 表示待被装饰的 Bean 的 {@link Object}。
     * @return 表示装饰后的 Bean 的 {@link Object}。
     */
    default Object decorate(BeanLifecycle lifecycle, Object bean) {
        return lifecycle.decorate(bean);
    }

    /**
     * 为 Bean 注入依赖。
     *
     * @param lifecycle 表示待拦截的 Bean 生命周期的 {@link BeanLifecycle}。
     * @param bean 表示需要注入依赖的 Bean 的 {@link Object}。
     */
    default void inject(BeanLifecycle lifecycle, Object bean) {
        lifecycle.inject(bean);
    }

    /**
     * 初始化 Bean 实例。
     *
     * @param lifecycle 表示待拦截的 Bean 生命周期的 {@link BeanLifecycle}。
     * @param bean 表示需要初始化的 Bean 的 {@link Object}。
     */
    default void initialize(BeanLifecycle lifecycle, Object bean) {
        lifecycle.initialize(bean);
    }

    /**
     * 销毁 Bean 实例。
     *
     * @param lifecycle 表示待拦截的 Bean 生命周期的 {@link BeanLifecycle}。
     * @param bean 表示待销毁的 Bean 的 {@link Object}。
     */
    default void destroy(BeanLifecycle lifecycle, Object bean) {
        lifecycle.destroy(bean);
    }
}
