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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import modelengine.fitframework.util.support.Unzip;
import modelengine.fitframework.util.support.Zip;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

/**
 * {@link FileUtils} 的单元测试。
 *
 * @author 季聿阶
 * @since 2021-11-11
 */
public class FileUtilsTest {
    @Nested
    @DisplayName("Test canonicalize")
    class TestCanonicalize {
        /**
         * 目标方法：{@link FileUtils#canonicalize(File)}。
         */
        @Nested
        @DisplayName("Test method: canonicalize(File file)")
        class TestCanonicalizeFile {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Input is null, output is null")
            void givenNullThenReturnNull() {
                File actual = FileUtils.canonicalize(ObjectUtils.<File>cast(null));
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Input is 'testFile.txt', output is not null")
            void givenNotNullThenReturnFile() throws IOException {
                File actual = FileUtils.canonicalize(FileUtilsTest.this.createTempFile());
                assertThat(actual).isFile();
            }

            @Test
            @DisplayName("Input is invalid file, output is exception")
            void givenInvalidFileThenReturnOriginalFile() {
                File expected = new File("/a\u0000");
                IllegalStateException exception =
                        catchThrowableOfType(() -> FileUtils.canonicalize(expected), IllegalStateException.class);
                assertThat(exception).hasMessage(StringUtils.format("Fail to canonicalize file. [file={0}]",
                        expected.getPath()));
            }
        }

        /**
         * 目标方法：{@link FileUtils#canonicalize(String)}。
         */
        @Nested
        @DisplayName("Test method: canonicalize(String fileName)")
        class TestCanonicalizeFileName {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Input is null, output is null")
            void givenNullThenReturnNull() {
                File actual = FileUtils.canonicalize(ObjectUtils.<String>cast(null));
                assertThat(actual).isNull();
            }

            @Test
            @DisplayName("Input is exist file name, output is not null")
            void givenNotNullThenReturnFile() throws IOException {
                File file = FileUtilsTest.this.createTempFile();
                File actual = FileUtils.canonicalize(file.getPath());
                assertThat(actual).isFile();
            }
        }
    }

    /**
     * 目标方法：{@link FileUtils#child(File, String...)}。
     */
    @Nested
    @DisplayName("Test method: child(File parent, String... paths)")
    class TestChild {
        @Test
        @DisplayName("Parent is '/a', path is null, output is '/a'")
        void givenPathNullThenReturnParent() {
            File parent = new File("/a");
            File actual = FileUtils.child(parent, (String[]) null);
            assertThat(actual).isEqualTo(parent);
        }

        @Test
        @DisplayName("Parent is '/a', path is ['/b', null, '/c'], output is '/a/b/c'")
        void givenCorrectPathThenReturnCorrectChild() {
            File actual = FileUtils.child(new File("/a"), "/b", null, "/c");
            assertThat(actual).isEqualTo(new File("/a/b/c"));
        }
    }

    @Nested
    @DisplayName("Test delete")
    class TestDelete {
        /**
         * 目标方法：{@link FileUtils#delete(File)}。
         */
        @Nested
        @DisplayName("Test method: delete(File file)")
        class TestDeleteFile {
            @Test
            @DisplayName("Input is null, execution has no exception")
            void givenNullThenReturnNoException() {
                assertThatNoException().isThrownBy(() -> FileUtils.delete(ObjectUtils.<File>cast(null)));
            }

            @Test
            @DisplayName("Input is not exist file, execution has no exception")
            void givenNotExistFileThenReturnNoException() {
                assertThatNoException().isThrownBy(() -> FileUtils.delete(new File("/NotExistFile")));
            }

            @Test
            @DisplayName("Input is exist file, execution has no exception")
            void givenExistFileThenReturnNoException() throws IOException {
                File file = FileUtilsTest.this.createTempFile();
                assertThatNoException().isThrownBy(() -> FileUtils.delete(file));
                assertThat(file).doesNotExist();
            }

            @Test
            @DisplayName("Input is exist directory, execution has no exception")
            void givenExistDirectoryThenReturnNoException() throws IOException {
                File directory = FileUtilsTest.this.createTempDirectory();
                File file = new File(directory, "a");
                Files.createFile(file.toPath());
                file.deleteOnExit();

                assertThatNoException().isThrownBy(() -> FileUtils.delete(directory));
                assertThat(file).doesNotExist();
                assertThat(directory).doesNotExist();
            }

