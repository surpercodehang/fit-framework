/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.resource.UrlUtils;
import modelengine.fitframework.util.FileUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * {@link UrlClassLoaderScanner} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-16
 */
@DisplayName("验证 UrlClassLoaderScanner")
public class UrlClassLoaderScannerTest {
    @Nested
    @DisplayName("验证创建一个 UrlClassLoaderScanner")
    class TestCreateUrlClassLoaderScanner {
        @Test
        @DisplayName("当使用指定类加载器初始化 UrlClassLoaderScanner 后，获得一个正确的扫描器")
        void givenSpecifiedClassLoaderThenReturnCorrectUrlClassLoaderScanner() {
            URLClassLoader classLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
            UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
            assertThatNoException().isThrownBy(() -> actual.addClassDetectedObserver(null));
            assertThatNoException().isThrownBy(() -> actual.addClassNameFilter(null));
            assertThat(actual.getClassLoader()).isEqualTo(classLoader);
        }
    }

    @Nested
    @DisplayName("验证扫描")
    class TestScan {
        @Nested
        @DisplayName("当扫描的类加载器为 AppClassLoader 时")
        class GivenClassLoaderIsAppClassLoader {
            @Test
            @DisplayName("当存在观察者时，扫描过程没有异常")
            void givenObserverThenNoException() {
                URLClassLoader classLoader =
                        new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                actual.addClassDetectedObserver(str -> {});
                assertThatNoException().isThrownBy(actual::scan);
            }

            @Test
            @DisplayName("当不存在观察者时，扫描过程没有异常")
            void givenNoObserverThenNoException() {
                URLClassLoader classLoader =
                        new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                assertThatNoException().isThrownBy(actual::scan);
            }
        }

