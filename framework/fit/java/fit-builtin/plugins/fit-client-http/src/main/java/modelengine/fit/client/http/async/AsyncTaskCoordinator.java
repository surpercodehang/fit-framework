/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.async;

import static modelengine.fit.serialization.http.Constants.FIT_ASYNC_LONG_POLLING_DURATION_MILLIS;

import modelengine.fit.client.Request;
import modelengine.fit.client.RequestContext;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.serialization.RequestMetadata;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * FIT 客户端异步任务运行协调中心，单例模式。
 *
 * @author 王成
 * @author 季聿阶
 * @since 2023-11-17
 */
class AsyncTaskCoordinator {
    /** 表示异步任务协调器的单例实现。 */
    static final AsyncTaskCoordinator INSTANCE = new AsyncTaskCoordinator();

    private static final long FIT_ASYNC_TIMEOUT_MILLIS = FIT_ASYNC_LONG_POLLING_DURATION_MILLIS * 2;

    private final ConcurrentHashMap<String, AsyncTaskServer> servers;

    private AsyncTaskCoordinator() {
        this.servers = new ConcurrentHashMap<>();
    }

    /**
     * 阻塞调用，获取任务执行结果。
     *
     * @param getTaskResultRequest 表示方法请求参数的 {@link GetAsyncTaskResultRequest}。
     * @return 表示异步任务执行结果的 {@link AsyncTaskResult}。
     */
    AsyncTaskResult getTaskResult(GetAsyncTaskResultRequest getTaskResultRequest) {
        AsyncTaskServer server;
        // 对哈希表的组合调用，需要对整个表加锁。
        synchronized (this.servers) {
            server = this.servers.get(getTaskResultRequest.targetWorkerId());
            // 如果这是向此服务器的第一次调用，则应创建新服务器对象。
            // 如果旧服务器对象的 instanceId 出现变化、或者已经被关闭，则应该使用新服务器对象替换。
            if (server == null || server.shouldBeReplaced(getTaskResultRequest.targetWorkerInstanceId())) {
                server = new AsyncTaskServer(getTaskResultRequest.container(),
                        getTaskResultRequest.workerConfig(),
                        getTaskResultRequest.client(),
                        this.createLongPollingRequest(getTaskResultRequest.request()),
                        getTaskResultRequest.targetWorkerInstanceId());
                AsyncTaskServer obsoleteServer = this.servers.put(getTaskResultRequest.targetWorkerId(), server);
                if (obsoleteServer != null) {
                    obsoleteServer.close();
                }
            }
        }
        return server.get(getTaskResultRequest.asyncTaskId());
    }

    private Request createLongPollingRequest(Request request) {
        return Request.custom()
                .protocol(request.protocol())
                .address(request.address())
                .returnType(request.returnType())
                .data(new Object[0])
                .metadata(RequestMetadata.custom().dataFormat(request.metadata().dataFormatByte()).build())
                .context(RequestContext.create(FIT_ASYNC_TIMEOUT_MILLIS,
                        TimeUnit.MILLISECONDS,
                        CommunicationType.SYNC,
                        new HashMap<>()))
                .build();
    }
}
