/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpVersion;
import modelengine.fit.http.protocol.RequestLine;

/**
 * 表示 {@link RequestLine} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-27
 */
public class DefaultRequestLine implements RequestLine {
    private final HttpVersion httpVersion;
    private final HttpRequestMethod method;
    private final String requestUri;

    public DefaultRequestLine(HttpVersion httpVersion, HttpRequestMethod method, String requestUri) {
        this.httpVersion = notNull(httpVersion, "The http version cannot be null.");
        this.method = notNull(method, "The request method cannot be null.");
        this.requestUri = notBlank(requestUri, "The request uri cannot be blank.");
    }

    @Override
    public HttpRequestMethod method() {
        return this.method;
    }

    @Override
    public String requestUri() {
        return this.requestUri;
    }

    @Override
    public HttpVersion httpVersion() {
        return this.httpVersion;
    }

    @Override
    public String toString() {
        return this.method + " " + this.requestUri + " " + this.httpVersion;
    }
}
