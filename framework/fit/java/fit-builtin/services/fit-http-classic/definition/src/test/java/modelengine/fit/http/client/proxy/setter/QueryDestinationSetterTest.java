/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.setter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.client.proxy.support.DefaultRequestBuilder;
import modelengine.fit.http.client.proxy.support.setter.PathVariableDestinationSetter;
import modelengine.fit.http.client.proxy.support.setter.QueryDestinationSetter;
import modelengine.fit.http.protocol.HttpRequestMethod;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link QueryDestinationSetter} 提供单元测试。
 *
 * @author 王攀博
 * @since 2024-06-12
 */
@DisplayName("测试 QueryDestinationSetter 接口")
class QueryDestinationSetterTest {
    private String key;
    private String pathVariable;
    private String protocol;
    private String domain;
    private HttpRequestMethod method;
    private String pathPattern;
    private String queryKey;
    private String queryValue;
    private String queryKey2;
    private String queryValue2;
    private String queryValue3;
    private RequestBuilder requestBuilder;
    private HttpClassicClientRequest expectRequest;
    private HttpClassicClient client;

    @BeforeEach
    void setup() {
        this.key = "gid";
        this.pathVariable = "test_gid";
        this.protocol = "http";
        this.domain = "test_domain";
        this.pathPattern = "/fit/{gid}";
        this.method = HttpRequestMethod.POST;
        this.queryKey = "query_key";
        this.queryValue = "query_value";
        this.queryKey2 = "query_key2";
        this.queryValue2 = "query_value2";
        this.queryValue3 = "query_value3";
        this.client = mock(HttpClassicClient.class);
        this.expectRequest = mock(HttpClassicClientRequest.class);
        this.requestBuilder = new DefaultRequestBuilder().client(this.client)
                .protocol(this.protocol)
                .domain(this.domain)
                .pathPattern(this.pathPattern)
                .method(this.method);
    }

    @AfterEach
    void teardown() {
        this.requestBuilder = null;
    }

    @Test
    @DisplayName("向request中设置url查询参数")
    void shouldReturnUrlWhenSetQueryParamGivenParam() {
        // given
        String url =
                "http://test_domain/fit/test_gid?query_key=query_value&query_key2=query_value2&query_key2=query_value3";
        when(this.client.createRequest(eq(this.method), eq(url))).thenReturn(this.expectRequest);
        when(this.expectRequest.requestUri()).thenReturn(url);
        // when
        DestinationSetter setter = new PathVariableDestinationSetter(this.key);
        setter.set(requestBuilder, this.pathVariable);
        DestinationSetter querySetter = new QueryDestinationSetter(this.queryKey);
        querySetter.set(requestBuilder, this.queryValue);
        DestinationSetter querySetter2 = new QueryDestinationSetter(this.queryKey2);
        querySetter2.set(requestBuilder, this.queryValue2);
        DestinationSetter querySetter3 = new QueryDestinationSetter(this.queryKey2);
        querySetter3.set(requestBuilder, this.queryValue3);
        HttpClassicClientRequest request = requestBuilder.build();
        String actualUri = request.requestUri();

        // then
        assertThat(actualUri).isEqualTo(url);
        verify(this.client, times(1)).createRequest(eq(this.method), eq(url));
    }
}
