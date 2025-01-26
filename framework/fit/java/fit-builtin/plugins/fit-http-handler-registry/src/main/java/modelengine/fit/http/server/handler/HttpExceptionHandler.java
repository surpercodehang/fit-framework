/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fitframework.annotation.Scope;

/**
 * 表示处理 {@link ExceptionHandler} 的处理器。
 *
 * @author 季聿阶
 * @since 2022-08-25
 */
public interface HttpExceptionHandler {
    /**
     * 获取异常返回的状态码。
     *
     * @return 表示异常返回的常态码的 {@code int}。
     */
    int statusCode();

    /**
     * 处理异常。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示 Http 响应的 {@link HttpClassicServerResponse}。
     * @param cause 表示待处理的异常的 {@link Throwable}。
     * @return 表示处理异常后的返回值的 {@link Object}。
     */
    Object handle(HttpClassicServerRequest request, HttpClassicServerResponse response, Throwable cause);

    /**
     * 获取异常处理器的生效范围。
     *
     * @return 表示异常处理器的生效范围的 {@link Scope}。
     */
    Scope scope();
}
