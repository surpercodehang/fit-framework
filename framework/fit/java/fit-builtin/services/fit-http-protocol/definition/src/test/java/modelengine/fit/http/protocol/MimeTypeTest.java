/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * {@link MimeType} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-15
 */
@DisplayName("测试 MimeType 类")
public class MimeTypeTest {
    @Nested
    @DisplayName("测试 from() 方法")
    class TestFrom {
        @DisplayName("给定一个存在的内容类型，返回值与对应枚举值相等")
        @ParameterizedTest
        @CsvSource({
                "application/xhtml+xml,APPLICATION_XHTML", "application/xml,APPLICATION_XML",
                "application/zstd,APPLICATION_ZSTD", "text/css,TEXT_CSS", "text/html,TEXT_HTML",
                "text/event-stream,TEXT_EVENT_STREAM"
        })
        void givenExistContentTypeThenReturnEqualsToTheCorrespondingValue(String contentType, MimeType mimeType) {
            MimeType applicationJson = MimeType.from(contentType);
            assertThat(applicationJson).isEqualTo(mimeType);
        }

        @Test
        @DisplayName("给定一个不存在的内容类型，返回值为 null")
        void givenNootExistContentTypeThenReturnNull() {
            String contentType = "notExistContentType";
            MimeType mimeType = MimeType.from(contentType);
            assertThat(mimeType).isNull();
        }
    }
}
