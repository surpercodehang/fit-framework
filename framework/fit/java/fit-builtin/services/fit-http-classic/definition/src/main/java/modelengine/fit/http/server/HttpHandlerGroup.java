/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.server.support.DefaultHttpHandlerGroup;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 表示 Http 处理器组。
 *
 * @author 季聿阶
 * @since 2023-08-22
 */
public interface HttpHandlerGroup {
    /**
     * 获取分组的名字。
     *
     * @return 表示分组名字的 {@link String}。
     */
    String getName();

    /**
     * 获取分组的描述信息。
     *
     * @return 表示分组的描述信息的 {@link String}。
     */
    String getDescription();

    /**
     * 获取分组的所有 Http 处理器列表。
     *
     * @return 表示分组的所有 Http 处理器列表的 {@link List}{@code <}{@link HttpHandler}{@code >}。
     */
    List<HttpHandler> getHandlers();

    /**
     * 获取方法对应的所有 Http 处理器列表。
     *
     * @return 表示方法对应的所有 Http 处理器列表的 {@link Map}{@code <}{@link Method}{@code , }{@link List}{@code
     * <}{@link HttpHandler}{@code >}{@code >}。
     */
    Map<Method, List<HttpHandler>> getMethodHandlersMapping();

    /**
     * 向分组中添加方法对应的 Http 处理器。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @param handler 表示 Http 处理器的 {@link HttpHandler}。
     */
    void addHandler(Method method, HttpHandler handler);

    /**
     * 创建一个 Http 处理器分组。
     *
     * @param name 表示分组名字的 {@link String}。
     * @param description 表示分组描述信息的 {@link String}。
     * @return 表示创建出来的 Http 处理器分组的 {@link HttpHandlerGroup}。
     */
    static HttpHandlerGroup create(String name, String description) {
        return new DefaultHttpHandlerGroup(name, description);
    }
}
