/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import modelengine.fitframework.io.virtualization.VirtualDirectory;
import modelengine.fitframework.resource.classpath.support.DefaultClassPath;
import modelengine.fitframework.resource.classpath.support.FileClassPathKey;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link ClassPath} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-28
 */
@DisplayName("测试 ClassPath 类以及相关类")
class ClassPathTest {
    private File root;

    @BeforeEach
    @DisplayName("创建 file")
    void setup() throws IOException {
        this.root = Files.createTempDirectory("virtualization").toFile();
        createDirectory("a");
        createDirectory("b", "c");
        createFile("b", "d");
        createFile("e");
    }

    @AfterEach
    @DisplayName("删除 file")
    void teardown() throws IOException {
        delete(this.root);
    }

    @Test
    @DisplayName("测试 ClassPath 类 of 方法时，返回正常信息")
    void givenFileShouldReturnClassPath() {
        FileClassPathKey key = new FileClassPathKey(this.root);
        VirtualDirectory directory = VirtualDirectory.of(this.root);
        ClassPath classPath = ClassPath.of(key, directory);
        Assertions.assertThat(classPath.directory()).isEqualTo(directory);
        assertThat(classPath.key()).isEqualTo(key);
    }

    @Test
    @DisplayName("测试 ClassPath 类 create 方法时，返回列表")
    void givenFileShouldReturnClassPaths() throws IOException {
        FileClassPathKey key = new FileClassPathKey(this.root);
        List<ClassPath> classPaths = ClassPath.create(Collections.singleton(key));
        assertThat(classPaths).hasSize(1);
    }

    @Test
    @DisplayName("测试 ClassPath 类 close 方法时，正常执行")
    void givenClassPathWhenCloseThenNotThrow() {
        FileClassPathKey key = new FileClassPathKey(this.root);
        VirtualDirectory directory = VirtualDirectory.of(this.root);
        ClassPath classPath = ClassPath.of(key, directory);
        assertDoesNotThrow(classPath::close);
    }

    @Test
    @DisplayName("测试 DefaultClassPath 类 toString 方法时，返回正常信息")
    void givenDefaultClassPathShouldReturnPath() {
        FileClassPathKey key = new FileClassPathKey(this.root);
        VirtualDirectory directory = VirtualDirectory.of(this.root);
        DefaultClassPath classPath = new DefaultClassPath(key, directory);
        assertThat(classPath.toString()).isEqualTo(directory.path());
    }

    @Test
    @DisplayName("测试 ClassPathKey 类 name 方法时，返回正常信息")
    void givenClassPathKeyShouldReturnName() throws IOException {
        ClassPathKey key = new FileClassPathKey(ClassPathTest.this.root);
        String name = key.name();
        assertThat(name).isEqualTo(ClassPathTest.this.root.getCanonicalPath());
    }

    @Test
    @DisplayName("测试 ClassPathKey 类 load 方法时，返回正常信息")
    void givenClassPathKeyShouldReturnKeys() throws IOException {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Set<ClassPathKey> load = ClassPathKey.load(loader);
        assertThat(load).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("测试 FileClassPathKey 类 file 方法时，返回正常信息")
    void givenFileClassPathKeyShouldReturnFile() throws IOException {
        FileClassPathKey key = new FileClassPathKey(this.root);
        assertThat(key.file()).isEqualTo(this.root.getCanonicalFile());
    }

    @Test
    @DisplayName("测试 FileClassPathKey 类 equals 方法与其他类型比较时，返回 false")
    void givenOtherTypeClassShouldReturnFalse() {
        FileClassPathKey key = new FileClassPathKey(this.root);
        assertThat(key.equals("123")).isFalse();
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

    private void delete(File file) throws IOException {
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
}