/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.wildcard.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 表示 {@link DefaultPathPattern} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-12-21
 */
@DisplayName("测试 DefaultPathPattern")
public class DefaultPathPatternTest {
    @ParameterizedTest
    @DisplayName("当匹配成功时，返回 true")
    @CsvSource({
            "/a,/a", "/a/*,/a/a", "/a/*,/a/ab", "/a/a?,/a/ab", "/a/**,/a", "/a/**,/a/b", "/a/**,/a/b/c",
            "/**/*.*,/a.html", "/**/*.*,/a/b/c.html"
    })
    void shouldReturnTrueWhenMatches(String pattern, String path) {
        boolean matches = Pattern.forPath(pattern, '/').matches(path);
        assertThat(matches).isTrue();
    }

    @ParameterizedTest
    @DisplayName("当匹配失败时，返回 false")
    @CsvSource({"/a,/b", "/a/*,/a/b/c", "/a/a?,/a/bb", "/a/**/b,/a", "/**/*.*,/a", "/**/*.*,/a/b/c"})
    void shouldReturnFalseWhenNotMatches(String pattern, String path) {
        boolean matches = Pattern.forPath(pattern, '/').matches(path);
        assertThat(matches).isFalse();
    }
}
