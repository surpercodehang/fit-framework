/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.protocol.HttpVersion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultStatusLine} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-15
 */
@DisplayName("测试 DefaultStatusLine 类")
public class DefaultStatusLineTest {
    private final HttpVersion httpVersion = HttpVersion.HTTP_1_0;
    private DefaultStatusLine defaultStatusLine;

    @BeforeEach
    void setup() {
        String reasonPhrase = "initialInformation";
        int statusCode = 200;
        this.defaultStatusLine = new DefaultStatusLine(this.httpVersion, statusCode, reasonPhrase);
    }

    @Test
    @DisplayName("给定一个状态码，状态码更新成功")
    void givenStatusCodeThenStatusCodeUpdateSuccessfully() {
        this.defaultStatusLine.statusCode(300);
        int actualStatusCode = this.defaultStatusLine.statusCode();
        assertThat(actualStatusCode).isEqualTo(300);
    }

    @Nested
    @DisplayName("测试 reasonPhrase() 方法")
    class TestReasonPhrase {
        @Test
        @DisplayName("给定非空数据，数据修改成功")
        void givenNotBlankParameterThenUpdateMessageSuccessfully() {
            defaultStatusLine.reasonPhrase("updateReasonPhrase");
            assertThat(defaultStatusLine.reasonPhrase()).isEqualTo("updateReasonPhrase");
        }

        @Test
        @DisplayName("给定空数据，数据不会修改")
        void givenBlankParameterThenNotUpdateMessage() {
            defaultStatusLine.reasonPhrase("");
            assertThat(defaultStatusLine.reasonPhrase()).isEqualTo("initialInformation");
        }
    }

    @Test
    @DisplayName("获取的 Http 版本值与给定的版本值相等")
    void theHttpVersionShouldBeEqualsToTheGivenVersion() {
        HttpVersion actualHttpVersion = this.defaultStatusLine.httpVersion();
        assertThat(actualHttpVersion).isEqualTo(this.httpVersion);
    }
}
