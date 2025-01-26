/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy;

/**
 * 表示 AOP 代理的工厂。
 *
 * @author 季聿阶
 * @since 2022-05-04
 */
public interface AopProxyFactory {
    /**
     * 判断指定类型是否可以创建 AOP 代理。
     *
     * @param targetClass 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 如果可以创建，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean support(Class<?> targetClass);

    /**
     * 通过指定拦截事件支持信息创建 AOP 代理。
     *
     * @param support 表示指定的拦截事件支持信息的 {@link InterceptSupport}。
     * @return 表示创建出来的 AOP 代理的 {@link Object}。
     */
    Object createProxy(InterceptSupport support);
}
