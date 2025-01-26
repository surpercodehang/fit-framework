/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

import static modelengine.fitframework.inspection.Validation.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.Serializers;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.client.WebSocketClassicListener;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.value.ValueFetcher;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * {@link HttpClassicClient} 的实现类
 *
 * @author 杭潇
 * @since 2023-02-20
 */
public class HttpClassicClientImplement implements HttpClassicClient {
    /**
     * 创建 Http 客户端对象。
     */
    public HttpClassicClientImplement() {}

    @Override
    public Serializers serializers() {
        return null;
    }

    @Override
    public ValueFetcher valueFetcher() {
        return null;
    }

    @Override
    public HttpClassicClientRequest createRequest(HttpRequestMethod method, String url) {
        return null;
    }

    @Override
    public Session createWebSocketSession(String url, WebSocketClassicListener listener) {
        return null;
    }

    @Override
    public HttpClassicClientResponse<Object> exchange(HttpClassicClientRequest request) {
        return this.exchange(request, Object.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> HttpClassicClientResponse<T> exchange(HttpClassicClientRequest request, Type responseType) {
        notNull(request, "The http classic request to exchange cannot be null.");
        HttpClassicClientResponse httpClassicClientResponse = mock(HttpClassicClientResponse.class);
        ObjectEntity objectEntity = mock(ObjectEntity.class);
        when(objectEntity.object()).thenReturn(24);
        Optional<ObjectEntity> optionalObjectEntity = Optional.of(objectEntity);
        when(httpClassicClientResponse.objectEntity()).thenReturn(optionalObjectEntity);
        TextEntity textEntity = mock(TextEntity.class);
        when(textEntity.content()).thenReturn("finishTextEntity");
        Optional<TextEntity> optionalTextEntity = Optional.of(textEntity);
        when(httpClassicClientResponse.textEntity()).thenReturn(optionalTextEntity);
        int statusCode = request.exchange(responseType).statusCode();
        when(httpClassicClientResponse.statusCode()).thenReturn(statusCode);
        return httpClassicClientResponse;
    }

    @Override
    public Choir<Object> exchangeStream(HttpClassicClientRequest request) {
        return null;
    }

    @Override
    public <T> Choir<T> exchangeStream(HttpClassicClientRequest request, Type responseType) {
        return null;
    }
}
