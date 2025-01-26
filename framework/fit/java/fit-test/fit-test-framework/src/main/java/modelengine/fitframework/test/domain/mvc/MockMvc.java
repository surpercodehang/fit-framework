/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.mvc;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.test.domain.mvc.request.RequestParam;

/**
 * 为测试提供模拟的 MVC。
 *
 * @author 王攀博
 * @author 易文渊
 * @since 2024-04-09
 */
public final class MockMvc {
    private final int port;

    /**
     * 通过插件启动端口初始化 {@link MockMvc} 的实例。
     *
     * @param port 表示插件启动端口的 {@code int}。
     */
    public MockMvc(int port) {
        this.port = port;
    }

    /**
     * 执行模拟的 http 调用。
     *
     * @param builder 表示构建客户端请求参数的 {@link MockRequestBuilder}
     * @param <T> 表示期待的返回值类型的 {@link T}。
     * @return 表示 Http 响应的 {@link HttpClassicClientResponse}{@code <}{@link T}{@code >}。
     */
    public <T> HttpClassicClientResponse<T> perform(MockRequestBuilder builder) {
        HttpClassicClient httpClassicClient = HttpClientFactory.create();
        RequestParam requestParam = builder.port(this.port).client(httpClassicClient).build();
        return httpClassicClient.exchange(requestParam.getRequest(), requestParam.getResponseType());
    }

    /**
     * 执行模拟的 http 调用，直接获取流式数据。
     *
     * @param builder 表示构建客户端请求参数的 {@link MockRequestBuilder}
     * @param <T> 表示期待的返回值类型的 {@link T}。
     * @return 表示 Http 响应的流式数据的 {@link Choir}{@code <}{@link T}{@code >}。
     */
    public <T> Choir<T> streamPerform(MockRequestBuilder builder) {
        HttpClassicClient httpClassicClient = HttpClientFactory.create();
        RequestParam requestParam = builder.port(this.port).client(httpClassicClient).build();
        return httpClassicClient.exchangeStream(requestParam.getRequest(), requestParam.getResponseType());
    }

    /**
     * 获取测试插件启动端口。
     *
     * @return 表示插件启动端口的 {@code int}。
     */
    public int getPort() {
        return this.port;
    }
}