/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.header.support.DefaultHeaderValue;
import modelengine.fit.http.protocol.MimeType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link HeaderValue} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 HeaderValue 类")
class HeaderValueTest {
    private ParameterCollection parameterCollection;
    private HeaderValue headerValue;

    @BeforeEach
    void setup() {
        this.parameterCollection = ParameterCollection.create();
        this.headerValue = HeaderValue.create(MimeType.TEXT_HTML.value(), this.parameterCollection);
    }

    @AfterEach
    void teardown() {
        this.parameterCollection = null;
        this.headerValue = null;
    }

    @Test
    @DisplayName("根据消息头的值构建消息头的对象")
    void shouldReturnHeaderValueObject() {
        final HeaderValue defaultHeaderValue = HeaderValue.create(MimeType.TEXT_HTML.value());
        assertThat(defaultHeaderValue.toString()).isEqualTo(MimeType.TEXT_HTML.value());
    }

    @Test
    @DisplayName("获取所有参数集合")
    void shouldReturnHeaderParameters() {
        final ParameterCollection parameters = this.headerValue.parameters();
        assertThat(parameters).isEqualTo(this.parameterCollection);
    }

    @Test
    @DisplayName("将当前消息头对象转换成 ContentType 对象")
    void shouldReturnHeaderContentType() {
        final ContentType contentType = this.headerValue.toContentType();
        assertThat(contentType.mediaType()).isEqualTo(MimeType.TEXT_HTML.value());
    }

    @Test
    @DisplayName("获取消息头的值")
    void shouldReturnHeaderValue() {
        final String value = this.headerValue.value();
        assertThat(value).isEqualTo(MimeType.TEXT_HTML.value());
    }

    @Test
    @DisplayName("将当前消息头对象转换成 ContentDisposition 对象")
    void shouldReturnHeaderContentDisposition() {
        final ContentDisposition contentDisposition = this.headerValue.toContentDisposition();
        assertThat(contentDisposition.parameters()).isEqualTo(this.parameterCollection);
    }

    @Test
    @DisplayName("返回字符串形式的消息头中的值")
    void shouldReturnHeaderValueToString() {
        this.parameterCollection.set("timeout", "3000");
        final String actual = this.headerValue.toString();
        final String expected =
                MimeType.TEXT_HTML.value() + DefaultHeaderValue.SEPARATOR + this.parameterCollection.toString();
        assertThat(actual).isEqualTo(expected);
    }
}
