/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

/**
 * 表示 Http 响应的状态码为 5xx 的异常。
 *
 * @author 季聿阶
 * @since 2023-01-29
 */
public class HttpServerErrorException extends HttpClientResponseException {
    /**
     * 使用指定的响应初始化 {@link HttpServerErrorException} 的新实例。
     *
     * @param response 表示响应的 {@link HttpClassicClientResponse}。
     * @throws IllegalArgumentException 当 {@code response} 为 {@code null} 时。
     */
    public HttpServerErrorException(HttpClassicClientResponse<?> response) {
        super(response);
    }

    /**
     * 使用指定的响应和原因初始化 {@link HttpServerErrorException} 的新实例。
     *
     * @param response 表示响应的 {@link HttpClassicClientResponse}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @throws IllegalArgumentException 当 {@code response} 为 {@code null} 时。
     */
    public HttpServerErrorException(HttpClassicClientResponse<?> response, Throwable cause) {
        super(response, cause);
    }
}
