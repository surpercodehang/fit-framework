/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.emitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.client.proxy.Authorization;
import modelengine.fit.http.client.proxy.PropertyValueApplier;
import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.protocol.HttpRequestMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 为 {@link  DefaultHttpEmitter} 提供单元测试。
 *
 * @author 王攀博
 * @since 2024-06-13
 */
@DisplayName("测试  DefaultHttpEmitter 接口")
class DefaultHttpEmitterTest {
    private RequestBuilder requestBuilder;
    private HttpClassicClientRequest expectRequest;
    private String protocol;
    private String domain;
    private HttpRequestMethod method;
    private String pathPattern;
    private HttpClassicClient client;
    private Authorization authorization;

    @BeforeEach
    void setup() {
        this.protocol = "http";
        this.domain = "test_domain";
        this.pathPattern = "/fit/{gid}";
        this.method = HttpRequestMethod.POST;
        this.client = mock(HttpClassicClient.class);
        this.authorization = this.buildAuthorization();
    }

    private Authorization buildAuthorization() {
        Map<String, Object> auth = new HashMap<>();
        auth.put("type", "ApiKey");
        return Authorization.create(auth);
    }

    @Test
    @DisplayName("当提供builder和值时，直接设置值")
    void shouldSetExpectedValueWhenApplyGivenBuilderAndValue() {
        // given
        PropertyValueApplier applier = mock(PropertyValueApplier.class);
        // when
        DefaultHttpEmitter emitter = new DefaultHttpEmitter(Collections.singletonList(applier),
                this.client,
                this.method,
                this.protocol,
                this.domain,
                this.pathPattern,
                this.authorization);

        Object[] args = {"test_args"};
        HttpClassicClientResponse<?> response = emitter.emit(args);

        // then
        verify(this.client, times(1)).exchange(any(), eq(Object.class));
        verify(applier, times(1)).apply(any(), eq(args[0]));
    }
}
