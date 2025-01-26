/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client;

import java.lang.reflect.Method;

/**
 * 为调用器提供工厂方法。
 *
 * @author 季聿阶
 * @since 2021-10-26
 */
public interface InvokerFactory {
    /**
     * 创建一个调用器。
     *
     * @param genericableId 表示待调用的泛服务的唯一标识的 {@link String}。
     * @param isMicro 表示待调用的泛服务是否为微观服务的标记的 {@code boolean}。
     * @param genericableMethod 表示待调用的泛服务的方法的 {@link Method}。
     * @param filter 表示路由规则的过滤器的 {@link Router.Filter}。
     * @return 表示创建出来的调用器的 {@link Invoker}。
     */
    Invoker create(String genericableId, boolean isMicro, Method genericableMethod, Router.Filter filter);
}
