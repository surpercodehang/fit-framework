/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.HttpResource;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.client.WebSocketClassicListener;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 表示 Http 客户端。
 *
 * @author 季聿阶
 * @since 2022-11-22
 */
public interface HttpClassicClient extends HttpResource {
    /**
     * 根据指定 Http 方法和请求地址，创建一个经典的 Http 请求。
     *
     * @param method 表示指定的 Http 方法的 {@link HttpRequestMethod}。
     * @param url 表示 Http 请求地址的 {@link String}。
     * @return 表示创建出来的经典的 Http 请求的 {@link HttpClassicClientRequest}。
     */
    HttpClassicClientRequest createRequest(HttpRequestMethod method, String url);

    /**
     * 根据指定 WebSocket 的请求地址和自定义消息监听器，创建一个 WebSocket 的会话。
     *
     * @param url 表示指定的 WebSocket 请求地址的 {@link String}。
     * @param listener 表示自定义消息监听器的 {@link WebSocketClassicListener}。
     * @return 表示创建出来的 WebSocket 的会话的 {@link Session}。
     */
    Session createWebSocketSession(String url, WebSocketClassicListener listener);

    /**
     * 发送 Http 请求，接收 Http 响应。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @return 表示接收的 Http 响应的 {@link HttpClassicClientResponse}。
     */
    HttpClassicClientResponse<Object> exchange(HttpClassicClientRequest request);

    /**
     * 发送 Http 请求，接收 Http 响应。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param responseType 表示期待的返回值类型的 {@link Type}。
     * @param <T> 表示期待的返回值类型的 {@link T}。
     * @return 表示 Http 响应的 {@link HttpClassicClientResponse}{@code <}{@link T}{@code >}。
     */
    <T> HttpClassicClientResponse<T> exchange(HttpClassicClientRequest request, Type responseType);

    /**
     * 发送 Http 请求，获取 Http 响应的数据内容。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param responseType 表示期待的返回值类型的 {@link Type}。
     * @param <T> 表示期待的返回值类型的 {@link T}。
     * @return 表示 Http 响应的数据内容的 {@link T}。
     */
    default <T> T exchangeForEntity(HttpClassicClientRequest request, Type responseType) {
        try (HttpClassicClientResponse<T> response = this.exchange(request, responseType)) {
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                if (responseType == String.class) {
                    return cast(response.textEntity().map(TextEntity::content).orElse(StringUtils.EMPTY));
                }
                return cast(response.objectEntity().map(ObjectEntity::object).orElse(null));
            } else if (response.statusCode() >= 400 && response.statusCode() < 500) {
                throw new HttpClientErrorException(response);
            } else if (response.statusCode() >= 500 && response.statusCode() < 600) {
                throw new HttpServerErrorException(response);
            } else {
                throw new HttpClientResponseException(response);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to close http classic client response.", e);
        }
    }

    /**
     * 发送 Http 请求，获取 Http 响应的流式数据内容。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @return 表示 Http 响应的流式数据内容的 {@link Choir}{@code <}{@link Object}{@code >}。
     */
    Choir<Object> exchangeStream(HttpClassicClientRequest request);

    /**
     * 发送 Http 请求，获取 Http 响应的流式数据内容。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param responseType 表示期待的流式返回值类型的 {@link Type}。
     * @param <T> 表示期待的流式返回值类型的 {@link T}。
     * @return 表示 Http 响应的流式数据内容的 {@link Choir}{@code <}{@link T}{@code >}。
     */
    <T> Choir<T> exchangeStream(HttpClassicClientRequest request, Type responseType);
}
