/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http;

import modelengine.fit.client.Request;
import modelengine.fit.client.Response;
import modelengine.fit.client.http.async.AsyncInvokeClient;
import modelengine.fit.client.http.support.SyncInvokeClient;
import modelengine.fit.client.http.websocket.WebSocketInvokeClient;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.conf.runtime.ClientConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;

/**
 * 表示真实调用的客户端。
 *
 * @author 季聿阶
 * @since 2024-02-17
 */
public interface InvokeClient {
    /**
     * 请求一个响应。
     *
     * @param request 表示请求的 {@link Request}。调用保证请求一定不为 {@code null}。
     * @return 表示响应的 {@link Response}。
     */
    Response requestResponse(@Nonnull Request request);

    /**
     * 获取调用客户端支持的通信类型。
     *
     * @return 表示调用客户端支持的通信类型的 {@link CommunicationType}。
     */
    CommunicationType support();

    /**
     * 创建一个同步调用的客户端。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param workerConfig 表示进程配置信息的 {@link WorkerConfig}。
     * @param clientConfig 表示 Http 客户端配置的 {@link ClientConfig}。
     * @return 表示创建出来的同步调用的客户端的 {@link InvokeClient}。
     */
    static InvokeClient sync(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        return new SyncInvokeClient(container, workerConfig, clientConfig);
    }

    /**
     * 创建一个异步调用的客户端。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param workerConfig 表示进程配置信息的 {@link WorkerConfig}。
     * @param clientConfig 表示 Http 客户端配置的 {@link ClientConfig}。
     * @return 表示创建出来的异步调用的客户端的 {@link InvokeClient}。
     */
    static InvokeClient async(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        return new AsyncInvokeClient(container, workerConfig, clientConfig);
    }

    /**
     * 创建一个流式调用的客户端。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param workerConfig 表示进程配置信息的 {@link WorkerConfig}。
     * @param clientConfig 表示 Http 客户端配置的 {@link ClientConfig}。
     * @return 表示创建出来的流式调用的客户端的 {@link InvokeClient}。
     */
    static InvokeClient fluent(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        return new WebSocketInvokeClient(container, workerConfig, clientConfig);
    }
}
