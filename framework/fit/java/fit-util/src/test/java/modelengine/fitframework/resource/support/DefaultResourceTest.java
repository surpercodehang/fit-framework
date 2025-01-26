/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * {@link DefaultResource} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-28
 */
@DisplayName("测试 DefaultResource 工具类")
class DefaultResourceTest {
    private DefaultResource resource;
    private URL url;
    private final byte[] bytes = {1, 2};

    @BeforeEach
    void setup() {
        this.url = this.getClass().getResource("/text/hello.txt");
        this.resource = new DefaultResource("hello", this.url, () -> new ByteArrayInputStream(this.bytes));
    }

    @Test
    @DisplayName("获取资源的名称")
    void shouldReturnFileName() {
        final String filename = this.resource.filename();
        assertThat(filename).isEqualTo("hello");
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
        assertThat(inputStream.available()).isEqualTo(this.bytes.length);
    }

    @Test
    @DisplayName("测试方法：toString()")
    void testToString() {
        final String toString = this.resource.toString();
        assertThat(toString).asString().endsWith("hello.txt");
    }
}
