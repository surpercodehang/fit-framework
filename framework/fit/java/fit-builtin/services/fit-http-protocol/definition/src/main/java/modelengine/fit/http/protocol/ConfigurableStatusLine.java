/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import modelengine.fit.http.protocol.support.DefaultStatusLine;

/**
 * 表示可修改的 Http 响应的状态行。
 *
 * @author 季聿阶
 * @since 2022-11-27
 */
public interface ConfigurableStatusLine extends StatusLine {
    /**
     * 设置 Http 响应的状态码。
     *
     * @param statusCode 表示 Http 响应状态码的 {@code int}。
     */
    void statusCode(int statusCode);

    /**
     * 设置 Http 响应状态码对应的简要说明。
     *
     * @param reasonPhrase 表示 Http 响应状态码对应的简要说明的 {@link String}。
     */
    void reasonPhrase(String reasonPhrase);

    /**
     * 根据 Http 版本、响应状态码和响应状态信息，创建一个新的可修改的响应状态行。
     *
     * @param httpVersion 表示 Http 版本的 {@link HttpVersion}。
     * @param statusCode 表示响应状态码的 {@code int}。
     * @param reasonPhrase 表示响应状态信息的 {@link String}。
     * @return 表示创建出来的新的可修改的响应状态行的 {@link ConfigurableStatusLine}。
     * @throws IllegalArgumentException 当 {@code httpVersion} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code reasonPhrase} 为 {@code null} 或空白字符串时。
     */
    static ConfigurableStatusLine create(HttpVersion httpVersion, int statusCode, String reasonPhrase) {
        return new DefaultStatusLine(httpVersion, statusCode, reasonPhrase);
    }
}
