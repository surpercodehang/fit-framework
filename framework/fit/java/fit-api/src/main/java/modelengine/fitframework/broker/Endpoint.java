/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.broker.support.DefaultEndpoint;

/**
 * 服务提供的服务端点。
 *
 * @author 季聿阶
 * @since 2022-06-23
 */
public interface Endpoint {
    /**
     * 获取通信协议。
     *
     * @return 表示通信协议的 {@link String}。
     */
    String protocol();

    /**
     * 获取通信协议。
     *
     * @return 表示通信协议的 {@code int}。
     */
    int protocolCode();

    /**
     * 获取端口号。
     *
     * @return 表示端口号的 {@code int}。
     */
    int port();

    /**
     * {@link Endpoint} 的构建器。
     */
    interface Builder {
        /**
         * 向构建器中设置通信协议。
         *
         * @param protocol 表示待设置的通信协议的 {@link String}。
         * @param code 表示待设置的通信协议编码的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder protocol(String protocol, int code);

        /**
         * 向构建器中设置端口号。
         *
         * @param port 表示待设置的端口号的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder port(int port);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Endpoint}。
         */
        Endpoint build();
    }

    /**
     * 获取 {@link Endpoint} 的构建器。
     *
     * @return 表示 {@link Endpoint} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link Endpoint} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link Endpoint}。
     * @return 表示 {@link Endpoint} 的构建器的 {@link Builder}。
     */
    static Builder custom(Endpoint value) {
        return new DefaultEndpoint.Builder(value);
    }
}
