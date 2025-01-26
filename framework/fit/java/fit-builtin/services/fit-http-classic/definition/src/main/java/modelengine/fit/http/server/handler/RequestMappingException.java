/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpServerResponseException;

/**
 * 表示 Http 请求映射的异常。
 *
 * @author 季聿阶
 * @since 2022-07-08
 */
public class RequestMappingException extends HttpServerResponseException {
    /**
     * 通过异常消息来实例化 {@link RequestMappingException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public RequestMappingException(String message) {
        this(message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link RequestMappingException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public RequestMappingException(String message, Throwable cause) {
        super(HttpResponseStatus.BAD_REQUEST, message, cause);
    }
}
