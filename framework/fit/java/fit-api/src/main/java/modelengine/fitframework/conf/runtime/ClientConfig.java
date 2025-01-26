/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import java.util.Optional;

/**
 * 表示运行时 {@code 'client.*.'} 前缀的配置项。
 *
 * @author 杭潇
 * @since 2024-03-18
 */
public interface ClientConfig {
    /**
     * 获取安全相关的配置信息。
     *
     * @return 表示安全相关的配置信息的 {@link Optional}{@code <}{@link Secure}{@code >}。
     */
    Optional<Secure> secure();

    /**
     * 表示运行时 {@code 'client.*.secure.'} 前缀的配置项。
     */
    interface Secure {
        /**
         * 获取端口是否忽略 hostname 的标志。
         * <p>如果显式地配置了 {@code 'client.*.secure.ignore-hostname'}，则以配置内容为准，如果没有配置表示关闭。</p>
         *
         * @return 如果忽略 hostname，则返回 {@code true}，否则，返回 {@code false}。
         */
        boolean ignoreHostName();

        /**
         * 获取端口是否忽略根证书的标志。
         * <p>如果显式地配置了 {@code 'client.*.secure.ignore-trust'}，则以配置内容为准，如果没有配置表示关闭。</p>
         *
         * @return 如果忽略根证书，则返回 {@code true}，否则，返回 {@code false}。
         */
        boolean ignoreTrust();

        /**
         * 获取密码是否加密。
         * <p>如果显式地配置了 {@code 'client.*.secure.encrypted'}，则以配置内容为准，如果没有配置表示关闭。</p>
         *
         * @return 如果加密，则返回 {@code true}，否则，返回 {@code false}。
         */
        boolean encrypted();

        /**
         * 获取秘钥库的文件地址。
         * <p>注意：当前约束输出文件的密码值与输入私钥文件的密码值均为相同内容。</p>
         *
         * @return 表示秘钥库的文件地址的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> keyStoreFile();

        /**
         * 获取秘钥库的文件地址。
         * <p>注意：当前约束密钥库的密码值与输入私钥文件的密码值均为相同内容。</p>
         *
         * @return 表示秘钥库的文件地址的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> trustStoreFile();

        /**
         * 获取秘钥库的密码。
         *
         * @return 表示秘钥库的秘钥的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> trustStorePassword();

        /**
         * 获取秘钥库的秘钥项的密码。
         *
         * @return 表示秘钥库的秘钥项的密码的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> keyStorePassword();

        /**
         * 获取是否启用安全随机数生成器。
         *
         * @return 如果启用，则返回 {@code true}，否则，返回 {@code false}。
         */
        boolean secureRandomEnabled();

        /**
         * 获取安全通信协议。
         *
         * @return 表示安全通信协议的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> secureProtocol();
    }
}
