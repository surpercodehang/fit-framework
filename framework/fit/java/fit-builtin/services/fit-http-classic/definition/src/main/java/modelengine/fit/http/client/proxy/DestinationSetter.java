/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy;

/**
 * 表示数据目标设置器。
 *
 * @author 季聿阶
 * @since 2024-05-11
 */
public interface DestinationSetter {
    /**
     * 将数据设置进 Http 请求。
     *
     * @param requestBuilder 表示 Http 请求建造者的 {@link RequestBuilder}。
     * @param value 表示待设置的值的 {@link Object}。
     */
    void set(RequestBuilder requestBuilder, Object value);
}
