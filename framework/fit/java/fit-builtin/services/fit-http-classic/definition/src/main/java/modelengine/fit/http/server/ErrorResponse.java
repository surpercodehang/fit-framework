/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.HttpResponseStatus;

import java.time.LocalDateTime;

/**
 * 表示默认的内部错误的返回值。
 *
 * @author 季聿阶
 * @since 2022-08-21
 */
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String suppressed;
    private final String path;
    private final String timestamp;

    private ErrorResponse(HttpResponseStatus status, String error, String suppressed, String path) {
        this.status = notNull(status, "The response status cannot be null.").statusCode();
        this.error = error;
        this.suppressed = suppressed;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
    }

    /**
     * 通过 Http 响应状态、错误信息和请求的路径来实例化 {@link ErrorResponse}。
     *
     * @param status 表示 Http 响应状态的 {@link HttpResponseStatus}。
     * @param error 表示 Http 响应的错误信息的 {@link String}。
     * @param path 表示 Http 请求路径的 {@link String}。
     * @return 表示创建的内部错误的返回值对象。
     * @throws IllegalArgumentException 当 {@code status} 为 {@code null} 时。
     */
    public static ErrorResponse create(HttpResponseStatus status, String error, String path) {
        return new ErrorResponse(status, error, null, path);
    }

    /**
     * 通过 Http 响应状态、错误信息、被抑制的错误信息和请求的路径来实例化 {@link ErrorResponse}。
     *
     * @param status 表示 Http 响应状态的 {@link HttpResponseStatus}。
     * @param error 表示 Http 响应的错误信息的 {@link String}。
     * @param suppressed 表示被抑制的错误信息的 {@link String}。
     * @param path 表示 Http 请求路径的 {@link String}。
     * @return 表示创建的内部错误的返回值对象。
     * @throws IllegalArgumentException 当 {@code status} 为 {@code null} 时。
     */
    public static ErrorResponse create(HttpResponseStatus status, String error, String suppressed, String path) {
        return new ErrorResponse(status, error, suppressed, path);
    }

    /**
     * 获取内部错误的 Http 状态码。
     *
     * @return 表示内部错误的 Http 状态码的 {@code int}。
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * 获取内部错误的 Http 错误信息。
     *
     * @return 表示内部错误的 Http 错误信息的 {@link String}。
     */
    public String getError() {
        return this.error;
    }

    /**
     * 获取内部错误的被抑制的错误信息。
     *
     * @return 表示内部错误的被抑制的错误信息的 {@link String}。
     */
    public String getSuppressed() {
        return this.suppressed;
    }

    /**
     * 获取内部错误的 Http 请求路径。
     *
     * @return 表示内部错误的 Http 请求路径的 {@link String}。
     */
    public String getPath() {
        return this.path;
    }

    /**
     * 获取内部错误的发生时间戳。
     *
     * @return 表示内部错误的发生时间戳的 {@link String}。
     */
    public String getTimestamp() {
        return this.timestamp;
    }
}
