/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.tree;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * {@link Tree} 的单元测试。
 *
 * @author 梁济时
 * @since 2022-08-11
 */
@DisplayName("测试 Tree 工具类")
class TreeTest {
    @Nested
    @DisplayName("测试方法：getOrCreate(String path)")
    class TestGetOrCreate {
        private Tree tree;

        @BeforeEach
        void setup() {
            tree = Tree.create();
        }

        @Test
        @DisplayName("当获取或创建的节点的路径为 null 时，抛出异常")
        void whenGetOrCreateNodeWithNullPathThenThrowException() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> tree.getOrCreate(null));
            assertEquals("The path of node cannot be null.", exception.getMessage());
        }

        @Test
        @DisplayName("当设置指定路径的值时，自动创建结构")
        void whenPutValueShouldCreateNodes() {
            TreeNode node = tree.getOrCreate("a.b.c");
            node.tag(1);

            assertNotNull(node);
            assertEquals("c", node.name());
            assertEquals("a.b.c", node.path());
            assertSame(tree, node.tree());
            assertEquals(1, node.tag());
            assertTrue(node.children().empty());

            TreeNode parent = node.parent();
            assertNotNull(parent);
            assertEquals("b", parent.name());
            assertEquals("a.b", parent.path());
            assertSame(tree, parent.tree());
            assertNull(parent.tag());
            assertEquals(1, parent.children().size());
            assertFalse(parent.children().empty());

            TreeNode root = parent.parent();
            assertEquals("a", root.name());
            assertEquals("a", root.path());
            assertSame(tree, root.tree());
            assertNull(root.tag());
            assertEquals(1, root.children().size());
            assertNull(root.parent());
            assertFalse(root.children().empty());
        }

        @Test
        @DisplayName("当向节点设置值时，会重用路径中已存在的节点并创建不存在的节点")
        void whenPutValueShouldReuseExistingNodeAndCreateRequired() {
            tree.getOrCreate("a.b.c");
            TreeNode root = tree.roots().get(0);
            TreeNode node = tree.getOrCreate("a.d");
            node.tag(-1);
            assertSame(root, node.parent());
        }
    }

    @Nested
    @DisplayName("测试方法：get(String path)")
    class TestGet {
        private Tree tree;

        @BeforeEach
        void setup() {
            tree = Tree.create();
        }

        @Test
        @DisplayName("当使用 null 路径获取节点时，返回 Optional.empty()")
        void whenGetWithNullPathShouldReturnEmpty() {
            TreeNode node = tree.get(null);
            assertNull(node);
        }

        @Test
        @DisplayName("当获取不存在的节点时，返回 Optional.empty()")
        void whenGetNodeThatDoesNotExistShouldReturnEmpty() {
            TreeNode node = tree.get("a.notExistNode");
            assertNull(node);
        }

        @Test
        @DisplayName("当向已存在的节点设置值时，直接操作已存在的节点")
        void whenPutValueShouldUseExistingNode() {
            tree.getOrCreate("a.b.c");
            TreeNode existing = tree.get("a.b");
            assertNotNull(existing);

            TreeNode putNode = tree.getOrCreate("a.b");
            putNode.tag(100);
            assertSame(existing, putNode);
            assertEquals(100, existing.tag());
        }
    }

    @Nested
    @DisplayName("测试方法：anyInPath(Predicate<TreeNode> predicate)")
    class TestAnyInPath {
        private Tree tree;

        @BeforeEach
        void setup() {
            tree = Tree.create();
        }

        @Test
        @DisplayName("当查找路径中已存在的节点时，返回 true")
        void whenFindExistNodeShouldReturnTrue() {
            tree.getOrCreate("a.b.c");
            tree.getOrCreate("a.d");
            TreeNode root = tree.roots().get(0);
            final boolean inPath = root.anyInPath(treeNode -> "a".equals(treeNode.name()));
            assertTrue(inPath);
        }

        @Test
        @DisplayName("当查找路径中不存在的节点时，返回 false")
        void whenFindNoSuchNodeShouldReturnFalse() {
            tree.getOrCreate("a.b.c");
            TreeNode root = tree.roots().get(0);
            final boolean inPath1 = root.anyInPath(null);
            final boolean inPath2 = root.anyInPath(treeNode -> "notExist".equals(treeNode.name()));
            assertFalse(inPath1);
            assertFalse(inPath2);
        }
    }

    @Nested
    @DisplayName("测试方法：bfs(Consumer<TreeNode> consumer)")
    class TestBfs {
        private Tree tree;

        @BeforeEach
        void setup() {
            tree = Tree.create();
        }

        @Test
        @DisplayName("广度优先遍历时，遍历的顺序符合预期")
        void whenBfsShouldTraverseTree() {
            tree.getOrCreate("a.b.c");
            tree.getOrCreate("a.d");
            List<String> names = new LinkedList<>();
            tree.bfs(node -> names.add(node.name()));
            assertIterableEquals(Arrays.asList("a", "b", "d", "c"), names);
        }

        @Test
        @DisplayName("当广度优先遍历消费者为 null时，不遍历")
        void whenBfsWithNullShouldReturn() {
            assertDoesNotThrow(() -> tree.bfs(null));
        }
    }

    @Nested
    @DisplayName("测试方法：dfs(Consumer<TreeNode> consumer)")
    class TestDfs {
        private Tree tree;

        @BeforeEach
        void setup() {
            tree = Tree.create();
        }

        @Test
        @DisplayName("深度优先遍历时，遍历的顺序符合预期")
        void whenDfsShouldTraverseTree() {
            tree.getOrCreate("a.b.c");
            tree.getOrCreate("a.d");
            List<String> names = new LinkedList<>();
            tree.dfs(node -> names.add(node.name()));
            assertIterableEquals(Arrays.asList("a", "b", "c", "d"), names);
        }

        @Test
        @DisplayName("当深度优先遍历消费者为 null 时，不遍历")
        void whenDfsWithNullShouldReturn() {
            assertDoesNotThrow(() -> tree.dfs(null));
        }
    }

    @Nested
    @DisplayName("测试方法：remove(String path)")
    class TestRemove {
        private Tree tree;

        @BeforeEach
        void setup() {
            tree = Tree.create();
        }

        @Test
        @DisplayName("当移除节点后，节点与其子节点将不再与树关联")
        void whenRemoveNodeShouldNotConnectTree() {
            TreeNode nullNode = tree.remove(null);
            assertNull(nullNode);

            final TreeNode treeNode = tree.getOrCreate("a.b.c");
            treeNode.tag(1);
            TreeNode putNode = tree.getOrCreate("a.b");
            putNode.tag(100);

            TreeNode node = tree.remove("a.b");
            assertNotNull(node);
            assertNull(node.tree());
            assertNull(node.parent());
            assertEquals("b", node.name());
            assertEquals("b", node.toString());
            assertEquals("b", node.path());
            assertEquals(100, node.tag());
            assertEquals(1, node.children().size());

            TreeNode child = node.children().get(0);
            assertNull(child.tree());
            assertSame(node, child.parent());
            assertEquals("c", child.name());
            assertEquals("c", child.path());
            assertEquals(1, child.tag());
            assertTrue(child.children().empty());
        }

        @Test
        @DisplayName("当移除不存在节点时，返回 null")
        void whenRemoveNoSuchNodeShouldReturnNull() {
            TreeNode node = tree.roots().remove("notExist");
            assertNull(node);
        }
    }

    @Nested
    @DisplayName("测试方法：stream()")
    class TestStream {
        @Test
        @DisplayName("当调用流操作，返回一个流")
        void whenStreamShouldReturnStream() {
            Tree tree = Tree.create();
            final Stream<TreeNode> stream = tree.roots().stream();
            assertNotNull(stream);
            assertInstanceOf(Stream.class, stream);
        }
    }
}
