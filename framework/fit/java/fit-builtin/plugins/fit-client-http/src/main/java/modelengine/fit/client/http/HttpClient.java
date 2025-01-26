/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.client.Client;
import modelengine.fit.client.Request;
import modelengine.fit.client.Response;
import modelengine.fit.client.http.util.HttpClientUtils;
import modelengine.fit.http.protocol.Protocol;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.conf.runtime.ClientConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.MapBuilder;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 Broker 提供基于 HTTP 调用的客户端。
 *
 * @author 季聿阶
 * @author 张越
 * @author 詹高扬
 * @since 2020-10-05
 */
@Component
public class HttpClient implements Client {
    private final Map<CommunicationType, InvokeClient> clients;
    private final InvokeClient fluentClient;

    /**
     * 创建 Http 客户端。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param workerConfig 表示当前进程的配置的 {@link WorkerConfig}。
     * @param clientConfig 表示 Http 客户端配置的 {@link ClientConfig}。
     */
    public HttpClient(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        notNull(container, "The bean container cannot be null.");
        notNull(workerConfig, "The worker config cannot be null.");
        InvokeClient syncInvokeClient = InvokeClient.sync(container, workerConfig, clientConfig);
        InvokeClient asyncInvokeClient = InvokeClient.async(container, workerConfig, clientConfig);
        this.clients = MapBuilder.<CommunicationType, InvokeClient>get()
                .put(syncInvokeClient.support(), syncInvokeClient)
                .put(asyncInvokeClient.support(), asyncInvokeClient)
                .build();
        this.fluentClient = InvokeClient.fluent(container, workerConfig, clientConfig);
    }

    @Override
    public Response requestResponse(@Nonnull Request request) {
        if (this.isFluent(request)) {
            return this.fluentClient.requestResponse(request);
        }
        return this.clients.get(request.context().communicationType()).requestResponse(request);
    }

    private boolean isFluent(Request request) {
        for (Type type : request.dataTypes()) {
            if (HttpClientUtils.isReactor(type)) {
                return true;
            }
        }
        return HttpClientUtils.isReactor(request.returnType());
    }

    @Override
    public Set<String> getSupportedProtocols() {
        return Stream.of(Protocol.values()).map(Protocol::protocol).collect(Collectors.toSet());
    }
}
