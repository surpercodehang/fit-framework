/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.authorization;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

/**
 * 鉴权信息管理的 Bearer 实现。
 *
 * @author 王攀博
 * @since 2024-11-26
 */
public class BearerAuthorization extends AbstractAuthorization {
    /**
     * 表示鉴权口令的键。
     */
    static final String AUTH_TOKEN = "token";

    /**
     * 表示鉴权信息要写入 Header 的键。
     */
    static final String AUTH_HEADER_KEY = "Authorization";

    private String token;

    public BearerAuthorization(String token) {
        this.token = token;
    }

    @Override
    public void setValue(String key, @Nonnull Object value) {
        if (StringUtils.equals(key, AUTH_TOKEN) && value instanceof String) {
            this.token = cast(value);
        } else {
            throw new IllegalArgumentException(StringUtils.format("Invalid key. [key={0}]", key));
        }
    }

    @Override
    public void assemble(RequestBuilder builder) {
        builder.header(AUTH_HEADER_KEY, BearerAuthorizationFactory.TYPE + " " + this.token);
    }
}
