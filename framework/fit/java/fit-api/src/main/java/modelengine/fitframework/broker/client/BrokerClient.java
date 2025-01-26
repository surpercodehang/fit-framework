/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client;

import modelengine.fitframework.broker.Genericable;

import java.lang.reflect.Method;

/**
 * 服务调用的代理客户端。
 * <p>所有的服务的定义需要遵循以下两种规范之一：
 * <ol>
 *     <li><b>单接口单方法规范：</b>接口类上存在 {@link modelengine.fitframework.annotation.Genericable}
 *     注解，且接口类中仅有一个方法，该方法的名字为
 *     {@link modelengine.fitframework.util.GenericableUtils#GENERICABLE_METHOD_NAME}。</li>
 *     <li><b>单接口多方法规范：</b>接口类中存在多个方法，每一个服务方法上存在 {@link modelengine.fitframework.annotation.Genericable}
 *     注解。</li>
 * </ol>
 * </p>
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-08-18
 */
public interface BrokerClient {
    /**
     * 获取一个服务的自定义动态路由器。
     * <p>该服务类上必定需要存在 {@link modelengine.fitframework.annotation.Genericable} 注解。</p>
     *
     * @param genericableClass 表示服务的类型的 {@link Class}{@code <?>}。
     * @return 表示服务的自定义动态路由器的 {@link Router}。
     * @throws RouterRetrievalFailureException 当 {@code genericableClass} 不满足单接口单方法的服务定义规范时。
     */
    Router getRouter(Class<?> genericableClass);

    /**
     * 获取一个服务的自定义动态路由器。
     * <p>该服务类的方法上必定需要存在 {@link modelengine.fitframework.annotation.Genericable}
     * 注解，且需要存在一个服务的唯一标识和指定的 {@code genericableId} 一致。</p>
     *
     * @param genericableClass 表示服务的类型的 {@link Class}{@code <?>}。
     * @param genericableId 表示指定的服务的唯一标识的 {@link String}。
     * @return 表示服务的自定义动态路由器的 {@link Router}。
     * @throws RouterRetrievalFailureException 当 {@code genericableClass} 不满足单接口多方法的服务定义规范时，或当 {@code
     * genericableId} 为 {@code null} 或空白字符串时。
     */
    Router getRouter(Class<?> genericableClass, String genericableId);

    /**
     * 获取一个服务的自定义动态路由器。
     *
     * @param genericableId 表示服务的唯一标识的 {@link String}。
     * @param genericableMethod 表示服务的方法的 {@link Method}。
     * @return 表示服务的自定义动态路由器的 {@link Router}。
     */
    default Router getRouter(String genericableId, Method genericableMethod) {
        return this.getRouter(genericableId, false, genericableMethod);
    }

    /**
     * 获取一个服务的自定义动态路由器。
     *
     * @param genericableId 表示服务的唯一标识的 {@link String}。
     * @param isMicro 表示服务是否为微观服务的标记的 {@code boolean}。
     * @param genericableMethod 表示服务的方法的 {@link Method}。
     * @return 表示服务的自定义动态路由器的 {@link Router}。
     */
    Router getRouter(String genericableId, boolean isMicro, Method genericableMethod);

    /**
     * 获取一个服务的自定义动态路由器。
     *
     * @param genericableId 表示服务的唯一标识的 {@link String}。
     * @return 表示服务的自定义动态路由器的 {@link Router}。
     */
    default Router getRouter(String genericableId) {
        return this.getRouter(genericableId, false, null);
    }

    /**
     * 获取一个服务。
     * <p>该服务类上必定需要存在 {@link modelengine.fitframework.annotation.Genericable} 注解。</p>
     *
     * @param genericableClass 表示服务的类型的 {@link Class}{@code <?>}。
     * @return 表示服务的 {@link Genericable}。
     * @throws RouterRetrievalFailureException 当 {@code genericableClass} 不满足单接口单方法的服务定义规范时。
     * @throws GenericableNotFoundException 当从服务仓库中找不到合适的服务时。
     */
    Genericable getGenericable(Class<?> genericableClass);

    /**
     * 获取一个服务。
     * <p>该服务类的方法上必定需要存在 {@link modelengine.fitframework.annotation.Genericable}
     * 注解，且需要存在一个服务的唯一标识和指定的 {@code genericableId} 一致。</p>
     *
     * @param genericableClass 表示服务的类型的 {@link Class}{@code <?>}。
     * @param genericableId 表示指定的服务的唯一标识的 {@link String}。
     * @return 表示服务的 {@link Genericable}。
     * @throws RouterRetrievalFailureException 当 {@code genericableClass} 不满足单接口多方法的服务定义规范时，或当 {@code
     * genericableId} 为 {@code null} 或空白字符串时。
     * @throws GenericableNotFoundException 当从服务仓库中找不到合适的服务时。
     */
    Genericable getGenericable(Class<?> genericableClass, String genericableId);

    /**
     * 获取一个服务。
     *
     * @param genericableId 表示服务的唯一标识的 {@link String}。
     * @return 表示服务的 {@link Genericable}。
     * @throws GenericableNotFoundException 当从服务仓库中找不到合适的服务时。
     */
    default Genericable getGenericable(String genericableId) {
        return this.getRouter(genericableId).route().getGenericable();
    }
}
