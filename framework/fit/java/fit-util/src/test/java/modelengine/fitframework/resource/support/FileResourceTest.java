/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.ObjectUtils;

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

/**
 * {@link FileResource} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-28
 */
@DisplayName("测试 FileResource 工具类")
class FileResourceTest {
    private Resource resource;
    private File file;
    private URL url;

    @BeforeEach
    void setup() throws URISyntaxException {
        this.url = this.getClass().getResource("/text/hello.txt");
        this.file = new File(this.url.toURI());
        this.resource = Resource.fromFile(this.file);
    }

    @Test
    @DisplayName("获取资源文件")
    void shouldReturnFile() {
        final FileResource fileResource = ObjectUtils.cast(this.resource);
        assertThat(fileResource.file()).isEqualTo(this.file);
    }

    @Test
    @DisplayName("获取资源的名称")
    void shouldReturnFileName() {
        final String filename = this.resource.filename();
        assertThat(filename).isEqualTo("hello.txt");
    }

    @Test
    @DisplayName("获取资源的唯一资源定位符")
    void shouldReturnFileUrl() throws MalformedURLException {
        final URL urlPath = this.resource.url();
        assertThat(urlPath).isEqualTo(this.url);
    }

    @Test
    @DisplayName("打开资源以读取内容")
    void shouldReturnInputStream() throws IOException {
        final InputStream inputStream = this.resource.read();
        final String content = IoUtils.content(inputStream);
        assertThat(content).isEqualTo("Hello World");
    }

    @Nested
    @DisplayName("测试方法：equals(Object obj)")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供资源实例与本身比较时时，返回 true")
        void givenSelfShouldReturnTrue() {
            assertThat(FileResourceTest.this.resource.equals(FileResourceTest.this.resource)).isTrue();
        }

        @Test
        @DisplayName("提供资源实例与相同配置的新对象比较时，返回 true")
        void givenSameTypeShouldReturnTrue() {
            final FileResource fileResource = new FileResource(FileResourceTest.this.file);
            assertThat(fileResource.equals(FileResourceTest.this.resource)).isTrue();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供资源实例与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            assertThat(FileResourceTest.this.resource.equals(String.class)).isFalse();
        }
    }

    @Test
    @DisplayName("测试方法：toString()")
    void testToString() {
        final String toString = this.resource.toString();
        assertThat(toString).asString().endsWith("hello.txt");
    }

    @Test
    @DisplayName("测试方法：hashCode()")
    void testHashCode() {
        final int actual = this.resource.hashCode();
        final FileResource fileResource = new FileResource(FileResourceTest.this.file);
        final int expected = fileResource.hashCode();
        assertThat(actual).isEqualTo(expected);
    }
}

