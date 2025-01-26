/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Permission;

/**
 * 为 {@link NestableJarUrlConnection} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-01-18
 */
@DisplayName("测试 NestableJarUrlConnection 类")
public class NestableJarUrlConnectionTest {
    @Nested
    @DisplayName("通过特殊 URL 参数实例化目标函数")
    class TestSpecialUrlInstantiateTargetFunction {
        @Test
        @DisplayName("当 URL 的 file 字段以 '!/' 结尾时，实例化函数成功")
        void whenFileEndWithSeparatorThenTestMethods() throws MalformedURLException {
            NestableJarUrlConnection urlConnection = new NestableJarUrlConnection(new URL("jar:file:jar!/"));
            assertThat(urlConnection.getJarFileURL().toString()).isEqualTo("jar:file:jar!/");
        }

        @Test
        @DisplayName("当 URL 的 file 字段不包含 '!/' 时，抛出异常")
        void whenFileNotContainsSeparatorThenThrowException() {
            MalformedURLException malformedURLException =
                    catchThrowableOfType(() -> new NestableJarUrlConnection(new URL("jar",
                            null,
                            -1,
                            "file:newJar.class")), MalformedURLException.class);
            assertThat(malformedURLException).hasMessageStartingWith("The URL does not specify a entry in JAR.");
        }
    }

    @Nested
    @DisplayName("通过普通 URL 参数实例化目标函数")
    class TestCommonUrlInstantiateTargetFunction {
        private NestableJarUrlConnection nestableJarUrlConnection;
        private String jarString;
        private final String entryName = "FIT-INF/lib/nested.jar";
        private File file;

        @BeforeEach
        void setup() throws IOException {
            this.file = Files.createTempFile("JarInJar-", ".jar").toFile();

            try (JarBuilder builder = JarBuilder.of(this.file)) {
                builder.store(this.entryName, "FIT-INF-INFO".getBytes(StandardCharsets.UTF_8));
            }
            this.jarString = Jar.from(this.file).location().file().toURI().toURL().toExternalForm();
            URL url = new URL("jar", null, -1, this.jarString + "!/" + this.entryName, new Handler());
            this.nestableJarUrlConnection = new NestableJarUrlConnection(url);
        }

        @AfterEach
        void teardown() throws IOException {
            this.nestableJarUrlConnection.getJarFile().close();
            Files.deleteIfExists(this.file.toPath());
        }

        @Test
        @DisplayName("调用 getJarFileURL() 方法，返回此连接的 Jar 文件的 URL")
        void invokeTheMethodThenReturnTheJarFileUrl() {
            URL jarFileURL = this.nestableJarUrlConnection.getJarFileURL();
            assertThat(jarFileURL.toString()).isEqualTo("jar:" + this.jarString + "!/");
        }

        @Test
        @DisplayName("调用 getEntryName() 方法，返回此连接的条目名称")
        void invokeTheMethodThenReturnTheEntryName() {
            String expectEntryName = this.nestableJarUrlConnection.getEntryName();
            assertThat(expectEntryName).isEqualTo(entryName);
        }

        @Test
        @DisplayName("调用 getURL() 方法，返回 URL 值存在")
        void invokeTheMethodThenReturnUrlValue() {
            URL url = this.nestableJarUrlConnection.getURL();
            assertThat(url.toString()).isEqualTo("jar:" + this.jarString + "!/" + entryName);
        }

        @Test
        @DisplayName("调用 getJarFile() 方法，返回此连接的JAR文件不为空")
        void invokeJarFileMethodThenReturnIsNotNull() throws IOException {
            NestableJarFile jarFile = this.nestableJarUrlConnection.getJarFile();
            assertThat(jarFile).isNotNull();
        }

        @Test
        @DisplayName("调用 getJarEntry() 方法，返回此连接的 JAR 条目对象存在")
        void invokeTheMethodThenReturnJarEntry() throws IOException {
            NestableJarFile.Entry jarEntry = this.nestableJarUrlConnection.getJarEntry();
            assertThat(jarEntry.toString()).isEqualTo(entryName);
        }

        @Test
        @DisplayName("调用 getDoInput() 方法，返回此 URLConnection 的 doInput 标志的值为 true")
        void invokeTheMethodThenReturnTrue() {
            boolean doInput = this.nestableJarUrlConnection.getDoInput();
            assertThat(doInput).isTrue();
        }

        @Test
        @DisplayName("调用 setDoInput(boolean) 方法，给定参数值为 false，抛出异常")
        void givenParameterFalseWhenInvokeSetDoInputMethodThenThrowException() {
            IllegalStateException illegalStateException =
                    catchThrowableOfType(() -> this.nestableJarUrlConnection.setDoInput(false),
                            IllegalStateException.class);
            assertThat(illegalStateException).hasMessage("The doInput is always be true.");
        }

