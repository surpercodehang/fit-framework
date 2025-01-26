/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external;

import modelengine.fit.ohscript.script.errors.ScriptExecutionException;

/**
 * 表示 Http 调用的异常。
 *
 * @author 季聿阶
 * @since 2023-12-21
 */
public class HttpExecutionException extends ScriptExecutionException {
    private final String method;

    private final String url;

    /**
     * 创建一个新的 {@link HttpExecutionException} 实例。
     *
     * @param method 表示 Http 调用的方法的 {@link String}。
     * @param url 表示 Http 调用的地址的 {@link String}。
     * @param message 表示异常的详细信息的 {@link String}。
     */
    public HttpExecutionException(String method, String url, String message) {
        super(message);
        this.method = method;
        this.url = url;
    }

    /**
     * 创建一个新的 {@link HttpExecutionException} 实例，并关联一个原始的 {@link Throwable}。
     *
     * @param method 表示 Http 调用的方法的 {@link String}。
     * @param url 表示 Http 调用的地址的 {@link String}。
     * @param message 表示异常的详细信息的 {@link String}。
     * @param cause 表示原始的 {@link Throwable} 的 {@link Throwable}。
     */
    public HttpExecutionException(String method, String url, String message, Throwable cause) {
        super(message, cause);
        this.method = method;
        this.url = url;
    }

    /**
     * 获取异常关联的 Http 调用的方法。
     *
     * @return 表示异常关联的 Http 调用的方法的 {@link String}。
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * 获取异常关联的 Http 调用的地址。
     *
     * @return 表示异常关联的 Http 调用的地址的 {@link String}。
     */
    public String getUrl() {
        return this.url;
    }
}
