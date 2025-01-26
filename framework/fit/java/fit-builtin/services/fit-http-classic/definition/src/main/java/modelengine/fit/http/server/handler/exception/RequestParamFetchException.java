/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.exception;

/**
 * 表示提取请求参数失败的异常。
 *
 * @author 曹嘉美
 * @since 2024-11-27
 */
public class RequestParamFetchException extends RuntimeException {
    /**
     * 使用异常信息初始化 {@link RequestParamFetchException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public RequestParamFetchException(String message) {
        super(message);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link RequestParamFetchException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public RequestParamFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}