        @Test
        @DisplayName("调用 setDoInput(boolean) 方法，给定参数值为 true，执行成功")
        void givenParameterTrueWhenInvokeSetDoInputMethodThenThrowNothing() {
            assertDoesNotThrow(() -> this.nestableJarUrlConnection.setDoInput(true));
        }

        @Test
        @DisplayName("调用 getDoOutput() 方法，返回此 URLConnection 的 doOutput 标志的值为 false")
        void invokeGetDoOutputMethodThenReturnFalse() {
            boolean doOutput = this.nestableJarUrlConnection.getDoOutput();
            assertThat(doOutput).isFalse();
        }

        @Test
        @DisplayName("调用 setDoOutput(boolean) 方法，给定参数值为 true，抛出异常")
        void givenParameterTrueWhenInvokeSetDoOutputThenThrowException() {
            IllegalStateException illegalStateException =
                    catchThrowableOfType(() -> this.nestableJarUrlConnection.setDoOutput(true),
                            IllegalStateException.class);
            assertThat(illegalStateException).hasMessage("The doOutput is always be false.");
        }

        @Test
        @DisplayName("调用 setDoOutput(boolean) 方法，给定参数值为 false，执行成功")
        void givenParameterFalseWhenInvokeSetDoOutputThenThrowNothing() {
            assertDoesNotThrow(() -> this.nestableJarUrlConnection.setDoOutput(false));
        }

        @Test
        @DisplayName("调用 getInputStream() 方法，返回输入流与写入数据对应")
        void invokeGetJarEntryMethodThenReturnIsEmpty() throws IOException {
            try (InputStream inputStream = this.nestableJarUrlConnection.getInputStream()) {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                assertThat(result.toString()).isEqualTo("FIT-INF-INFO");
            }
        }

        @Test
        @DisplayName("调用 getOutputStream() 方法，返回写入此连接的输出流，抛出异常")
        void invokeGetOutputStreamMethodThenThrowException() {
            IOException ioException =
                    catchThrowableOfType(() -> this.nestableJarUrlConnection.getOutputStream(), IOException.class);
            assertThat(ioException).hasMessage("Cannot write data into a nested JAR.");
        }

        @Test
        @DisplayName("调用 getContentType() 方法，返回 content-type 头字段的值")
        void invokeTheMethodThenReturnContentTypeValue() {
            String contentType = this.nestableJarUrlConnection.getContentType();
            assertThat(contentType).isEqualTo("application/java-archive");
        }

        @Test
        @DisplayName("调用 getConnectTimeout() 方法，返回链接超时设置值为 0")
        void invokeGetConnectTimeoutMethodThenReturnZero() {
            int connectTimeout = this.nestableJarUrlConnection.getConnectTimeout();
            assertThat(connectTimeout).isEqualTo(0);
        }

        @Test
        @DisplayName("建立连接，调用 getContentLengthLong() 方法，将 content-length 头字段的值作为 long 类型返回")
        void invokeGetContentLengthLongMethodThenReturnZero() throws IOException {
            this.nestableJarUrlConnection.connect();
            long contentLengthLong = this.nestableJarUrlConnection.getContentLengthLong();
            assertThat(contentLengthLong).isEqualTo(12L);
        }

        @Test
        @DisplayName("调用 getPermission() 方法，返回建立此对象所表示的连接所需的权限值为 read")
        void invokeTheMethodThenReturnThePermissionValue() throws IOException {
            Permission permission = this.nestableJarUrlConnection.getPermission();
            assertThat(permission).isNotNull();
            assertThat(permission.getActions()).isEqualTo("read");
        }

        @Test
        @DisplayName("调用 getReadTimeout() 方法，返回读取超时的设置值为 0")
        void invokeGetReadTimeoutMethodThenReturnZero() {
            int readTimeout = this.nestableJarUrlConnection.getReadTimeout();
            assertThat(readTimeout).isEqualTo(0);
        }

        @Test
        @DisplayName("调用 getExpiration() 方法，返回过期头字段的值为 0")
        void invokeGetExpirationMethodThenReturnZero() {
            long expiration = this.nestableJarUrlConnection.getExpiration();
            assertThat(expiration).isEqualTo(0);
        }

        @Test
        @DisplayName("调用 getDate() 方法，返回日期头字段的值为 0")
        void invokeGetDateMethodThenReturnZero() {
            long date = this.nestableJarUrlConnection.getDate();
            assertThat(date).isEqualTo(0);
        }

        @Test
        @DisplayName("未修改条目值，调用获取上次调用时间，返回 0")
        void getLastModifiedTimeWhenNotChangedEntryThenReturn0() {
            long lastModified = this.nestableJarUrlConnection.getLastModified();
            assertThat(lastModified).isEqualTo(0);
        }
    }
}
