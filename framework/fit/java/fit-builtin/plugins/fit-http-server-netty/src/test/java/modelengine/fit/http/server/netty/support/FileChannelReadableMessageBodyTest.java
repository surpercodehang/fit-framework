/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import modelengine.fit.http.server.netty.NettyReadableMessageBody;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link FileChannelReadableMessageBody} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-10-09
 */
@DisplayName("测试 FileChannelReadableMessageBody")
public class FileChannelReadableMessageBodyTest {
    private NettyReadableMessageBody messageBody;

    @BeforeEach
    void setup() {
        FileChannelReadableMessageBodyTest.this.messageBody = NettyReadableMessageBody.large();
    }

    @AfterEach
    void teardown() throws IOException {
        FileChannelReadableMessageBodyTest.this.messageBody.close();
        FileChannelReadableMessageBodyTest.this.messageBody = null;
    }

    void write(String data) throws IOException {
        ByteBuf byteBuf = Unpooled.buffer(data.length()).writeBytes(data.getBytes(StandardCharsets.UTF_8));
        FileChannelReadableMessageBodyTest.this.messageBody.write(byteBuf, false);
    }

    void writeLast(String data) throws IOException {
        ByteBuf byteBuf = Unpooled.buffer(data.length()).writeBytes(data.getBytes(StandardCharsets.UTF_8));
        FileChannelReadableMessageBodyTest.this.messageBody.write(byteBuf, true);
    }

    @Test
    @DisplayName("当创建文件失败时，初始化抛出异常")
    void shouldThrowException() {
        try (MockedStatic<Files> mocked = mockStatic(Files.class)) {
            mocked.when(() -> Files.createTempFile(anyString(), anyString())).thenThrow(new IOException());
            IllegalStateException cause =
                    catchThrowableOfType(FileChannelReadableMessageBody::new, IllegalStateException.class);
            assertThat(cause).hasMessage("Failed to create tmp file by channel.");
        }
    }

