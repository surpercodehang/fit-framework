/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import modelengine.fit.http.protocol.ConfigurableStatusLine;
import modelengine.fit.http.protocol.HttpVersion;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示 {@link ConfigurableStatusLine} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-27
 */
public class DefaultStatusLine implements ConfigurableStatusLine {
    private final HttpVersion httpVersion;
    private int statusCode;
    private String reasonPhrase;

    public DefaultStatusLine(HttpVersion httpVersion, int statusCode, String reasonPhrase) {
        this.httpVersion = Validation.notNull(httpVersion, "The http version cannot be null.");
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public void statusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void reasonPhrase(String reasonPhrase) {
        if (StringUtils.isNotBlank(reasonPhrase)) {
            this.reasonPhrase = reasonPhrase;
        }
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    @Override
    public HttpVersion httpVersion() {
        return this.httpVersion;
    }
}
