/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

/**
 * 表示 Http 响应的状态行。
 *
 * @author 季聿阶
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-6.1">RFC 2616</a>
 * @since 2022-07-07
 */
public interface StatusLine extends StartLine {
    /**
     * 获取 Http 响应的状态码。
     *
     * @return 表示 Http 响应状态码的 {@code int}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-6.1.1">RFC 2616</a>
     */
    int statusCode();

    /**
     * 获取 Http 响应状态码对应的简要说明。
     *
     * @return 表示 Http 响应状态码对应的简要说明的 {@link String}。
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-6.1.1">RFC 2616</a>
     */
    String reasonPhrase();
}
