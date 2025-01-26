/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.serializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.EntityReadException;
import modelengine.fit.http.entity.EntitySerializer;
import modelengine.fit.http.entity.EntityWriteException;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.entity.support.DefaultPartitionedEntity;
import modelengine.fit.http.header.ContentType;
import modelengine.fit.http.header.HeaderValue;
import modelengine.fit.http.header.ParameterCollection;
import modelengine.fit.http.header.support.DefaultContentType;
import modelengine.fit.http.header.support.DefaultHeaderValue;
import modelengine.fit.http.header.support.DefaultParameterCollection;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 为 {@link MultiPartEntitySerializer} 提供单元测试。
 *
 * @author 杭潇
 * @author 季聿阶
 * @since 2023-02-21
 */
@DisplayName("测试 MultiPartEntitySerializer 类")
public class MultiPartEntitySerializerTest {
    private final EntitySerializer<PartitionedEntity> multiPartEntitySerializer = MultiPartEntitySerializer.INSTANCE;
    private final Charset charset = StandardCharsets.UTF_8;
    private final HttpMessage httpMessage = mock(HttpMessage.class);
    private PartitionedEntity entity;

    @AfterEach
    void teardown() throws IOException {
        if (this.entity != null) {
            this.entity.close();
        }
    }

    @Test
    @DisplayName("调用 serializeEntity() 方法，抛出异常")
    void invokeSerializeEntityMethodThenThrowException() {
        List<NamedEntity> list = new ArrayList<>();
        this.entity = new DefaultPartitionedEntity(this.httpMessage, list);
        EntityWriteException entityWriteException =
                catchThrowableOfType(() -> this.multiPartEntitySerializer.serializeEntity(this.entity, this.charset),
                        EntityWriteException.class);
        assertThat(entityWriteException).hasMessage("Unsupported to serialize entity of Content-Type 'multipart/*'.");
    }

    @Nested
    @DisplayName("测试 deserializeEntity() 方法")
    class TestDeserialize {
        private EntitySerializer<PartitionedEntity> getSerializer() {
            return MultiPartEntitySerializerTest.this.multiPartEntitySerializer;
        }

        @Test
        @DisplayName("未指定分隔符，抛出异常")
        void givenNoBoundaryThenThrowException() {
            EntityReadException entityReadException = catchThrowableOfType(() -> this.getSerializer()
                    .deserializeEntity("testEntity".getBytes(StandardCharsets.UTF_8),
                            MultiPartEntitySerializerTest.this.charset,
                            MultiPartEntitySerializerTest.this.httpMessage), EntityReadException.class);
            assertThat(entityReadException).hasMessage("The boundary is not present.");
        }

        @Nested
        @DisplayName("指定分隔符")
        class GivenBoundary {
            private EntitySerializer<PartitionedEntity> getSerializer() {
                return MultiPartEntitySerializerTest.this.multiPartEntitySerializer;
            }

            @BeforeEach
            void setup() {
                ParameterCollection parameterCollection = new DefaultParameterCollection();
                parameterCollection.set("boundary", "--token");
                HeaderValue headerValue = new DefaultHeaderValue("multipart/form-data", parameterCollection);
                ContentType contentType = new DefaultContentType(headerValue);
                Optional<ContentType> optionalContentType = Optional.of(contentType);
                when(MultiPartEntitySerializerTest.this.httpMessage.contentType()).thenReturn(optionalContentType);
            }

            @Test
            @DisplayName("给定参数中没有任何内容，抛出异常")
            void givenNoAnyDataThenThrowException() {
                String content = "";
                EntityReadException entityReadException = catchThrowableOfType(() -> this.getSerializer()
                        .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                MultiPartEntitySerializerTest.this.charset,
                                MultiPartEntitySerializerTest.this.httpMessage), EntityReadException.class);
                assertThat(entityReadException).hasMessage(
                        "Failed to deserialize message body. [mimeType='multipart/*']");
            }

