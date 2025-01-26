/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.HttpResponseStatus;

/**
 * 表示 Http 服务端的带响应的异常。
 *
 * @author 季聿阶
 * @since 2022-07-08
 */
public class HttpServerResponseException extends HttpServerException {
    private final HttpResponseStatus status;

    /**
     * 通过异常消息来实例化 {@link HttpServerResponseException}。
     *
     * @param status 表示 Http 响应的状态的 {@link HttpResponseStatus}。
     * @param message 表示异常消息的 {@link String}。
     */
    public HttpServerResponseException(HttpResponseStatus status, String message) {
        this(status, message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link HttpServerResponseException}。
     *
     * @param status 表示 Http 响应的状态的 {@link HttpResponseStatus}。
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public HttpServerResponseException(HttpResponseStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = notNull(status, "The response status cannot be null.");
    }

    /**
     * 获取 Http 响应的状态。
     *
     * @return 表示 Http 响应状态的 {@link HttpResponseStatus}。
     */
    public HttpResponseStatus responseStatus() {
        return this.status;
    }
}
