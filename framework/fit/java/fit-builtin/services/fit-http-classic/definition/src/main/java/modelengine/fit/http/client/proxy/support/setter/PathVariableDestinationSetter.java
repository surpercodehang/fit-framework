/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.setter;

import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fit.http.client.proxy.RequestBuilder;

/**
 * 表示向 Http 请求路径设置值的 {@link DestinationSetter}。
 *
 * @author 王攀博
 * @since 2024-06-07
 */
public class PathVariableDestinationSetter extends AbstractDestinationSetter {
    /**
     * 使用指定的键初始化 {@link PathVariableDestinationSetter} 的新实例。
     *
     * @param key 表示键的 {@link String}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 时。
     */
    public PathVariableDestinationSetter(String key) {
        super(key);
    }

    @Override
    public void set(RequestBuilder requestBuilder, Object pathVariable) {
        if (pathVariable != null) {
            requestBuilder.pathVariable(this.key(), pathVariable.toString());
        }
    }
}