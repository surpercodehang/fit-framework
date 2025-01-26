/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fit.http.server.handler.RequestMappingException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link AbstractSourceFetcher} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2025-01-09
 */
@DisplayName("测试 AbstractSourceFetcher 类")
class AbstractSourceFetcherTest {
    @Test
    @DisplayName("当源数据不为空时，返回自身的值。")
    void givenNotNullSourceThenReturnItself() {
        AbstractSourceFetcher sourceFetcher =
                new HeaderFetcher(ParamValue.custom().name("name").required(true).defaultValue("default").build());
        List<String> list1 = Collections.singletonList("value1");
        assertThat(sourceFetcher.resolveValue(list1)).isEqualTo(list1);
        List<String> list2 = Arrays.asList("value1", "value2");
        assertThat(sourceFetcher.resolveValue(list2)).isEqualTo(list2);
        String str = "value";
        assertThat(sourceFetcher.resolveValue(str)).isEqualTo(str);
    }

    @Test
    @DisplayName("当源数据为空且有默认值，返回默认值。")
    void givenNullSourceAndNotRequiredThenReturnItself() {
        AbstractSourceFetcher sourceFetcher =
                new HeaderFetcher(ParamValue.custom().name("name").required(false).defaultValue("default").build());
        assertThat(sourceFetcher.resolveValue(null)).isEqualTo("default");
        assertThat(sourceFetcher.resolveValue(new ArrayList<>())).isEqualTo(Collections.singletonList("default"));
    }

    @Test
    @DisplayName("当源数据为空,没有默认值，且参数必须时，抛出异常。")
    void givenEmptyListSourceAndRequiredThenReturnItself() {
        AbstractSourceFetcher sourceFetcher =
                new HeaderFetcher(ParamValue.custom().name("name").required(true).build());
        assertThatThrownBy(() -> sourceFetcher.resolveValue(null)).isInstanceOf(RequestMappingException.class);
        assertThatThrownBy(
                () -> sourceFetcher.resolveValue(new ArrayList<>())).isInstanceOf(RequestMappingException.class);
    }
}