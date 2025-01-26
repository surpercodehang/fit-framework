/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.runtime.direct.DirectFitRuntime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link AbstractFitRuntime} 的单元测试。
 *
 * @author 李金绪
 * @since 2024-09-27
 */
@DisplayName("测试 AbstractFitRuntime")
public class AbstractFitRuntimeTest {
    private AbstractFitRuntime abstractFitRuntime = new DirectFitRuntime(null, null);

    @Test
    @DisplayName("测试正确获取当含特殊分隔符时")
    void shouldOkWhenWithFitSeparator() {
        Map<String, String> env = new HashMap<>();
        env.put("FIT_SEPARATOR", "__");
        env.put("application__home", "hello");

        Map<String, String> result = abstractFitRuntime.loadSpecificEnv(env);
        assertThat(result.getOrDefault("application.home", "")).isEqualTo("hello");
    }

    @Test
    @DisplayName("测试正确获取当不含特殊分隔符时")
    void shouldOkWhenWithoutFitSeparator() {
        Map<String, String> env = new HashMap<>();
        env.put("application__home", "hello");

        Map<String, String> result = abstractFitRuntime.loadSpecificEnv(env);
        assertThat(result.getOrDefault("application.home", "")).isEqualTo("");
    }
}