        @Nested
        @DisplayName("当扫描的 URL 为文件时")
        class GivenUrlIsFile {
            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("当扫描的 URL 的名字包含 '-' 符号时，不会调用观察者")
            void givenNameIsInvalidThenConsumerWillNotBeInvoked() throws IOException {
                File file = Files.createTempFile("UrlClassLoaderScannerTest-", ".class").toFile();
                file.deleteOnExit();
                URL[] urls = new URL[] {file.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                Consumer<String> consumer = mock(Consumer.class);
                actual.addClassDetectedObserver(consumer);
                assertThatNoException().isThrownBy(actual::scan);
                verify(consumer, times(0)).accept(any());
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("当扫描的 URL 的名字不满足要求时，不会调用观察者")
            void givenNameIsFilteredThenConsumerWillNotBeInvoked() throws IOException {
                File file = Files.createTempFile("UrlClassLoaderScannerTest", ".class").toFile();
                file.deleteOnExit();
                URL[] urls = new URL[] {file.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                Consumer<String> consumer = mock(Consumer.class);
                actual.addClassDetectedObserver(consumer);
                actual.addClassNameFilter(name -> !name.startsWith("UrlClassLoaderScannerTest"));
                assertThatNoException().isThrownBy(actual::scan);
                verify(consumer, times(0)).accept(any());
            }

            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("当扫描的 URL 的名字不以 '.class' 结尾时，不会调用观察者")
            void givenNameIsNotClassThenConsumerWillNotBeInvoked() throws IOException {
                File file = Files.createTempFile("UrlClassLoaderScannerTest-", ".txt").toFile();
                file.deleteOnExit();
                URL[] urls = new URL[] {file.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                Consumer<String> consumer = mock(Consumer.class);
                actual.addClassDetectedObserver(consumer);
                assertThatNoException().isThrownBy(actual::scan);
                verify(consumer, times(0)).accept(any());
            }

            @Test
            @DisplayName("当扫描的文件夹在遍历时发生输入输出异常时，会抛出 IllegalStateException")
            void givenWalkDirectoryWithExceptionThenThrowException() throws IOException {
                File directory = Files.createTempDirectory("UrlClassLoaderScannerTest-").toFile();
                directory.deleteOnExit();
                File file = new File(directory, "a.class");
                Files.createFile(file.toPath());
                file.deleteOnExit();
                URL[] urls = new URL[] {directory.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                try (MockedStatic<Files> mocked = mockStatic(Files.class)) {
                    mocked.when(() -> Files.walk(any(), anyInt(), any())).thenThrow(new IOException());
                    IllegalStateException exception = catchThrowableOfType(actual::scan, IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("Failed to scan class directory. [directory=" + FileUtils.path(directory) + "]")
                            .getCause()
                            .isInstanceOf(IOException.class);
                }
            }
        }

        @Nested
        @DisplayName("当扫描的 URL 为 JAR 文件时")
        class GivenUrlIsJar {
            @SuppressWarnings("unchecked")
            @Test
            @DisplayName("当扫描的文件不存在时，不会调用观察者")
            void givenFileNotExistThenConsumerWillNotBeInvoked() throws IOException {
                File file = Files.createTempFile("UrlClassLoaderScannerTest-", ".jar").toFile();
                boolean isDeleted = file.delete();
                assertThat(isDeleted).isTrue();
                assertThat(file.exists()).isFalse();
                URL[] urls = new URL[] {file.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                Consumer<String> consumer = mock(Consumer.class);
                actual.addClassDetectedObserver(consumer);
                assertThatNoException().isThrownBy(actual::scan);
                verify(consumer, times(0)).accept(any());
            }

            @Test
            @DisplayName("当扫描时发生输入输出异常时，会抛出 IllegalStateException")
            void givenScanWithIOExceptionThenThrowException() throws IOException {
                File file = Files.createTempFile("UrlClassLoaderScannerTest-", ".jar").toFile();
                file.deleteOnExit();
                URL[] urls = new URL[] {file.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                try (MockedStatic<UrlUtils> mock = mockStatic(UrlUtils.class)) {
                    JarFile jar = mock(JarFile.class);
                    when(jar.getManifest()).thenThrow(new IOException());
                    mock.when(() -> UrlUtils.toJarFile(any())).thenReturn(jar);
                    mock.when(() -> UrlUtils.isJar(any())).thenCallRealMethod();
                    mock.when(() -> UrlUtils.exists(any())).thenCallRealMethod();
                    IllegalStateException exception = catchThrowableOfType(actual::scan, IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("Failed to load JAR file. [url=" + urls[0].toExternalForm() + "]")
                            .getCause()
                            .isInstanceOf(IOException.class);
                }
            }

            @Test
            @DisplayName("当解析类路径时发生文件名异常时，会抛出 IllegalStateException")
            void givenParseWithMalformedURLExceptionThenThrowException() throws IOException {
                URL url = Mockito.mock(URL.class);
                URL[] urls = new URL[] {url};
                URLClassLoader classLoader = new URLClassLoader(urls);
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                try (MockedStatic<UrlUtils> mock = mockStatic(UrlUtils.class)) {
                    JarFile jar = mock(JarFile.class);
                    Manifest manifest = mock(Manifest.class);
                    Attributes attributes = mock(Attributes.class);
                    when(jar.getManifest()).thenReturn(manifest);
                    when(manifest.getMainAttributes()).thenReturn(attributes);
                    when(attributes.containsKey(eq(Attributes.Name.CLASS_PATH))).thenReturn(true);
                    when(attributes.getValue(eq(Attributes.Name.CLASS_PATH))).thenReturn("://fit.lab?q=%");
                    mock.when(() -> UrlUtils.toJarFile(any())).thenReturn(jar);
                    mock.when(() -> UrlUtils.isJar(any())).thenReturn(true);
                    mock.when(() -> UrlUtils.exists(any())).thenReturn(true);
                    IllegalStateException exception = catchThrowableOfType(actual::scan, IllegalStateException.class);
                    assertThat(exception).isNotNull()
                            .hasMessage("Failed to parse class path. [classPath=://fit.lab?q=%]")
                            .getCause()
                            .isInstanceOf(IOException.class);
                }
            }

            @Test
            @DisplayName("当解析类路径一切正常时，整个调用一切正常")
            void givenParseWithoutMalformedURLExceptionThenInvokedCorrectly() throws IOException {
                File file = Files.createTempFile("UrlClassLoaderScannerTest-", ".jar").toFile();
                file.deleteOnExit();
                URL[] urls = new URL[] {file.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);
                UrlClassLoaderScanner actual = new UrlClassLoaderScanner(classLoader, null);
                try (MockedStatic<UrlUtils> mock = mockStatic(UrlUtils.class)) {
                    JarFile jar = mock(JarFile.class);
                    Manifest manifest = mock(Manifest.class);
                    Attributes attributes = mock(Attributes.class);
                    when(jar.getManifest()).thenReturn(manifest);
                    when(manifest.getMainAttributes()).thenReturn(attributes);
                    when(attributes.containsKey(eq(Attributes.Name.CLASS_PATH))).thenReturn(true);
                    when(attributes.getValue(eq(Attributes.Name.CLASS_PATH))).thenReturn("file://fit.lab?q=%");
                    mock.when(() -> UrlUtils.toJarFile(any())).thenReturn(jar);
                    mock.when(() -> UrlUtils.isJar(any())).thenCallRealMethod();
                    mock.when(() -> UrlUtils.exists(any())).thenCallRealMethod();
                    assertThatNoException().isThrownBy(actual::scan);
                }
            }
        }
    }
}
