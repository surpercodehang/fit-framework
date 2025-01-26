/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http;

import static modelengine.fit.http.header.HttpHeaderKey.FIT_TLV;
import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.DoHttpHandlerException;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.AbstractHttpHandler;
import modelengine.fit.serialization.MessageSerializer;
import modelengine.fit.serialization.http.HttpUtils;
import modelengine.fit.serialization.util.MessageSerializerUtils;
import modelengine.fit.server.http.support.AsyncTaskExecutor;
import modelengine.fit.server.http.util.HttpServerUtils;
import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.LocalGenericableRepository;
import modelengine.fitframework.broker.server.Dispatcher;
import modelengine.fitframework.broker.server.Response;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.serialization.RequestMetadata;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.Version;
import modelengine.fitframework.serialization.tlv.TlvUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 表示处理 FIT 通信方式的处理器。
 *
 * @author 季聿阶
 * @since 2022-09-14
 */
public class FitHttpHandler extends AbstractHttpHandler {
    private final BeanContainer container;
    private final Dispatcher dispatcher;
    private final LocalGenericableRepository repository;
    private final WorkerConfig workerConfig;

    FitHttpHandler(BeanContainer container, Dispatcher dispatcher, LocalGenericableRepository repository,
            WorkerConfig workerConfig, StaticInfo staticInfo, ExecutionInfo executionInfo) {
        super(staticInfo, executionInfo);
        this.container = container;
        this.dispatcher = dispatcher;
        this.repository = repository;
        this.workerConfig = workerConfig;
    }

    @Override
    public void handle(HttpClassicServerRequest request, HttpClassicServerResponse response)
            throws DoHttpHandlerException {
        RequestMetadata metadata = this.getRequestMetadata(request);
        Object[] data = this.getRequestData(request.entityBytes(), metadata);
        String asyncTaskId = HttpUtils.getAsyncTaskId(metadata.tagValues());
        if (StringUtils.isBlank(asyncTaskId)) {
            this.doSyncHandle(metadata, data, response);
        } else {
            this.doAsyncHandle(metadata, data, response);
        }
    }

    private RequestMetadata getRequestMetadata(HttpClassicServerRequest request) {
        List<String> pathList = StringUtils.split(request.path(), '/', ArrayList::new, StringUtils::isNotBlank);
        greaterThanOrEquals(pathList.size(), 3, "Illegal fit path. [path={0}]", request.path());
        String genericableId = pathList.get(pathList.size() - 2);
        String fitableId = pathList.get(pathList.size() - 1);
        TagLengthValues tagLengthValues = request.headers()
                .first(FIT_TLV.value())
                .map(HttpUtils::decode)
                .map(TagLengthValues::deserialize)
                .orElseGet(TagLengthValues::create);
        return RequestMetadata.custom()
                .dataFormat(HttpServerUtils.getDataFormat(request))
                .genericableId(genericableId)
                .genericableVersion(Version.builder(HttpServerUtils.getGenericableVersion(request)).build())
                .fitableId(fitableId)
                .fitableVersion(Version.builder(FitableMetadata.DEFAULT_VERSION).build())
                .tagValues(tagLengthValues)
                .build();
    }

    private Object[] getRequestData(byte[] dataBytes, RequestMetadata metadata) {
        Genericable genericable = this.getGenericable(metadata);
        Method method = genericable.method().method();
        notNull(method, "The genericable method cannot be null. [genericableId={0}]", genericable.id());
        int format = metadata.dataFormat();
        MessageSerializer messageSerializer = MessageSerializerUtils.getMessageSerializer(this.container, format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
        Type[] argumentTypes =
                Stream.of(method.getParameters()).map(Parameter::getParameterizedType).toArray(Type[]::new);
        return messageSerializer.deserializeRequest(argumentTypes, dataBytes);
    }

    private Genericable getGenericable(RequestMetadata metadata) {
        return this.repository.get(metadata.genericableId(), metadata.genericableVersion().toString())
                .orElseThrow(() -> new DoHttpHandlerException(StringUtils.format(
                        "No genericable. [genericableId={0}, genericableVersion={1}]",
                        metadata.genericableId(),
                        metadata.genericableVersion().toString())));
    }

    private void doSyncHandle(RequestMetadata metadata, Object[] data, HttpClassicServerResponse response) {
        Response result = this.dispatcher.dispatch(metadata, data);
        HttpServerUtils.setResponseCode(response, HttpResponseStatus.OK);
        HttpServerUtils.setResponseHeaders(response, result);
        HttpServerUtils.setResponseEntity(this.container, metadata.dataFormat(), response, result);
    }

    private void doAsyncHandle(RequestMetadata metadata, Object[] data, HttpClassicServerResponse response) {
        int code = AsyncTaskExecutor.INSTANCE.submit(metadata, () -> {
            Response asyncResult = this.dispatcher.dispatch(metadata, data);
            String asyncTaskId = HttpUtils.getAsyncTaskId(metadata.tagValues());
            HttpUtils.setAsyncTaskId(asyncResult.metadata().tagValues(), asyncTaskId);
            return asyncResult;
        });
        // 不等待计算任务结束，只返回异步任务提交结果。
        Response result =
                Response.create(ResponseMetadata.custom().dataFormat(metadata.dataFormat()).code(code).build());
        TlvUtils.setWorkerId(result.metadata().tagValues(), this.workerConfig.id());
        TlvUtils.setWorkerInstanceId(result.metadata().tagValues(), this.workerConfig.instanceId());
        HttpServerUtils.setResponseCode(response, HttpResponseStatus.ACCEPTED);
        HttpServerUtils.setResponseHeaders(response, result);
        HttpServerUtils.setResponseEntity(this.container, metadata.dataFormat(), response, result);
    }
}
