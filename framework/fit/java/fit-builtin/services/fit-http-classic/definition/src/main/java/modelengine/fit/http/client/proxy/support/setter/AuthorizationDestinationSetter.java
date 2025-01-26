/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.setter;

import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fitframework.util.StringUtils;

/**
 * 鉴权信息设置器。
 *
 * @author 王攀博
 * @since 2024-11-26
 */
public class AuthorizationDestinationSetter extends AbstractDestinationSetter {
    public AuthorizationDestinationSetter(String key) {
        super(key);
    }

    /**
     * 表示设置鉴权信息的键对应的值，如 Bearer 鉴权是设置令牌。
     *
     * @param requestBuilder 表示 Http 请求建造者的 {@link RequestBuilder}。
     * @param value 表示待设置的鉴权信息值的 {@link Object}。
     */
    @Override
    public void set(RequestBuilder requestBuilder, Object value) {
        if (value instanceof String) {
            requestBuilder.authorizationInfo(this.key(), value.toString());
        } else {
            throw new IllegalArgumentException(StringUtils.format("Value type must be string. [type={0}].",
                    this.key()));
        }
    }
}
