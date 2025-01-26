/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.broker.server.GenericableServerFilter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link DefaultGenericableServerFilterChain} 的测试类。
 *
 * @author 李金绪
 * @since 2024-08-27
 */
@DisplayName("测试 DefaultGenericableServerFilterChain")
public class DefaultGenericableServerFilterChainTest {
    @Test
    @DisplayName("当参数为 null 时，没有过滤链场景，不进行过滤操作")
    void givenNullThenNotFiltering() {
        final DefaultGenericableServerFilterChain filterChain =
                new DefaultGenericableServerFilterChain("mockGenericableId", Collections.emptyList());
        assertDoesNotThrow(() -> filterChain.doFilter(null));
    }

    @Test
    @DisplayName("当参数不为 null 时，提供过滤器列表，顺序执行过滤过滤操作")
    void givenFilterThenExecuteFiltering() {
        final GenericableServerFilter serverFilter1 = mock(GenericableServerFilter.class);
        when(serverFilter1.mismatchPatterns()).thenReturn(Collections.singletonList("com.*"));
        final GenericableServerFilter serverFilter2 = mock(GenericableServerFilter.class);
        when(serverFilter2.matchPatterns()).thenReturn(Collections.singletonList("com.*"));
        final List<GenericableServerFilter> filters = Arrays.asList(serverFilter1, serverFilter2);
        final DefaultGenericableServerFilterChain filterChain =
                new DefaultGenericableServerFilterChain("com.mock", filters);
        filterChain.doFilter(null);
        verify(serverFilter2, times(1)).doFilter(any(), any(), any());
    }
}
