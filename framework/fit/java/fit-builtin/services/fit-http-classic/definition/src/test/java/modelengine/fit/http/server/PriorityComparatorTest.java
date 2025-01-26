/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.server.HttpServerFilter.PriorityComparator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link PriorityComparator} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-09-13
 */
@DisplayName("测试 HttpServerFilter.PriorityComparator")
public class PriorityComparatorTest {
    private final Filter1 f1 = new Filter1();
    private final Filter2 f2 = new Filter2();
    private final Filter3 f3 = new Filter3();

    @Test
    @DisplayName("当优先级不一样时，按照优先级进行排序")
    void shouldSortByPriority() {
        List<HttpServerFilter> filters = new ArrayList<>();
        filters.add(this.f3);
        filters.add(this.f1);
        filters.sort(PriorityComparator.INSTANCE);
        assertThat(filters).containsExactly(this.f1, this.f3);
    }

    @Test
    @DisplayName("当优先级一样时，按照名字进行排序")
    void shouldSortByName() {
        List<HttpServerFilter> filters = new ArrayList<>();
        filters.add(this.f2);
        filters.add(this.f1);
        filters.sort(PriorityComparator.INSTANCE);
        assertThat(filters).containsExactly(this.f1, this.f2);
    }

    static class Filter1 implements HttpServerFilter {
        @Override
        public String name() {
            return "a";
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public List<String> matchPatterns() {
            return null;
        }

        @Override
        public List<String> mismatchPatterns() {
            return null;
        }

        @Override
        public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
                HttpServerFilterChain chain) throws DoHttpServerFilterException {}
    }

    static class Filter2 implements HttpServerFilter {
        @Override
        public String name() {
            return "b";
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public List<String> matchPatterns() {
            return null;
        }

        @Override
        public List<String> mismatchPatterns() {
            return null;
        }

        @Override
        public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
                HttpServerFilterChain chain) throws DoHttpServerFilterException {}
    }

    static class Filter3 implements HttpServerFilter {
        @Override
        public String name() {
            return "c";
        }

        @Override
        public int priority() {
            return 1;
        }

        @Override
        public List<String> matchPatterns() {
            return null;
        }

        @Override
        public List<String> mismatchPatterns() {
            return null;
        }

        @Override
        public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
                HttpServerFilterChain chain) throws DoHttpServerFilterException {}
    }
}
