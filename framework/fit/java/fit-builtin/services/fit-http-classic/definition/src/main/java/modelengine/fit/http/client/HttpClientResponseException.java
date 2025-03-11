/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.http.client;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 表示携带 Http 响应的异常。
 *
 * @author 季聿阶
 * @since 2023-01-29
 */
public class HttpClientResponseException extends HttpClientException {
    /** 表示 Http 客户端请求的 {@link HttpClassicClientRequest}。 */
    private final HttpClassicClientRequest request;

    /** 表示 Http 客户端响应的 {@link HttpClassicClientResponse}。 */
    private final HttpClassicClientResponse<?> response;

    /**
     * 创建携带 Http 请求和响应的异常对象。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param response 表示 Http 响应的 {@link HttpClassicClientResponse}{@code <?>}。
     */
    public HttpClientResponseException(HttpClassicClientRequest request, HttpClassicClientResponse<?> response) {
        this(request, response, null);
    }

    /**
     * 创建携带 Http 请求和响应的异常对象。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param response 表示 Http 响应的 {@link HttpClassicClientResponse}{@code <?>}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public HttpClientResponseException(HttpClassicClientRequest request, HttpClassicClientResponse<?> response,
            Throwable cause) {
        super(simpleMessage(request, response), cause);
        this.request = request;
        this.response = response;
    }

    /**
     * 获取 Http 响应的状态码。
     *
     * @return 表示 Http 响应的状态码的 {@code int}。
     */
    public int statusCode() {
        return this.response.statusCode();
    }

    /**
     * 获取简单错误信息。
     *
     * @return 表示简单错误信息的 {@link String}。
     */
    public String getSimpleMessage() {
        return simpleMessage(this.request, this.response);
    }

    /**
     * 获取详细错误信息。
     *
     * @return 表示详细错误信息的 {@link String}。
     */
    public String getDetailMessage() {
        return detailMessage(this.request, this.response);
    }

    private static String detailMessage(HttpClassicClientRequest request, HttpClassicClientResponse<?> response) {
        notNull(request, "The http request cannot be null.");
        notNull(response, "The http response cannot be null.");
        StringBuilder errorMessage = new StringBuilder();
        String newLine = System.lineSeparator();

        // 请求信息
        errorMessage.append("HTTP Request Failed").append(newLine);
        errorMessage.append("Request URL: ").append(request.path()).append(newLine);
        errorMessage.append("HTTP Method: ").append(request.method()).append(newLine);

        // 请求头信息
        errorMessage.append("Request Headers:").append(newLine);
        List<String> names = request.headers().names();
        for (String name : names) {
            List<String> values = request.headers().all(name);
            errorMessage.append("  ").append(name).append(": ").append(String.join(", ", values)).append(newLine);
        }

        // 响应状态
        errorMessage.append(newLine).append("Response Status: ").append(response.statusCode());
        if (StringUtils.isNotBlank(response.reasonPhrase())) {
            errorMessage.append(" (").append(response.reasonPhrase()).append(")");
        }
        errorMessage.append(newLine);

        // 响应头信息
        errorMessage.append("Response Headers:").append(newLine);
        names = response.headers().names();
        for (String name : names) {
            List<String> values = response.headers().all(name);
            errorMessage.append("  ").append(name).append(": ").append(String.join(", ", values)).append(newLine);
        }

        // 响应体
        String responseBody = new String(response.entityBytes(), StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(responseBody)) {
            errorMessage.append("Response Body: ").append(responseBody);
        }

        return errorMessage.toString();
    }

    private static String simpleMessage(HttpClassicClientRequest request, HttpClassicClientResponse<?> response) {
        notNull(request, "The http request cannot be null.");
        notNull(response, "The http response cannot be null.");
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(response.statusCode());
        if (StringUtils.isNotBlank(response.reasonPhrase())) {
            errorMessage.append("(").append(response.reasonPhrase()).append(")");
        }
        String message = new String(response.entityBytes(), StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(message)) {
            errorMessage.append(": ").append(message);
        }
        return errorMessage.toString();
    }
}
