/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fit.http.protocol.support.InputStreamReadableMessageBody;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * {@link BodyUtils} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-15
 */
@DisplayName("测试 BodyUtils 类")
public class BodyUtilsTest {
    @Nested
    @DisplayName("测试 readBodyByChunked() 方法")
    class TestReadBodyByChunked {
        @Test
        @DisplayName("读取的消息体中的数据与给定数据相等")
        void theDataInTheReadBodyShouldBeEqualsToTheSpecifiedData() throws IOException {
            ReadableMessageBody body =
                    new InputStreamReadableMessageBody(new ByteArrayInputStream("readBodyByChunked".getBytes(
                            StandardCharsets.UTF_8)));
            MessageHeaders headers = ConfigurableMessageHeaders.create().add("Transfer-Encoding", "chunked");
            byte[] bytes = BodyUtils.readBody(body, headers);
            String expected = Arrays.toString("readBodyByChunked".getBytes(StandardCharsets.UTF_8));
            assertThat(Arrays.toString(bytes)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("测试 readBodyByLength() 方法")
    class TestReadBodyByLength {
        @Test
        @DisplayName("给定长度值与给定 body 长度相等，读取的消息体中的数据与给定数据相等")
        void givenSameLengthOfBodyDataThenTheReadDataShouldBeEqualsToTheSpecifiedData() throws IOException {
            ReadableMessageBody body =
                    new InputStreamReadableMessageBody(new ByteArrayInputStream("readBodyByChunked".getBytes(
                            StandardCharsets.UTF_8)));
            MessageHeaders headers = ConfigurableMessageHeaders.create().add("Content-Length", "17");
            byte[] bytes = BodyUtils.readBody(body, headers);
            String expected = Arrays.toString("readBodyByChunked".getBytes(StandardCharsets.UTF_8));
            assertThat(Arrays.toString(bytes)).isEqualTo(expected);
        }

        @Test
        @DisplayName("给定长度值大于给定 body 的长度值，抛出异常")
        void givenLengthGreaterThanBodyLengthThenThrowException() {
            ReadableMessageBody body =
                    new InputStreamReadableMessageBody(new ByteArrayInputStream("readBodyByChunked".getBytes(
                            StandardCharsets.UTF_8)));
            MessageHeaders headers = ConfigurableMessageHeaders.create().add("Content-Length", "18");
            IllegalStateException illegalStateException =
                    catchThrowableOfType(() -> BodyUtils.readBody(body, headers), IllegalStateException.class);
            assertThat(illegalStateException).hasMessage(StringUtils.format(
                    "Failed to read enough message body by Content-Length. [read={0}]",
                    "readBodyByChunked".length()));
        }
    }
}
