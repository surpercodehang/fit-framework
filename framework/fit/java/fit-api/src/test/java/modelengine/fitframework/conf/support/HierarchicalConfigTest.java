/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fitframework.conf.ModifiableConfigListener;
import modelengine.fitframework.util.CollectionUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link HierarchicalConfig} 的测试类。
 */
@DisplayName("测试 HierarchicalConfig 类")
class HierarchicalConfigTest {
    @Test
    @DisplayName("应将初始化的键值对进行展开")
    void shouldReturnValueInInitialMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("a.b", "x");
        values.put("a.c", "y");
        HierarchicalConfig config = new HierarchicalConfig(null, values);
        Object x = config.get("a.b");
        Object y = config.get("a.c");
        assertEquals("x", x);
        assertEquals("y", y);
    }

    @Test
    @DisplayName("初始值中空白的键的项将被忽略掉")
    void shouldIgnoreItemWithBlankKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("a.b", "x");
        values.put(" . ", "y");
        HierarchicalConfig config = new HierarchicalConfig(null, values);
        Set<String> keys = config.keys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("a.b"));
    }

    @Test
    @DisplayName("初始值中空的值的项将被忽略掉")
    void shouldIgnoreItemWithEmptyValue() {
        Map<String, Object> values = new HashMap<>();
        values.put("a.b", 100);
        values.put("a.c", new Object[0]);
        values.put("d.e", "  ");
        values.put("d.f", Collections.emptyList());
        values.put("g", Collections.emptyMap());
        HierarchicalConfig config = new HierarchicalConfig(null, values);
        Set<String> keys = config.keys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("a.b"));
    }

    @Test
    @DisplayName("应忽略掉集合中的空项")
    void shouldIgnoreItemsInCollectionValue() {
        HierarchicalConfig config = new HierarchicalConfig(null);
        config.set("a.b", Arrays.asList("  ", "x", Collections.emptyList(), Collections.emptyMap(), new Object[0]));
        Object value = config.get("a.b");
        assertInstanceOf(Collection.class, value);
        Collection<?> collection = (Collection<?>) value;
        assertEquals(1, collection.size());
        Assertions.assertEquals("x", CollectionUtils.firstOrDefault(collection));
    }

    @Test
    @DisplayName("将统一使用 List 保存数组内容")
    void shouldStoreArrayAsList() {
        HierarchicalConfig config = new HierarchicalConfig(null);
        config.set("a.b", new Object[] {"x"});
        Object value = config.get("a.b");
        assertInstanceOf(List.class, value);
        List<?> list = (List<?>) value;
        assertEquals(1, list.size());
        assertEquals("x", list.get(0));
    }

    @Test
    @DisplayName("当为空白的键设置值时，抛出异常")
    void shouldThrowWhenSetValueOfBlankKey() {
        HierarchicalConfig config = new HierarchicalConfig(null);
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> config.set(" . . ", "x"));
        assertEquals("The key of config to modify value cannot be blank.", exception.getMessage());
    }

    @Test
    @DisplayName("当获取空白的键对应的值时，返回 null")
    void shouldReturnNullWhenGetValueOfBlankKey() {
        Map<String, Object> values = new HashMap<>();
        values.put(" . . ", "x");
        HierarchicalConfig config = new HierarchicalConfig(null, values);
        Object value = config.get(" . . ");
        assertNull(value);
    }

    @Test
    @DisplayName("当将指定的键设置为 null 时，移除该键")
    void shouldRemoveKeyWhenSetNull() {
        HierarchicalConfig config = new HierarchicalConfig(null);
        config.set("a.b", "x");
        assertEquals(1, config.keys().size());
        config.set("a.b", null);
        assertTrue(config.keys().isEmpty());
    }

    @Test
    @DisplayName("当清除不存在的键的值时，不会抛出异常")
    void shouldDoNothingWhenClearValuesForNonExistKey() {
        HierarchicalConfig config = new HierarchicalConfig(null);
        assertDoesNotThrow(() -> config.set("a.b", null));
    }

    @Test
    @DisplayName("当配置的值发生变化时，通知监听程序，并且当监听程序被移除后，值的变化不再通知")
    void shouldNotifyListenerWhenValueIsChanged() {
        HierarchicalConfig config = new HierarchicalConfig(null);
        ModifiableConfigListener listener = mock(ModifiableConfigListener.class);
        config.subscribe(listener);
        config.set("a.b", "x");
        verify(listener, times(1)).onValueChanged(config, "a.b");
        config.unsubscribe(listener);
        config.set("a.b", "y");
        verify(listener, times(1)).onValueChanged(config, "a.b");
    }
}