            @Test
            @DisplayName("给定参数中仅包含一个字节，抛出异常")
            void givenOnly1ByteThenThrowException() {
                String content = "1";
                EntityReadException entityReadException = catchThrowableOfType(() -> this.getSerializer()
                        .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                MultiPartEntitySerializerTest.this.charset,
                                MultiPartEntitySerializerTest.this.httpMessage), EntityReadException.class);
                assertThat(entityReadException).hasMessage(
                        "Failed to deserialize message body. [mimeType='multipart/*']");
            }

            @Test
            @DisplayName("给定参数中不包含第一个分隔符，抛出异常")
            void givenNoFirstBoundaryThenThrowException() {
                String content = "Content";
                EntityReadException entityReadException = catchThrowableOfType(() -> this.getSerializer()
                        .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                MultiPartEntitySerializerTest.this.charset,
                                MultiPartEntitySerializerTest.this.httpMessage), EntityReadException.class);
                assertThat(entityReadException).hasMessage(
                        "Failed to deserialize message body. [mimeType='multipart/*']");
            }

            @Test
            @DisplayName("给定参数中不包含终止分隔符，抛出异常")
            void givenNoEndBoundaryThenThrowException() {
                String content = "----token\r\n" + "Content";
                EntityReadException entityReadException = catchThrowableOfType(() -> this.getSerializer()
                        .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                MultiPartEntitySerializerTest.this.charset,
                                MultiPartEntitySerializerTest.this.httpMessage), EntityReadException.class);
                assertThat(entityReadException).hasMessage(
                        "Failed to deserialize message body. [mimeType='multipart/*']");
            }

            @Test
            @DisplayName("给定参数中分隔符后包含2字符后意外终止，抛出异常")
            void given2ExtraBytesAfterBoundaryThenThrowException() {
                String content = "----token  ";
                EntityReadException entityReadException = catchThrowableOfType(() -> this.getSerializer()
                        .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                MultiPartEntitySerializerTest.this.charset,
                                MultiPartEntitySerializerTest.this.httpMessage), EntityReadException.class);
                assertThat(entityReadException).hasMessage(
                        "Failed to deserialize message body. [mimeType='multipart/*']");
            }

            @Test
            @DisplayName("给定参数中不包含回车换行符，抛出异常")
            void givenNoCrlfThenThrowException() {
                String content = "----tokenContent----token--";
                EntityReadException entityReadException = catchThrowableOfType(() -> this.getSerializer()
                        .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                MultiPartEntitySerializerTest.this.charset,
                                MultiPartEntitySerializerTest.this.httpMessage), EntityReadException.class);
                assertThat(entityReadException).hasMessage(
                        "Failed to deserialize message body. [mimeType='multipart/*']");
            }

            @Test
            @DisplayName("给定参数中包含小型缓冲大小的元数据头，然后异常中断，抛出异常")
            void givenSmallBufferHeaderAndExitThenThrowException() {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < MultiPartEntitySerializer.SMALL_BUFFER; i++) {
                    sb.append("1");
                }
                String content = "----token\r\n" + sb;
                EntityReadException entityReadException = catchThrowableOfType(() -> this.getSerializer()
                        .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                MultiPartEntitySerializerTest.this.charset,
                                MultiPartEntitySerializerTest.this.httpMessage), EntityReadException.class);
                assertThat(entityReadException).hasMessage(
                        "Failed to deserialize message body. [mimeType='multipart/*']");
            }

            @Test
            @DisplayName("给定参数中只有终止分隔符，返回空列表")
            void givenOnlyEndBoundaryThenReturnEmptyEntities() {
                String content = "----token--";
                MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                        .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                MultiPartEntitySerializerTest.this.charset,
                                MultiPartEntitySerializerTest.this.httpMessage);
                assertThat(MultiPartEntitySerializerTest.this.entity.entities()).isEmpty();
            }

            @Nested
            @DisplayName("测试文本场景")
            class TestText {
                private EntitySerializer<PartitionedEntity> getSerializer() {
                    return MultiPartEntitySerializerTest.this.multiPartEntitySerializer;
                }

                @Test
                @DisplayName("给定参数中包含前置信息，返回忽略前置信息的文本")
                void givenPreambleThenReturnActualText() {
                    String content = "Preamble\r\n" + "----token\r\n" + "Content-Disposition: form-data\r\n" + "\r\n"
                            + "Content\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns(StringUtils.EMPTY, NamedEntity::name)
                            .returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }

                @Test
                @DisplayName("给定参数中包含超长前置信息，返回忽略前置信息的文本")
                void givenLongPreambleThenReturnActualText() {
                    String content =
                            "This is the preamble. It is to be ignored, though it is a handy place for composition "
                                    + "agents to include an explanatory note to non-MIME conformant readers.\r\n"
                                    + "----token\r\n" + "Content-Disposition: form-data\r\n" + "\r\n" + "Content\r\n"
                                    + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns(StringUtils.EMPTY, NamedEntity::name)
                            .returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }

                @Test
                @DisplayName("给定参数中包含后置信息，返回忽略后置信息的文本")
                void givenEpilogueThenReturnActualText() {
                    String content = "----token\r\n" + "Content-Disposition: form-data\r\n" + "\r\n" + "Content\r\n"
                            + "----token--\r\n" + "epilogue";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns(StringUtils.EMPTY, NamedEntity::name)
                            .returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }

                @Test
                @DisplayName("给定参数中的分隔符后包含空白信息，返回忽略空白信息的文本")
                void givenExtraTextAfterBoundaryThenReturnActualText() {
                    String content = "----token  \r\n" + "Content-Disposition: form-data\r\n" + "\r\n" + "Content\r\n"
                            + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns(StringUtils.EMPTY, NamedEntity::name)
                            .returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }

                @Test
                @DisplayName("给定参数中不包含变量名信息，返回对应的文本")
                void givenNoNameThenReturnActualText() {
                    String content = "----token\r\n" + "Content-Disposition: form-data\r\n" + "\r\n" + "Content\r\n"
                            + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns(StringUtils.EMPTY, NamedEntity::name)
                            .returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }

                @Test
                @DisplayName("给定参数中包含类似分隔符的内容，返回对应的文本")
                void givenLikeBoundaryThenReturnActualText() {
                    String content = "----token\r\n" + "Content-Disposition: form-data\r\n" + "\r\n" + "----tokem\r\n"
                            + "Content\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns(StringUtils.EMPTY, NamedEntity::name)
                            .returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("----tokem\r\nContent");
                }

                @Test
                @DisplayName("给定参数中不存在显示位置，返回对应的文本")
                void givenNoContentDispositionThenReturnActualText() {
                    String content = "----token\r\n" + "\r\n" + "Content\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns(StringUtils.EMPTY, NamedEntity::name)
                            .returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }

                @Test
                @DisplayName("给定参数中文件信息为空，返回对应的文本")
                void givenNoFileNameThenReturnActualText() {
                    String content = "----token\r\n" + "Content-disposition: form-data; name=\"key\"\r\n" + "\r\n"
                            + "Content\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns("key", NamedEntity::name).returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }

                @Test
                @DisplayName("给定参数中包含错误格式的头，忽略，返回对应的文本")
                void givenWrongHeaderThenReturnActualText() {
                    String content =
                            "----token\r\n" + "Wrong\r\n" + "Content-disposition: form-data; name=\"key\"\r\n" + "\r\n"
                                    + "Content\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns("key", NamedEntity::name).returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }

                @Test
                @DisplayName("给定参数中包含无用的头，忽略，返回对应的文本")
                void givenUnusedHeaderThenReturnActualText() {
                    String content =
                            "----token\r\n" + "Unused: value\r\n" + "Content-disposition: form-data; name=\"key\"\r\n"
                                    + "\r\n" + "Content\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns("key", NamedEntity::name).returns(true, NamedEntity::isText);
                    assertThat(namedEntity.asText().content()).isEqualTo("Content");
                }
            }

            @Nested
            @DisplayName("测试文件场景")
            class TestFile {
                private EntitySerializer<PartitionedEntity> getSerializer() {
                    return MultiPartEntitySerializerTest.this.multiPartEntitySerializer;
                }

                @Test
                @DisplayName("给定参数中包含文件名，返回对应的文件流")
                void givenFileNameThenReturnActualFile() throws IOException {
                    String content =
                            "----token\r\n" + "Content-Disposition: form-data; name=\"key\"; filename=\"test.txt\"\r\n"
                                    + "\r\n" + "Content\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns("key", NamedEntity::name).returns(true, NamedEntity::isFile);
                    FileEntity fileEntity = namedEntity.asFile();
                    assertThat(IoUtils.content(fileEntity.getInputStream())).isEqualTo("Content");
                    assertThat(fileEntity.filename()).isEqualTo("test.txt");
                }

                @Test
                @DisplayName("给定参数中包含超长文件名，返回对应的文件流")
                void givenLongFileNameThenReturnActualFile() throws IOException {
                    String fileName = "abcdefghijklmnopqrstuvwxyz0123456789.txt";
                    String content = "----token\r\n" + "Content-Disposition: form-data; name=\"long-file\"; filename=\""
                            + fileName + "\"\r\n" + "\r\n" + "Content\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns("long-file", NamedEntity::name).returns(true, NamedEntity::isFile);
                    FileEntity fileEntity = namedEntity.asFile();
                    assertThat(IoUtils.content(fileEntity.getInputStream())).isEqualTo("Content");
                    assertThat(fileEntity.filename()).isEqualTo(fileName);
                }

                @Test
                @DisplayName("给定参数中换行符处于缓存边界，返回对应的文件流")
                void givenNewlineCharAtCacheBoundaryThenReturnActualFile() throws IOException {
                    String content =
                            "----token\r\n" + "Content-Disposition: form-data; name=\"key\"; filename=\"test.txt\"\r\n"
                                    + "\r\n" + "1234567890\r\n" + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(1);
                    NamedEntity namedEntity = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity).returns("key", NamedEntity::name).returns(true, NamedEntity::isFile);
                    FileEntity fileEntity = namedEntity.asFile();
                    assertThat(IoUtils.content(fileEntity.getInputStream())).isEqualTo("1234567890");
                    assertThat(fileEntity.filename()).isEqualTo("test.txt");
                }
            }

            @Nested
            @DisplayName("测试混合场景")
            class TestMixed {
                private EntitySerializer<PartitionedEntity> getSerializer() {
                    return MultiPartEntitySerializerTest.this.multiPartEntitySerializer;
                }

                @Test
                @DisplayName("给定参数中包含文件名和文本，返回对应的文件和文本")
                void givenFileNameThenReturnActualFile() throws IOException {
                    String content =
                            "----token\r\n" + "Content-Disposition: form-data; name=\"key\"; filename=\"test.txt\"\r\n"
                                    + "\r\n" + "Content\r\n" + "----token\r\n"
                                    + "Content-Disposition: form-data; name=\"another\"\r\n" + "\r\n" + "Content\r\n"
                                    + "----token--";
                    MultiPartEntitySerializerTest.this.entity = this.getSerializer()
                            .deserializeEntity(content.getBytes(StandardCharsets.UTF_8),
                                    MultiPartEntitySerializerTest.this.charset,
                                    MultiPartEntitySerializerTest.this.httpMessage);
                    assertThat(MultiPartEntitySerializerTest.this.entity.entities()).hasSize(2);
                    NamedEntity namedEntity1 = MultiPartEntitySerializerTest.this.entity.entities().get(0);
                    assertThat(namedEntity1).returns("key", NamedEntity::name).returns(true, NamedEntity::isFile);
                    assertThat(IoUtils.content(namedEntity1.asFile().getInputStream())).isEqualTo("Content");
                    NamedEntity namedEntity2 = MultiPartEntitySerializerTest.this.entity.entities().get(1);
                    assertThat(namedEntity2).returns("another", NamedEntity::name).returns(true, NamedEntity::isText);
                    assertThat(namedEntity2.asText().content()).isEqualTo("Content");
                }
            }
        }
    }
}
