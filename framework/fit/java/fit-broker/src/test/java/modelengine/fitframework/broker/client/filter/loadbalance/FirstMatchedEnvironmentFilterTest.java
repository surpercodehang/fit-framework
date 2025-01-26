/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.loadbalance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.Target;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link FirstMatchedEnvironmentFilter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-07-15
 */
@DisplayName("测试 FirstMatchedEnvironmentFilter")
public class FirstMatchedEnvironmentFilterTest {
    private FirstMatchedEnvironmentFilter filter;
    private FitableMetadata fitable;

    @BeforeEach
    void setup() {
        this.filter = new FirstMatchedEnvironmentFilter(Arrays.asList("dev", "test"));
        this.fitable = mock(FitableMetadata.class);
        Genericable genericable = mock(Genericable.class);
        when(this.fitable.genericable()).thenReturn(genericable);
        when(genericable.id()).thenReturn("g");
        when(this.fitable.id()).thenReturn("f");
    }

    @AfterEach
    void teardown() {
        this.filter = null;
        this.fitable = null;
    }

    @Test
    @DisplayName("当存在环境调用链中环境匹配时，返回正确的地址")
    void shouldReturnCorrectTargetsWithSpecifiedEnvironment() {
        List<Target> actual = this.filter.filter(this.fitable,
                "local",
                Arrays.asList(Target.custom().environment("dev").build(), Target.custom().environment("test").build()),
                null);
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("当不存在环境调用链中环境匹配时，返回空的地址")
    void shouldReturnEmptyTargetsWithoutSpecifiedEnvironment() {
        List<Target> actual = this.filter.filter(this.fitable,
                "local",
                Arrays.asList(Target.custom().environment("local").build(),
                        Target.custom().environment("alpha").build()),
                null);
        assertThat(actual).isEmpty();
    }
}
