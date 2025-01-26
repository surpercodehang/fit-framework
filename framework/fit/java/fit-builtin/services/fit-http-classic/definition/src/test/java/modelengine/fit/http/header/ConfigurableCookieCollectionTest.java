/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fit.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 表示 {@link ConfigurableCookieCollection} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 ConfigurableCookieCollection 类")
class ConfigurableCookieCollectionTest {
    @Test
    @DisplayName("当构建 Cookie 集合的参数为 null 时，抛出异常")
    void givenNullThenThrowException() {
        assertThatThrownBy(this::buildNullCookieCollection).isInstanceOf(IllegalArgumentException.class);
    }

    private void buildNullCookieCollection() {
        ConfigurableCookieCollection.create(null);
    }

    @Test
    @DisplayName("返回所有的 Cookie")
    void shouldReturnAllCookie() {
        final Cookie cookie = Cookie.builder()
                .name("idea")
                .value("00ae-u98i")
                .version(1)
                .comment("")
                .domain("localhost")
                .maxAge(10)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .build();
        ConfigurableCookieCollection cookieCollection = ConfigurableCookieCollection.create();
        cookieCollection.add(cookie);
        final List<Cookie> cookies = cookieCollection.all();
        assertThat(cookies).hasSize(1);
    }
}
