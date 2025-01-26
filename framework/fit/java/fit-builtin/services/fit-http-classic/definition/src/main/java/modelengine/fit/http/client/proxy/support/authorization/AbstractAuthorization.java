/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.authorization;

import modelengine.fit.http.client.proxy.Authorization;
import modelengine.fitframework.inspection.Nonnull;

/**
 * 鉴权信息管理的抽象接口。
 *
 * @author 王攀博
 * @since 2024-11-26
 */
public abstract class AbstractAuthorization implements Authorization {
    @Override
    public void set(String key, Object value) {
        if (value == null) {
            return;
        }
        this.setValue(key, value);
    }

    /**
     * 表示设置值到鉴权信息中。
     *
     * @param key 表示鉴权信息的参数键值 {@link String}。
     * @param value 表示鉴权信息值的 {@link Object}。
     */
    protected abstract void setValue(String key, @Nonnull Object value);
}
