/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Locale;

@DisplayName("测试 JarEntryLocation 接口")
final class JarEntryLocationTest {
    private static File FILE;

    @BeforeAll
    static void setupAll() throws IOException {
        FILE = Files.createTempFile("JarEntryLocation-", ".jar").toFile().getCanonicalFile();
    }

    @AfterAll
    static void teardownAll() throws IOException {
        Files.delete(FILE.toPath());
    }

    @Nested
    @DisplayName("测试构建过程")
    class BuildTest {
        private final JarLocation jar = Mockito.mock(JarLocation.class);
        private final String entry = "Main.class";

        @Test
        @DisplayName("构建的归档记录位置信息包含正确的数据")
        void shouldReturnLocationWithCorrectData() {
            JarEntryLocation location = JarEntryLocation.custom().jar(this.jar).entry(this.entry).build();
            assertSame(jar, location.jar());
            assertEquals(entry, location.entry());
        }

        @Test
        @DisplayName("当所属的归档位置信息为 null 时，抛出异常")
        void shouldThrowWhenJarIsNull() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> JarEntryLocation.custom().entry(this.entry).build());
            assertEquals("The owning JAR of an entry cannot be null.", exception.getMessage());
        }

        @Test
        @DisplayName("当归档记录的名称为空字符串时，抛出异常")
        void shouldThrowWhenEntryIsEmpty() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> JarEntryLocation.custom().jar(this.jar).entry("").build());
            assertEquals("The name of entry in JAR cannot be null or an empty string.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试解析过程")
    class ParseTest {
        private final String baseUrl;
        private final String entryName;
        private final String entryUrl;

        ParseTest() {
            try {
                this.baseUrl = "jar:" + FILE.toURI().toURL().toExternalForm() + "!/";
            } catch (MalformedURLException ignored) {
                throw new Error();
            }
            this.entryName = "Main.class";
            this.entryUrl = this.baseUrl + this.entryName;
        }

        @Test
        @DisplayName("返回从 URL 中解析到的归档记录位置信息")
        void shouldReturnParsedEntryLocation() {
            JarEntryLocation location = JarEntryLocation.parse(this.entryUrl);
            assertEquals(FILE, location.jar().file());
            assertTrue(location.jar().nests().isEmpty());
            assertEquals(this.entryName, location.entry());
        }

        @Test
        @DisplayName("当未指定归档记录时，抛出异常")
        void shouldThrowWhenNoEntrySpecified() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> JarEntryLocation.parse(this.baseUrl));
            String error = String.format(Locale.ROOT,
                    "The URL to parse does not point to an entry in a nestable JAR. [url=%s]", this.baseUrl);
            assertEquals(error, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试重写的 Object 方法")
    class ObjectOverrideTest {
        private final JarEntryLocation origin;
        private final JarEntryLocation same;
        private final JarEntryLocation different;

        ObjectOverrideTest() {
            JarLocation jar = JarLocation.custom().file(FILE).build();
            this.origin = JarEntryLocation.custom().jar(jar).entry("Main.class").build();
            this.same = this.origin.copy().build();
            this.different = JarEntryLocation.custom().jar(jar).entry("Demo.class").build();
        }

        @Test
        @DisplayName("当包含的数据相同时，返回相同的哈希值")
        void shouldReturnSameHashCodeWhenContainsSameData() {
            assertEquals(this.same.hashCode(), this.origin.hashCode());
        }

        @Test
        @DisplayName("当包含的数据不同时，返回不同的哈希值")
        void shouldReturnDifferentHashCodeWhenContainsDifferentData() {
            assertNotEquals(this.different.hashCode(), this.origin.hashCode());
        }

        @Test
        @DisplayName("当使用 equals 方法与自身比较时，返回 true")
        void shouldReturnTrueWhenEqualsWithSelf() {
            assertEquals(this.origin, this.origin);
        }

        @Test
        @DisplayName("当使用 equals 方法与包含相同数据的不同实例比较时，返回 true")
        void shouldReturnTrueWhenEqualsWithAnotherContainsSameData() {
            assertEquals(this.origin, this.same);
        }

        @Test
        @DisplayName("当使用 equals 方法与包含不同数据的实例比较时，返回 false")
        void shouldReturnFalseWhenEqualsWithAnotherContainsDifferentData() {
            assertNotEquals(this.origin, this.different);
        }

        @Test
        @DisplayName("当使用 equals 方法与其他类型的对象比较时，返回 false")
        void shouldReturnFalseWhenEqualsWithObjectInAnotherType() {
            Object obj2 = 100;
            assertNotEquals(this.origin, obj2);
        }

        @Test
        @DisplayName("通过 toString 方法返回的字符串为归档记录的 URL 文本")
        void should_return_url_string() throws MalformedURLException {
            String actual = this.origin.toString();
            String expected = String.format(Locale.ROOT, "jar:%s!/Main.class", FILE.toURI().toURL());
            assertEquals(expected, actual);
        }
    }
}
