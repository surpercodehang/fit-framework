/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http;

import static modelengine.fit.http.header.HttpHeaderKey.FIT_TLV;

import modelengine.fit.http.exception.AsyncTaskExecutionException;
import modelengine.fit.http.exception.AsyncTaskNotCompletedException;
import modelengine.fit.http.exception.AsyncTaskNotFoundException;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.DoHttpHandlerException;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.AbstractHttpHandler;
import modelengine.fit.serialization.http.HttpUtils;
import modelengine.fit.server.http.support.AsyncTaskExecutor;
import modelengine.fit.server.http.util.HttpServerUtils;
import modelengine.fitframework.broker.server.Response;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.serialization.RequestMetadata;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.tlv.TlvUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Optional;

/**
 * 表示处理 FIT 通信方式的处理器。
 *
 * @author 王成
 * @since 2023-11-16
 */
public class FitHttpAsyncTaskHandler extends AbstractHttpHandler {
    private final BeanContainer container;
    private final WorkerConfig worker;

    FitHttpAsyncTaskHandler(BeanContainer container, WorkerConfig worker, StaticInfo staticInfo,
            ExecutionInfo executionInfo) {
        super(staticInfo, executionInfo);
        this.container = container;
        this.worker = worker;
    }

    @Override
    public void handle(HttpClassicServerRequest request, HttpClassicServerResponse response)
            throws DoHttpHandlerException {
        RequestMetadata metadata = this.getRequestMetadata(request);
        try {
            String sourceWorkerId = TlvUtils.getWorkerId(metadata.tagValues());
            String sourceWorkerInstanceId = TlvUtils.getWorkerInstanceId(metadata.tagValues());
            Optional<Response> resultOp =
                    AsyncTaskExecutor.INSTANCE.longPolling(sourceWorkerId, sourceWorkerInstanceId);
            if (resultOp.isPresent()) {
                HttpServerUtils.setResponseCode(response, HttpResponseStatus.OK);
                Response result = resultOp.get();
                HttpServerUtils.setResponseHeaders(response, result);
                HttpServerUtils.setResponseEntity(this.container, metadata.dataFormat(), response, result);
            } else {
                this.fail(response, metadata.dataFormat(), AsyncTaskNotCompletedException.CODE, StringUtils.EMPTY);
            }
        } catch (AsyncTaskNotFoundException | AsyncTaskExecutionException e) {
            this.fail(response, metadata.dataFormat(), e.getCode(), e.getMessage());
        }
    }

    private void fail(HttpClassicServerResponse response, int dataFormatCode, int code, String message) {
        HttpServerUtils.setResponseCode(response, HttpResponseStatus.OK);
        Response result = Response.create(ResponseMetadata.custom()
                .dataFormat(dataFormatCode)
                .code(code)
                .message(message)
                .build());
        TlvUtils.setWorkerId(result.metadata().tagValues(), this.worker.id());
        TlvUtils.setWorkerInstanceId(result.metadata().tagValues(), this.worker.instanceId());
        HttpServerUtils.setResponseHeaders(response, result);
    }

    private RequestMetadata getRequestMetadata(HttpClassicServerRequest request) {
        int dataFormat = HttpServerUtils.getDataFormat(request);
        TagLengthValues tagLengthValues = request.headers()
                .first(FIT_TLV.value())
                .map(HttpUtils::decode)
                .map(TagLengthValues::deserialize)
                .orElseGet(TagLengthValues::create);
        return RequestMetadata.custom().dataFormat(dataFormat).tagValues(tagLengthValues).build();
    }
}
