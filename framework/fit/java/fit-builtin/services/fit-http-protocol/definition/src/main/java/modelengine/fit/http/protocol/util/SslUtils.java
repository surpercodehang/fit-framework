/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.util;

import modelengine.fitframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * 表示加密通道的工具类
 *
 * @author 杭潇
 * @since 2024-03-15
 */
public class SslUtils {
    /**
     * 加载根证书（信任证书）。
     *
     * @param httpsTrustStoreFilePath 表示给定的根信任证书的路径值的 {@code String}。
     * @param httpsTrustStoreFilePassword 表示给定的根信任证书的密码值的 {@code String}。
     * @return 表示信任管理器数组的 {@link TrustManager}{@code []}。
     * @throws KeyStoreException 当使用 KeyStore 出现问题时。
     * @throws NoSuchAlgorithmException 当加密算法使用不当时。
     */
    public static TrustManager[] getTrustManagers(String httpsTrustStoreFilePath, String httpsTrustStoreFilePassword)
            throws KeyStoreException, NoSuchAlgorithmException {
        KeyStore trustStore = getKeyStore(httpsTrustStoreFilePath, httpsTrustStoreFilePassword);
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }

    /**
     * 加载客户端证书和私钥或服务端证书和私钥。
     *
     * @param httpsKeyStoreFilePath 表示给定的客户端/服务端信任证书的路径值的 {@code String}。
     * @param httpsKeyStoreFilePassword 表示给定的客户端/服务端信任证书的密码值的 {@code String}。
     * @return 表示密钥管理器数组的 {@link KeyManager}{@code []}。
     * @throws KeyStoreException 当使用 KeyStore 出现问题时。
     * @throws NoSuchAlgorithmException 当加密算法使用不当时。
     * @throws UnrecoverableKeyException 当密钥的密码不正确或者密钥已经被损坏时。
     */
    public static KeyManager[] getKeyManagers(String httpsKeyStoreFilePath, String httpsKeyStoreFilePassword)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore clientKeyStore = getKeyStore(httpsKeyStoreFilePath, httpsKeyStoreFilePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(clientKeyStore, httpsKeyStoreFilePassword.toCharArray());
        return keyManagerFactory.getKeyManagers();
    }

    /**
     * 获取 SSL 上下文。
     *
     * @param keyManagers 表示密钥管理器数组的 {@link KeyManager}{@code []}。
     * @param trustManagers 表示信任管理器数组的 {@link TrustManager}{@code []}。
     * @param isSecureRandomEnabled 表示是否启用安全随机数的 {@code boolean}。
     * @param secureProtocol 表示通信协议的 {@code String}。
     * @return 表示获取 SSL 上下文的 {@link SSLContext}。
     * @throws NoSuchAlgorithmException 当加密算法使用不当时。
     * @throws KeyManagementException 当密钥的密码不正确或者密钥已经被损坏时。
     */
    public static SSLContext getSslContext(KeyManager[] keyManagers, TrustManager[] trustManagers,
            boolean isSecureRandomEnabled, String secureProtocol)
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance(secureProtocol);
        SecureRandom secureRandom = isSecureRandomEnabled ? SecureRandom.getInstanceStrong() : new SecureRandom();
        sslContext.init(keyManagers, trustManagers, secureRandom);
        return sslContext;
    }

    private static KeyStore getKeyStore(String keystoreFile, String password) {
        try (FileInputStream fis = new FileInputStream(keystoreFile)) {
            return getKeyStore(fis, "JKS", password);
        } catch (IOException | GeneralSecurityException e1) {
            try (FileInputStream fis = new FileInputStream(keystoreFile)) {
                return getKeyStore(fis, "PKCS12", password);
            } catch (IOException | GeneralSecurityException e2) {
                e1.addSuppressed(e2);
                throw new IllegalStateException(StringUtils.format("Not supported certificate type. [certificate={0}]",
                        keystoreFile), e1);
            }
        }
    }

    private static KeyStore getKeyStore(FileInputStream fis, String type, String password)
            throws IOException, GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(type);
        keyStore.load(fis, password.toCharArray());
        return keyStore;
    }
}
