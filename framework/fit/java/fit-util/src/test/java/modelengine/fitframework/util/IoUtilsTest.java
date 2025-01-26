/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;

/**
 * {@link IoUtils} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-09-29
 */
public class IoUtilsTest {
    private static final int BUFFER_SIZE = 16;

    private final String contentHello = "Hello";
    private final String contentHelloWorld = "Hello World";

    @Nested
    @DisplayName("Test method: close(InputStream in)")
    class TestClose {
        @Test
        @DisplayName("Given close without exception then input stream closed quietly")
        void givenCloseWithoutExceptionThenInputStreamClosedQuietly() throws IOException {
            InputStream in = mock(InputStream.class);
            IoUtils.close(in);
            verify(in, times(1)).close();
        }

        @Test
        @DisplayName("Given close with exception then input stream closed quietly")
        void givenCloseWithExceptionThenInputStreamClosedQuietly() throws IOException {
            InputStream in = mock(InputStream.class);
            doThrow(new IOException()).when(in).close();
            assertThatNoException().isThrownBy(() -> IoUtils.close(in));
            verify(in, times(1)).close();
        }

        @Test
        @DisplayName("Given input stream is null then nothing happened")
        void givenInputStreamNullThenNothingHappened() {
            assertThatNoException().isThrownBy(() -> IoUtils.close(null));
        }
    }

    @Nested
    @DisplayName("Test copy")
    class TestCopy {
        private final long length = 5L;

