/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import modelengine.fit.http.protocol.support.DefaultRequestLine;

/**
 * 表示 Http 请求的起始行。
 *
 * @author 季聿阶
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-5.1">RFC 2616</a>
 * @since 2022-07-07
 */
public interface RequestLine extends StartLine {
    /**
     * 获取 Http 请求的方法。
     *
     * @return 表示 Http 请求方法的 {@link HttpRequestMethod}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-5.1.1">RFC 2616</a>
     */
    HttpRequestMethod method();

    /**
     * 获取 Http 请求的 URI。
     *
     * @return 表示 Http 请求 URI 的 {@link String}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-5.1.2">RFC 2616</a>
     */
    String requestUri();

    /**
     * 根据 Http 版本、请求方法和请求地址，创建一个新的请求起始行。
     *
     * @param httpVersion 表示 Http 版本的 {@link HttpVersion}。
     * @param method 表示请求方法的 {@link HttpRequestMethod}。
     * @param requestUri 表示请求地址的 {@link String}。
     * @return 表示创建出来的新的请求起始行的 {@link RequestLine}。
     * @throws IllegalArgumentException 当 {@code httpVersion} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code requestUri} 为 {@code null} 或空白字符串时。
     */
    static RequestLine create(HttpVersion httpVersion, HttpRequestMethod method, String requestUri) {
        return new DefaultRequestLine(httpVersion, method, requestUri);
    }
}
