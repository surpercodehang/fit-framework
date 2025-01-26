/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Permission;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.stream.Stream;

/**
 * 为 {@link NestableJarFile} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-01-17
 */
@DisplayName("测试 NestableJarFile 工具类")
public class NestableJarFileTest {
    @Nested
    @DisplayName("实例化类对象：NestableJarFile")
    class TestInstantiateClassObjectNestableJarFile {
        private NestableJarFile nestableJarFile;
        private File file;

        @BeforeEach
        void setup() throws IOException {
            this.file = Files.createTempFile("JarInJar-", ".jar").toFile();

            try (JarBuilder builder = JarBuilder.of(this.file)) {
                builder.store("FIT-INF/lib/nested.jar", "FIT-INF-INFO".getBytes(StandardCharsets.UTF_8));
            }
            Jar jar = Jar.from(this.file);
            this.nestableJarFile = new NestableJarFile(jar);
        }

        @AfterEach
        void teardown() throws IOException {
            this.nestableJarFile.close();
            Files.deleteIfExists(this.file.toPath());
        }

        @Nested
        @DisplayName("调用 getEntry(String) 方法")
        class WhenInvokeGetEntryMethod {
            @Test
            @DisplayName("给定空的 String 值，返回值为 null")
            void givenEmptyStringValueThenReturnNull() {
                NestableJarFile.Entry jarEntry = nestableJarFile.getEntry("");
                assertThat(jarEntry).isNull();
            }

            @Test
            @DisplayName("当 String 不为空字符串，但没有相应名称的记录时，返回 null")
            void givenNotEmptyButNotExistStringValueThenReturnNull() {
                NestableJarFile.Entry jarEntry = nestableJarFile.getEntry("NOT-EXIST-INFO");
                assertThat(jarEntry).isNull();
            }

            @Test
            @DisplayName("给定非空的 String 值为对应的目录时，返回该目录类型的记录")
            void givenStringValueCorrespondCatalogueThenReturnCatalogueInfo() {
                NestableJarFile.Entry jarEntry = nestableJarFile.getEntry("FIT-INF/");
                assertThat(jarEntry).isNotNull();
                assertThat(jarEntry.toString()).isEqualTo("FIT-INF/");
            }

            @Test
            @DisplayName("给定非空的 String 值为对应的文件时，返回该文件类型的记录")
            void givenStringValueCorrespondFileThenReturnFileInfo() {
                NestableJarFile.Entry jarEntry = nestableJarFile.getEntry("FIT-INF/lib/nested.jar");
                assertThat(jarEntry).isNotNull();
                assertThat(jarEntry.toString()).isEqualTo("FIT-INF/lib/nested.jar");
            }
        }

        @Test
        @DisplayName("调用 entries() 方法，返回值不为 null")
        void invokeEntriesMethodThenReturnNotNull() {
            Enumeration<JarEntry> entries = nestableJarFile.entries();
            assertThat(entries).isNotNull();
            boolean entriesHasElement = false;
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.equals("FIT-INF/lib/")) {
                    entriesHasElement = true;
                    break;
                }
            }
            assertThat(entriesHasElement).isTrue();
        }

        @Test
        @DisplayName("调用 stream() 方法，返回获取流值不为空")
        void whenInvokeStreamMethodThenReturnIsNotNull() {
            Stream<java.util.jar.JarEntry> stream = nestableJarFile.stream();
            assertThat(stream).isNotNull();
            assertThat(stream.count()).isGreaterThan(0);
        }

        @Test
        @DisplayName("调用 getInputStream(ZipEntry) 方法，给定 Entry 类型的 ZipEntry，返回值为空")
        void givenEntryInstanceWhenInvokeTheMethodThenReturnNotNull() throws IOException {
            try (InputStream inputStream = nestableJarFile.getInputStream(
                    nestableJarFile.getEntry("FIT-INF/lib/nested.jar"))) {
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
        @DisplayName("调用 getComment() 方法，返回获取 nestableJarFile 的注释字符串不为空")
        void invokeGetCommentMethodThenReturnIsNotNull() {
            String comment = nestableJarFile.getComment();
            assertThat(comment).isNotNull();
        }

        @Test
        @DisplayName("调用 getName() 方法，返回文件路径")
        void invokeGetNameThenReturnThePath() {
            String name = nestableJarFile.getName();
            assertThat(name).isNotNull();
            assertThat(name).endsWith(".jar");
        }

        @Test
        @DisplayName("调用 getPermission() 方法，返回其权限值为 read")
        void invokeGetPermissionThenReturnPermissionIsRead() {
            Permission permission = nestableJarFile.getPermission();
            assertThat(permission).isNotNull();
            assertThat(permission.getActions()).isEqualTo("read");
        }

        @Nested
        @DisplayName("实例化类对象：NestableJarFile.Entry")
        class EntryInstantiationTest {
            private NestableJarFile.Entry entry;

            @BeforeEach
            void setup() {
                this.entry = nestableJarFile.getEntry("FIT-INF");
            }

            @Test
            @DisplayName("调用 getName() 方法，返回条目的名字存在")
            void getTheNameIsExist() {
                String name = entry.getName();
                assertThat(name).isEqualTo("FIT-INF/");
            }

            @Test
            @DisplayName("调用 getTime() 方法，返回条目的最后修改时间大于 0")
            void getTheTimeIsGreaterThanZero() {
                long time = entry.getTime();
                assertThat(time).isGreaterThan(0L);
            }

            @Test
            @DisplayName("调用 getSize() 方法，返回条目未压缩数据大小为 0")
            void getTheSizeIsZero() {
                long size = entry.getSize();
                assertThat(size).isEqualTo(0L);
            }

            @Test
            @DisplayName("调用 getCompressedSize() 方法，返回条目压缩数据大小大于 0")
            void getCompressedSizeIsGreaterThanZero() {
                long compressedSize = entry.getCompressedSize();
                assertThat(compressedSize).isGreaterThan(0L);
            }

            @Test
            @DisplayName("调用 getCrc() 方法，返回条目的未压缩条目数据的 CRC-32 校验和为 0")
            void getCrcIsZero() {
                long crc = entry.getCrc();
                assertThat(crc).isEqualTo(0L);
            }

            @Test
            @DisplayName("调用 getMethod() 方法，返回条目的压缩方法数值大于 0")
            void getMethodIsExist() {
                int method = entry.getMethod();
                assertThat(method).isGreaterThan(0);
            }

            @Test
            @DisplayName("调用 getComment() 方法，返回条目的注释字符串不为空")
            void getCommentIsExist() {
                String comment = entry.getComment();
                assertThat(comment).isNotNull();
            }

            @Test
            @DisplayName("调用 isDirectory() 方法，返回值为 true")
            void isDirectory() {
                boolean directory = entry.isDirectory();
                assertThat(directory).isTrue();
            }

            @Test
            @DisplayName("调用 getExtra() 方法，返回条目的额外字段数据为空")
            void getExtraIsEmpty() {
                byte[] extra = entry.getExtra();
                assertThat(extra.length).isEqualTo(0);
            }

            @Test
            @DisplayName("调用 getInputStream() 方法，获取一个输入流，读取条目中的内容值为空")
            void getInputStreamIsEmpty() throws IOException {
                try (InputStream inputStream = entry.getInputStream()) {
                    assertThat(inputStream).isEmpty();
                }
            }
        }
    }
}
