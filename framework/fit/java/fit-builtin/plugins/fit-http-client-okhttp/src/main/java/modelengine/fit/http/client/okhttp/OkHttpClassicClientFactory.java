/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.okhttp;

import static modelengine.fit.http.client.okhttp.OkHttpClientBuilderFactory.getOkHttpClientBuilder;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import modelengine.fit.http.Serializers;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.value.ValueFetcher;
import okhttp3.OkHttpClient;

import java.util.Map;

/**
 * 表示 {@link HttpClassicClientFactory} 的 OkHttp 实现。
 *
 * @author 杭潇
 * @since 2024-04-08
 */
@Order(Order.HIGH)
@Component
public class OkHttpClassicClientFactory implements HttpClassicClientFactory {
    private final Serializers serializers;
    private final ValueFetcher valueFetcher;
    private final Cache<Config, OkHttpClient> okHttpClientCache;

    public OkHttpClassicClientFactory(Map<String, ObjectSerializer> serializers, ValueFetcher valueFetcher,
            @Value("${okhttp.config-client.cache.max-size}") long cacheSize) {
        this.serializers = Serializers.create(serializers);
        this.valueFetcher = notNull(valueFetcher, "The value fetcher cannot be null.");
        this.okHttpClientCache = Caffeine.newBuilder().maximumSize(cacheSize).build();
    }

    @Override
    public HttpClassicClient create() {
        return this.create(Config.builder().build());
    }

    @Override
    public HttpClassicClient create(Config config) {
        Config actualConfig = getIfNull(config, () -> HttpClassicClientFactory.Config.builder().build());
        OkHttpClient okHttpClient =
                this.okHttpClientCache.get(actualConfig, tempConfig -> getOkHttpClientBuilder(tempConfig).build());
        return new OkHttpClassicClient(this.serializers, this.valueFetcher, okHttpClient);
    }
}
