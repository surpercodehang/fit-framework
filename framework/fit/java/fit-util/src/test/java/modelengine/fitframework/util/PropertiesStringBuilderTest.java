/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link PropertiesStringBuilder} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2021-11-05
 */
class PropertiesStringBuilderTest {
    @Nested
    @DisplayName("Test expected scenario")
    class TestExpectedScenario {
        @Test
        @DisplayName("Given ordered entries then return ordered string")
        void givenOrderedEntriesThenReturnOrderedString() throws IOException {
            List<Map.Entry<String, String>> entries = Arrays.asList(new AbstractMap.SimpleEntry<>("key2", "value2"),
                    new AbstractMap.SimpleEntry<>("key1", "value1"),
                    new AbstractMap.SimpleEntry<>("key3", "value3"));
            String required = entries.stream()
                    .map(entry -> String.format(Locale.ROOT, "%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(LineSeparator.CR.value()));
            PropertiesStringBuilder builder = new PropertiesStringBuilder();
            builder = builder.setLineSeparator(LineSeparator.CR);
            for (Map.Entry<String, String> entry : entries) {
                builder = builder.setProperty(entry.getKey(), entry.getValue());
            }
            String content = builder.build();
            assertEquals(required, content);

            Properties properties = new Properties();
            properties.load(new StringReader(content));
            assertEquals(3, properties.size());
            for (Map.Entry<String, String> entry : entries) {
                assertEquals(entry.getValue(), properties.getProperty(entry.getKey()));
            }
        }

        @Test
        @DisplayName("Given duplicated values then return new value")
        void givenDuplicatedValuesThenReturnNewValue() {
            String key = "key";
            String oldValue = "value1";
            String newValue = "value2";
            String actual = new PropertiesStringBuilder().setProperty(key, oldValue).setProperty(key, newValue).build();
            assertEquals("key=value2", actual.trim());
        }
    }

    @Nested
    @DisplayName("Test OrderedProperties.class")
    class TestOrderedProperties {
        private Properties properties;

        @BeforeEach
        void setup() throws NoSuchFieldException, IllegalAccessException {
            Field propertiesField = PropertiesStringBuilder.class.getDeclaredField("properties");
            propertiesField.setAccessible(true);
            PropertiesStringBuilder builder =
                    new PropertiesStringBuilder().setProperty("k1", "v1").setProperty("k2", "v2");
            this.properties = ObjectUtils.cast(propertiesField.get(builder));
        }

        @AfterEach
        void teardown() {
            this.properties = null;
        }

        @Nested
        @DisplayName("Test method: entrySet()")
        class TestEntrySet {
            @Test
            @DisplayName("Given properties with {'k1': 'v1', 'k2': 'v2'} then return 2 entries")
            void givenPropertiesWith2EntriesThenReturnCorrectResult() {
                Set<Map.Entry<Object, Object>> entrySet = TestOrderedProperties.this.properties.entrySet();
                assertThat(entrySet).hasSize(2);
                for (Map.Entry<Object, Object> entry : entrySet) {
                    assertThat(entry).isNotNull();
                }
            }
        }

        @Nested
        @DisplayName("Test method: keys()")
        class TestKeys {
            @Test
            @DisplayName("Given properties with {'k1': 'v1', 'k2': 'v2'} then return 2 keys")
            void givenPropertiesWith2EntriesThenReturnCorrectResult() {
                Enumeration<Object> keys = TestOrderedProperties.this.properties.keys();
                assertThat(keys).isNotNull();
                int count = 0;
                while (keys.hasMoreElements()) {
                    count++;
                    Object key = keys.nextElement();
                    assertThat(key).isNotNull();
                }
                assertThat(count).isEqualTo(2);
            }
        }
    }
}
