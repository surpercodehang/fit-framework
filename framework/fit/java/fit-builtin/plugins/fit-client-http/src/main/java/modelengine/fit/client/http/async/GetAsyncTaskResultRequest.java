/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.async;

import modelengine.fit.client.Request;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.pattern.builder.BuilderFactory;

/**
 * 表示获取异步任务结果方法的参数集合。
 *
 * @author 季聿阶
 * @since 2024-02-19
 */
public interface GetAsyncTaskResultRequest {
    /**
     * 获取 Bean 容器。
     *
     * @return 表示 Bean 容器的 {@link BeanContainer}。
     */
    BeanContainer container();

    /**
     * 获取 Http 调用客户端。
     *
     * @return 表示 Http 调用客户端的 {@link HttpClassicClient}。
     */
    HttpClassicClient client();

    /**
     * 获取调用请求。
     *
     * @return 表示调用请求的 {@link Request}。
     */
    Request request();

    /**
     * 获取当前进程的配置信息。
     *
     * @return 表示当前进程配置信息的 {@link WorkerConfig}。
     */
    WorkerConfig workerConfig();

    /**
     * 获取异步任务的唯一标识。
     *
     * @return 表示异步任务唯一标识的 {@link String}。
     */
    String asyncTaskId();

    /**
     * 获取目标进程的唯一标识。
     *
     * @return 表示目标进程唯一标识的 {@link String}。
     */
    String targetWorkerId();

    /**
     * 获取目标进程实例的唯一标识。
     *
     * @return 表示目标进程实例唯一标识的 {@link String}。
     */
    String targetWorkerInstanceId();

    /**
     * 表示 {@link GetAsyncTaskResultRequest} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置 Bean 容器。
         *
         * @param container 表示待设置的 Bean 容器的 {@link BeanContainer}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder container(BeanContainer container);

        /**
         * 向当前构建器中设置 Http 调用的客户端。
         *
         * @param client 表示待设置的 Http 调用的客户端的 {@link HttpClassicClient}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder client(HttpClassicClient client);

        /**
         * 向当前构建器中设置调用请求。
         *
         * @param request 表示待设置的调用请求的 {@link Request}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder request(Request request);

        /**
         * 向当前构建器中设置当前进程的配置。
         *
         * @param workerConfig 表示待设置的当前进程配置的 {@link WorkerConfig}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder workerConfig(WorkerConfig workerConfig);

        /**
         * 向当前构建器中设置异步任务的唯一标识。
         *
         * @param asyncTaskId 表示待设置的异步任务的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder asyncTaskId(String asyncTaskId);

        /**
         * 向当前构建器中设置目标进程的唯一标识。
         *
         * @param targetWorkerId 表示待设置的目标进程的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder targetWorkerId(String targetWorkerId);

        /**
         * 向当前构建器中设置目标进程实例的唯一标识。
         *
         * @param targetWorkerInstanceId 表示待设置的目标进程实例的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder targetWorkerInstanceId(String targetWorkerInstanceId);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link GetAsyncTaskResultRequest}。
         */
        GetAsyncTaskResultRequest build();
    }

    /**
     * 获取 {@link GetAsyncTaskResultRequest} 的构建器。
     *
     * @return 表示 {@link GetAsyncTaskResultRequest} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link GetAsyncTaskResultRequest} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link GetAsyncTaskResultRequest}。
     * @return 表示 {@link GetAsyncTaskResultRequest} 的构建器的 {@link Builder}。
     */
    static Builder custom(GetAsyncTaskResultRequest value) {
        return BuilderFactory.get(GetAsyncTaskResultRequest.class, Builder.class).create(value);
    }
}
