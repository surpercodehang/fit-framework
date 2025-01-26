/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ConfigPath} 的测试类。
 */
@DisplayName("测试 ConfigPath 类")
class ConfigPathTest {
    @Test
    @DisplayName("当解析空字符串时，返回空的路径")
    void shouldReturnEmptyPathWhenParseNull() {
        ConfigPath path = ConfigPath.parse(null);
        assertNotNull(path);
        assertTrue(path.empty());
    }

    @Test
    @DisplayName("忽略配置路径中的空白项")
    void shouldIgnoreBlankPathItem() {
        ConfigPath path = ConfigPath.parse(". a.b . .");
        assertEquals(2, path.length());
        assertEquals("a", path.get(0));
        assertEquals("b", path.get(1));
    }

    @Test
    @DisplayName("当获取表示空白路径的实例时，都返回相同的实例")
    void shouldReturnSameInstanceOfEmptyPath() {
        ConfigPath path1 = ConfigPath.of(null);
        ConfigPath path2 = ConfigPath.of(Arrays.asList(null, null));
        assertSame(path1, path2);
    }

    @Test
    @DisplayName("空路径的父路径依旧是空路径")
    void shouldReturnEmptyWhenObtainParentOfEmpty() {
        ConfigPath empty = ConfigPath.EMPTY;
        ConfigPath parent = empty.parent();
        assertSame(empty, parent);
    }

    @Test
    @DisplayName("根路径（仅包含一个键）的父路径是空路径")
    void shouldReturnEmptyWhenObtainParentOfRoot() {
        ConfigPath root = ConfigPath.of(Collections.singletonList("a"));
        ConfigPath parent = root.parent();
        assertNotNull(parent);
        assertTrue(parent.empty());
    }

    @Test
    @DisplayName("通过 parent() 方法获取父路径")
    void shouldReturnParentPath() {
        ConfigPath path = ConfigPath.of(Arrays.asList("a", "b"));
        ConfigPath parent = path.parent();
        assertNotNull(parent);
        assertEquals(1, parent.length());
        assertEquals("a", parent.get(0));
    }

    @Test
    @DisplayName("通过 child(String) 方法获取子路径")
    void shouldReturnChildPath() {
        ConfigPath path = ConfigPath.of(Arrays.asList("a", "b"));
        ConfigPath child = path.child("c");
        assertNotNull(child);
        assertEquals(3, child.length());
        assertEquals("a", child.get(0));
        assertEquals("b", child.get(1));
        assertEquals("c", child.get(2));
    }

    @Test
    @DisplayName("获取已经存在的内嵌映射")
    void shouldReturnExistNestedMap() {
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> interim = new HashMap<>();
        Map<String, Object> target = new HashMap<>();
        root.put("a", interim);
        interim.put("b", target);
        ConfigPath path = ConfigPath.of(Arrays.asList("a", "b"));
        Map<String, Object> actual = path.get(root, false);
        assertNotNull(actual);
        assertSame(target, actual);
    }

    @Test
    @DisplayName("当不存在对应的内嵌映射时，返回 null")
    void shouldReturnNullWhenNoNestedMap() {
        Map<String, Object> root = new HashMap<>();
        ConfigPath path = ConfigPath.of(Arrays.asList("a", "b"));
        Map<String, Object> actual = path.get(root, false);
        assertNull(actual);
    }

    @Test
    @DisplayName("当不存在对应的内嵌映射时，返回自动创建的内嵌映射")
    void shouldReturnNewCreatedNestedMap() {
        Map<String, Object> root = new HashMap<>();
        ConfigPath path = ConfigPath.of(Arrays.asList("a", "b"));
        Map<String, Object> actual = path.get(root, true);
        Object interim = root.get("a");
        assertInstanceOf(Map.class, interim);
        Object target = ((Map<?, ?>) interim).get("b");
        assertSame(target, actual);
    }

    @Test
    @DisplayName("当使用 equals 方法比较同一个路径时，返回 true")
    void shouldReturnTrueWhenEqualsSamePaths() {
        ConfigPath path = ConfigPath.of(Arrays.asList("a", "b"));
        assertEquals(path, path);
    }

    @Test
    @DisplayName("当使用 equals 比较两个包含相同内容的路径时，返回 true")
    void shouldReturnTrueWhenEqualsPathsWithSameData() {
        List<String> keys = Arrays.asList("a", "b");
        ConfigPath path1 = ConfigPath.of(keys);
        ConfigPath path2 = ConfigPath.of(keys);
        assertNotSame(path1, path2);
        assertEquals(path1, path2);
    }

    @Test
    @DisplayName("当使用 equals 比较两个包含不同内容的路径时，返回 false")
    void shouldReturnFalseWhenEqualsPathsWithDifferentData() {
        ConfigPath path1 = ConfigPath.of(Arrays.asList("a", "b"));
        ConfigPath path2 = ConfigPath.of(Arrays.asList("a", "c"));
        assertNotEquals(path1, path2);
    }

    @Test
    @DisplayName("当使用 equals 方法与其他类型的对象比较时，返回 false")
    void shouldReturnFalseWhenEqualsWithObjectOfAnotherClass() {
        Object path = ConfigPath.of(Arrays.asList("a", "b"));
        Object another = 100;
        assertNotEquals(path, another);
    }

    @Test
    @DisplayName("当使用 equals 判断两个包含相同内容的路径时，返回 true")
    void shouldReturnSameHashWhenEqualsPathsWithSameData() {
        List<String> keys = Arrays.asList("a", "b");
        ConfigPath path1 = ConfigPath.of(keys);
        ConfigPath path2 = ConfigPath.of(keys);
        assertNotSame(path1, path2);
        assertEquals(path1.hashCode(), path2.hashCode());
    }

    @Test
    @DisplayName("返回友好的字符串，以描述路径")
    void shouldReturnFriendlyStringOfPath() {
        ConfigPath path = ConfigPath.of(Arrays.asList("a", "b"));
        assertEquals("a.b", path.toString());
    }
}
