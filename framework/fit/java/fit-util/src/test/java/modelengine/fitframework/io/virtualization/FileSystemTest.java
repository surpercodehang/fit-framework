/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.virtualization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DisplayName("测试虚拟文件系统基于文件系统的实现")
class FileSystemTest {
    private static File root;

    /**
     * 临时文件目录：root
     *   [d] a
     *   [d] b
     *     [d] c
     *     [f] d
     *   [f] e
     *
     * @throws IOException 当创建文件时异常时。
     */
    @BeforeAll
    static void setup() throws IOException {
        root = Files.createTempDirectory("virtualization").toFile();
        createDirectory("a");
        createDirectory("b", "c");
        createFile("b", "d");
        createFile("e");
    }

    private static File createDirectory(String... path) throws IOException {
        File parent = root;
        for (String part : path) {
            File child = new File(parent, part);
            if (!child.exists()) {
                Files.createDirectory(child.toPath());
            }
            parent = child;
        }
        return parent;
    }

    private static void createFile(String... path) throws IOException {
        File directory = createDirectory(Arrays.copyOf(path, path.length - 1));
        File file = new File(directory, path[path.length - 1]);
        if (!file.exists()) {
            Files.createFile(file.toPath());
        }
    }

    @AfterAll
    static void teardown() throws IOException {
        delete(root);
    }

    private static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            List<File> children = Optional.ofNullable(file.listFiles())
                    .map(Stream::of)
                    .orElse(Stream.empty())
                    .collect(Collectors.toList());
            for (File child : children) {
                delete(child);
            }
        }
        Files.delete(file.toPath());
    }

    @Test
    @DisplayName("可以遍历目录中的目录和文件")
    void should_iterate_directories_and_files() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        assertFalse(directory.file("a").isPresent());
        assertTrue(directory.child("a").isPresent());
        Optional<VirtualDirectory> optionalC = directory.child("b", "c");
        assertTrue(optionalC.isPresent());
        Optional<VirtualFile> optionalD = directory.file("b", "d");
        assertTrue(optionalD.isPresent());
    }

    @Test
    @DisplayName("取所包含的文件的集合")
    void shouldReturnFiles() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        final Collection<VirtualFile> files = directory.files();
        final Iterator<VirtualFile> fileIterator = files.iterator();
        final VirtualFile expect = VirtualFile.of(new File(root, "e"));
        while (fileIterator.hasNext()) {
            final VirtualFile virtualFile = fileIterator.next();
            assertThat(virtualFile.path()).isEqualTo(expect.path());
            assertThat(virtualFile.name()).isEqualTo(expect.name());
            assertThat(virtualFile.directory()).isEqualTo(expect.directory());
        }
    }

    @Test
    @DisplayName("获取文件系统元素的名称")
    void shouldReturnName() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        final String name = directory.name();
        assertThat(name).startsWith("virtualization");
    }

    @Test
    @DisplayName("获取文件系统元素的路径")
    void shouldReturnPath() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        final String path = directory.path();
        assertThat(path).endsWith(directory.name());
    }

    @Test
    @DisplayName("获取父目录")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void shouldReturnParent() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        final VirtualDirectory parent = directory.parent();
        final VirtualDirectory child = parent.child(root.getName()).get();
        assertThat(child).isEqualTo(directory);
    }

    @Test
    @DisplayName("获取文件系统元素的 url")
    void shouldReturnUrl() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        final URL url = directory.url();
        assertThat(url.getPath()).contains("virtualization");
    }

    @Test
    @DisplayName("获取所包含的子目录的集合")
    void shouldReturnChildren() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        final Collection<VirtualDirectory> children = directory.children();
        assertThat(children).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("测试方法：hashCode()")
    void testHashCode() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        final int hashCode = directory.hashCode();
        assertThat(hashCode).isNotEqualTo(0);
    }

    @Test
    @DisplayName("测试方法：toString()")
    void testToString() {
        VirtualDirectory directory = VirtualDirectory.of(root);
        final String toString = directory.toString();
        assertThat(toString).contains("virtualization");
    }

    @Nested
    @DisplayName("测试方法：equals(Object obj)")
    class TestEquals {
        @Test
        @DisplayName("当和自己比较相等时，返回 true")
        @SuppressWarnings("EqualsWithItself")
        void givenSelfThenReturnTrue() {
            VirtualDirectory directory = VirtualDirectory.of(root);
            assertThat(directory.equals(directory)).isTrue();
        }

        @Test
        @DisplayName("当和不同类型比较时，返回 false")
        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        void givenOtherTypeThenReturnFalse() {
            VirtualDirectory directory = VirtualDirectory.of(root);
            assertThat(directory.equals("")).isFalse();
        }
    }

    @Test
    @DisplayName("当目录为 null 时抛出异常")
    void should_throw_when_directory_is_null() {
        assertThrows(IllegalArgumentException.class, () -> VirtualDirectory.of(null));
    }

    @Test
    @DisplayName("当目录不存在时抛出异常")
    void should_throw_when_directory_not_exist() {
        File directory = new File(root, "x");
        assertThrows(IllegalArgumentException.class, () -> VirtualDirectory.of(directory));
    }

    @Test
    @DisplayName("当目录不是目录时抛出异常")
    void should_throw_when_directory_is_file() {
        File file = new File(root, "e");
        assertThrows(IllegalArgumentException.class, () -> VirtualDirectory.of(file));
    }
}
