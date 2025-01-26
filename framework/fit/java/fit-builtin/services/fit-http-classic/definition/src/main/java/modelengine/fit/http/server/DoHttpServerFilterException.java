/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.protocol.HttpResponseStatus;

/**
 * 表示执行 {@link HttpServerFilter#doFilter(HttpClassicServerRequest, HttpClassicServerResponse,
 * HttpServerFilterChain)} 过程中发生的异常。
 *
 * @author 季聿阶
 * @since 2022-07-18
 */
public class DoHttpServerFilterException extends HttpServerResponseException {
    /**
     * 通过异常消息来实例化 {@link DoHttpServerFilterException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public DoHttpServerFilterException(String message) {
        this(message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link DoHttpServerFilterException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public DoHttpServerFilterException(String message, Throwable cause) {
        super(HttpResponseStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}
