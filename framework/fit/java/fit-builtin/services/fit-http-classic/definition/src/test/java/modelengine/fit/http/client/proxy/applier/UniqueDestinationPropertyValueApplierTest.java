/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.applier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.client.proxy.support.DefaultRequestBuilder;
import modelengine.fit.http.client.proxy.support.applier.UniqueDestinationPropertyValueApplier;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.value.ValueFetcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link UniqueDestinationPropertyValueApplier} 提供单元测试。
 *
 * @author 王攀博
 * @since 2024-06-13
 */
@DisplayName("测试 UniqueDestinationPropertyValueApplier 接口")
class UniqueDestinationPropertyValueApplierTest {
    private String sourcePath;
    private String value;
    private RequestBuilder requestBuilder;
    private HttpClassicClientRequest expectRequest;
    private String protocol;
    private String domain;
    private HttpRequestMethod method;
    private String pathPattern;
    private HttpClassicClient client;

    @BeforeEach
    void setup() {
        this.sourcePath = "k1.k2";
        this.value = "test_value";
        this.protocol = "http";
        this.domain = "test_domain";
        this.pathPattern = "/fit/{gid}";
        this.method = HttpRequestMethod.POST;
        this.client = mock(HttpClassicClient.class);
        this.expectRequest = mock(HttpClassicClientRequest.class);
        this.requestBuilder = new DefaultRequestBuilder().client(this.client)
                .protocol(this.protocol)
                .domain(this.domain)
                .pathPattern(this.pathPattern)
                .method(this.method);
        when(this.client.createRequest(any(), any())).thenReturn(this.expectRequest);
    }

    @AfterEach
    void teardown() {
        this.requestBuilder = null;
    }

    @Test
    @DisplayName("当提供builder和值时，直接设置值")
    void shouldSetExpectedValueWhenApplyGivenBuilderAndValue() {
        // given
        DestinationSetter setter = mock(DestinationSetter.class);
        ValueFetcher valueFetcher = mock(ValueFetcher.class);
        RequestBuilder builder = mock(RequestBuilder.class);
        doNothing().when(setter).set(eq(builder), eq(this.value));

        // when
        UniqueDestinationPropertyValueApplier applier = new UniqueDestinationPropertyValueApplier(setter, valueFetcher);
        applier.apply(builder, this.value);

        // then
        verify(setter, times(1)).set(eq(builder), eq(this.value));
    }
}
