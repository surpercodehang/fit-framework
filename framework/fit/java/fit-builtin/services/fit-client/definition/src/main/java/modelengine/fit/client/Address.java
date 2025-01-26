/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client;

import modelengine.fit.client.support.DefaultAddress;

/**
 * 表示请求的目标地址。
 * <p>该类的实现已实现 {@link Object#equals(Object)} 和 {@link Object#hashCode()} 方法，可以作为 {@link java.util.Map} 的键。</p>
 *
 * @author 季聿阶
 * @since 2022-09-19
 */
public interface Address {
    /**
     * 获取请求的主机地址。
     *
     * @return 表示请求主机地址的 {@link String}。
     */
    String host();

    /**
     * 获取请求的端口号。
     *
     * @return 表示请求端口号的 {@code int}。
     */
    int port();

    /**
     * 创建一个请求地址。
     *
     * @param host 表示请求主机地址的 {@link String}。
     * @param port 表示请求端口号的 {@code int}。
     * @return 表示创建的请求地址的 {@link Address}。
     * @throws IllegalArgumentException 当 {@code host} 为 {@code null} 或空白字符串时。
     * @throws IllegalArgumentException 当 {@code port} 小于 {@code 0} 时。
     */
    static Address create(String host, int port) {
        return new DefaultAddress(host, port);
    }
}
