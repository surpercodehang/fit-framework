/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.util;

import static modelengine.fit.http.header.HttpHeaderKey.FIT_ACCESS_TOKEN;
import static modelengine.fit.http.header.HttpHeaderKey.FIT_CODE;
import static modelengine.fit.http.header.HttpHeaderKey.FIT_DATA_FORMAT;
import static modelengine.fit.http.header.HttpHeaderKey.FIT_GENERICABLE_VERSION;
import static modelengine.fit.http.header.HttpHeaderKey.FIT_MESSAGE;
import static modelengine.fit.http.header.HttpHeaderKey.FIT_TLV;

import modelengine.fit.client.Request;
import modelengine.fit.client.Response;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fit.serialization.MessageSerializer;
import modelengine.fit.serialization.http.HttpUtils;
import modelengine.fit.serialization.util.MessageSerializerUtils;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.resource.UrlUtils;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.tlv.TlvUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Type;

/**
 * FIT 调用关于 Http 客户端相关的工具类。
 *
 * @author 王成
 * @since 2023-11-17
 */
public class HttpClientUtils {
    /**
     * 获取 Http 响应中的 TLV。
     *
     * @param clientResponse 表示响应的 {@link HttpClassicClientResponse}{@code <}{@link Object}{@code >}。
     * @return 表示响应中的 TLV 的 {@link TagLengthValues}。
     */
    public static TagLengthValues getResponseTagLengthValue(HttpClassicClientResponse<Object> clientResponse) {
        return clientResponse.headers()
                .first(FIT_TLV.value())
                .map(HttpUtils::decode)
                .map(TagLengthValues::deserialize)
                .orElseGet(TagLengthValues::create);
    }

    /**
     * 向 Http 请求中设置消息头。
     *
     * @param clientRequest 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param request 表示 Http 请求信息的 {@link Request}。
     * @param workerConfig 表示当前进程配置信息的 {@link WorkerConfig}。
     */
    public static void fillBaseHeaders(HttpClassicClientRequest clientRequest, Request request,
            WorkerConfig workerConfig) {
        TagLengthValues tagLengthValues = request.metadata().tagValues();
        TlvUtils.setWorkerId(tagLengthValues, workerConfig.id());
        TlvUtils.setWorkerInstanceId(tagLengthValues, workerConfig.instanceId());
        clientRequest.headers()
                .add(FIT_DATA_FORMAT.value(), String.valueOf(request.metadata().dataFormat()))
                .add(FIT_GENERICABLE_VERSION.value(), request.metadata().genericableVersion().toString())
                .add(FIT_TLV.value(), HttpUtils.encode(tagLengthValues.serialize()))
                .add(MessageHeaderNames.ACCEPT, MimeType.APPLICATION_OCTET_STREAM.value());
        if (StringUtils.isNotBlank(request.metadata().accessToken())) {
            clientRequest.headers().add(FIT_ACCESS_TOKEN.value(), request.metadata().accessToken());
        }
    }

    /**
     * 获取 Http 响应。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param request 表示 Http 请求的 {@link Request}。
     * @param clientResponse 表示 Http 客户端响应的 {@link HttpClassicClientResponse}{@code <}{@link Object}{@code >}。
     * @return 表示 Http 响应的 {@link Response}。
     */
    public static Response getResponse(BeanContainer container, Request request,
            HttpClassicClientResponse<Object> clientResponse) {
        ResponseMetadata responseMetadata = HttpClientUtils.getResponseMetadata(request, clientResponse);
        if (responseMetadata.code() == ResponseMetadata.CODE_OK) {
            Object result = getResponseData(container, request, responseMetadata, clientResponse);
            return Response.create(responseMetadata, result);
        }
        return Response.create(responseMetadata, null);
    }

    /**
     * 获取 Http 响应中的错误码。
     *
     * @param request 表示 Http 请求的 {@link Request}。
     * @param clientResponse 表示 Http 客户端响应的 {@link HttpClassicClientResponse}{@code <}{@link Object}{@code >}。
     * @return 表示错误码的 {@code int}。
     */
    public static int getResponseCode(Request request, HttpClassicClientResponse<Object> clientResponse) {
        String code = clientResponse.headers()
                .first(FIT_CODE.value())
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No response code. [protocol={0}, address={1}, header={2}]",
                        request.protocol(),
                        request.address(),
                        FIT_CODE.value())));
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Incorrect response code. [protocol={0}, address={1}, code={2}]",
                    request.protocol(),
                    request.address(),
                    code));
        }
    }

    private static Object getResponseData(BeanContainer container, Request request, ResponseMetadata responseMetadata,
            HttpClassicClientResponse<Object> clientResponse) {
        int format = responseMetadata.dataFormat();
        MessageSerializer messageSerializer = MessageSerializerUtils.getMessageSerializer(container, format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
        return messageSerializer.deserializeResponse(request.returnType(), clientResponse.entityBytes());
    }

    private static ResponseMetadata getResponseMetadata(Request request,
            HttpClassicClientResponse<Object> clientResponse) {
        return ResponseMetadata.custom()
                .dataFormat(getResponseDataFormat(request, clientResponse))
                .code(getResponseCode(request, clientResponse))
                .message(getResponseMessage(clientResponse))
                .tagValues(HttpClientUtils.getResponseTagLengthValue(clientResponse))
                .build();
    }

    private static int getResponseDataFormat(Request request, HttpClassicClientResponse<Object> clientResponse) {
        String dataFormat = clientResponse.headers()
                .first(FIT_DATA_FORMAT.value())
                .orElse(String.valueOf(SerializationFormat.UNKNOWN.code()));
        try {
            return Integer.parseInt(dataFormat);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Incorrect response data format. [protocol={0}, address={1}, dataFormat={2}]",
                    request.protocol(),
                    request.address(),
                    dataFormat));
        }
    }

    private static String getResponseMessage(HttpClassicClientResponse<Object> clientResponse) {
        String encodedMessage = clientResponse.headers().first(FIT_MESSAGE.value()).orElse(StringUtils.EMPTY);
        return UrlUtils.decodePath(encodedMessage);
    }

    /**
     * 判断指定类型是否为响应式支持的类型。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @return 如果指定类型为响应式支持的类型，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isReactor(Type type) {
        if (type == null) {
            return false;
        }
        Class<?> clazz = TypeUtils.toClass(type);
        return Publisher.class.isAssignableFrom(clazz);
    }
}
