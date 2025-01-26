/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header.support;

import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.header.HeaderValue;
import modelengine.fit.http.header.ParameterCollection;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示 {@link HeaderValue} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-04
 */
public class DefaultHeaderValue implements HeaderValue {
    /**
     * 消息头中属性间的分隔符。
     */
    public static final String SEPARATOR = ";";

    private final String value;
    private final ParameterCollection parameterCollection;

    public DefaultHeaderValue(String value, ParameterCollection parameterCollection) {
        this.value = ObjectUtils.nullIf(value, StringUtils.EMPTY);
        this.parameterCollection = getIfNull(parameterCollection, ParameterCollection::create);
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public ParameterCollection parameters() {
        return this.parameterCollection;
    }

    @Override
    public String toString() {
        if (this.parameterCollection.size() > 0) {
            return this.value + SEPARATOR + this.parameterCollection;
        }
        return this.value;
    }
}
