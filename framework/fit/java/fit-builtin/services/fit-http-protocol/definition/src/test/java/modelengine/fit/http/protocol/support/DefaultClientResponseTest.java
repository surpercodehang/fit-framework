/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.protocol.ClientResponse;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fit.http.protocol.StatusLine;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.model.support.DefaultMultiValueMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * {@link DefaultClientResponse} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-15
 */
@DisplayName("测试 DefaultClientResponse 类")
public class DefaultClientResponseTest {
    private ClientResponse defaultClientResponse;
    private int statusCode;
    private String reasonPhrase;

    @BeforeEach
    void setup() {
        this.statusCode = 200;
        this.reasonPhrase = "makeDefaultClientResponseTest";
        MultiValueMap<String, String> headers = new DefaultMultiValueMap<>();
        InputStream responseStream =
                new ByteArrayInputStream("TestOfDefaultClientResponse".getBytes(StandardCharsets.UTF_8));

        headers.add("testKey1", "testValue1");
        headers.add("testKey2", "testValue2");
        headers.add(null, "testValue3");

        this.defaultClientResponse = ClientResponse.create(this.statusCode, this.reasonPhrase, headers, responseStream);
    }

    @AfterEach
    void teardown() throws IOException {
        this.defaultClientResponse.close();
    }

    @Test
    @DisplayName("获取的起始行信息与给定参数值相等")
    void theStartLineInformationShouldBeEqualsToTheGivenParameters() {
        StatusLine statusLine = this.defaultClientResponse.startLine();
        assertThat(statusLine.statusCode()).isEqualTo(this.statusCode);
        assertThat(statusLine.reasonPhrase()).isEqualTo(this.reasonPhrase);
    }

    @Test
    @DisplayName("获取的信息头值与给定值相等")
    void theHeadersShouldBeEqualsToTheGivenParameter() {
        MessageHeaders messageHeaders = this.defaultClientResponse.headers();
        ConfigurableMessageHeaders configurableMessageHeaders = ConfigurableMessageHeaders.create();
        configurableMessageHeaders.set("testKey1", "testValue1");
        configurableMessageHeaders.set("testKey2", "testValue2");
        assertThat(messageHeaders.size()).isEqualTo(configurableMessageHeaders.size());
    }

    @Nested
    @DisplayName("测试 body() 方法")
    class TestBody {
        @Test
        @DisplayName("客户端响应关闭时，返回值不为空")
        void whenClosedClientResponseThenReturnEmpty() throws IOException {
            DefaultClientResponseTest.this.defaultClientResponse.close();
            ReadableMessageBody body = DefaultClientResponseTest.this.defaultClientResponse.body();
            assertThat(body).isNotNull();
        }

        @Test
        @DisplayName("客户端响应未关闭时，返回值不为空")
        void whenNotClosedClientResponseThenReturnNotEmpty() {
            ReadableMessageBody body = DefaultClientResponseTest.this.defaultClientResponse.body();
            assertThat(body).isNotNull();
        }
    }
}
