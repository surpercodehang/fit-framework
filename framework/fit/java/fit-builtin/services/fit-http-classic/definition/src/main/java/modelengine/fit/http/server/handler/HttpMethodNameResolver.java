/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 表示 Http 方法名字的解析器。
 *
 * @author 季聿阶
 * @since 2023-01-11
 */
@FunctionalInterface
public interface HttpMethodNameResolver {
    /**
     * 从指定方法上解析支持的 Http 方法的名字列表。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示从指定方法上解析到的支持的 Http 方法的名字列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> resolve(Method method);
}
