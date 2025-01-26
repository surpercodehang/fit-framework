/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.AttributeCollection;
import modelengine.fit.http.server.HttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * 表示 {@link PathVariableFetcher} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 PathVariableFetcher 类")
class PathVariableFetcherTest {
    @Test
    @DisplayName("从 Http 请求和响应中获取数据")
    void shouldReturnDataFromRequest() {
        final HttpClassicServerRequest serverRequest = mock(HttpClassicServerRequest.class);
        final AttributeCollection attributeCollection = mock(AttributeCollection.class);
        final PathVariableFetcher variableFetcher =
                new PathVariableFetcher(ParamValue.custom().name(("demo1")).build());
        when(serverRequest.attributes()).thenReturn(attributeCollection);
        when(attributeCollection.get(anyString())).thenReturn(Optional.of("{demo1}"));
        when(serverRequest.path()).thenReturn("demo1");
        final Object obj = variableFetcher.get(serverRequest, null);
        assertThat(obj).isEqualTo("demo1");
    }
}
