/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.okhttp;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.client.HttpClassicClientFactory;
import okhttp3.OkHttpClient;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 为 {@link OkHttpClientBuilderFactory} 提供单测。
 *
 * @author 杭潇
 * @since 2024-10-29
 */
@DisplayName("测试 OkHttpClientBuilderFactory")
class OkHttpClientBuilderFactoryTest {
    @DisplayName("设置 ignore-trust 为 true，构建的 OkHttpClient 不为空")
    @Test
    void givenIgnoreTrustTrueThenOkHttpClientIsNotNull() {
        Map<String, Object> config = new HashMap<>();
        config.put("client.http.secure.ignore-trust", true);
        HttpClassicClientFactory.Config build = HttpClassicClientFactory.Config.builder().custom(config).build();
        OkHttpClient.Builder okHttpClientBuilder = OkHttpClientBuilderFactory.getOkHttpClientBuilder(build);
        assertThat(okHttpClientBuilder).isNotNull();
    }
}