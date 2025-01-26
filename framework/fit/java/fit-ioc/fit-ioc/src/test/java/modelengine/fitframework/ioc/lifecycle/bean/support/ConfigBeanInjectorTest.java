/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * {@link ConfigBeanInjector} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-03
 */
@DisplayName("测试 ConfigBeanInjector 类")
class ConfigBeanInjectorTest {
    private final Map<String, Object> employee =
            MapBuilder.<String, Object>get().put("id", "100").put("name", "zhang").build();

    @SuppressWarnings("unused")
    public static class Bean {
        private int id;
        private String name;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    @DisplayName("当配置中存在 Bean 的配置名称，注入成功")
    void whenConfigMatchBeanThenInjectSuccessfully() {
        final Config config = mock(Config.class);
        when(config.get(argThat("employee"::equals), any(Type.class))).thenReturn(this.employee);
        when(config.get(argThat("employee.id"::equals), any(Type.class))).thenReturn(100);
        when(config.get(argThat("employee.name"::equals), any(Type.class))).thenReturn("zhang");
        final ConfigBeanInjector beanInjector = new ConfigBeanInjector(config, "employee");
        final Bean bean = new Bean();
        beanInjector.inject(bean);
        assertThat(bean).hasFieldOrPropertyWithValue("id", 100);
        assertThat(bean).hasFieldOrPropertyWithValue("name", "zhang");
    }

    @Test
    @DisplayName("当配置中不存在 Bean 的配置名称，注入失败")
    void whenConfigNotMatchBeanThenInjectFailed() {
        final Config config = mock(Config.class);
        when(config.get(argThat("employee"::equals), any(Type.class))).thenReturn(this.employee);
        final ConfigBeanInjector beanInjector = new ConfigBeanInjector(config, "notExistKey");
        final Bean bean = new Bean();
        beanInjector.inject(bean);
        assertThat(bean).hasFieldOrPropertyWithValue("id", 0);
    }
}
