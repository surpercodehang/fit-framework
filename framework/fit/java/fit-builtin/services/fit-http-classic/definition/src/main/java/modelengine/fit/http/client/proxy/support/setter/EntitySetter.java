/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.setter;

import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.entity.Entity;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示向消息体设置值的 {@link DestinationSetter}。
 *
 * @author 王攀博
 * @since 2024-06-07
 */
public class EntitySetter implements DestinationSetter {
    @Override
    public void set(RequestBuilder requestBuilder, Object value) {
        if (value == null) {
            return;
        }
        this.setToRequest(requestBuilder, value);
    }

    /**
     * 默认的向请求构建器中设置值。
     *
     * @param requestBuilder 表示请求构建器的 {@link RequestBuilder}。
     * @param value 表示要设置的值的 {@link Object}。
     */
    protected void setToRequest(RequestBuilder requestBuilder, @Nonnull Object value) {
        if (value instanceof Entity) {
            requestBuilder.entity(ObjectUtils.cast(value));
        }
    }
}