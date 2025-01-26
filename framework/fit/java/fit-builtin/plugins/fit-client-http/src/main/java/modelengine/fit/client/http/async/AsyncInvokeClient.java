/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.async;

import modelengine.fit.client.Request;
import modelengine.fit.client.Response;
import modelengine.fit.client.http.InvokeClient;
import modelengine.fit.client.http.support.AbstractInvokeClient;
import modelengine.fit.client.http.util.HttpClientUtils;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.serialization.http.HttpUtils;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.conf.runtime.ClientConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.tlv.TlvUtils;
import modelengine.fitframework.util.UuidUtils;

import java.io.IOException;

/**
 * 表示 {@link InvokeClient} 的异步实现。
 *
 * @author 季聿阶
 * @since 2024-02-17
 */
public class AsyncInvokeClient extends AbstractInvokeClient {
    private static final Logger log = Logger.get(AsyncInvokeClient.class);
    private static final int FIT_ASYNC_TIMEOUT_MILLISECONDS = 300_000;

    public AsyncInvokeClient(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        super(container, workerConfig, clientConfig);
    }

    @Override
    public Response requestResponse(@Nonnull Request request) {
        HttpClassicClient client = this.buildHttpClient(request);
        String asyncTaskId = UuidUtils.randomUuidString();
        String targetWorkerId;
        String targetWorkerInstanceId;

        // 第一步：提交异步任务。
        HttpClassicClientRequest clientRequest = this.buildAsyncClientRequest(client, request, asyncTaskId);
        clientRequest.entity(this.buildHttpEntity(clientRequest, request));
        try (HttpClassicClientResponse<Object> clientResponse = client.exchange(clientRequest, request.returnType())) {
            Response response = HttpClientUtils.getResponse(this.getContainer(), request, clientResponse);
            if (clientResponse.statusCode() != HttpResponseStatus.ACCEPTED.statusCode()) {
                // 服务器不支持异步长任务，应按照同步请求处理。
                log.warn("Async task not supported. Use Sync instead. [asyncTaskId={}]", asyncTaskId);
                return response;
            } else if (response.metadata().code() != ResponseMetadata.CODE_OK) {
                // 如果返回值不为 OK，则将结果返回给上层。
                return response;
            } else {
                targetWorkerId = TlvUtils.getWorkerId(response.metadata().tagValues());
                targetWorkerInstanceId = TlvUtils.getWorkerInstanceId(response.metadata().tagValues());
            }
        } catch (IOException e) {
            throw new ClientException("Failed to close http classic client response.", e);
        }

        // 第二步：从指定服务器的长轮询链接中获取异步结果数据，该操作为阻塞操作。
        GetAsyncTaskResultRequest getTaskResultRequest = GetAsyncTaskResultRequest.custom()
                .container(this.getContainer())
                .client(client)
                .request(request)
                .workerConfig(this.getWorkerConfig())
                .asyncTaskId(asyncTaskId)
                .targetWorkerId(targetWorkerId)
                .targetWorkerInstanceId(targetWorkerInstanceId)
                .build();
        return AsyncTaskCoordinator.INSTANCE.getTaskResult(getTaskResultRequest).getResponse();
    }

    @Override
    protected int getTimeout(Request request) {
        return Math.max(FIT_ASYNC_TIMEOUT_MILLISECONDS, super.getTimeout(request));
    }

    @Override
    public CommunicationType support() {
        return CommunicationType.ASYNC;
    }

    private HttpClassicClientRequest buildAsyncClientRequest(HttpClassicClient client, Request request, String taskId) {
        HttpUtils.setAsyncTaskId(request.metadata().tagValues(), taskId);
        return this.buildClientRequest(client, request);
    }
}
