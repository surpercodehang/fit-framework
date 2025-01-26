/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

/**
 * 表示注册 Http 请求处理器时发生的异常。
 *
 * @author 季聿阶
 * @since 2022-07-20
 */
public class RegisterHttpHandlerException extends HttpServerException {
    /**
     * 通过异常消息来实例化 {@link RegisterHttpHandlerException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public RegisterHttpHandlerException(String message) {
        this(message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link RegisterHttpHandlerException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public RegisterHttpHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
