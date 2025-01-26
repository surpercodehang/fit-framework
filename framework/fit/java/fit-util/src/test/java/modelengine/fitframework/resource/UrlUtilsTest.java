/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.Zip;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.jar.JarFile;

/**
 * 为 {@link UrlUtils} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class UrlUtilsTest {
    private final String originForm = "你 好";
    private final String originPath = "name=Jack Ma";
    private final String encodedForm = "%E4%BD%A0+%E5%A5%BD";
    private final String encodedPath = "name%3DJack%20Ma";

    @Nested
    @DisplayName("Test method: combine(String base, String path)")
    class TestCombine {
        private static final String BASE_URL = "https://www.huawei.com///";
        private static final String PATH_URL = "///suffix";
        private static final String COMBINED_URL = "https://www.huawei.com/suffix";

        @SuppressWarnings("ConstantConditions")
        @Nested
        @DisplayName("Given base is null")
        class GivenBaseNull {
            private final String base = null;

            @Test
            @DisplayName("Given path is null then return null")
            void givenPathNullThenReturnNull() {
                String actual = UrlUtils.combine(this.base, null);
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Given path is '///suffix' then return '///suffix'")
            void givenPathNotNullThenReturnPath() {
                String actual = UrlUtils.combine(this.base, PATH_URL);
                assertThat(actual).isNotNull().isEqualTo(PATH_URL);
            }
        }

        @Nested
        @DisplayName("Given base is 'https://www.huawei.com///'")
        class GivenBaseNotNull {
            @Test
            @DisplayName("Given path is null then return 'https://www.huawei.com///'")
            void givenPathNullThenReturnBase() {
                String actual = UrlUtils.combine(BASE_URL, null);
                assertThat(actual).isNotNull().isEqualTo(BASE_URL);
            }

            @Test
            @DisplayName("Given path is '///suffix' then return 'https://www.huawei.com/suffix'")
            void givenPathNotNullThenReturnCombined() {
                String actual = UrlUtils.combine(BASE_URL, PATH_URL);
                assertThat(actual).isNotNull().isEqualTo(COMBINED_URL);
            }
        }
    }

    @Nested
    @DisplayName("Test method: decode(String toDecode)")
    class TestDecode {
        @Nested
        @DisplayName("Expected scenario")
        class TestExpectedScenario {
            @Test
            @DisplayName("Given encoded form string then return origin form string")
            void givenEncodedStringThenReturnOriginFormString() {
                String actual = UrlUtils.decodeForm(UrlUtilsTest.this.encodedForm);
                assertThat(actual).isEqualTo(UrlUtilsTest.this.originForm);
            }

            @Test
            @DisplayName("Given encoded path string then return origin path string")
            void givenEncodedStringThenReturnOriginPathString() {
                String actual = UrlUtils.decodePath(UrlUtilsTest.this.encodedPath);
                assertThat(actual).isEqualTo(UrlUtilsTest.this.originPath);
            }
        }

        @Nested
        @DisplayName("Exception scenario")
        class TestExceptionScenario {
            @Test
            @DisplayName("Given decode with exception then throw IllegalStateException")
            void givenDecodeWithExceptionThenThrowException() {
                try (MockedStatic<URLDecoder> mocked = mockStatic(URLDecoder.class)) {
                    mocked.when(() -> URLDecoder.decode(eq(UrlUtilsTest.this.encodedForm),
                            eq(StandardCharsets.UTF_8.toString()))).thenThrow(new UnsupportedEncodingException());
                    IllegalStateException exception =
                            catchThrowableOfType(() -> UrlUtils.decodeForm(UrlUtilsTest.this.encodedForm),
                                    IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("Unsupported decoding type: UTF-8.")
                            .getCause()
                            .isInstanceOf(UnsupportedEncodingException.class);
                }
            }
        }
    }

    @Nested
    @DisplayName("Test method: encode(String toEncode)")
    class TestEncode {
        @Nested
        @DisplayName("Expected scenario")
        class TestExpectedScenario {
            @Test
            @DisplayName("Given origin form string then return encoded form string")
            void givenOriginStringThenReturnEncodedString() {
                String actual = UrlUtils.encodeForm(UrlUtilsTest.this.originForm);
                assertThat(actual).isEqualTo(UrlUtilsTest.this.encodedForm);
            }

            @Test
            @DisplayName("Given origin path string then return encoded path string")
            void givenOriginStringThenReturnEncodedPathString() {
                String actual = UrlUtils.encodePath(UrlUtilsTest.this.originPath);
                assertThat(actual).isEqualTo(UrlUtilsTest.this.encodedPath);
            }
        }

        @Nested
        @DisplayName("Exception scenario")
        class TestExceptionScenario {
            @Test
            @DisplayName("Given encode with exception then throw IllegalStateException")
            void givenEncodeWithExceptionThenThrowException() {
                try (MockedStatic<URLEncoder> mocked = mockStatic(URLEncoder.class)) {
                    mocked.when(() -> URLEncoder.encode(eq(UrlUtilsTest.this.originForm),
                            eq(StandardCharsets.UTF_8.toString()))).thenThrow(new UnsupportedEncodingException());
                    IllegalStateException exception =
                            catchThrowableOfType(() -> UrlUtils.encodeForm(UrlUtilsTest.this.originForm),
                                    IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("Unsupported encoding type: UTF-8.")
                            .getCause()
                            .isInstanceOf(UnsupportedEncodingException.class);
                }
            }
        }
    }

    @Nested
    @DisplayName("Test method: exists(URL url)")
    class TestExists {
        @Nested
        @DisplayName("Expected scenario")
        class TestExpectedScenario {
            @Test
            @DisplayName("Given exist file then return true")
            void givenExistFileThenReturnTrue() throws IOException {
                File file = Files.createTempFile("UrlUtilsTest-", ".tmp").toFile();
                file.deleteOnExit();
                boolean actual = UrlUtils.exists(file.toURI().toURL());
                assertThat(actual).isTrue();
            }
        }

        @Nested
        @DisplayName("Exception scenario")
        class TestExceptionScenario {
            @Test
            @DisplayName("Given URL is 'http://fit.lab?q=%' then throw IllegalStateException")
            void givenInvalidUrlThenThrowException() throws MalformedURLException {
                URL url = new URL("http://fit.lab?q=%");
                IllegalStateException exception =
                        catchThrowableOfType(() -> UrlUtils.exists(url), IllegalStateException.class);
                assertThat(exception).isNotNull()
                        .hasMessage("Failed to convert url to file. [url=]")
                        .getCause()
                        .isInstanceOf(URISyntaxException.class);
            }
        }
    }

    @Nested
    @DisplayName("Test method: isJar(URL url)")
    class TestIsJar {
        @Test
        @DisplayName("Given url suffix is '.jar' then return true")
        void givenUrlSuffixIsJarThenReturnTrue() throws IOException {
            File file = Files.createTempFile("UrlUtilsTest-", ".jar").toFile();
            file.deleteOnExit();
            boolean actual = UrlUtils.isJar(file.toURI().toURL());
            assertThat(actual).isTrue();
        }
    }

    @Nested
    @DisplayName("Test method: toJarFile(URL url)")
    class TestToJarFile {
        @Nested
        @DisplayName("Expected scenario")
        class TestExpectedScenario {
            private File jarFile;

            @BeforeEach
            void setup() throws IOException {
                this.jarFile = new File("src/test/resources/url-utils-to-jar-file-tmp.jar");
                Zip zip = new Zip(this.jarFile, null).override(true).add(new File("src/test/resources/zip"));
                zip.start();
            }

            @AfterEach
            void teardown() {
                FileUtils.delete(this.jarFile);
            }

            @Test
            @DisplayName("Given url suffix is '.jar' then return correct jar file")
            void givenUrlSuffixIsJarThenReturnCorrectJarFile() throws IOException {
                try (JarFile actual = UrlUtils.toJarFile(this.jarFile.toURI().toURL())) {
                    assertThat(actual).isNotNull();
                }
            }
        }

        @Nested
        @DisplayName("Exception scenario")
        class TestExceptionScenario {
            @Test
            @DisplayName("Given url suffix is '.jar' with empty content then throw IllegalStateException")
            void givenUrlSuffixIsJarWithEmptyContentThenThrowException() throws IOException {
                File file = Files.createTempFile("UrlUtilsTest-", ".jar").toFile();
                file.deleteOnExit();
                IllegalStateException exception = catchThrowableOfType(() -> UrlUtils.toJarFile(file.toURI().toURL()),
                        IllegalStateException.class);
                assertThat(exception).isNotNull()
                        .hasMessage(StringUtils.format("Failed to create jar file. [url={0}]",
                                file.toURI().toURL().getPath()))
                        .getCause()
                        .isInstanceOf(IOException.class);
            }
        }
    }

    @Nested
    @DisplayName("测试方法: extractInnerJarNameFromURL(URL url)")
    class TestExtractInnerJarNameFromURL {
        @Test
        @DisplayName("当 URL 为 Jar in Jar 格式时，返回最后一层 Jar 文件的文件名")
        void givenJarInJarThenReturnInnerJarName() {
            URL url = Mockito.mock(URL.class);
            when(url.toString()).thenReturn("jar:file:/path/to/outer.jar!/resource/inside/inner.jar");
            Optional<String> actual = UrlUtils.extractInnerJarNameFromURL(url);
            assertThat(actual).isPresent().get().isEqualTo("inner.jar");
        }

        @Test
        @DisplayName("当 URL 为普通 Jar 格式时，返回 Jar 文件的文件名")
        void givenJarThenReturnJarName() throws MalformedURLException {
            URL url = new URL("file:/path/to/single.jar");
            Optional<String> actual = UrlUtils.extractInnerJarNameFromURL(url);
            assertThat(actual).isPresent().get().isEqualTo("single.jar");
        }
    }

    @Nested
    @DisplayName("测试方法：isUrl()")
    class TestIsUrl {
        @ParameterizedTest
        @ValueSource(strings = {"https://github.com", "ftp://github.com"})
        @DisplayName("当输入是合法 URL 时，返回 true")
        void shouldReturnTrue(String input) {
            assertThat(UrlUtils.isUrl(input)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "github.com"})
        @DisplayName("当输入是非法时，返回 false")
        void shouldReturnFalse(String input) {
            assertThat(UrlUtils.isUrl(input)).isFalse();
        }
    }
}
