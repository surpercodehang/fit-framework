/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import modelengine.fitframework.conf.runtime.MatataConfig.Registry.SecureAccess;

/**
 * 表示 {@link SecureAccess} 的默认实现。
 *
 * @author 李金绪
 * @since 2024-08-13
 */
public class DefaultSecureAccess implements SecureAccess {
    private boolean enabled;
    private boolean encrypted;
    private String accessKey;
    private String secretKey;

    /**
     * 创建一个新的 {@link DefaultSecureAccess} 实例。
     *
     * @param enabled 表示鉴权认证配置的开关的 {@code boolean}。
     * @param encrypted 表示鉴权认证配置的加密的 {@code boolean}。
     * @param accessKey 表示鉴权认证配置的公钥的 {@code String}。
     * @param secretKey 表示鉴权认证配置的秘钥的 {@code String}。
     */
    public DefaultSecureAccess(boolean enabled, boolean encrypted, String accessKey, String secretKey) {
        this.enabled = enabled;
        this.encrypted = encrypted;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 创建一个新的 {@link DefaultSecureAccess} 实例。
     */
    public DefaultSecureAccess() {}

    /**
     * 设置鉴权认证配置的开关。
     *
     * @param enabled 表示待设置的开关的 {@code boolean}。
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 设置鉴权认证配置的加密开关。
     *
     * @param encrypted 表示待设置的加密开关的 {@code boolean}。
     */
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * 设置鉴权认证配置的访问令牌。
     *
     * @param accessKey 表示待设置的访问令牌的 {@code String}。
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * 设置鉴权认证配置的秘密令牌。
     *
     * @param secretKey 表示待设置的秘密令牌的 {@code String}。
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public boolean encrypted() {
        return this.encrypted;
    }

    @Override
    public String accessKey() {
        return this.accessKey;
    }

    @Override
    public String secretKey() {
        return this.secretKey;
    }

    @Override
    public String toString() {
        return "DefaultSecureAccess{" + "enabled=" + enabled + ", encrypted=" + encrypted + ", accessKey='" + accessKey
                + '\'' + ", secretKey='" + secretKey + '\'' + '}';
    }
}