            @Test
            @DisplayName("Input is exist file, delete cause IOException, output is exception")
            void givenExistFileAndDeleteCauseExceptionThenThrowException() throws IOException {
                File file = FileUtilsTest.this.createTempFile();
                try (MockedStatic<Files> mocked = mockStatic(Files.class)) {
                    mocked.when(() -> Files.deleteIfExists(any())).thenThrow(IOException.class);
                    IllegalStateException exception =
                            catchThrowableOfType(() -> FileUtils.delete(file), IllegalStateException.class);
                    assertThat(exception).isNotNull();
                }
            }
        }

        /**
         * 目标方法：{@link FileUtils#delete(String)}。
         */
        @Nested
        @DisplayName("Test method: delete(String path)")
        class TestDeletePath {
            @Test
            @DisplayName("Input is not exist file, execution has no exception")
            void givenPathThenReturnNoException() {
                assertThatNoException().isThrownBy(() -> FileUtils.delete("/NotExistFile"));
            }
        }
    }

    /**
     * 目标方法：{@link FileUtils#depth(File)}。
     */
    @Nested
    @DisplayName("Test method: depth(File file)")
    class TestDepth {
        @Test
        @DisplayName("Input is '/a/b', output is 3")
        void given2DepthWithRootThenReturn3() {
            int actual = FileUtils.depth(new File("/a/b"));
            assertThat(actual).isEqualTo(3);
        }
    }

    /**
     * 目标方法：{@link FileUtils#ensureDirectory(File)}。
     */
    @Nested
    @DisplayName("Test method: ensureDirectory(File directory)")
    class TestEnsureDirectory {
        @Test
        @DisplayName("Input is file, execution has exception")
        void givenNotDirectoryThenThrowException() throws IOException {
            File file = FileUtilsTest.this.createTempFile();
            IllegalStateException exception =
                    catchThrowableOfType(() -> FileUtils.ensureDirectory(file), IllegalStateException.class);
            assertThat(exception).hasMessage(StringUtils.format("The directory to ensure is a file. [file={0}]",
                    file.getPath()));
        }

        @Test
        @DisplayName("Input is directory, execution has no exception")
        void givenDirectoryThenReturnNoException() throws IOException {
            File directory = FileUtilsTest.this.createTempDirectory();
            assertThatNoException().isThrownBy(() -> FileUtils.ensureDirectory(directory));
        }

        @Test
        @DisplayName("Input is not exist directory, execution has no exception")
        void givenNotExistDirectoryThenReturnNoException() {
            File directory = new File("src/test/resources/not-exist");
            directory.deleteOnExit();
            assertThatNoException().isThrownBy(() -> FileUtils.ensureDirectory(directory));
        }
    }

    @Nested
    @DisplayName("测试方法: extension(String filename)")
    class TestExtension {
        @Test
        @DisplayName("当提供文件名包含点时，返回文件后缀名")
        void givenFileNameIncludeDotThenReturnFileExtension() {
            final String fileName = "hello.txt";
            final String extension = FileUtils.extension(fileName);
            assertThat(extension).isEqualTo(".txt");
        }

