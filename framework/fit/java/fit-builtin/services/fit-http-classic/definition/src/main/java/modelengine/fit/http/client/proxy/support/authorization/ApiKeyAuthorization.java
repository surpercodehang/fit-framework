/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.authorization;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.server.handler.Source;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

/**
 * 鉴权信息管理的 Api Key 实现。
 *
 * @author 王攀博
 * @since 2024-11-26
 */
public class ApiKeyAuthorization extends AbstractAuthorization {
    /**
     * 表示鉴权的键。
     */
    static final String AUTH_KEY = "key";

    /**
     * 表示鉴权的值。
     */
    static final String AUTH_VALUE = "value";

    private String key;
    private String value;
    private final Source httpSource;

    public ApiKeyAuthorization(String key, String value, Source httpSource) {
        this.key = key;
        this.value = value;
        this.httpSource = httpSource;
    }

    @Override
    public void setValue(String key, @Nonnull Object value) {
        if (StringUtils.equals(key, AUTH_KEY) && value instanceof String) {
            this.key = cast(value);
        } else if (StringUtils.equals(key, AUTH_VALUE) && value instanceof String) {
            this.value = cast(value);
        } else {
            throw new IllegalArgumentException(StringUtils.format("Invalid key. [key={0}]", key));
        }
    }

    @Override
    public void assemble(RequestBuilder builder) {
        if (this.httpSource == Source.QUERY) {
            builder.query(this.key, this.value);
        } else if (this.httpSource == Source.HEADER) {
            builder.header(this.key, this.value);
        } else {
            throw new IllegalStateException(StringUtils.format("Unsupported http source. [source={0}]",
                    this.httpSource));
        }
    }
}
