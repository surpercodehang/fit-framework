/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.beans.convert.ConversionService;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * {@link Config} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-16
 */
@DisplayName("测试 Config 类以及相关类")
class ConfigTest {
    @Nested
    @DisplayName("测试类：Config")
    class TestConfig {
        @Test
        @DisplayName("提供 Config 类 list 方法时，返回列表信息")
        void givenConfigShouldReturnValue() {
            ConfigImpl config = new ConfigImpl();
            String key = "key";
            Object value = 3;
            config.put(key, value);
            List<Integer> configValue = config.list(key, Integer.class);
            assertThat(configValue).isEqualTo(Collections.singletonList(value));
        }

        @Test
        @DisplayName("提供 Config 类 list 方法类型为 String 时，返回信息列表")
        void givenConfigWhenTypeIsStringThenReturnValue() {
            ConfigImpl config = new ConfigImpl();
            String key = "key";
            Object value = 3;
            config.put(key, value);
            List<String> list = config.list(key, String.class);
            assertThat(list).contains("3");
        }
    }

    @Test
    @DisplayName("当创建一个 Map 配置时，返回一个正确的配置。")
    void shouldReturnMapConfig() {
        ModifiableConfig actual = Config.fromMap("map", MapBuilder.<String, Object>get().build());
        actual.set("key", "value");
        assertThat(actual).isNotNull().returns("value", config -> actual.get("key", String.class));
    }

    @Test
    @DisplayName("当创建一个 Properties 配置时，返回一个正确的配置。")
    void shouldReturnPropertiesConfig() {
        Properties properties = new Properties();
        ModifiableConfig actual = Config.fromProperties("map", properties);
        actual.set("key", "value");
        assertThat(actual).isNotNull().returns("value", config -> actual.get("key", String.class));
    }

    @Test
    @DisplayName("当创建一个只读的 Map 配置时，返回一个正确的配置。")
    void shouldReturnReadonlyMapConfig() {
        Config actual = Config.fromReadonlyMap("map", MapBuilder.<String, Object>get().put("key", "value").build());
        assertThat(actual).isNotNull().returns("value", config -> actual.get("key", String.class));
    }

    @Test
    @DisplayName("当创建一个只读的 Properties 配置时，返回一个正确的配置。")
    void shouldReturnReadonlyPropertiesConfig() {
        Properties properties = new Properties();
        properties.setProperty("key", "value");
        Config actual = Config.fromReadonlyProperties("map", properties);
        assertThat(actual).isNotNull().returns("value", config -> actual.get("key", String.class));
    }

    @Test
    @DisplayName("当创建一个层次化的配置时，返回一个正确的配置。")
    void shouldReturnHierarchicalConfig() {
        ModifiableConfig actual = Config.fromHierarchical("map",
                MapBuilder.<String, Object>get()
                        .put("key", MapBuilder.<String, String>get().put("subKey", "value").build())
                        .build());
        assertThat(actual).isNotNull().returns("value", config -> actual.get("key.subKey", String.class));
    }

    @Nested
    @DisplayName("测试可视化键的方法")
    class TestVisualizeKey {
        @Test
        @DisplayName("可视化键成功")
        void shouldReturnCorrectKey() {
            String actual = Config.visualizeKey("a.helloWorld.c");
            assertThat(actual).isEqualTo("a.hello-world.c");
        }
    }

    private static class ConfigImpl implements Config {
        private final Map<String, Object> map;

        public ConfigImpl() {
            this.map = new HashMap<>();
        }

        private void put(String key, Object value) {
            this.map.put(key, value);
        }

        @Override
        public String name() {
            return this.getClass().getSimpleName();
        }

        @Override
        public Set<String> keys() {
            return this.map.keySet();
        }

        private Object get(String key) {
            return this.map.get(key);
        }

        @Override
        public Object get(String key, Type type) {
            Object value = this.get(key);
            return ConversionService.forConfig().convert(value, type);
        }

        @Override
        public <T> T get(String key, Class<T> clazz) {
            Object value = this.get(key);
            return ConversionService.forConfig().convert(value, clazz);
        }

        @Override
        public void decrypt(@Nonnull ConfigDecryptor decryptor) {}
    }
}
