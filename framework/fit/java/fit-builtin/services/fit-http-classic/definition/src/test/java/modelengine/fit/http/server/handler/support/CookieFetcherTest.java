/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.server.handler.MockHttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link CookieFetcher} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 CookieFetcher 类")
class CookieFetcherTest {
    @Test
    @DisplayName("当请求和响应中没有 cookie 名称时，返回 null")
    void givenEntityImplThenReturnParameterMapper() {
        final MockHttpClassicServerRequest serverRequest = new MockHttpClassicServerRequest();
        final CookieFetcher cookieFetcher = new CookieFetcher(ParamValue.custom().name("notExistCookieName").build());
        final Object obj = cookieFetcher.get(serverRequest.getRequest(), null);
        assertThat(obj).isNull();
    }

    @Test
    @DisplayName("判断来源数据的常用格式是否是数组")
    void shouldReturnIsArrayAble() {
        final CookieFetcher cookieFetcher = new CookieFetcher(ParamValue.custom().name("notExistCookieName").build());
        final boolean isArrayAble = cookieFetcher.isArrayAble();
        assertThat(isArrayAble).isFalse();
    }
}
