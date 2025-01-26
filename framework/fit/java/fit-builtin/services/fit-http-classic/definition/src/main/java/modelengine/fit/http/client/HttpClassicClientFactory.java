/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

import modelengine.fitframework.pattern.builder.BuilderFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link HttpClassicClient} 的工厂类。
 * <p>如果需要支持 Https，请查看相关配置，并将其在创建时通过 {@link Config} 传入：
 * <ul>
 *     <li>client.http.secure.ignore-trust。表示是否忽略安全检测，适用于 SSL 连接请求。</li>
 *     <li>client.http.secure.ignore-hostname。表示是否忽略主机名验证，适用于 SSL 连接请求。</li>
 *     <li>client.http.secure.trust-store-file。表示客户端根证书路径，适用于 SSL 连接请求。</li>
 *     <li>client.http.secure.trust-store-password。表示客户端根证书密码，适用于 SSL 连接请求。</li>
 *     <li>client.http.secure.key-store-file。表示客户端证书路径，适用于 SSL 连接请求。</li>
 *     <li>client.http.secure.key-store-password。表示客户端证书密码，适用于 SSL 连接请求。</li>
 * </ul></p>
 *
 * @author 季聿阶
 * @since 2022-12-04
 */
public interface HttpClassicClientFactory {
    /**
     * 创建一个经典的 Http 客户端。
     *
     * @return 表示创建出来的经典的 Http 客户端的 {@link HttpClassicClient}。
     */
    HttpClassicClient create();

    /**
     * 创建一个经典的 Http 客户端。
     *
     * @param config 表示创建客户端的配置信息的 {@link Config}。
     * @return 表示创建出来的经典的 Http 客户端的 {@link HttpClassicClient}。
     */
    HttpClassicClient create(Config config);

    /**
     * 表示 {@link HttpClassicClient} 的相关配置信息。
     */
    interface Config {
        /**
         * 获取自定义配置信息。
         *
         * @return 表示自定义配置信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         */
        Map<String, Object> custom();

        /**
         * 获取 Socket 的超时时间。
         * <p>单位为毫秒。</p>
         *
         * @return 表示 Socket 的超时时间的 {@code int}。
         */
        int socketTimeout();

        /**
         * 获取连接请求的超时时间。
         * <p>单位为毫秒。</p>
         *
         * @return 表示连接请求的超时时间的 {@code int}。
         */
        int connectionRequestTimeout();

        /**
         * 获取连接的超时时间。
         * <p>单位为毫秒。</p>
         *
         * @return 表示连接的超时时间的 {@code int}。
         */
        int connectTimeout();

        /**
         * {@link Config} 的构建器。
         */
        interface Builder {
            /**
             * 向当前构建器中设置自定义配置。
             *
             * @param custom 表示待设置的自定义配置的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder custom(Map<String, Object> custom);

            /**
             * 向当前构建器中设置 Socket 的超时时间。
             * <p>单位为毫秒。</p>
             *
             * @param socketTimeout 表示待设置的 Socket 的超时时间的 {@code int}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder socketTimeout(int socketTimeout);

            /**
             * 向当前构建器中设置连接请求的超时时间。
             * <p>单位为毫秒。</p>
             *
             * @param connectionRequestTimeout 表示待设置的连接请求的超时时间的 {@code int}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder connectionRequestTimeout(int connectionRequestTimeout);

            /**
             * 向当前构建器中设置连接的超时时间。
             * <p>单位为毫秒。</p>
             *
             * @param connectTimeout 表示待设置的连接的超时时间的 {@code int}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder connectTimeout(int connectTimeout);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link Config}。
             */
            Config build();
        }

        /**
         * 获取 {@link Config} 的构建器。
         *
         * @return 表示 {@link Config} 的构建器的 {@link Builder}。
         */
        static Builder builder() {
            return builder(null).custom(new HashMap<>())
                    .socketTimeout(-1)
                    .connectTimeout(-1)
                    .connectionRequestTimeout(-1);
        }

        /**
         * 获取 {@link Config} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link Config}。
         * @return 表示 {@link Config} 的构建器的 {@link Builder}。
         */
        static Builder builder(Config value) {
            return BuilderFactory.get(Config.class, Builder.class).create(value);
        }
    }
}
