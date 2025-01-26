/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.entity;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

/**
 * 表示令牌信息的类。
 *
 * @author 李金绪
 * @since 2024-07-19
 */
public class ClientTokenInfo {
    private static final String ACCESS_TYPE = "access_token";
    private static final String REFRESH_TYPE = "refresh_token";
    private static final String INVALID_STATUS = "invalid";

    private TokenInfo accessToken;
    private TokenInfo refreshToken;
    private Instant obtainedAt;

    /**
     * 用于实例化 ClientTokenInfo。
     *
     * @param accessToken 表示公钥的 {@link String}。
     * @param refreshToken 表示私钥的 {@link String}。
     * @param obtainedAt 表示获取令牌的时间的 {@link Instant}。
     */
    public ClientTokenInfo(TokenInfo accessToken, TokenInfo refreshToken, Instant obtainedAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.obtainedAt = obtainedAt;
    }

    /**
     * 获取访问令牌。
     *
     * @return 表示访问令牌的 {@link TokenInfo}。
     */
    public TokenInfo getAccessToken() {
        return this.accessToken;
    }

    /**
     * 设置访问令牌。
     *
     * @param accessToken 表示访问令牌的 {@link TokenInfo}。
     */
    public void setAccessToken(TokenInfo accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 获取刷新令牌。
     *
     * @return 表示刷新令牌的 {@link TokenInfo}。
     */
    public TokenInfo getRefreshToken() {
        return this.refreshToken;
    }

    /**
     * 设置刷新令牌。
     *
     * @param refreshToken 表示刷新令牌的 {@link TokenInfo}。
     */
    public void setRefreshToken(TokenInfo refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 获取令牌的时间。
     *
     * @return 表示获取令牌的时间的 {@link Instant}。
     */
    public Instant getObtainAt() {
        return this.obtainedAt;
    }

    /**
     * 设置令牌的时间。
     *
     * @param obtainedAt 表示获取令牌的时间的 {@link Instant}。
     */
    public void setObtainAt(Instant obtainedAt) {
        this.obtainedAt = obtainedAt;
    }

    /**
     * 本地检查访问令牌是否过期。
     *
     * @return 表示访问令牌是否过期的 {@code boolean}。
     */
    public boolean isAccessTokenExpired() {
        return StringUtils.isBlank(this.accessToken.getToken()) || Instant.now()
                .isAfter(this.obtainedAt.plusSeconds(this.accessToken.getTimeout()));
    }

    /**
     * 本地检查刷新令牌是否过期。
     *
     * @return 表示刷新令牌是否过期的 {@code boolean}。
     */
    public boolean isRefreshTokenExpired() {
        return StringUtils.isBlank(this.refreshToken.getToken()) || Instant.now()
                .isAfter(this.obtainedAt.plusSeconds(this.refreshToken.getTimeout()));
    }

    /**
     * 检查访问令牌是否无效。
     *
     * @return 表示访问令牌是否无效的 {@code boolean}。
     */
    public boolean isAccessTokenInvalid() {
        return StringUtils.isBlank(this.accessToken.getToken()) || StringUtils.equals(this.accessToken.getStatus(),
                INVALID_STATUS);
    }

    /**
     * 检查刷新令牌是否无效。
     *
     * @return 表示刷新令牌是否无效的 {@code boolean}。
     */
    public boolean isRefreshTokenInvalid() {
        return StringUtils.isBlank(this.refreshToken.getToken()) || StringUtils.equals(this.refreshToken.getStatus(),
                INVALID_STATUS);
    }

    /**
     * 转换服务端的返回信息。
     *
     * @param tokenInfos 表示服务端返回的令牌信息的 {@link List}{@code <}{@link TokenInfo}{@code >}。
     * @param obtainedAt 表示获取令牌的时间的 {@link Instant}。
     * @return 表示客户端的令牌信息的 {@link ClientTokenInfo}。
     */
    public static ClientTokenInfo convert(List<TokenInfo> tokenInfos, Instant obtainedAt) {
        Validation.notEmpty(tokenInfos, "the list of token info cannot be empty.");
        Validation.equals(tokenInfos.size(), 2, "the size of token info must be equal to 2.");
        Validation.equals(tokenInfos.get(0).getType(), REFRESH_TYPE, "the refresh token type is incorrect.");
        Validation.equals(tokenInfos.get(1).getType(), ACCESS_TYPE, "the access token type is incorrect.");
        return new ClientTokenInfo(tokenInfos.get(1), tokenInfos.get(0), obtainedAt);
    }
}
