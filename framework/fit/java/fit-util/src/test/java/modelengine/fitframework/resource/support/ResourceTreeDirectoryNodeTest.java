/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

/**
 * {@link ResourceTreeDirectoryNode} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-03-01
 */
@DisplayName("测试 ResourceTreeDirectoryNode 类以及相关类")
class ResourceTreeDirectoryNodeTest {
    private final String name = "b";
    private File root;
    private ResourceTree tree;

    @BeforeEach
    @DisplayName("初始化 ResourceTreeDirectoryNode")
    void setup() throws IOException {
        this.root = Files.createTempDirectory("virtualization").toFile();
        createDirectory("a");
        createDirectory("b", "c");
        createFile("b", "d");
        createFile("b", "f");
        this.tree = ResourceTree.of(this.root);
    }

    @AfterEach
    void teardown() {
        FileUtils.delete(this.root);
    }

    @Test
    @DisplayName("测试 ResourceTreeDirectoryNode 类 path 方法时，返回正常信息")
    void givenResourceTreeDirectoryNodeShouldReturnTree() {
        ResourceTreeDirectoryNode node = ObjectUtils.cast(this.tree.roots().get(this.name));
        ResourceTree actual = node.tree();
        ResourceTree expected = ResourceTree.of(this.root);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("测试 ResourceTreeDirectoryNode 类 tree 方法时，返回正常信息")
    void givenResourceTreeDirectoryNodeShouldReturnPath() {
        ResourceTreeDirectoryNode node = ObjectUtils.cast(this.tree.roots().get(this.name));
        String path = node.path();
        assertThat(path).isEqualTo(this.name);
    }

    @Test
    @DisplayName("测试 ResourceTreeDirectoryNode 类 traverse 方法时，返回正常信息")
    void givenResourceTreeDirectoryNodeShouldReturnStringValue() {
        ResourceTreeDirectoryNode node = ObjectUtils.cast(this.tree.roots().get(this.name));
        StringBuilder builder = new StringBuilder();
        node.traverse(ele -> builder.append(ele.filename()));
        assertThat(builder.toString()).contains("d", "f");
    }

    @Test
    @DisplayName("测试 ResourceTreeDirectoryNode 类 traverse 方法过滤时，返回正常信息")
    void givenResourceTreeDirectoryNodeWhenFilterThenReturnStringValue() {
        ResourceTreeDirectoryNode node = ObjectUtils.cast(this.tree.roots().get(this.name));
        StringBuilder builder = new StringBuilder();
        String expected = "d";
        node.traverse(ele -> Objects.equals(ele.filename(), expected), ele -> builder.append(ele.filename()));
        assertThat(builder.toString()).isEqualTo(expected);
    }

    private void createFile(String... path) throws IOException {
        File directory = createDirectory(Arrays.copyOf(path, path.length - 1));
        File file = new File(directory, path[path.length - 1]);
        if (!file.exists()) {
            Files.createFile(file.toPath());
        }
    }

    private File createDirectory(String... path) throws IOException {
        File parent = this.root;
        for (String part : path) {
            File child = new File(parent, part);
            if (!child.exists()) {
                Files.createDirectory(child.toPath());
            }
            parent = child;
        }
        return parent;
    }
}