/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.resource.classpath.ClassPathKey;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link UriClassPathKeyResolverComposite} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-28
 */
@DisplayName("测试 UriClassPathKeyResolverComposite 类以及相关类")
class UriClassPathKeyResolverCompositeTest {
    private UriClassPathKeyResolverComposite composite;
    private File root;

    @BeforeEach
    @DisplayName("初始化 UriClassPathKeyResolverComposite")
    void setup() throws IOException {
        FileUriClassPathKeyResolver resolver1 = FileUriClassPathKeyResolver.INSTANCE;
        FileUriClassPathKeyResolver resolver2 = FileUriClassPathKeyResolver.INSTANCE;
        this.composite = new UriClassPathKeyResolverComposite(resolver1, resolver2);
        this.root = Files.createTempDirectory("virtualization").toFile();
    }

    @AfterEach
    @DisplayName("删除 file")
    void teardown() throws IOException {
        delete(this.root);
    }

    @Test
    @DisplayName("测试 UriClassPathKeyResolverComposite 类 resolve 方法不是 file 类型 uri 时，返回空")
    void givenNotFileTypeShouldReturnEmpty() throws IOException {
        URI uri = URI.create("http://localhost:8000/index.html");
        Optional<ClassPathKey> resolve = this.composite.resolve(uri);
        assertThat(resolve).isNotPresent();
    }

    @Test
    @DisplayName("测试 UriClassPathKeyResolverComposite 类 resolve 方法是 file 类型 uri 时，返回正常信息")
    void givenFileTypeShouldReturnFilePath() throws IOException {
        URI uri = this.root.toURI();
        Optional<ClassPathKey> resolve = this.composite.resolve(uri);
        FileClassPathKey key = new FileClassPathKey(this.root);
        assertThat(resolve).isPresent().get().isEqualTo(key);
    }

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 UriClassPathKeyResolverComposite 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            assertThat(UriClassPathKeyResolverCompositeTest.this.composite.equals(
                    UriClassPathKeyResolverCompositeTest.this.composite)).isTrue();
        }

        @Test
        @DisplayName("提供 UriClassPathKeyResolverComposite 类 equals 方法与相同类型的新对象比较时，返回 true")
        void givenSameTypeShouldReturnFalse() {
            FileUriClassPathKeyResolver resolver1 = FileUriClassPathKeyResolver.INSTANCE;
            FileUriClassPathKeyResolver resolver2 = FileUriClassPathKeyResolver.INSTANCE;
            UriClassPathKeyResolverComposite otherComposite =
                    new UriClassPathKeyResolverComposite(resolver1, resolver2);
            assertThat(UriClassPathKeyResolverCompositeTest.this.composite.equals(otherComposite)).isTrue();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 UriClassPathKeyResolverComposite 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            assertThat(UriClassPathKeyResolverCompositeTest.this.composite.equals("test")).isFalse();
        }
    }

    @Test
    @DisplayName("测试 UriClassPathKeyResolverComposite 类 hashCode 方法与相同类型的新对象比较时，返回正常信息")
    void givenUriClassPathKeyResolverCompositeShouldReturnHasCode() {
        FileUriClassPathKeyResolver resolver1 = FileUriClassPathKeyResolver.INSTANCE;
        FileUriClassPathKeyResolver resolver2 = FileUriClassPathKeyResolver.INSTANCE;
        UriClassPathKeyResolverComposite otherComposite =
                new UriClassPathKeyResolverComposite(resolver1, resolver2);
        assertThat(this.composite.hashCode()).isEqualTo(otherComposite.hashCode());
    }

    @Test
    @DisplayName("测试 UriClassPathKeyResolverComposite 类 toString 方法与相同类型的新对象比较时，返回正常信息")
    void givenUriClassPathKeyResolverCompositeShouldReturnStringValue() {
        FileUriClassPathKeyResolver resolver1 = FileUriClassPathKeyResolver.INSTANCE;
        FileUriClassPathKeyResolver resolver2 = FileUriClassPathKeyResolver.INSTANCE;
        UriClassPathKeyResolverComposite otherComposite =
                new UriClassPathKeyResolverComposite(resolver1, resolver2);
        assertThat(this.composite.toString()).isEqualTo(otherComposite.toString());
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