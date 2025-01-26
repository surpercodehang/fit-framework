/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.header.ContentDisposition;
import modelengine.fit.http.header.HeaderValue;

import java.util.Optional;

/**
 * 表示 {@link ContentDisposition} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-04
 */
public class DefaultContentDisposition extends DefaultHeaderValue implements ContentDisposition {
    private static final String NAME = "name";
    private static final String FILENAME = "filename";
    private static final String FILENAME_STAR = "filename*";

    public DefaultContentDisposition(HeaderValue headerValue) {
        super(notNull(headerValue, "The header value cannot be null.").value(), headerValue.parameters());
    }

    @Override
    public Optional<String> name() {
        return this.parameters().get(NAME);
    }

    @Override
    public Optional<String> fileName() {
        return this.parameters().get(FILENAME);
    }

    @Override
    public Optional<String> fileNameStar() {
        return this.parameters().get(FILENAME_STAR);
    }
}