        @Nested
        @DisplayName("Test method: copy(File inputFile, File outputFile)")
        class TestCopyFileToFileWithoutBuffer {
            @Nested
            @DisplayName("Given input file with content 'Hello'")
            class GivenInputFileWithContentHello {
                @Test
                @DisplayName("Given output file is empty then output file with content 'Hello'")
                void givenOutputFileEmptyThenOutputFileWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHello);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(input, output);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(File inputFile, File outputFile, int bufferSize)")
        class TestCopyFileToFileWithBuffer {
            @Nested
            @DisplayName("Given input file with content 'Hello' and buffer size is 16")
            class GivenInputFileWithContentHelloAndBuffer16 {
                @Test
                @DisplayName("Given output file is empty then output file with content 'Hello'")
                void givenOutputFileEmptyThenOutputFileWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHello);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(input, output, IoUtilsTest.BUFFER_SIZE);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(File inputFile, OutputStream out)")
        class TestCopyFileToOutputStreamWithoutBuffer {
            @Nested
            @DisplayName("Given input file with content 'Hello'")
            class GivenInputFileWithContentHello {
                @Test
                @DisplayName("Given output stream is empty then output stream with content 'Hello'")
                void givenOutputStreamEmptyThenOutputStreamWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHello);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(input, new FileOutputStream(output));

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(File inputFile, OutputStream out, int bufferSize)")
        class TestCopyFileToOutputStreamWithBuffer {
            @Nested
            @DisplayName("Given input file with content 'Hello' and buffer size is 16")
            class GivenInputFileWithContentHelloAndBuffer16 {
                @Test
                @DisplayName("Given output stream is empty then output stream with content 'Hello'")
                void givenOutputStreamEmptyThenOutputStreamWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHello);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(input, new FileOutputStream(output), IoUtilsTest.BUFFER_SIZE);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(InputStream in, File outputFile)")
        class TestCopyInputStreamToFileWithoutBuffer {
            @Nested
            @DisplayName("Given input stream with content 'Hello'")
            class GivenInputStreamWithContentHello {
                @Test
                @DisplayName("Given output file is empty then output file with content 'Hello'")
                void givenOutputFileEmptyThenOutputFileWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHello);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(new FileInputStream(input), output);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(InputStream in, File outputFile, int bufferSize)")
        class TestCopyInputStreamToFileWithBuffer {
            @Nested
            @DisplayName("Given input stream with content 'Hello' and buffer size is 16")
            class GivenInputStreamWithContentHelloAndBuffer16 {
                @Test
                @DisplayName("Given output file is empty then output file with content 'Hello'")
                void givenOutputFileEmptyThenOutputFileWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHello);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(new FileInputStream(input), output, IoUtilsTest.BUFFER_SIZE);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(InputStream in, File outputFile, int bufferSize, long length)")
        class TestCopyInputStreamToFileWithBufferAndLength {
            @Nested
            @DisplayName("Given input stream with content 'Hello World', buffer size is 16 and length is 5")
            class GivenInputStreamWithContentHelloWorldAndBuffer16AndLength5 {
                @Test
                @DisplayName("Given output file is empty then output file with content 'Hello'")
                void givenOutputFileEmptyThenOutputFileWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHelloWorld);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(new FileInputStream(input), output, IoUtilsTest.BUFFER_SIZE, TestCopy.this.length);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(InputStream in, File outputFile, long length)")
        class TestCopyInputStreamToFileWithLength {
            @Nested
            @DisplayName("Given input stream with content 'Hello World' and length is 5")
            class GivenInputStreamWithContentHelloWorldAndLength5 {
                @Test
                @DisplayName("Given output file is empty then output file with content 'Hello'")
                void givenOutputFileEmptyThenOutputFileWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHelloWorld);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(new FileInputStream(input), output, TestCopy.this.length);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(InputStream in, OutputStream out)")
        class TestCopyInputStreamToOutputStreamWithoutBuffer {
            @Nested
            @DisplayName("Given input stream with content 'Hello'")
            class GivenInputStreamWithContentHello {
                @Test
                @DisplayName("Given output stream is empty then output stream with content 'Hello'")
                void givenOutputStreamEmptyThenOutputStreamWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHello);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(new FileInputStream(input), new FileOutputStream(output));

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(InputStream in, OutputStream out, int bufferSize)")
        class TestCopyInputStreamToOutputStreamWithBuffer {
            @Nested
            @DisplayName("Given input stream with content 'Hello' and buffer size is 16")
            class GivenInputStreamWithContentHelloAndBuffer16 {
                @Test
                @DisplayName("Given output stream is empty then output stream with content 'Hello'")
                void givenOutputStreamEmptyThenOutputStreamWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHello);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(new FileInputStream(input), new FileOutputStream(output), IoUtilsTest.BUFFER_SIZE);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(InputStream in, OutputStream out, int bufferSize, long length)")
        class TestCopyInputStreamToOutputStreamWithBufferAndLength {
            @Nested
            @DisplayName("Given input stream with content 'Hello World', buffer size is 16 and length is 5")
            class GivenInputStreamWithContentHelloWorldAndBuffer16AndLength5 {
                @Test
                @DisplayName("Given output stream is empty then output stream with content 'Hello'")
                void givenOutputStreamEmptyThenOutputStreamWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHelloWorld);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(new FileInputStream(input),
                            new FileOutputStream(output),
                            IoUtilsTest.BUFFER_SIZE,
                            TestCopy.this.length);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: copy(InputStream in, OutputStream out, long length)")
        class TestCopyInputStreamToOutputStreamWithLength {
            @Nested
            @DisplayName("Given input stream with content 'Hello World' and length is 5")
            class GivenInputStreamWithContentHelloWorldAndLength5 {
                @Test
                @DisplayName("Given output stream is empty then output stream with content 'Hello'")
                void givenOutputStreamEmptyThenOutputStreamWithContentHello() throws IOException {
                    File input = IoUtilsTest.this.createTempFile(IoUtilsTest.this.contentHelloWorld);
                    File output = IoUtilsTest.this.createTempFile(StringUtils.EMPTY);
                    IoUtils.copy(new FileInputStream(input), new FileOutputStream(output), TestCopy.this.length);

                    String content = IoUtils.content(new FileInputStream(output));
                    assertThat(content).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }
    }

