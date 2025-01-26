/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.okhttp;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.Serializers;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.client.okhttp.websocket.OkHttpWebSocketSession;
import modelengine.fit.http.client.support.AbstractHttpClassicClient;
import modelengine.fit.http.client.support.DefaultHttpClassicClientRequest;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.client.WebSocketClassicListener;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.value.ValueFetcher;
import okhttp3.OkHttpClient;

import java.lang.reflect.Type;

/**
 * 表示 {@link HttpClassicClient} 的 OkHttp 实现。
 *
 * @author 杭潇
 * @since 2024-04-08
 */
public class OkHttpClassicClient extends AbstractHttpClassicClient {
    private final OkHttpClient okHttpClient;

    /**
     * 创建 {@link HttpClassicClient} 的 OkHttp 实现对象。
     *
     * @param serializers 表示序列化器集合的 {@link Serializers}。
     * @param valueFetcher 表示值的获取工具的 {@link ValueFetcher}。
     * @param okHttpClient 表示底层使用的 OkHttp 客户端的 {@link OkHttpClient}。
     */
    public OkHttpClassicClient(Serializers serializers, ValueFetcher valueFetcher, OkHttpClient okHttpClient) {
        super(serializers, valueFetcher);
        this.okHttpClient = notNull(okHttpClient, "The okhttp client cannot be null.");
    }

    @Override
    public HttpClassicClientRequest createRequest(HttpRequestMethod method, String url) {
        OkHttpClientRequest clientRequest = new OkHttpClientRequest(method, url, this.okHttpClient);
        return new DefaultHttpClassicClientRequest(this, clientRequest);
    }

    @Override
    public Session createWebSocketSession(String url, WebSocketClassicListener listener) {
        return new OkHttpWebSocketSession(this, url, listener);
    }

    @Override
    public HttpClassicClientResponse<Object> exchange(HttpClassicClientRequest request) {
        return this.exchange(request, Object.class);
    }

    @Override
    public <T> HttpClassicClientResponse<T> exchange(HttpClassicClientRequest request, Type responseType) {
        notNull(request, "The http classic request to exchange cannot be null.");
        return request.exchange(responseType);
    }

    @Override
    public Choir<Object> exchangeStream(HttpClassicClientRequest request) {
        return this.exchangeStream(request, Object.class);
    }

    @Override
    public <T> Choir<T> exchangeStream(HttpClassicClientRequest request, Type responseType) {
        notNull(request, "The http classic request to exchange cannot be null.");
        return request.exchangeStream(responseType);
    }
}
