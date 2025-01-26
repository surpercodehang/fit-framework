/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.HttpClassicResponse;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.server.dispatch.DefaultHttpDispatcher;

import java.util.List;
import java.util.Map;

/**
 * Http 请求的分发器。
 *
 * @author 季聿阶
 * @since 2022-07-18
 */
public interface HttpDispatcher {
    /**
     * 分发 Http 请求，获取处理当前 Http 请求的处理器。
     *
     * @param request 表示当前 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示当前 Http 响应的 {@link HttpClassicResponse}。
     * @return 表示处理当前 Http 请求的处理器的 {@link HttpHandler}。
     * @throws HttpHandlerNotFoundException 当无法找到合适的处理器时。
     */
    HttpHandler dispatch(HttpClassicServerRequest request, HttpClassicResponse response);

    /**
     * 注册 Http 请求的处理器。
     *
     * @param httpMethod 表示 Http 请求的方法的 {@link String}。
     * @param handler 表示处理 Http 请求的处理器的 {@link HttpHandler}。
     * @throws IllegalArgumentException 当 {@code httpMethod} 不在 {@link HttpRequestMethod 支持列表} 中时，或当
     * {@code handler} 为 {@code null} 时，或当 {@code handler} 的路径为 {@code null} 或空白字符串时。
     * @throws RegisterHttpHandlerException 当注册过程中发生异常时。
     */
    void register(String httpMethod, HttpHandler handler) throws RegisterHttpHandlerException;

    /**
     * 取消注册 Http 请求的处理器。
     *
     * @param httpMethod 表示 Http 请求的方法的 {@link String}。
     * @param handler 表示处理 Http 请求的处理器的 {@link HttpHandler}。
     * @throws IllegalArgumentException 当 {@code httpMethod} 不在 {@link HttpRequestMethod 支持列表} 中时，或当
     * {@code handler} 为 {@code null} 时，或当 {@code handler} 的路径为 {@code null} 或空白字符串时。
     */
    void unregister(String httpMethod, HttpHandler handler);

    /**
     * 创建 Http 请求的分发器。
     *
     * @return 表示创建的 Http 请求的分发器的 {@link HttpDispatcher}。
     */
    static HttpDispatcher create() {
        return new DefaultHttpDispatcher();
    }

    /**
     * 获取 Http 处理器的映射关系。
     * <p>其中 key 为 Http 请求的类型，value 为类型相对应的处理器列表</p>
     *
     * @return 表示 Http 处理器的映射关系的 {@link Map}{@code <}{@link HttpRequestMethod}{@code ,}{@link List}{@code <}{@link
     * HttpHandler}{@code >>}。
     */
    Map<HttpRequestMethod, List<HttpHandler>> getHttpHandlersMapping();

    /**
     * 注册 Http 请求的处理器组。
     *
     * @param group 表示 Http 请求的处理器组的 {@link HttpHandlerGroup}。
     */
    void registerGroup(HttpHandlerGroup group);

    /**
     * 取消注册 Http 请求的处理器组。
     *
     * @param groupName 表示 Http 处理器组的组名的 {@link String}。
     */
    void unregisterGroup(String groupName);

    /**
     * 获取所有的 Http 请求处理器组的集合。
     *
     * @return 表示所有的 Http 请求处理器组的集合的 {@link Map}{@code <}{@link String}{@code , }{@link
     * HttpHandlerGroup}{@code >}。
     */
    Map<String, HttpHandlerGroup> getHttpHandlerGroups();
}