    @Nested
    @DisplayName("测试方法：fromHexString(String hexString)")
    class WhenCallHexStringToBytes {
        @Test
        @DisplayName("当十六进制字符串为空白字符串时，返回空的字节数组")
        void shouldReturnEmptyWhenHexStringIsBlank() {
            byte[] actual = IoUtils.fromHexString(StringUtils.EMPTY);
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("当十六进制字符串为小写字母时，返回正确的字节数组")
        void shouldReturnCorrectBytesWhenHexStringIsLowerCaseLetter() {
            byte[] actual = IoUtils.fromHexString("a");
            assertThat(actual).hasSize(1).containsSequence(10);
        }

        @Test
        @DisplayName("当十六进制字符串为大写字母时，返回正确的字节数组")
        void shouldReturnCorrectBytesWhenHexStringIsUpperCaseLetter() {
            byte[] actual = IoUtils.fromHexString("F");
            assertThat(actual).hasSize(1).containsSequence(15);
        }

        @Test
        @DisplayName("当十六进制字符串为偶数位时，返回正确的字节数组")
        void shouldReturnCorrectBytesWhenHexStringLengthIsEven() {
            byte[] actual = IoUtils.fromHexString("12");
            assertThat(actual).hasSize(1).containsSequence(18);
        }

        @Test
        @DisplayName("当十六进制字符串为奇数位时，返回正确的字节数组")
        void shouldReturnCorrectBytesWhenHexStringLengthIsOdd() {
            byte[] actual = IoUtils.fromHexString("1");
            assertThat(actual).hasSize(1).containsSequence(1);
        }

        @Test
        @DisplayName("当十六进制字符串中包含非十六进制字符时，抛出异常")
        void shouldThrowExceptionWhenHexStringContainsNonHexChar() {
            IllegalStateException exception =
                    catchThrowableOfType(() -> IoUtils.fromHexString("g"), IllegalStateException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("Char is out of range, legal char range is [0-9A-Fa-f]. [ch='g']");
        }
    }

    @Nested
    @DisplayName("Test properties")
    class TestProperties {
        private final String invalidResourceKey = "invalidResource";

        @Nested
        @DisplayName("Test method: properties(Class<?> clazz, String resourceKey)")
        class TestPropertiesWithClass {
            @Nested
            @DisplayName("Given class is IoUtilsTest.class")
            class GivenClass {
                @Test
                @DisplayName("Given resource not found then throw IllegalStateException")
                void givenResourceNotFoundThenThrowException() {
                    IllegalStateException exception = catchThrowableOfType(() -> IoUtils.properties(IoUtilsTest.class,
                            "c5def8de-d547-40dd-91e7-8a09c3a6d3b6"), IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("The embedded resource with specific key not found. "
                                    + "[key=c5def8de-d547-40dd-91e7-8a09c3a6d3b6]");
                }

                @Test
                @DisplayName("Given resource exist then return correct properties")
                void givenResourceExistThenReturnCorrectProperties() {
                    Properties actual = IoUtils.properties(IoUtilsTest.class, "/property/valid.properties");
                    assertThat(actual).isNotNull().hasSize(2).containsEntry("k1", "v1").containsEntry("k2", "v2");
                }

                @Test
                @DisplayName("Given resource exist in different charset")
                void givenResourceInMultipleCharsetThenReturnCorrectProperties() {
                    Properties actual = IoUtils.properties(IoUtilsTest.class,
                            "/property/charset.properties",
                            StandardCharsets.UTF_8);
                    assertThat(actual).isNotNull()
                            .hasSize(2)
                            .containsEntry("code1", "测试")
                            .containsEntry("code2", "测试");
                }

                @Test
                @DisplayName("Given load resource error then throw IllegalStateException")
                void givenLoadResourceErrorThenThrowException() throws IOException {
                    try (MockedStatic<IoUtils> mocked = mockStatic(IoUtils.class)) {
                        InputStream in = Mockito.mock(InputStream.class);
                        when(in.read(any())).thenThrow(new IOException());
                        mocked.when(() -> IoUtils.resource(eq(IoUtilsTest.class),
                                eq(TestProperties.this.invalidResourceKey))).thenReturn(in);
                        mocked.when(() -> IoUtils.properties((Class<?>) any(), any())).thenCallRealMethod();
                        IllegalStateException exception =
                                catchThrowableOfType(() -> IoUtils.properties(IoUtilsTest.class,
                                        TestProperties.this.invalidResourceKey), IllegalStateException.class);
                        assertThat(exception).isNotNull()
                                .hasMessage("Failed to read properties from embedded resource. [resourceKey="
                                        + TestProperties.this.invalidResourceKey + "]")
                                .getCause()
                                .isInstanceOf(IOException.class);
                    }
                }
            }
        }

        @Nested
        @DisplayName("Test method: properties(ClassLoader loader, String resourceKey)")
        class TestPropertiesWithClassLoader {
            @Nested
            @DisplayName("Given loader is Thread.currentThread().getContextClassLoader()")
            class GivenClassLoader {
                @Test
                @DisplayName("Given resource exist then return correct properties")
                void givenResourceExistThenReturnCorrectProperties() {
                    Properties actual = IoUtils.properties(Thread.currentThread().getContextClassLoader(),
                            "property/valid.properties");
                    assertThat(actual).isNotNull().hasSize(2).containsEntry("k1", "v1").containsEntry("k2", "v2");
                }

                @Test
                @DisplayName("Given load resource error then throw IllegalStateException")
                void givenLoadResourceErrorThenThrowException() throws IOException {
                    try (MockedStatic<IoUtils> mocked = mockStatic(IoUtils.class)) {
                        InputStream in = Mockito.mock(InputStream.class);
                        when(in.read(any())).thenThrow(new IOException());
                        mocked.when(() -> IoUtils.resource(eq(Thread.currentThread().getContextClassLoader()),
                                eq(TestProperties.this.invalidResourceKey))).thenReturn(in);
                        mocked.when(() -> IoUtils.properties(ObjectUtils.<ClassLoader>cast(any()), any()))
                                .thenCallRealMethod();
                        IllegalStateException exception =
                                catchThrowableOfType(() -> IoUtils.properties(Thread.currentThread()
                                                .getContextClassLoader(), TestProperties.this.invalidResourceKey),
                                        IllegalStateException.class);
                        assertThat(exception).isNotNull()
                                .hasMessage("Failed to read properties from embedded resource. [resourceKey="
                                        + TestProperties.this.invalidResourceKey + "]")
                                .getCause()
                                .isInstanceOf(IOException.class);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("Test read")
    class TestRead {
        private final ByteArrayInputStream in =
                new ByteArrayInputStream(IoUtilsTest.this.contentHello.getBytes(StandardCharsets.UTF_8));

        @Nested
        @DisplayName("Test method: read(Class<?> clazz, String resourceName)")
        class TestReadClass {
            @Nested
            @DisplayName("Given class is IoUtilsTest.class")
            class GivenClassThis {
                @Test
                @DisplayName("Given resource is /text/hello.txt then return 'Hello World'")
                void givenResourceHelloThenReturnHelloWorld() throws IOException {
                    String actual = IoUtils.content(IoUtilsTest.class, "/text/hello.txt");
                    assertThat(actual).isEqualTo(IoUtilsTest.this.contentHelloWorld);
                }

                @Test
                @DisplayName("Given resource is text/hello.txt then throw IllegalStateException")
                void givenResourceNotExistThenThrowException() {
                    IllegalStateException exception =
                            catchThrowableOfType(() -> IoUtils.content(IoUtilsTest.class, "text/hello.txt"),
                                    IllegalStateException.class);
                    assertThat(exception).isNotNull().hasMessage("The input stream to read cannot be null.");
                }
            }
        }

        @Nested
        @DisplayName("Test method: read(ClassLoader classLoader, String resourceName)")
        class TestReadClassLoader {
            @Nested
            @DisplayName("Given class loader is Thread.currentThread().getContextClassLoader()")
            class GivenClassLoaderCurrent {
                @Test
                @DisplayName("Given resource is text/hello.txt then return 'Hello World'")
                void givenResourceHelloThenReturnHelloWorld() throws IOException {
                    String actual = IoUtils.content(Thread.currentThread().getContextClassLoader(), "text/hello.txt");
                    assertThat(actual).isEqualTo(IoUtilsTest.this.contentHelloWorld);
                }
            }
        }

        @Nested
        @DisplayName("Test method: read(InputStream in)")
        class TestReadInputStream {
            @Test
            @DisplayName("Given input stream with content 'Hello' then return 'Hello'")
            void givenInputStreamWithHelloThenReturnHello() throws IOException {
                String actual = IoUtils.content(TestRead.this.in);
                assertThat(actual).hasSize(5).isEqualTo(IoUtilsTest.this.contentHello);
            }
        }

        @Nested
        @DisplayName("Test method: read(InputStream in, Charset charset)")
        class TestReadInputStreamWithCharset {
            @Nested
            @DisplayName("Given input stream with content 'Hello'")
            class GivenInputStreamWith5Bytes {
                @Test
                @DisplayName("Given charset is UTF-8 then return 'Hello'")
                void givenCharsetUtf8ThenReturnHello() throws IOException {
                    String actual = IoUtils.content(TestRead.this.in, StandardCharsets.UTF_8);
                    assertThat(actual).hasSize(5).isEqualTo(IoUtilsTest.this.contentHello);
                }
            }
        }

        @Nested
        @DisplayName("Test method: read(InputStream in, int length)")
        class TestReadInputStreamWithLength {
            @Nested
            @DisplayName("Given input stream with content 'Hello'")
            class GivenInputStreamWith5Bytes {
                @Test
                @DisplayName("Given length 0 then return empty byte array")
                void givenLength0ThenReturnEmptyByteArray() throws IOException {
                    byte[] actual = IoUtils.read(TestRead.this.in, 0);
                    assertThat(actual).isEmpty();
                }

                @Test
                @DisplayName("Given length 6 then throw IOException")
                void givenLength6ThenThrowException() {
                    IOException exception =
                            catchThrowableOfType(() -> IoUtils.read(TestRead.this.in, 6), IOException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("Failed to read from input stream: no enough available bytes. "
                                    + "[expectedLength=6, actualLength=5]");
                }

                @Test
                @DisplayName("Given length 5 then return 5 bytes array")
                void givenLength5ThenReturnHelloByteArray() throws IOException {
                    byte[] actual = IoUtils.read(TestRead.this.in, 5);
                    assertThat(actual).hasSize(5)
                            .isEqualTo(IoUtilsTest.this.contentHello.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    @Nested
    @DisplayName("Test resource")
    class TestResource {
        @Nested
        @DisplayName("Test method: resource(Class<?> clazz, String key)")
        class TestResourceClass {
            @Nested
            @DisplayName("Given class is IoUtilsTest.class")
            class GivenClassThis {
                @Test
                @DisplayName("Given key not exist then throw IllegalStateException")
                void givenResourceNotExistThenThrowException() {
                    IllegalStateException exception =
                            catchThrowableOfType(() -> IoUtils.resource(IoUtils.class, "NotExist"),
                                    IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("The embedded resource with specific key not found. [key=NotExist]");
                }

                @Test
                @DisplayName("Given key exist then return not null")
                void givenResourceExistThenReturnNotNull() {
                    InputStream actual = IoUtils.resource(IoUtils.class, "/text/hello.txt");
                    assertThat(actual).isNotNull();
                }
            }
        }

        @Nested
        @DisplayName("Test method: resource(ClassLoader loader, String key)")
        class TestResourceClassLoader {
            @Nested
            @DisplayName("Given class loader is Thread.currentThread().getContextClassLoader()")
            class GivenClassThis {
                @Test
                @DisplayName("Given key not exist then throw IllegalStateException")
                void givenResourceNotExistThenThrowException() {
                    IllegalStateException exception =
                            catchThrowableOfType(() -> IoUtils.resource(Thread.currentThread().getContextClassLoader(),
                                    "NotExist"), IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("The embedded resource with specific key not found. [key=NotExist]");
                }

                @Test
                @DisplayName("Given key exist then return not null")
                void givenResourceExistThenReturnNotNull() {
                    InputStream actual =
                            IoUtils.resource(Thread.currentThread().getContextClassLoader(), "text/hello.txt");
                    assertThat(actual).isNotNull();
                }
            }
        }
    }

    @Nested
    @DisplayName("当调用 toHexString(byte[] bytes) 方法时")
    class WhenCallToHexString {
        @Test
        @DisplayName("当字节数组为 null 时，返回空字符串")
        void shouldReturnEmptyWhenBytesIsNull() {
            String actual = IoUtils.toHexString(null);
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("当字节数组不为 null 时，返回正确的十六进制字符串")
        void shouldReturnCorrectHexString() {
            String actual = IoUtils.toHexString(new byte[] {18});
            assertThat(actual).isEqualTo("12");
        }
    }

    @Nested
    @DisplayName("测试方法：emptyInputStream()")
    class TestEmptyInputStream {
        @Test
        @DisplayName("应该返回空的输入流")
        void shouldReturnEmptyInputStream() throws IOException {
            final InputStream inputStream = IoUtils.emptyInputStream();
            assertThat(inputStream.available()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("提供随机访问文件")
    class GivenRandomAccessFile {
        private final byte[] bytes = {10, 1, 20, 4, 13, 2};

        private File createTempFile() throws IOException {
            File file = Files.createTempFile("IOUtilsTest-", ".tmp").toFile();
            try (final FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(this.bytes);
            }
            file.deleteOnExit();
            return file;
        }

        @Nested
        @DisplayName("测试方法：read()")
        class TestRead {
            @Test
            @DisplayName("接收内容字节数组等于文件大小时，应该返回文件的内容")
            void whenFillBytesSizeEqualsFileSizeThenReadSuccess() throws IOException {
                final File tempFile = GivenRandomAccessFile.this.createTempFile();
                RandomAccessFile file = new RandomAccessFile(tempFile, "rw");
                final byte[] read = IoUtils.read(file, 0, GivenRandomAccessFile.this.bytes.length);
                assertThat(read).isEqualTo(GivenRandomAccessFile.this.bytes);
            }
        }

        @Nested
        @DisplayName("测试方法：fill(RandomAccessFile file, byte[] bytes)")
        class TestFill {
            @Test
            @DisplayName("接收内容字节数组小于文件大小时，读取成功")
            void whenFillBytesSizeLowerFileSizeThenReadSuccess() throws IOException {
                final File tempFile = GivenRandomAccessFile.this.createTempFile();
                RandomAccessFile file = new RandomAccessFile(tempFile, "rw");
                byte[] readBytes = new byte[GivenRandomAccessFile.this.bytes.length - 1];
                IoUtils.fill(file, readBytes);
                assertThat(readBytes).isEqualTo(Arrays.copyOf(GivenRandomAccessFile.this.bytes, readBytes.length));
            }

            @Test
            @DisplayName("接收内容字节数组超过文件大小时，抛出异常")
            void whenFillBytesSizeGreaterFileSizeThenThrowException() throws IOException {
                final File tempFile = GivenRandomAccessFile.this.createTempFile();
                RandomAccessFile file = new RandomAccessFile(tempFile, "rw");
                byte[] readBytes = new byte[GivenRandomAccessFile.this.bytes.length + 1];
                assertThatThrownBy(() -> IoUtils.fill(file, readBytes)).isInstanceOf(EOFException.class);
            }
        }
    }

    private File createTempFile(String content) throws IOException {
        File file = Files.createTempFile("IoUtilsTest-", ".tmp").toFile();
        file.deleteOnExit();
        if (StringUtils.isBlank(content)) {
            return file;
        }
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return file;
    }
}