/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.setter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.HttpResource;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.client.proxy.support.DefaultRequestBuilder;
import modelengine.fit.http.client.proxy.support.setter.ObjectEntitySetter;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.value.ValueFetcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * 为 {@link ObjectEntitySetter} 提供单元测试。
 *
 * @author 王攀博
 * @since 2024-06-12
 */
@DisplayName("测试 ObjectEntitySetter 接口")
class ObjectEntitySetterTest {
    private String propertyPath;
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
        this.propertyPath = "test_property_path";
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
    @DisplayName("当提供key时，返回object entity中的value")
    void shouldReturnValueWhenGetValueByKeyFromObjectEntityGivenKey() {
        // given
        doNothing().when(this.expectRequest).jsonEntity(any());
        ObjectEntity<?> entity = mock(ObjectEntity.class);
        when(this.expectRequest.entity()).thenReturn(Optional.ofNullable(entity));
        HttpResource httpResource = mock(HttpResource.class);
        ValueFetcher valueFetcher = mock(ValueFetcher.class);
        when(this.expectRequest.httpResource()).thenReturn(httpResource);
        when(httpResource.valueFetcher()).thenReturn(valueFetcher);
        when(valueFetcher.fetch(any(), any())).thenReturn(this.value);

        // when
        DestinationSetter objectEntitySetter = new ObjectEntitySetter(this.propertyPath);
        objectEntitySetter.set(requestBuilder, this.value);
        ObjectEntity<?> actualEntity = ObjectUtils.cast(requestBuilder.build().entity().get());
        String actualValue = this.expectRequest.httpResource()
                .valueFetcher()
                .fetch(objectEntitySetter, this.propertyPath)
                .toString();

        // then
        assertThat(actualValue).isEqualTo(this.value);
    }
}