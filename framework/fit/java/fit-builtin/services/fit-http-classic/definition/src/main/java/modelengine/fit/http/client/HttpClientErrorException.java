/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.http.client;

/**
 * 表示 Http 响应的状态码为 4xx 的异常。
 *
 * @author 季聿阶
 * @since 2023-01-29
 */
public class HttpClientErrorException extends HttpClientResponseException {
    /**
     * 使用指定的请求和响应初始化 {@link HttpClientErrorException} 的新实例。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param response 表示响应的 {@link HttpClassicClientResponse}。
     * @throws IllegalArgumentException 当 {@code response} 为 {@code null} 时。
     */
    public HttpClientErrorException(HttpClassicClientRequest request, HttpClassicClientResponse<?> response) {
        super(request, response);
    }

    /**
     * 使用指定的请求、响应和原因初始化 {@link HttpClientErrorException} 的新实例。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param response 表示响应的 {@link HttpClassicClientResponse}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @throws IllegalArgumentException 当 {@code response} 为 {@code null} 时。
     */
    public HttpClientErrorException(HttpClassicClientRequest request, HttpClassicClientResponse<?> response,
            Throwable cause) {
        super(request, response, cause);
    }
}
