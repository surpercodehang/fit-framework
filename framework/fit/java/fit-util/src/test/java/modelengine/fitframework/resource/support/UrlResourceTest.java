/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.util.IoUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link UrlResource} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-28
 */
@DisplayName("测试 UrlResource 工具类")
class UrlResourceTest {
    private UrlResource resource;
    private URL url;

    @BeforeEach
    void setup() {
        this.url = this.getClass().getResource("/text/hello.txt");
        this.resource = new UrlResource(this.url);
    }

    @Test
    @DisplayName("获取资源的名称")
    void shouldReturnFileName() {
        final String filename = this.resource.filename();
        assertThat(filename).isEqualTo("hello.txt");
    }

    @Test
    @DisplayName("当提供异常 Url 时，创建实例抛出异常")
    void givenUnNormalUrlThenThrowException() throws MalformedURLException {
        URL unNormalUrl = new URL("jar", null, -1, "file:my.jar{");
        assertThatThrownBy(() -> new UrlResource(unNormalUrl)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("获取资源的唯一资源定位符")
    void shouldReturnFileUrl() {
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
            assertThat(UrlResourceTest.this.resource.equals(UrlResourceTest.this.resource)).isTrue();
        }

        @Test
        @DisplayName("提供资源实例与相同配置的新对象比较时，返回 true")
        void givenSameTypeShouldReturnTrue() {
            final UrlResource urlResource = new UrlResource(UrlResourceTest.this.url);
            assertThat(urlResource.equals(UrlResourceTest.this.resource)).isTrue();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供资源实例与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            assertThat(UrlResourceTest.this.resource.equals(String.class)).isFalse();
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
        final UrlResource urlResource = new UrlResource(this.url);
        final int expected = urlResource.hashCode();
        assertThat(actual).isEqualTo(expected);
    }
}
