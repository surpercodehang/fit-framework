/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.security.Decryptor;
import modelengine.fit.service.RegisterAuthService;
import modelengine.fit.service.TokenService;
import modelengine.fit.service.entity.ClientTokenInfo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.LockUtils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 用于处理令牌的服务类。
 *
 * @author 李金绪
 * @since 2024-07-17
 */
@Component
public class DefaultRegisterAuthService implements RegisterAuthService {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final int MILL = 1000;
    private static final String SECURE_ACCESS_KEY = "matata.registry.secure-access.access-key";
    private static final String SECURE_SECRET_KEY = "matata.registry.secure-access.secret-key";
    private static final String ENCRYPTED_SWITCH = "matata.registry.secure-access.encrypted";

    private final TokenService tokenService;
    private final Lock lock;
    private final Config config;
    private final BeanContainer container;
    private ClientTokenInfo clientTokenInfo;
    private LazyLoader<String> accessKey = new LazyLoader<>(this::getAccessKey);
    private LazyLoader<String> secretKey = new LazyLoader<>(this::getSecretKey);

    /**
     * 用于创建一个新实例。
     *
     * @param tokenService 表示服务器令牌服务的 {@link TokenService}。
     * @param container 表示bean容器的 {@link BeanContainer}。
     * @param config 表示配置的 {@link Config}。
     */
    public DefaultRegisterAuthService(TokenService tokenService, BeanContainer container, Config config) {
        this.tokenService = notNull(tokenService, "The token service cannot be null.");
        this.container = notNull(container, "The bean container cannot be null.");
        this.config = notNull(config, "The config cannot be null.");
        this.clientTokenInfo = null;
        this.lock = LockUtils.newReentrantLock();
    }

    @Override
    public void applyToken(Instant obtainedAt) {
        LockUtils.synchronize(this.lock, () -> {
            String timestamp = String.valueOf(System.currentTimeMillis() / MILL);
            this.clientTokenInfo = ClientTokenInfo.convert(this.tokenService.applyToken(this.accessKey.get(),
                    timestamp,
                    sign(timestamp, this.accessKey.get(), this.secretKey.get())), obtainedAt);
        });
    }

    @Override
    public void refreshToken(Instant obtainedAt) {
        LockUtils.synchronize(this.lock, () -> {
            String refreshKey = this.clientTokenInfo.getRefreshToken().getToken();
            this.clientTokenInfo = ClientTokenInfo.convert(this.tokenService.refreshToken(refreshKey), obtainedAt);
            if (this.clientTokenInfo.isAccessTokenInvalid() || this.clientTokenInfo.isRefreshTokenInvalid()) {
                this.applyToken(obtainedAt);
            }
        });
    }

    @Override
    public ClientTokenInfo getToken() {
        LockUtils.synchronize(this.lock, () -> {
            Instant obtainedAt = Instant.now();
            if (this.clientTokenInfo == null) {
                this.applyToken(obtainedAt);
            }
            if (this.clientTokenInfo.isAccessTokenExpired() || this.clientTokenInfo.isAccessTokenInvalid()) {
                this.refreshToken(obtainedAt);
            }
        });
        return this.clientTokenInfo;
    }

    /**
     * 生成签名。
     *
     * @param timestamp 表示时间戳的 {@link String}。
     * @param accessKey 表示公钥的 {@link String}。
     * @param secretKey 表示秘钥的 {@link String}。
     * @return 返回生成的签名的 {@link String}。
     */
    public static String sign(String timestamp, String accessKey, String secretKey) {
        try {
            Mac sha256Hmac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            sha256Hmac.init(secretKeySpec);
            byte[] hash = sha256Hmac.doFinal((accessKey + timestamp).getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new FitException("Signature generation failed.", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02x", bytes[i] & 0xFF))
                .collect(Collectors.joining());
    }

    private String getAccessKey() {
        return decryptValue(this.config.get(SECURE_ACCESS_KEY, String.class));
    }

    private String getSecretKey() {
        return decryptValue(this.config.get(SECURE_SECRET_KEY, String.class));
    }

    private String decryptValue(String key) {
        Boolean isEncrypted = this.config.get(ENCRYPTED_SWITCH, Boolean.class);
        if (isEncrypted == null || !isEncrypted) {
            return key;
        }
        Decryptor decryptor = notNull(this.container.beans().lookup(Decryptor.class), "The Decryptor cannot be null.");
        return decryptor.decrypt(key);
    }

    @Override
    public ClientTokenInfo getTokenWithoutCheck() {
        return this.clientTokenInfo;
    }
}
