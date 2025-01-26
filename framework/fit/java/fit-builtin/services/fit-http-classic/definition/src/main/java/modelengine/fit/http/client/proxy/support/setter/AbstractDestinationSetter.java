/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.setter;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.proxy.DestinationSetter;

/**
 * 表示设置键值对的键。
 *
 * @author 王攀博
 * @since 2024-06-08
 */
public abstract class AbstractDestinationSetter implements DestinationSetter {
    private final String key;

    public AbstractDestinationSetter(String key) {
        this.key = notNull(key, "The key cannot be null.");
    }

    /**
     * 获取键值对的键。
     *
     * @return 表示键值对的键的 {@link String}。
     */
    protected String key() {
        return this.key;
    }
}
