/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import modelengine.fitframework.pattern.builder.BuilderFactory;

import java.net.InetSocketAddress;

/**
 * 表示 Http 通信地址。
 *
 * @author 邬涨财
 * @since 2022-11-21
 */
public interface Address {
    /**
     * 获取通信地址。
     *
     * @return 表示通信地址的 {@link InetSocketAddress}。
     */
    InetSocketAddress socketAddress();

    /**
     * 获取通信地址的主机 IP 地址。
     *
     * @return 表示通信地址的主机 IP 地址的 {@link String}。
     */
    String hostAddress();

    /**
     * 获取通信地址的端口号。
     *
     * @return 表示通信地址的端口号的 {@code int}。
     */
    int port();

    /**
     * {@link Address} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置通信地址。
         *
         * @param socketAddress 表示待设置的通信地址的 {@link InetSocketAddress}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder socketAddress(InetSocketAddress socketAddress);

        /**
         * 向当前构建器中设置主机的 IP 地址。
         *
         * @param hostAddress 表示待设置的主机的 IP 地址的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder hostAddress(String hostAddress);

        /**
         * 向当前构建器中设置端口号。
         *
         * @param port 表示待设置的端口号的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder port(int port);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Address}。
         */
        Address build();
    }

    /**
     * 获取 {@link Address} 的构建器。
     *
     * @return 表示 {@link Address} 的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null);
    }

    /**
     * 获取 {@link Address} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link Address}。
     * @return 表示 {@link Address} 的构建器的 {@link Builder}。
     */
    static Builder builder(Address value) {
        return BuilderFactory.get(Address.class, Builder.class).create(value);
    }
}