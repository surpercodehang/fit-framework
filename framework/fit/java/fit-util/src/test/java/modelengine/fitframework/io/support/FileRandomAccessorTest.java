/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;

@DisplayName("测试 FileRandomAccessor 类")
class FileRandomAccessorTest {
    private static final String FILE_NAME_PREFIX = "RandomAccess-";
    private static final String FILE_NAME_SUFFIX = ".test";

    private static File randomAccessorFile;

    @BeforeAll
    static void setup() throws IOException {
        randomAccessorFile = Files.createTempFile(FILE_NAME_PREFIX, FILE_NAME_SUFFIX).toFile();
        try (OutputStream out = Files.newOutputStream(randomAccessorFile.toPath())) {
            for (int i = 0; i < 128; i++) {
                out.write(i);
            }
        }
    }

    @AfterAll
    static void teardown() throws IOException {
        Files.delete(randomAccessorFile.toPath());
    }

    @Nested
    @DisplayName("构造实例")
    class Construct {
        @Test
        @DisplayName("当文件为 null 时抛出异常")
        void should_throw_when_file_is_null() {
            // noinspection resource
            IllegalArgumentException cause =
                    catchThrowableOfType(() -> new FileRandomAccessor(null), IllegalArgumentException.class);
            assertThat(cause).isNotNull().hasMessage("The file to access cannot be null.");
        }

        @Test
        @DisplayName("当文件不能被标准化时抛出异常")
        void should_throw_when_file_is_not_canonical() {
            File file = new File("\u0000");
            // noinspection resource
            IOException cause = catchThrowableOfType(() -> new FileRandomAccessor(file), IOException.class);
            assertThat(cause).isNotNull();
        }

        @Test
        @DisplayName("当文件不存在时抛出异常")
        void should_throw_when_file_not_exists() {
            File file = new File(UUID.randomUUID().toString());
            while (file.exists()) {
                file = new File(UUID.randomUUID().toString());
            }
            File nonExist = file;
            // noinspection resource
            IOException cause = catchThrowableOfType(() -> new FileRandomAccessor(nonExist), IOException.class);
            assertThat(cause).isNotNull();
        }
    }

    @Nested
    @DisplayName("读取文件内容")
    class Read {
        private FileRandomAccessor access;

        @BeforeEach
        void setup() throws IOException {
            this.access = new FileRandomAccessor(randomAccessorFile);
        }

        @AfterEach
        void teardown() throws IOException {
            this.access.close();
        }

        @Test
        @DisplayName("应读取到文件中指定位置的数据")
        void should_return_data_at_expected_position() throws IOException {
            byte[] buffer = this.access.read(10, 2);
            assertEquals(2, buffer.length);
            assertEquals(10, Byte.toUnsignedInt(buffer[0]));
            assertEquals(11, Byte.toUnsignedInt(buffer[1]));
        }

        @Test
        @DisplayName("当 offset 为负数时抛出异常")
        void should_throw_when_offset_is_negative() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> this.access.read(-1, 10));
            assertEquals("The offset of data to access is out of bound. [offset=-1, total=128]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当 offset 超出文件大小时抛出异常")
        void should_throw_when_offset_is_greater_than_size() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> this.access.read(129, 0));
            assertEquals("The offset of data to access is out of bound. [offset=129, total=128]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当 length 为负数时抛出异常")
        void should_throw_when_length_is_negative() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> this.access.read(0, -1));
            assertEquals("The length of data to access is out of bounds. [length=-1, offset=0, total=128]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当 offset 与 length 的和大于 size 时抛出异常")
        void should_throw_when_sum_of_offset_and_length_is_greater_than_size() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> this.access.read(0, 129));
            assertEquals("The length of data to access is out of bounds. [length=129, offset=0, total=128]",
                    exception.getMessage());
        }
    }

    @Test
    @DisplayName("当文件相同时，哈希值也相同")
    void should_return_same_hash_code_when_contains_same_data() throws IOException {
        try (FileRandomAccessor access1 = new FileRandomAccessor(randomAccessorFile);
             FileRandomAccessor access2 = new FileRandomAccessor(randomAccessorFile)) {
            assertEquals(access1.hashCode(), access2.hashCode());
        }
    }

    @Test
    @DisplayName("当比较同一个访问程序时，返回 true")
    void should_return_true_when_equals_with_itself() throws IOException {
        try (FileRandomAccessor access = new FileRandomAccessor(randomAccessorFile)) {
            assertEquals(access, access);
        }
    }

    @Test
    @DisplayName("当文件相同时，哈希值也相同")
    void should_return_true_when_contains_same_data() throws IOException {
        try (FileRandomAccessor access1 = new FileRandomAccessor(randomAccessorFile);
             FileRandomAccessor access2 = new FileRandomAccessor(randomAccessorFile)) {
            assertEquals(access1, access2);
        }
    }

    @Test
    @DisplayName("当与 null 比较时，返回 false")
    void should_return_false_when_equals_with_null() throws IOException {
        try (FileRandomAccessor access = new FileRandomAccessor(randomAccessorFile)) {
            assertNotEquals(access, null);
        }
    }

    @Test
    @DisplayName("toString 方法应返回友好的信息")
    void should_return_friendly_message_from_to_string() throws IOException {
        try (FileRandomAccessor access = new FileRandomAccessor(randomAccessorFile)) {
            String expected = randomAccessorFile.getCanonicalPath();
            String actual = access.toString();
            assertEquals(expected, actual);
        }
    }
}