    @Test
    @DisplayName("当读取下一个字节过程首次发生异常时，重试读取成功")
    void shouldRetrySuccessfully() throws IOException {
        FileChannel channel = mock(FileChannel.class);
        AtomicInteger readByte = new AtomicInteger(-1);
        when(channel.write(any(), anyLong())).thenAnswer(invocation -> {
            ByteBuffer buffer = invocation.getArgument(0);
            readByte.set(buffer.get());
            return 1;
        });
        when(channel.read(any(), anyLong())).thenAnswer(invocation -> {
            ByteBuffer buffer = invocation.getArgument(0);
            buffer.put((byte) readByte.get());
            return 1;
        });
        this.messageBody = new FileChannelReadableMessageBody(channel);
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.schedule(() -> {
            try {
                write("H");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 1, TimeUnit.SECONDS);
        int read = this.messageBody.read();
        assertThat(read).isEqualTo('H');
    }

    @Nested
    @DisplayName("测试 ReadableMessageBody 接口的方法")
    class TestReadableMessageBody {
        @Nested
        @DisplayName("测试 read 方法")
        class TestRead {
            @Nested
            @DisplayName("当调用 read() 方法时")
            class WhenReadSingleByte {
                @Test
                @DisplayName("当消息体中没有数据时，读取结果为 -1")
                void givenNoDataInBodyThenReturnMinus1() throws IOException {
                    FileChannelReadableMessageBodyTest.this.writeLast("");
                    int read = FileChannelReadableMessageBodyTest.this.messageBody.read();
                    assertThat(read).isEqualTo(-1);
                }

                @Test
                @DisplayName("当消息体中有充足的数据时，读取结果为下一个字节")
                void givenDataInBodyThenReturnActualByte() throws IOException {
                    FileChannelReadableMessageBodyTest.this.write("Hello");
                    int read = FileChannelReadableMessageBodyTest.this.messageBody.read();
                    assertThat(read).isEqualTo('H');
                }

                @Test
                @DisplayName("当消息体中仅有一个字节时，读取结果为下一个字节")
                void given1ByteInBodyThenReturnActualByte() throws IOException {
                    FileChannelReadableMessageBodyTest.this.write("H");
                    int read = FileChannelReadableMessageBodyTest.this.messageBody.read();
                    assertThat(read).isEqualTo('H');
                }
            }

            @Nested
            @DisplayName("当调用 read(byte[] bytes) 方法时")
            class WhenReadAllBytes {
                @Test
                @DisplayName("当消息体中没有数据时，读取结果为 0")
                void givenNoDataInBodyThenReturn0() throws IOException {
                    ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
                    scheduledExecutorService.schedule(() -> {
                        try {
                            write("H");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, 1, TimeUnit.SECONDS);
                    byte[] bytes = new byte[1];
                    int read = FileChannelReadableMessageBodyTest.this.messageBody.read(bytes);
                    assertThat(read).isEqualTo(1);
                }

                @Nested
                @DisplayName("当消息体中有数据时")
                class GivenDataInBody {
                    @Test
                    @DisplayName("当字节数组大小小于现有数据量时，读取到整个字节数组的数据")
                    void givenBytesSizeLessThanDataInBodyThenReturnBytesSize() throws IOException {
                        byte[] bytes = new byte[1];
                        FileChannelReadableMessageBodyTest.this.write("Hello");
                        int read = FileChannelReadableMessageBodyTest.this.messageBody.read(bytes);
                        assertThat(read).isEqualTo(1);
                        assertThat(bytes[0]).isEqualTo((byte) 'H');
                    }

                    @Test
                    @DisplayName("当字节数组大小大于现有数据量时，读取到所有的数据")
                    void givenBytesSizeGreaterThanDataInBodyThenReturnAllDataSize() throws IOException {
                        byte[] bytes = new byte[6];
                        FileChannelReadableMessageBodyTest.this.write("Hello");
                        int read = FileChannelReadableMessageBodyTest.this.messageBody.read(bytes);
                        assertThat(read).isEqualTo(5);
                        byte[] newBytes = new byte[read];
                        System.arraycopy(bytes, 0, newBytes, 0, read);
                        assertThat(new String(newBytes, StandardCharsets.UTF_8)).isEqualTo("Hello");
                    }
                }

                @Test
                @DisplayName("当消息体已经关闭，读取结果为 -1")
                void givenBodyIsClosedThenReturnMinus1() throws IOException {
                    FileChannelReadableMessageBodyTest.this.writeLast("");
                    byte[] bytes = new byte[1];
                    int read = FileChannelReadableMessageBodyTest.this.messageBody.read(bytes);
                    assertThat(read).isEqualTo(-1);
                }
            }

            @Nested
            @DisplayName("当调用 read(byte[] bytes, int off, int len) 方法时")
            class WhenReadPartialBytes {
                @Test
                @DisplayName("当 off 小于 0 时，抛出 IndexOutOfBoundsException")
                void givenOffLessThan0ThenThrowIndexOutOfBoundsException() {
                    byte[] bytes = new byte[1];
                    IndexOutOfBoundsException exception =
                            catchThrowableOfType(() -> FileChannelReadableMessageBodyTest.this.messageBody.read(bytes,
                                    -1,
                                    1), IndexOutOfBoundsException.class);
                    assertThat(exception).isNotNull().hasMessage("The off in read cannot be negative. [off=-1]");
                }

                @Test
                @DisplayName("当 len 小于 0 时，抛出 IndexOutOfBoundsException")
                void givenLenLessThan0ThenThrowIndexOutOfBoundsException() {
                    byte[] bytes = new byte[1];
                    IndexOutOfBoundsException exception =
                            catchThrowableOfType(() -> FileChannelReadableMessageBodyTest.this.messageBody.read(bytes,
                                    0,
                                    -1), IndexOutOfBoundsException.class);
                    assertThat(exception).isNotNull().hasMessage("The len in read cannot be negative. [len=-1]");
                }

                @Test
                @DisplayName("当 off + len 超过字节数组大小时，抛出 IndexOutOfBoundsException")
                void givenOffLenSumGreaterThanBytesSizeThenThrowIndexOutOfBoundsException() {
                    byte[] bytes = new byte[1];
                    IndexOutOfBoundsException exception =
                            catchThrowableOfType(() -> FileChannelReadableMessageBodyTest.this.messageBody.read(bytes,
                                    1,
                                    1), IndexOutOfBoundsException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("The (off + len) in read cannot be greater than bytes.length. "
                                    + "[off=1, len=1, bytesLength=1]");
                }

                @Test
                @DisplayName("当缓冲区首页够读且正好读完时，读取内容正确")
                void givenFirstRemainedBufferSizeIsEqualsToLenThenReturnCorrectData() throws IOException {
                    FileChannelReadableMessageBodyTest.this.messageBody = new ByteBufReadableMessageBody();
                    byte[] bytes = new byte[1];
                    FileChannelReadableMessageBodyTest.this.write("H");
                    int read = FileChannelReadableMessageBodyTest.this.messageBody.read(bytes, 0, 1);
                    assertThat(read).isEqualTo(1);
                    assertThat(bytes[0]).isEqualTo((byte) 'H');
                }

                @Test
                @DisplayName("当缓冲区首页不够读时，读取内容正确")
                void givenFirstRemainedBufferSizeIsLessThanLenThenReturnCorrectData() throws IOException {
                    FileChannelReadableMessageBodyTest.this.messageBody = new ByteBufReadableMessageBody();
                    byte[] bytes = new byte[4];
                    FileChannelReadableMessageBodyTest.this.write("H");
                    FileChannelReadableMessageBodyTest.this.write("e");
                    FileChannelReadableMessageBodyTest.this.writeLast("l");
                    int read = FileChannelReadableMessageBodyTest.this.messageBody.read(bytes, 0, 4);
                    assertThat(read).isEqualTo(3);
                    assertThat(bytes[0]).isEqualTo((byte) 'H');
                    assertThat(bytes[1]).isEqualTo((byte) 'e');
                    assertThat(bytes[2]).isEqualTo((byte) 'l');
                }
            }
        }

        @Nested
        @DisplayName("当调用 available() 方法时")
        class WhenAvailable {
            @Test
            @DisplayName("当消息体中没有数据时，返回 0")
            void givenNoDataThenReturn0() throws IOException {
                int available = FileChannelReadableMessageBodyTest.this.messageBody.available();
                assertThat(available).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("当调用 close() 方法时")
        class WhenClose {
            @Test
            @DisplayName("当写入数据后未读就关闭时，关闭正常")
            void shouldCloseResource() throws IOException {
                FileChannelReadableMessageBodyTest.this.write("Hello");
                int available = FileChannelReadableMessageBodyTest.this.messageBody.available();
                assertThat(available).isEqualTo(5);
                FileChannelReadableMessageBodyTest.this.messageBody.close();
                available = FileChannelReadableMessageBodyTest.this.messageBody.available();
                assertThat(available).isEqualTo(0);
            }

            @Nested
            @DisplayName("关闭之后")
            class AfterClosing {
                @BeforeEach
                void setup() throws IOException {
                    this.getMessageBody().close();
                }

                @Test
                @DisplayName("读取单个字节数据，抛出异常")
                void shouldThrowExceptionWhenReadByte() {
                    IOException exception = catchThrowableOfType(() -> this.getMessageBody().read(), IOException.class);
                    assertThat(exception).hasMessage("The netty readable message body has already been closed.");
                }

                @Test
                @DisplayName("读取字节数组，抛出异常")
                void shouldThrowExceptionWhenReadBytes() {
                    IOException exception =
                            catchThrowableOfType(() -> this.getMessageBody().read(new byte[1]), IOException.class);
                    assertThat(exception).hasMessage("The netty readable message body has already been closed.");
                }

                private NettyReadableMessageBody getMessageBody() {
                    return FileChannelReadableMessageBodyTest.this.messageBody;
                }
            }
        }
    }
}
