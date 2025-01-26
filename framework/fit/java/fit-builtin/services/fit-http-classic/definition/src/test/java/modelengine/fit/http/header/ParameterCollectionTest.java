/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.header.support.DefaultParameterCollection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link ParameterCollection} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 ParameterCollection 类")
class ParameterCollectionTest {
    private static final String HEADER_TIMEOUT = "timeout";
    private static final String HEADER_TIMEOUT_VALUE = "3000";

    private ParameterCollection parameterCollection;

    @BeforeEach
    void setup() {
        this.parameterCollection = ParameterCollection.create();
        this.parameterCollection.set(HEADER_TIMEOUT, HEADER_TIMEOUT_VALUE);
    }

    @AfterEach
    void teardown() {
        this.parameterCollection = null;
    }

    @Test
    @DisplayName("获取所有的参数键的列表")
    void shouldReturnHeaderKeys() {
        final List<String> keys = this.parameterCollection.keys();
        assertThat(keys).hasSize(1).containsSequence(HEADER_TIMEOUT);
    }

    @Test
    @DisplayName("获取指定参数的值")
    void shouldReturnHeaderValue() {
        final Optional<String> optional = this.parameterCollection.get(HEADER_TIMEOUT);
        assertThat(optional).isPresent().get().isEqualTo(HEADER_TIMEOUT_VALUE);
    }

    @Test
    @DisplayName("获取所有的参数的数量")
    void shouldReturnHeaderSize() {
        final int size = this.parameterCollection.size();
        assertThat(size).isEqualTo(1);
    }

    @Test
    @DisplayName("获取所有参数的文本内容")
    void shouldReturnHeaderToString() {
        final String actual = this.parameterCollection.toString();
        final String expected = HEADER_TIMEOUT + DefaultParameterCollection.SEPARATOR + HEADER_TIMEOUT_VALUE;
        assertThat(actual).isEqualTo(expected);
    }
}
