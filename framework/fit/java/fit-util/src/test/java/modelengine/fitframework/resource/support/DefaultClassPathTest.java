/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.resource.ClassPath;
import modelengine.fitframework.resource.ClassPathResource;
import modelengine.fitframework.resource.ResourcePath;
import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.wildcard.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * {@link DefaultClassPath} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-28
 */
@DisplayName("测试 DefaultClassPath 工具类")
class DefaultClassPathTest {
    private DefaultClassPath classPath;
    private ClassLoader classLoader;
    private URL url;

    @BeforeEach
    void setup() throws URISyntaxException {
        this.classLoader = this.getClass().getClassLoader();
        this.url = this.getClass().getResource("/text");
        File file = new File(this.url.toURI());
        ResourceTree resourceTree = ResourceTree.of(file);
        this.classPath = ObjectUtils.cast(ClassPath.of(this.classLoader, resourceTree));
    }

    @Test
    @DisplayName("测试方法：loader()")
    void shouldReturnClassLoader() {
        final ClassLoader loader = this.classPath.loader();
        assertThat(loader).isEqualTo(this.classLoader);
    }

    @Test
    @DisplayName("测试方法：url()")
    void shouldReturnUrl() throws MalformedURLException {
        final URL classPathUrl = this.classPath.url();
        assertThat(classPathUrl).isNotNull();
    }

    @Test
    @DisplayName("测试方法：resources()")
    void shouldReturnResources() {
        final ResourceTree resources = this.classPath.resources();
        assertThat(resources).isNotNull();
    }

    @Test
    @DisplayName("测试方法：from(ClassLoader loader, boolean recursive)")
    void shouldReturnClassPaths() {
        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        final List<ClassPath> classPaths = DefaultClassPath.from(systemClassLoader, false);
        assertThat(classPaths).hasSizeGreaterThan(0);
    }

    @Nested
    @DisplayName("测试方法：equals(Object obj)")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供资源实例与本身比较时时，返回 true")
        void givenSelfShouldReturnTrue() {
            assertThat(DefaultClassPathTest.this.classPath.equals(DefaultClassPathTest.this.classPath)).isTrue();
        }

        @Test
        @DisplayName("提供资源实例与相同配置的新对象比较时，返回 true")
        void givenSameTypeShouldReturnTrue() {
            final ClassPath defaultClassPath =
                    ClassPath.of(DefaultClassPathTest.this.classLoader, DefaultClassPathTest.this.url);
            assertThat(defaultClassPath.equals(DefaultClassPathTest.this.classPath)).isTrue();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供资源实例与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            assertThat(DefaultClassPathTest.this.classPath.equals(String.class)).isFalse();
        }
    }

    @Test
    @DisplayName("测试方法：toString()")
    void testToString() {
        final String toString = this.classPath.toString();
        assertThat(toString).asString().endsWith("text");
    }

    @Test
    @DisplayName("测试方法：hashCode()")
    void testHashCode() {
        final int actual = this.classPath.hashCode();
        final ClassPath defaultClassPath = ClassPath.of(this.classLoader, this.url);
        final int expected = defaultClassPath.hashCode();
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    @DisplayName("测试内部类")
    class TestClassPathResource {
        private ClassPathResource classPathResource;

        @BeforeEach
        void setup() {
            ResourcePath resourcePath = ResourcePath.parse("hello.txt");
            Pattern<String> pattern = resourcePath.asPattern();
            final List<ClassPathResource> resolveList = DefaultClassPathTest.this.classPath.resolve(pattern);
            this.classPathResource = resolveList.get(0);
        }

        @Test
        @DisplayName("测试方法：key()")
        void shouldReturnKey() {
            final String key = this.classPathResource.key();
            assertThat(key).isEqualTo("hello.txt");
        }

        @Test
        @DisplayName("测试方法：url()")
        void shouldReturnUrl() throws MalformedURLException {
            final URL resourceUrl = this.classPathResource.url();
            assertThat(resourceUrl.getPath()).asString().endsWith("hello.txt");
        }

        @Test
        @DisplayName("测试方法：loader()")
        void shouldReturnLoader() {
            final ClassLoader loader = this.classPathResource.loader();
            assertThat(loader).isEqualTo(DefaultClassPathTest.this.classLoader);
        }

        @Test
        @DisplayName("测试方法：filename()")
        void shouldReturnFilename() {
            final String filename = this.classPathResource.filename();
            assertThat(filename).isEqualTo("hello.txt");
        }

        @Test
        @DisplayName("测试方法：read()")
        void shouldReturnInputStream() throws IOException {
            final InputStream inputStream = this.classPathResource.read();
            final String content = IoUtils.content(inputStream);
            assertThat(content).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("测试方法：toString()")
        void testToString() {
            final String toString = this.classPathResource.toString();
            assertThat(toString).asString().endsWith("hello.txt");
        }
    }
}
