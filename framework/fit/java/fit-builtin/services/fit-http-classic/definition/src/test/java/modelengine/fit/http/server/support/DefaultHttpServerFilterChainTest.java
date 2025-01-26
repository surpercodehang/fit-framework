/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.handler.AbstractHttpHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link DefaultHttpServerFilterChain} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-21
 */
@DisplayName("测试 DefaultHttpServerFilterChain 类")
class DefaultHttpServerFilterChainTest {
    @Test
    @DisplayName("当参数为 null 时，没有过滤链场景，不进行过滤操作")
    void givenNullThenNotFiltering() {
        final AbstractHttpHandler httpHandler = mock(AbstractHttpHandler.class);
        final DefaultHttpServerFilterChain filterChain = new DefaultHttpServerFilterChain(httpHandler);
        assertDoesNotThrow(() -> filterChain.doFilter(null, null));
    }

    @Test
    @DisplayName("当参数不为 null 时，提供过滤器列表，顺序执行过滤过滤操作")
    void givenFilterThenExecuteFiltering() {
        final AbstractHttpHandler httpHandler = mock(AbstractHttpHandler.class);
        final HttpServerFilter serverFilter1 = mock(HttpServerFilter.class);
        when(serverFilter1.mismatchPatterns()).thenReturn(Collections.singletonList("/a/*"));
        final HttpServerFilter serverFilter2 = mock(HttpServerFilter.class);
        when(serverFilter2.matchPatterns()).thenReturn(Collections.singletonList("/a/*"));
        final List<HttpServerFilter> filters = Arrays.asList(serverFilter1, serverFilter2);
        when(httpHandler.preFilters()).thenReturn(filters);
        final DefaultHttpServerFilterChain filterChain = new DefaultHttpServerFilterChain(httpHandler);
        final HttpClassicServerRequest request = mock(HttpClassicServerRequest.class);
        when(request.path()).thenReturn("/a/b");
        filterChain.doFilter(request, null);
        verify(serverFilter2, times(1)).doFilter(any(), any(), any());
    }
}
