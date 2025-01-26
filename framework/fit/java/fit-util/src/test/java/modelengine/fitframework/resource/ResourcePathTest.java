/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link ResourcePath} 的单元测试。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
@DisplayName("测试 ResourcePath")
class ResourcePathTest {
    @Test
    @DisplayName("当样式为空字符串时，路径为 null")
    void should_return_null_path_when_pattern_is_empty() {
        ResourcePath path = ResourcePath.parse(StringUtils.EMPTY);
        assertNull(path.head());
    }

    @Test
    @DisplayName("测试方法：toString()")
    void testToString() {
        ResourcePath path = ResourcePath.parse("x");
        assertThat(path.head()).hasToString("x");
    }

    @Test
    @DisplayName("测试方法：empty()")
    void testEmpty() {
        ResourcePath path = ResourcePath.parse("x");
        assertThat(path.empty()).isFalse();
    }

    @Test
    @DisplayName("当样式不包含分隔符时，返回仅有一个节点的路径")
    void should_return_path_with_single_node_when_pattern_does_not_contain_separator() {
        ResourcePath path = ResourcePath.parse("x");
        assertNotNull(path.head());
        assertEquals("x", path.head().label());
        assertNull(path.head().previous());
        assertNull(path.head().next());
    }

    @Test
    @DisplayName("当存在分隔符时，返回包含多个节点的路径")
    void should_return_path_with_multiple_nodes_when_pattern_contains_separator() {
        ResourcePath path = ResourcePath.parse("conf/*.yml");
        assertNotNull(path.head());
        assertEquals("conf", path.head().label());
        assertNull(path.head().previous());
        assertNotNull(path.head().next());

        ResourcePath.Node next = path.head().next();
        assertEquals("*.yml", next.label());
        assertNull(next.next());
        assertSame(path.head(), next.previous());
    }

    @Test
    @DisplayName("忽略路径中为空白字符串的部分")
    void should_ignore_empty_nodes_in_path() {
        ResourcePath path = ResourcePath.parse("//x//y//");
        assertNotNull(path.head());
        assertEquals("x", path.head().label());
        assertNull(path.head().previous());
        assertNotNull(path.head().next());

        ResourcePath.Node next = path.head().next();
        assertEquals("y", next.label());
        assertNull(next.next());
        assertSame(path.head(), next.previous());
    }

    @Test
    @DisplayName("将剪裁路径节点标签前后的空白字符")
    void should_trim_labels_in_path() {
        ResourcePath path = ResourcePath.parse(" x ");
        assertNotNull(path.head());
        assertEquals("x", path.head().label());
        assertNull(path.head().previous());
        assertNull(path.head().next());
    }

    @Test
    @DisplayName("对路径中的标签进行标准化")
    void should_canonicalize_labels() {
        ResourcePath path = ResourcePath.parse("//****/**.x/A***B");

        ResourcePath.Node node = path.head();
        assertNotNull(node);
        assertEquals("**", node.label());

        node = node.next();
        assertNotNull(node);
        assertEquals("*.x", node.label());

        node = node.next();
        assertNotNull(node);
        assertEquals("A*B", node.label());

        node = node.next();
        assertNull(node);
    }
}