        @Test
        @DisplayName("当提供文件名不包含点时，返回空字符串")
        void givenFileNameExcludeDotThenReturnEmptyString() {
            final String fileName = "hello";
            final String extension = FileUtils.extension(fileName);
            assertThat(extension).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("测试方法: ignoreExtension(String filename)")
    class TestIgnoreExtension {
        @Test
        @DisplayName("当提供文件名包含点时，返回不含后缀的文件名")
        void givenFileNameIncludeDotThenReturnFileName() {
            final String fileName = "hello.txt";
            final String extension = FileUtils.ignoreExtension(fileName);
            assertThat(extension).isEqualTo("hello");
        }

        @Test
        @DisplayName("当提供文件名不包含点时，返回原字符串")
        void givenFileNameExcludeDotThenReturnEmptyString() {
            final String fileName = "hello";
            final String extension = FileUtils.ignoreExtension(fileName);
            assertThat(extension).isEqualTo(fileName);
        }
    }

    /**
     * 目标方法：{@link FileUtils#file(URL)}。
     */
    @Nested
    @DisplayName("Test method: file(URL url)")
    class TestFile {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is null")
        void givenNullThenReturnNull() {
            File actual = FileUtils.file(null);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is URL, output is File")
        void givenValidUrlThenReturnFile() throws IOException {
            File file = FileUtilsTest.this.createTempFile();
            File actual = FileUtils.file(file.toURI().toURL());
            assertThat(actual).isNotNull();
        }

        @Test
        @DisplayName("Input invalid URL, output is exception")
        void givenInvalidUrlThenThrowException() throws MalformedURLException {
            URL url = new URL("http://fit.lab?q=%");
            IllegalStateException exception =
                    catchThrowableOfType(() -> FileUtils.file(url), IllegalStateException.class);
            assertThat(exception).hasMessage("To uri failed. [url=http://fit.lab?q=%]");
        }
    }

    /**
     * 目标方法：{@link FileUtils#isAbsolute(String)}。
     */
    @Nested
    @DisplayName("Test method: isAbsolute(String path)")
    class TestIsAbsolute {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is false")
        void givenNullThenReturnFalse() {
            boolean actual = FileUtils.isAbsolute(null);
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is 'a/b', output is false")
        void givenNotAbsolutePathThenReturnFalse() {
            boolean actual = FileUtils.isAbsolute("a/b");
            assertThat(actual).isFalse();
        }
    }

    /**
     * 目标方法：{@link FileUtils#isJar(File)}。
     */
    @Nested
    @DisplayName("Test method: isJar(File file)")
    class TestIsJar {
        @Test
        @DisplayName("Input is '/a', output is false")
        void givenNotJarFileNameThenReturnFalse() {
            boolean actual = FileUtils.isJar(new File("/a"));
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("Input is '/a.jar', output is true")
        void givenJarFileNameThenReturnTrue() {
            boolean actual = FileUtils.isJar(new File("/a.jar"));
            assertThat(actual).isTrue();
        }
    }

    @Nested
    @DisplayName("Test method: list(File file)")
    class TestList {
        @Test
        @DisplayName("Input is null, output is []")
        void givenFileNullThenReturnEmptyList() {
            List<File> actual = FileUtils.list(null);
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("Input is file, output is []")
        void givenFileNotDirectoryThenReturnEmptyList() throws IOException {
            File file = FileUtilsTest.this.createTempFile();
            List<File> actual = FileUtils.list(file);
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("Input is directory with 1 file, output is [file]")
        void givenFileIsDirectoryThenReturnItsSubFiles() throws IOException {
            File directory = FileUtilsTest.this.createTempDirectory();
            File file = new File(directory, "a");
            Files.createFile(file.toPath());
            file.deleteOnExit();
            List<File> actual = FileUtils.list(directory);
            assertThat(actual).isNotEmpty().hasSize(1);
        }
    }

    /**
     * 目标方法：{@link FileUtils#path(File)}。
     */
    @Nested
    @DisplayName("Test method: path(File file)")
    class TestPath {
        @SuppressWarnings("ConstantConditions")
        @Test
        @DisplayName("Input is null, output is null")
        void givenNullThenReturnNull() {
            String actual = FileUtils.path(null);
            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("Input is exist file, output is correct path")
        void givenExistFileThenReturnCorrectPath() throws IOException {
            File file = FileUtilsTest.this.createTempFile();
            String actual = FileUtils.path(file);
            assertThat(actual).isEqualTo(FileUtils.canonicalize(file).getPath());
        }
    }

    /**
     * 目标方法：{@link FileUtils#traverse(File)}。
     */
    @Nested
    @DisplayName("Test method: traverse(File file)")
    class TestTraverse {
        @Test
        @DisplayName("Input is null, output is null")
        void givenNullThenReturnNull() {
            Stream<File> actual = FileUtils.traverse(null);
            assertThat(actual).hasSize(0);
        }

        @Test
        @DisplayName("Input is empty directory, output is [directory]")
        void givenEmptyDirectoryThenReturnOnly1File() throws IOException {
            File directory = FileUtilsTest.this.createTempDirectory();
            Stream<File> actual = FileUtils.traverse(directory);
            assertThat(actual).hasSize(1).containsSequence(directory.getCanonicalFile());
        }

        @Test
        @DisplayName("Input is directory with 1 sub-file, output is [directory, sub-file]")
        void givenNotEmptyDirectoryThenReturnDirectoryWithSubFiles() throws IOException {
            File directory = FileUtilsTest.this.createTempDirectory();
            File file = new File(directory, "a");
            Files.createFile(file.toPath());
            file.deleteOnExit();
            Stream<File> actual = FileUtils.traverse(directory);
            assertThat(actual).hasSize(2).containsSequence(directory.getCanonicalFile(), file.getCanonicalFile());
        }
    }

    @Nested
    @DisplayName("Test unzip")
    class TestUnzip {
        /**
         * 目标方法：{@link FileUtils#unzip(File)}。
         */
        @Nested
        @DisplayName("Test method: unzip(File zipFile)")
        class TestUnzipFile {
            @Test
            @DisplayName("Input is zip file, output is Unzip")
            void givenZipFileThenReturnUnzip() {
                File file = new File("a");
                Unzip actual = FileUtils.unzip(file);
                assertThat(actual).isNotNull()
                        .hasFieldOrPropertyWithValue("zipFile", file)
                        .hasFieldOrPropertyWithValue("charset", FileUtils.DEFAULT_CHARSET);
            }
        }

        /**
         * 目标方法：{@link FileUtils#unzip(File, Charset)}。
         */
        @Nested
        @DisplayName("Test method: unzip(File zipFile, Charset charset)")
        class TestUnzipFileWithCharset {
            @Test
            @DisplayName("Input is zip file, output is Unzip")
            void givenZipFileThenReturnUnzip() {
                File file = new File("a");
                Unzip actual = FileUtils.unzip(file, StandardCharsets.ISO_8859_1);
                assertThat(actual).isNotNull()
                        .hasFieldOrPropertyWithValue("zipFile", file)
                        .hasFieldOrPropertyWithValue("charset", StandardCharsets.ISO_8859_1);
            }
        }
    }

    @Nested
    @DisplayName("Test zip")
    class TestZip {
        /**
         * 目标方法：{@link FileUtils#zip(File)}。
         */
        @Nested
        @DisplayName("Test method: zip(File zipFile)")
        class TestZipFile {
            @Test
            @DisplayName("Input is zip file, output is Zip")
            void givenZipFileThenReturnZip() {
                File file = new File("a");
                Zip actual = FileUtils.zip(file);
                assertThat(actual).isNotNull()
                        .hasFieldOrPropertyWithValue("zipFile", file)
                        .hasFieldOrPropertyWithValue("charset", FileUtils.DEFAULT_CHARSET);
            }
        }

        /**
         * 目标方法：{@link FileUtils#zip(File, Charset)}。
         */
        @Nested
        @DisplayName("Test method: zip(File zipFile, Charset charset)")
        class TestZipFileWithCharset {
            @Test
            @DisplayName("Input is zip file, output is Unzip")
            void givenZipFileThenReturnZip() {
                File file = new File("a");
                Zip actual = FileUtils.zip(file, StandardCharsets.ISO_8859_1);
                assertThat(actual).isNotNull()
                        .hasFieldOrPropertyWithValue("zipFile", file)
                        .hasFieldOrPropertyWithValue("charset", StandardCharsets.ISO_8859_1);
            }
        }
    }

    @Nested
    @DisplayName("测试方法：urlOf(File file)")
    class TestUrlOf {
        @Test
        @DisplayName("提供一个正常文件，返回文件地址")
        void givenNormalFileThenReturnUrl() throws IOException {
            File file = FileUtilsTest.this.createTempDirectory();
            final URL url = FileUtils.urlOf(file);
            assertThat(url.getPath()).isNotNull().contains("FileUtilsTest-");
        }

        @Test
        @DisplayName("提供一个异常文件，抛出异常")
        void givenFileWhenUrlUnNormalThenThrowException() {
            final File mock = mock(File.class);
            when(mock.toURI()).thenAnswer(invocation -> {
                throw new MalformedURLException();
            });
            assertThatThrownBy(() -> FileUtils.urlOf(mock)).isInstanceOf(IllegalStateException.class);
        }
    }

    private File createTempFile() throws IOException {
        File file = Files.createTempFile("FileUtilsTest-", ".tmp").toFile();
        file.deleteOnExit();
        return file;
    }

    private File createTempDirectory() throws IOException {
        File directory = Files.createTempDirectory("FileUtilsTest-").toFile();
        directory.deleteOnExit();
        return directory;
    }
}
