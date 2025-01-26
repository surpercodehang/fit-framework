/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.header.support.DefaultContentDisposition;
import modelengine.fit.http.protocol.MimeType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * 表示 {@link ContentDisposition} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-15
 */
@DisplayName("测试 ContentDisposition 类")
class ContentDispositionTest {
    private static final String FILENAME_EXT = "filename.ext";
    private static final String NAME = "name";

    private ContentDisposition contentDisposition;

    @BeforeEach
    void setup() {
        ParameterCollection collection = ParameterCollection.create();
        collection.set("name", NAME);
        collection.set("filename", FILENAME_EXT);
        HeaderValue headerValue = HeaderValue.create(MimeType.TEXT_HTML.value(), collection);
        this.contentDisposition = new DefaultContentDisposition(headerValue);
    }

    @AfterEach
    void teardown() {
        this.contentDisposition = null;
    }

    @Test
    @DisplayName("获取显示位置")
    void shouldReturnDispositionType() {
        final String dispositionType = this.contentDisposition.dispositionType();
        assertThat(dispositionType).isEqualTo(MimeType.TEXT_HTML.value());
    }

    @Test
    @DisplayName("获取变量名")
    void shouldReturnName() {
        final Optional<String> name = this.contentDisposition.name();
        assertThat(name).isPresent().get().isEqualTo(NAME);
    }

    @Test
    @DisplayName("获取文件名")
    void shouldReturnFileName() {
        final Optional<String> fileName = this.contentDisposition.fileName();
        assertThat(fileName).isPresent().get().isEqualTo(FILENAME_EXT);
    }
}
