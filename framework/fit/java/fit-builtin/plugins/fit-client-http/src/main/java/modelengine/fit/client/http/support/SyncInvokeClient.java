/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.support;

import modelengine.fit.client.Request;
import modelengine.fit.client.Response;
import modelengine.fit.client.http.InvokeClient;
import modelengine.fit.client.http.util.HttpClientUtils;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.conf.runtime.ClientConfig;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanContainer;

import java.io.IOException;

/**
 * 表示 {@link InvokeClient} 的同步实现。
 *
 * @author 季聿阶
 * @since 2024-02-17
 */
public class SyncInvokeClient extends AbstractInvokeClient {
    public SyncInvokeClient(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        super(container, workerConfig, clientConfig);
    }

    @Override
    public Response requestResponse(@Nonnull Request request) {
        HttpClassicClient client = this.buildHttpClient(request);
        HttpClassicClientRequest clientRequest = this.buildClientRequest(client, request);
        clientRequest.entity(this.buildHttpEntity(clientRequest, request));
        try (HttpClassicClientResponse<Object> clientResponse = client.exchange(clientRequest, request.returnType())) {
            return HttpClientUtils.getResponse(this.getContainer(), request, clientResponse);
        } catch (IOException e) {
            throw new ClientException("Failed to close http classic client.", e);
        }
    }

    @Override
    public CommunicationType support() {
        return CommunicationType.SYNC;
    }
}
