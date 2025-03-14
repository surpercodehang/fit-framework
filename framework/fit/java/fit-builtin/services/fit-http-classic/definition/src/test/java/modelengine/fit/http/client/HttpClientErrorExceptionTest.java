/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.support.AbstractHttpClassicClient;
import modelengine.fit.http.client.support.DefaultHttpClassicClientResponse;
import modelengine.fit.http.protocol.ClientResponse;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.support.DefaultClientResponse;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.model.support.DefaultMultiValueMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 为 {@link HttpClientErrorException} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-16
 */
@DisplayName("测试 HttpClientErrorException 类")
public class HttpClientErrorExceptionTest {
    private static final String DETAIL_MESSAGE = """
            HTTP Request Failed
            Request URL: /test
            HTTP Method: GET
            Request Headers:
              h1: v1
              h2: v2
            
            Response Status: 200 (testHttpClientErrorException)
            Response Headers:
              testkey: testValue
            """;

    private HttpClassicClientRequest request;
    private HttpClassicClientResponse<?> response;
    private int statusCode;
    private String reasonPhrase;

    @BeforeEach
    void setup() throws IOException {
        AbstractHttpClassicClient mock = mock(AbstractHttpClassicClient.class);
        this.statusCode = 200;
        this.reasonPhrase = "testHttpClientErrorException";
        MultiValueMap<String, String> headers = new DefaultMultiValueMap<>();
        headers.add("testKey", "testValue");
        this.request = mock(HttpClassicClientRequest.class);
        ConfigurableMessageHeaders requestHeaders = ConfigurableMessageHeaders.create();
        requestHeaders.add("h1", "v1");
        requestHeaders.add("h2", "v2");
        when(this.request.headers()).thenReturn(requestHeaders);
        when(this.request.method()).thenReturn(HttpRequestMethod.GET);
        when(this.request.path()).thenReturn("/test");
        try (InputStream responseStream = new ByteArrayInputStream("TestOfHttpClientErrorException".getBytes(
                StandardCharsets.UTF_8))) {
            ClientResponse clientResponse =
                    new DefaultClientResponse(this.statusCode, this.reasonPhrase, headers, responseStream);
            Class<?> responseType = String.class;
            this.response = new DefaultHttpClassicClientResponse<>(mock, clientResponse, responseType);
        }
    }

    @Test
    @DisplayName("给定一个有效的 http 经典客户端响应，初始化对象成功")
    void givenValidHttpClassicClientResponseThenInitializedSuccessfully() {
        HttpClientErrorException cause = new HttpClientErrorException(this.request, this.response);
        assertThat(cause.statusCode()).isEqualTo(this.statusCode);
        assertThat(cause.getMessage()).isEqualTo(this.getSimpleMessage());
        assertThat(cause.getSimpleMessage()).isEqualTo(this.getSimpleMessage());
        assertThat(normalizeLineEndings(cause.getDetailMessage())).isEqualTo(DETAIL_MESSAGE);
    }

    @Test
    @DisplayName("给定有效的 http 经典客户端响应与 Throwable，初始化对象成功")
    void givenValidHttpClassicClientResponseAndThrowableThenInitializedSuccessfully() {
        Throwable throwable = new Throwable("throwSomeThing");
        HttpClientErrorException cause = new HttpClientErrorException(this.request, this.response, throwable);
        assertThat(cause.statusCode()).isEqualTo(this.statusCode);
        assertThat(cause.getMessage()).isEqualTo(this.getSimpleMessage());
        assertThat(cause.getSimpleMessage()).isEqualTo(this.getSimpleMessage());
        assertThat(normalizeLineEndings(cause.getDetailMessage())).isEqualTo(DETAIL_MESSAGE);
    }

    private String getSimpleMessage() {
        return this.statusCode + "(" + this.reasonPhrase + ")";
    }

    private static String normalizeLineEndings(String str) {
        // 将 CRLF 和 CR 替换为 LF。
        return str.replaceAll("\\r\\n?", "\n");
    }
}
