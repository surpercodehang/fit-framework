/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.handler.support.HttpResponseStatusResolverComposite;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 表示 Http 响应状态的解析器。
 *
 * @author 季聿阶
 * @since 2023-01-11
 */
@FunctionalInterface
public interface HttpResponseStatusResolver {
    /**
     * 从指定方法上解析 Http 的响应状态。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示解析到的 Http 的响应状态的 {@link Optional}{@code <}{@link HttpResponseStatus}{@code >}。
     */
    Optional<HttpResponseStatus> resolve(Method method);

    /**
     * 将多个 Http 响应状态的解析器合并为一个。
     *
     * @param resolvers 表示多个 Http 响应状态的解析器的 {@link HttpResponseStatusResolver}{@code []}。
     * @return 表示合并后的 Http 响应状态的解析器的 {@link HttpResponseStatusResolver}。
     */
    static HttpResponseStatusResolver combine(HttpResponseStatusResolver... resolvers) {
        return new HttpResponseStatusResolverComposite(resolvers);
    }
}
