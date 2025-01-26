/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.dispatch.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.dispatch.MappingTree;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

/**
 * {@link MappingTree} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-07-27
 */
@DisplayName("测试 DefaultMappingTree")
public class DefaultMappingTreeTest {
    private MappingTree<HttpHandler> mappingTree;
    private HttpHandler handler;

    @BeforeEach
    void setup() {
        this.mappingTree = new DefaultMappingTree<>();
        this.handler = mock(HttpHandler.class);
    }

    @AfterEach
    void teardown() {
        this.mappingTree = null;
        this.handler = null;
    }

    @ParameterizedTest(name = "{index}: 搜索路径为 `{0}`")
    @DisplayName("当搜索时，返回空的处理器")
    @ValueSource(strings = {"/a", "/a/b"})
    void returnEmptyWhenSearching(String path) {
        Optional<HttpHandler> searchedHandler = DefaultMappingTreeTest.this.mappingTree.search(path);
        assertThat(searchedHandler).isEmpty();
    }

    @Nested
    @DisplayName("注册了一个含通配符的路径后")
    class AfterRegistering1WildPattern {
        @ParameterizedTest(name = "{index}: 注册路径样式为 `{0}`，搜索路径为 `{1}`")
        @DisplayName("搜索匹配的路径可以获取处理器")
        @CsvSource({"/*/a,/a/a", "/*/*,/a/b", "/a/*,/a/b"})
        void returnHandlerWhenSearchingMatchedPath(String pathPattern, String searchPath) {
            DefaultMappingTreeTest.this.mappingTree.register(pathPattern, DefaultMappingTreeTest.this.handler);
            Optional<HttpHandler> search = DefaultMappingTreeTest.this.mappingTree.search(searchPath);
            assertThat(search).isNotEmpty().get().isEqualTo(DefaultMappingTreeTest.this.handler);
        }
    }
}
