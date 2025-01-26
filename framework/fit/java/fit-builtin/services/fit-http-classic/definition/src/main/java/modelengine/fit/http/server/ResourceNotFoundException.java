/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.protocol.HttpResponseStatus;

/**
 * 表示资源未找到的异常。
 *
 * @author 邬涨财
 * @since 2023-08-09
 */
public class ResourceNotFoundException extends HttpServerResponseException {
    /**
     * 通过异常消息来实例化 {@link ResourceNotFoundException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public ResourceNotFoundException(String message) {
        this(message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link ResourceNotFoundException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(HttpResponseStatus.NOT_FOUND, message, cause);
    }
}
