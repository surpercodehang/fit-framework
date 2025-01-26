/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.util;

import static modelengine.fit.http.header.HttpHeaderKey.FIT_CODE;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.client.Address;
import modelengine.fit.client.Request;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.Protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link HttpClientUtils} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2024-08-26
 */
@DisplayName("测试 HttpClientUtils")
public class HttpClientUtilsTest {
    @Test
    @DisplayName("获取响应编码正确")
    void shouldGetCodeSuccessfully() {
        Request request = mock(Request.class);
        HttpClassicClientResponse<Object> response = cast(mock(HttpClassicClientResponse.class));
        ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create().add(FIT_CODE.value(), "200");
        when(response.headers()).thenReturn(headers);
        int code = HttpClientUtils.getResponseCode(request, response);
        assertThat(code).isEqualTo(200);
    }

    @Test
    @DisplayName("获取响应编码错误：没有响应编码")
    void shouldGetCodeFailedWhenNoCode() {
        Request request = mock(Request.class);
        when(request.protocol()).thenReturn(Protocol.HTTP.protocol());
        when(request.address()).thenReturn(Address.create("localhost", 8080));
        HttpClassicClientResponse<Object> response = cast(mock(HttpClassicClientResponse.class));
        ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create();
        when(response.headers()).thenReturn(headers);
        IllegalStateException cause = catchThrowableOfType(() -> HttpClientUtils.getResponseCode(request, response),
                IllegalStateException.class);
        assertThat(cause).isNotNull();
    }

    @Test
    @DisplayName("获取响应编码错误：编码格式错误")
    void shouldGetCodeFailedWhenCodeIsNotNumber() {
        Request request = mock(Request.class);
        when(request.protocol()).thenReturn(Protocol.HTTP.protocol());
        when(request.address()).thenReturn(Address.create("localhost", 8080));
        HttpClassicClientResponse<Object> response = cast(mock(HttpClassicClientResponse.class));
        ConfigurableMessageHeaders headers = ConfigurableMessageHeaders.create().add(FIT_CODE.value(), "Hello");
        when(response.headers()).thenReturn(headers);
        IllegalStateException cause = catchThrowableOfType(() -> HttpClientUtils.getResponseCode(request, response),
                IllegalStateException.class);
        assertThat(cause).isNotNull();
    }
}
