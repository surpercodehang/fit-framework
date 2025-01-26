/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.service.RegisterAuthService;
import modelengine.fit.service.TokenService;
import modelengine.fit.service.entity.ClientTokenInfo;
import modelengine.fit.service.entity.TokenInfo;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.ioc.BeanContainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link DefaultRegisterAuthService} 的测试类。
 *
 * @author 李金绪
 * @since 2024-08-15
 */
@DisplayName("测试 RegisterAuthServiceTest")
public class RegisterAuthServiceTest {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    private RegisterAuthService registerAuthService;
    private TokenService tokenService = mock(TokenService.class);
    private Config config = mock(Config.class);
    private BeanContainer container = mock(BeanContainer.class);

    @BeforeEach
    void setUp() {
        when(this.config.get("matata.registry.secure-access.access-key", String.class)).thenReturn("mockAccessKey");
        when(this.config.get("matata.registry.secure-access.secret-key", String.class)).thenReturn("mockSecretKey");
        this.registerAuthService = new DefaultRegisterAuthService(tokenService, container, config);
        List<TokenInfo> tokenInfos = getTokenInfos("mockAccessToken", "mockRefreshToken", "normal", 1);
        when(tokenService.applyToken(any(), any(), any())).thenReturn(tokenInfos);
    }

    void setUpNormal() {
        List<TokenInfo> tokenInfos = getTokenInfos("mockAccessTokenR", "mockRefreshTokenR", "normal", 1);
        when(this.tokenService.refreshToken(any())).thenReturn(tokenInfos);
    }

    void setUpRefreshTokenExpired() {
        List<TokenInfo> tokenInfos = getTokenInfos("mockAccessToken", "mockRefreshToken", "normal", 1);
        when(this.tokenService.refreshToken(any())).thenReturn(tokenInfos);
    }

    @Test
    @DisplayName("测试申请令牌")
    void testApplyToken() {
        this.setUpNormal();
        this.registerAuthService.applyToken(Instant.now());
        ClientTokenInfo clientTokenInfo = this.registerAuthService.getTokenWithoutCheck();
        assertEquals("mockAccessToken", clientTokenInfo.getAccessToken().getToken());
        assertEquals("mockRefreshToken", clientTokenInfo.getRefreshToken().getToken());
        assertEquals(1, clientTokenInfo.getAccessToken().getTimeout());
    }

    @Test
    @DisplayName("测试第一次获取令牌")
    void testGetToken() {
        this.setUpNormal();
        ClientTokenInfo token = this.registerAuthService.getToken();
        assertEquals("mockAccessToken", token.getAccessToken().getToken());
        assertEquals("mockRefreshToken", token.getRefreshToken().getToken());
        assertEquals(1, token.getAccessToken().getTimeout());
    }

    @Test
    @DisplayName("测试访问令牌过期时，获取令牌")
    void testGetTokenWhenAccessExpired() throws InterruptedException {
        this.setUpNormal();
        ClientTokenInfo token = this.registerAuthService.getToken();
        Thread.sleep(1001);
        ClientTokenInfo refreshTokenInfo = this.registerAuthService.getToken();
        assertEquals("mockAccessTokenR", refreshTokenInfo.getAccessToken().getToken());
        assertEquals("mockRefreshTokenR", refreshTokenInfo.getRefreshToken().getToken());
        assertEquals(1, refreshTokenInfo.getAccessToken().getTimeout());
    }

    @Test
    @DisplayName("测试刷新令牌未过期时，刷新令牌")
    void testRefreshTokenWhenNotExpired() {
        this.setUpNormal();
        ClientTokenInfo clientTokenInfo = this.registerAuthService.getToken();
        this.registerAuthService.refreshToken(Instant.now());
        ClientTokenInfo refreshTokenInfo = this.registerAuthService.getTokenWithoutCheck();
        assertEquals("mockAccessTokenR", refreshTokenInfo.getAccessToken().getToken());
        assertEquals("mockRefreshTokenR", refreshTokenInfo.getRefreshToken().getToken());
        assertEquals(1, refreshTokenInfo.getAccessToken().getTimeout());
    }

    @Test
    @DisplayName("测试刷新令牌过期时，刷新令牌")
    void testRefreshTokenWhenExpired() throws InterruptedException {
        this.setUpRefreshTokenExpired();
        ClientTokenInfo token = this.registerAuthService.getToken();
        Thread.sleep(1001);
        this.registerAuthService.refreshToken(Instant.now());
        ClientTokenInfo refreshTokenInfo = this.registerAuthService.getTokenWithoutCheck();
        assertEquals("mockAccessToken", refreshTokenInfo.getAccessToken().getToken());
        assertEquals("mockRefreshToken", refreshTokenInfo.getRefreshToken().getToken());
        assertEquals(1, refreshTokenInfo.getAccessToken().getTimeout());
    }

    @Test
    @DisplayName("测试刷新令牌过期时，获取令牌")
    void testGetTokenWhenRefreshExpired() throws InterruptedException {
        this.setUpRefreshTokenExpired();
        ClientTokenInfo token = this.registerAuthService.getToken();
        Thread.sleep(1001);
        ClientTokenInfo refreshTokenInfo = this.registerAuthService.getToken();
        assertEquals("mockAccessToken", refreshTokenInfo.getAccessToken().getToken());
        assertEquals("mockRefreshToken", refreshTokenInfo.getRefreshToken().getToken());
        assertEquals(1, refreshTokenInfo.getAccessToken().getTimeout());
    }

    private List<TokenInfo> getTokenInfos(String accessToken, String refreshToken, String tokenType, int timeout) {
        TokenInfo accessTokenInfo = new TokenInfo(accessToken, tokenType, timeout, ACCESS_TOKEN);
        TokenInfo refreshTokenInfo = new TokenInfo(refreshToken, tokenType, timeout, REFRESH_TOKEN);
        return new ArrayList<>(Arrays.asList(refreshTokenInfo, accessTokenInfo));
    }
}
