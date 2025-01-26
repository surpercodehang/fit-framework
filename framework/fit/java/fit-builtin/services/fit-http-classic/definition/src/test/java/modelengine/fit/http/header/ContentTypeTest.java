/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.header.support.DefaultContentType;
import modelengine.fit.http.protocol.MimeType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.Optional;

/**
 * 表示 {@link ContentType} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 ContentType 类")
class ContentTypeTest {
    private static final String BOUNDARY_0 = "-----boundary---0";

    private ParameterCollection collection;
    private ContentType contentType;

    @BeforeEach
    void setup() {
        this.collection = ParameterCollection.create();
        this.collection.set(DefaultContentType.CHARSET, Charset.defaultCharset().displayName());
        this.collection.set("boundary", BOUNDARY_0);
        HeaderValue headerValue = HeaderValue.create(MimeType.TEXT_HTML.value(), this.collection);
        this.contentType = new DefaultContentType(headerValue);
    }

    @AfterEach
    void teardown() {
        this.collection = null;
        this.contentType = null;
    }

    @Nested
    @DisplayName("测试方法：charset()")
    class TestCharset {
        @Test
        @DisplayName("当请求头设置的编码方式在字符集内时，返回对应的编码方式")
        void givenExistCharsetStringThenReturnCharset() {
            final Optional<Charset> charset = ContentTypeTest.this.contentType.charset();
            assertThat(charset).isPresent().get().isEqualTo(Charset.defaultCharset());
        }

        @Test
        @DisplayName("当请求头设置的编码方式不在字符集内时，返回 null")
        void givenNotExistCharsetStringThenReturnNull() {
            ContentTypeTest.this.collection.set(DefaultContentType.CHARSET, "notExistCharset");
            final Optional<Charset> charset = ContentTypeTest.this.contentType.charset();
            assertThat(charset).isEmpty();
        }
    }

    @Test
    @DisplayName("获取消息内容的边界")
    void shouldReturnBoundary() {
        final Optional<String> boundary = this.contentType.boundary();
        assertThat(boundary).isPresent().get().isEqualTo(BOUNDARY_0);
    }
}
