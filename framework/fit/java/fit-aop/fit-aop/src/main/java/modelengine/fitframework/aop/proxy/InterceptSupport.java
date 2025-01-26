/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.proxy;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;

import java.util.List;

/**
 * 表示拦截事件支持信息。
 *
 * @author 季聿阶
 * @since 2022-05-04
 */
public interface InterceptSupport {
    /**
     * 获取被代理对象的类型。
     *
     * @return 表示被代理对象的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     */
    Class<?> getTargetClass();

    /**
     * 获取被代理对象。
     *
     * @return 表示被代理对象的 {@link Object}。
     */
    Object getTarget();

    /**
     * 获取调用的方法拦截器列表。
     *
     * @return 表示调用的方法拦截器列表的 {@link List}{@code <}{@link MethodInterceptor}{@code >}。
     */
    List<MethodInterceptor> getMethodInterceptors();
}
