/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime.support;

import modelengine.fitframework.conf.runtime.ServerConfig.Secure;

import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link Secure} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-09-14
 */
public class DefaultSecure implements Secure {
    /**
     * 配置项：{@code 'enabled'}。
     */
    private Boolean isEnabled;

    /**
     * 配置项：{@code 'is-ssl-enabled'}。
     */
    private Boolean isSslEnabled;

    /**
     * 配置项：{@code 'need-client-auth'}。
     */
    private Boolean needClientAuth;

    /**
     * 配置项：{@code 'port'}。
     */
    private Integer port;

    /**
     * 配置项：{@code 'to-register-port'}。
     */
    private Integer toRegisterPort;

    /**
     * 配置项：{@code 'trust-store-file'}。
     */
    private String trustStoreFile;

    /**
     * 配置项：{@code 'encrypted'}。
     */
    private Boolean encrypted;

    /**
     * 配置项：{@code 'key-store-file'}。
     */
    private String keyStoreFile;

    /**
     * 配置项：{@code 'trust-store-password'}。
     */
    private String trustStorePassword;

    /**
     * 配置项：{@code 'key-store-password'}。
     */
    private String keyStorePassword;

    /**
     * 配置项：{@code 'secure-random-enabled'}。
     */
    private Boolean secureRandomEnabled;

    /**
     * 配置项：{@code 'secure-protocol'}。
     */
    private String secureProtocol;

    /**
     * 配置项：{@code 'ssl-ciphers'}。
     */
    private List<String> sslCiphers;

    /**
     * 设置端口是否打开的标志。
     *
     * @param isEnabled 表示端口是否打开的标志的 {@link Boolean}。
     */
    public void setEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * 设置 ssl 是否启用的标志。
     *
     * @param isSslEnabled 表示 ssl 是否启用的标志的 {@link Boolean}。
     */
    public void setIsSslEnabled(Boolean isSslEnabled) {
        this.isSslEnabled = isSslEnabled;
    }

    /**
     * 设置是否加密密码。
     *
     * @param encrypted 表示是否加密密码的 {@link Boolean}。
     */
    public void setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * 设置端口号。
     *
     * @param port 表示端口号的 {@link Integer}。
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 设置去注册的端口号。
     *
     * @param toRegisterPort 表示去注册的端口号的 {@link Integer}。
     */
    public void setToRegisterPort(Integer toRegisterPort) {
        this.toRegisterPort = toRegisterPort;
    }

    /**
     * 设置秘钥库文件的地址。
     *
     * @param keyStoreFile 表示秘钥库文件地址的 {@link String}。
     */
    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    /**
     * 设置根证书文件的地址。
     *
     * @param trustStoreFile 表示根证书密码的 {@link String}。
     */
    public void setTrustStoreFile(String trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    /**
     * 设置是否校验客户端证书。
     *
     * @param needClientAuth 表示是否校验客户端证书的标识的 {@link Boolean}。
     */
    public void setNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    /**
     * 设置秘钥库的密码。
     *
     * @param trustStorePassword 表示秘钥库密码的 {@link String}。
     */
    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    /**
     * 设置秘钥库内容的密码。
     *
     * @param keyStorePassword 表示秘钥库内容的密码的 {@link String}。
     */
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * 设置是否启用安全随机数生成器。
     *
     * @param secureRandomEnabled 表示是否启用安全随机数生成器的 {@link Boolean}。
     */
    public void setSecureRandomEnabled(Boolean secureRandomEnabled) {
        this.secureRandomEnabled = secureRandomEnabled;
    }

    /**
     * 设置安全通信协议。
     *
     * @param secureProtocol 表示安全通信协议的 {@link String}。
     */
    public void setSecureProtocol(String secureProtocol) {
        this.secureProtocol = secureProtocol;
    }

    /**
     * 设置 ssl 加密套件。
     *
     * @param sslCiphers 表示 ssl 加密套件的 {@link List}{@code <}{@link String}{@code >}。
     */
    public void setSslCiphers(List<String> sslCiphers) {
        this.sslCiphers = sslCiphers;
    }

    @Override
    public boolean isProtocolEnabled() {
        return this.isEnabled != null ? this.isEnabled : this.port != null;
    }

    @Override
    public boolean isSslEnabled() {
        return this.isSslEnabled != null ? this.isSslEnabled : true;
    }

    @Override
    public boolean needClientAuth() {
        return this.needClientAuth != null ? this.needClientAuth : true;
    }

    @Override
    public Optional<Integer> port() {
        return Optional.ofNullable(this.port);
    }

    @Override
    public boolean encrypted() {
        return this.encrypted != null ? this.encrypted : false;
    }

    @Override
    public Optional<Integer> toRegisterPort() {
        return Optional.ofNullable(this.toRegisterPort);
    }

    @Override
    public Optional<String> keyStoreFile() {
        return Optional.ofNullable(this.keyStoreFile);
    }

    @Override
    public Optional<String> trustStoreFile() {
        return Optional.ofNullable(this.trustStoreFile);
    }

    @Override
    public Optional<String> trustStorePassword() {
        return Optional.ofNullable(this.trustStorePassword);
    }

    @Override
    public Optional<String> keyStorePassword() {
        return Optional.ofNullable(this.keyStorePassword);
    }

    @Override
    public boolean secureRandomEnabled() {
        return this.secureRandomEnabled != null ? this.secureRandomEnabled : true;
    }

    @Override
    public Optional<String> secureProtocol() {
        return Optional.ofNullable(this.secureProtocol);
    }

    @Override
    public List<String> sslCiphers() {
        return this.sslCiphers;
    }
}
