/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.websocket;

import modelengine.fit.client.Request;
import modelengine.fit.client.Response;
import modelengine.fit.client.http.InvokeClient;
import modelengine.fit.client.http.support.AbstractInvokeClient;
import modelengine.fit.client.http.support.ConnectionBuilder;
import modelengine.fit.client.http.support.ConnectionBuilderFactory;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.protocol.Protocol;
import modelengine.fit.http.websocket.Session;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.conf.runtime.ClientConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;

/**
 * 表示 {@link InvokeClient} 的 WebSocket 流式实现。
 *
 * @author 季聿阶
 * @since 2024-05-06
 */
public class WebSocketInvokeClient extends AbstractInvokeClient {
    public WebSocketInvokeClient(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        super(container, workerConfig, clientConfig);
    }

    @Override
    public Response requestResponse(@Nonnull Request request) {
        HttpClassicClient client = this.buildHttpClient(request);
        ConnectionBuilder builder = ConnectionBuilderFactory.getConnectionBuilder(Protocol.from(request.protocol()));
        String url = builder.buildUrl(request);
        WebsocketInvoker invoker = new WebsocketInvoker(this.getContainer(), request);
        Session session = client.createWebSocketSession(url, invoker);
        invoker.request(session, request);
        try {
            return invoker.waitAndgetResponse();
        } catch (InterruptedException cause) {
            throw new ClientException("Failed to wait websocket request.", cause);
        }
    }

    @Override
    public CommunicationType support() {
        return CommunicationType.SYNC;
    }
}
