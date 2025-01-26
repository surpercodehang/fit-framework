/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.okhttp;

import static modelengine.fit.http.protocol.util.SslUtils.getKeyManagers;
import static modelengine.fit.http.protocol.util.SslUtils.getTrustManagers;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.client.http.HttpsConstants;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.protocol.util.SslUtils;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;
import okhttp3.OkHttpClient;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 创建 OkHttpClient.Builder 实例工厂。
 *
 * @author 杭潇
 * @since 2024-04-15
 */
public class OkHttpClientBuilderFactory {
    private static final Logger log = Logger.get(OkHttpClientBuilderFactory.class);
    private static final String SECURE_DEFAULT_PROTOCOL = "TLSv1.2";

    private OkHttpClientBuilderFactory() {}

    /**
     * 根据配置获取工厂实例的 {@link OkHttpClient.Builder}。
     *
     * @param config 表示配置的 {@link HttpClassicClientFactory.Config}。
     * @return 表示工厂创建实例的 {@link OkHttpClient.Builder}。
     */
    public static OkHttpClient.Builder getOkHttpClientBuilder(HttpClassicClientFactory.Config config) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        setTimeout(clientBuilder, config);
        try {
            setSslConfig(clientBuilder, config);
        } catch (GeneralSecurityException e) {
            log.error("Failed to set https config.", e);
            throw new IllegalStateException("Failed to set https config.", e);
        }
        return clientBuilder;
    }

    private static void setTimeout(OkHttpClient.Builder clientBuilder, HttpClassicClientFactory.Config config) {
        if (config.connectTimeout() >= 0) {
            clientBuilder.connectTimeout(config.connectTimeout(), TimeUnit.MILLISECONDS);
        }
        if (config.socketTimeout() >= 0) {
            clientBuilder.readTimeout(config.socketTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(config.socketTimeout(), TimeUnit.MILLISECONDS);
        }
    }

    private static void setSslConfig(OkHttpClient.Builder clientBuilder, HttpClassicClientFactory.Config config)
            throws GeneralSecurityException {
        boolean isStrongRandom = Boolean.parseBoolean(String.valueOf(config.custom()
                .getOrDefault(HttpsConstants.CLIENT_SECURE_STRONG_RANDOM, true)));
        String secureProtocol = cast(config.custom()
                .getOrDefault(HttpsConstants.CLIENT_SECURE_SECURITY_PROTOCOL, SECURE_DEFAULT_PROTOCOL));
        boolean isIgnoreTrust = Boolean.parseBoolean(String.valueOf(config.custom()
                .getOrDefault(HttpsConstants.CLIENT_SECURE_IGNORE_TRUST, false)));

        KeyManager[] keyManagers = getKeyManagersConfig(config, isIgnoreTrust);
        TrustManager[] trustManagers = getTrustManagersConfig(config, isIgnoreTrust);

        SSLContext sslContext = SslUtils.getSslContext(keyManagers, trustManagers, isStrongRandom, secureProtocol);
        if (isIgnoreTrust || isTrustManagerSet(trustManagers)) {
            clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
        }
        if (isIgnoreTrust || isHostnameVerificationIgnored(trustManagers, config)) {
            clientBuilder.hostnameVerifier((hostname, session) -> true);
        }
    }

    private static KeyManager[] getKeyManagersConfig(HttpClassicClientFactory.Config config, boolean isIgnoreTrust)
            throws GeneralSecurityException {
        String keyStoreFile = cast(config.custom().get(HttpsConstants.CLIENT_SECURE_KEY_STORE_FILE));
        String keyStorePassword = cast(config.custom().get(HttpsConstants.CLIENT_SECURE_KEY_STORE_PASSWORD));
        if (StringUtils.isNotBlank(keyStoreFile) && StringUtils.isNotBlank(keyStorePassword) && !isIgnoreTrust) {
            return getKeyManagers(keyStoreFile, keyStorePassword);
        }
        return null;
    }

    private static TrustManager[] getTrustManagersConfig(HttpClassicClientFactory.Config config, boolean isIgnoreTrust)
            throws GeneralSecurityException {
        if (isIgnoreTrust) {
            return getTrustAllCerts();
        }
        String trustStoreFile = cast(config.custom().get(HttpsConstants.CLIENT_SECURE_TRUST_STORE_FILE));
        String trustStorePassword = cast(config.custom().get(HttpsConstants.CLIENT_SECURE_TRUST_STORE_PASSWORD));
        if (StringUtils.isNotBlank(trustStoreFile) && StringUtils.isNotBlank(trustStorePassword)) {
            return getTrustManagers(trustStoreFile, trustStorePassword);
        }
        return null;
    }

    private static boolean isTrustManagerSet(TrustManager[] trustManagers) {
        return trustManagers != null && trustManagers.length > 0 && trustManagers[0] instanceof X509TrustManager;
    }

    private static boolean isHostnameVerificationIgnored(TrustManager[] trustManagers,
            HttpClassicClientFactory.Config config) {
        return trustManagers != null && Boolean.parseBoolean(String.valueOf(config.custom()
                .getOrDefault(HttpsConstants.CLIENT_SECURE_IGNORE_HOSTNAME, false)));
    }

    private static TrustManager[] getTrustAllCerts() {
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
        };
        return new TrustManager[] {x509TrustManager};
    }
}
