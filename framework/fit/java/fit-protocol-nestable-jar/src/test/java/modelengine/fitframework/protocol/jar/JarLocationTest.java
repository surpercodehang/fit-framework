/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Locale;

@DisplayName("测试 JarLocation 接口")
final class JarLocationTest {
    private static File FILE;

    @BeforeAll
    static void setupAll() throws IOException {
        FILE = Files.createTempFile("JarLocationTest-", ".jar").toFile().getCanonicalFile();
    }

    @AfterAll
    static void teardownAll() throws IOException {
        Files.delete(FILE.toPath());
    }

    @Nested
    @DisplayName("测试构建构建")
    class BuildTest {
        @Test
        @DisplayName("构建的归档件位置信息包含正确的数据")
        void shouldBuildJarLocationWithCorrectData() {
            JarLocation location = JarLocation.custom().file(FILE).nests(Arrays.asList("n1", "n2")).nest("n3").build();
            assertEquals(FILE, location.file());
            assertEquals(3, location.nests().size());
            assertEquals("n1", location.nests().get(0));
            assertEquals("n2", location.nests().get(1));
            assertEquals("n3", location.nests().get(2));
        }

        @Test
        @DisplayName("当未提供归档件所在文件时，抛出异常")
        void shouldThrowWhenFileIsNotSpecified() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> JarLocation.custom().nest("nest").build());
            assertEquals("The file of a JAR location cannot be null.", exception.getMessage());
        }

        @Test
        @DisplayName("忽略空的嵌套路径")
        void shouldIgnoreEmptyNests() {
            JarLocation location = JarLocation.custom().file(FILE).nests(Arrays.asList(null, "nest", "")).build();
            assertEquals(FILE, location.file());
            assertEquals(1, location.nests().size());
            assertEquals("nest", location.nests().get(0));
        }
    }

    @Nested
    @DisplayName("测试解析过程")
    class ParseTest {
        @Test
        @DisplayName("从文件的 URL 中解析归档件的位置信息")
        void shouldParseFromFileUrl() throws MalformedURLException {
            String url = FILE.toURI().toURL().toExternalForm();
            JarLocation location = JarLocation.parse(url);
            assertEquals(FILE, location.file());
            assertTrue(location.nests().isEmpty());
        }

        @Test
        @DisplayName("从文件直接包装的归档件 URL 中解析归档件的位置信息")
        void shouldParseFromJarDecoratedUrl() throws MalformedURLException {
            String url = "jar:" + FILE.toURI().toURL().toExternalForm() + "!/";
            JarLocation location = JarLocation.parse(url);
            assertEquals(FILE, location.file());
            assertTrue(location.nests().isEmpty());
        }

        @Test
        @DisplayName("从嵌套归档件的 URL 中解析归档件的位置信息")
        void shouldParseFromJarNestedUrl() throws MalformedURLException {
            String url = "jar:" + FILE.toURI().toURL().toExternalForm() + "!/nested.jar!/";
            JarLocation location = JarLocation.parse(url);
            assertEquals(FILE, location.file());
            assertEquals(1, location.nests().size());
            assertEquals("nested.jar", location.nests().get(0));
        }

        @Test
        @DisplayName("从嵌套目录的 URL 中解析归档件的位置信息")
        void shouldParseFromDirectoryInNestedJarUrl() throws MalformedURLException {
            String url = "jar:" + FILE.toURI().toURL().toExternalForm() + "!/nested.jar!/META-INF/";
            JarLocation location = JarLocation.parse(url);
            assertEquals(FILE, location.file());
            assertEquals(2, location.nests().size());
            assertEquals("nested.jar", location.nests().get(0));
            assertEquals("META-INF/", location.nests().get(1));
        }

        @Test
        @DisplayName("当从归档记录的 URL 解析归档件位置信息时，抛出异常")
        void shouldThrowWhenParseFromRegularFileInJar() throws MalformedURLException {
            String url = "jar:" + FILE.toURI().toURL().toExternalForm() + "!/nested.jar";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> JarLocation.parse(url));
            String error = String.format(Locale.ROOT,
                    "The URL to parse does not point to a nestable JAR. [url=%s]", url);
            assertEquals(error, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试重写的 Object 方法")
    class ObjectOverrideTest {
        @Test
        @DisplayName("当包含相同的数据时，返回相同的哈希值")
        void shouldReturnSameHashCodeWhenContainsSameData() {
            JarLocation location1 = JarLocation.custom().file(FILE).nest("nest").build();
            JarLocation location2 = location1.copy().build();
            assertNotSame(location1, location2);
            assertEquals(location1.hashCode(), location2.hashCode());
        }

        @Test
        @DisplayName("当包含不同的数据时，返回不同的哈希值")
        void shouldReturnDifferentHashCodeWhenContainsDifferentData() {
            JarLocation location1 = JarLocation.custom().file(FILE).nest("nest1.jar").build();
            JarLocation location2 = JarLocation.custom().file(FILE).nest("nest2.jar").build();
            assertNotEquals(location1.hashCode(), location2.hashCode());
        }

        @Test
        @DisplayName("当与自身通过 equals 方法比较时，返回 true")
        void shouldReturnTrueWhenEqualsWithSelf() {
            JarLocation location = JarLocation.custom().file(FILE).build();
            assertEquals(location, location);
        }

        @Test
        @DisplayName("当与另一个包含相同数据的实例通过 equals 方法比较时，返回 true")
        void shouldReturnTrueWhenContainsSameData() {
            JarLocation location1 = JarLocation.custom().file(FILE).nest("nest").build();
            JarLocation location2 = location1.copy().build();
            assertNotSame(location1, location2);
            assertEquals(location1, location2);
        }

        @Test
        @DisplayName("当与其他类型的对象通过 equals 比较时，返回 false")
        void shouldFalseWhenEqualsWithObjectInAnotherType() {
            Object obj1 = JarLocation.custom().file(FILE).build();
            Object obj2 = 100;
            assertNotEquals(obj1, obj2);
        }

        @Test
        @DisplayName("返回位置信息的 URL 字符串")
        void shouldReturnUrlStringOfLocation() throws MalformedURLException {
            JarLocation location = JarLocation.custom().file(FILE).nest("n1.jar").nest("n2/").build();
            String actual = location.toString();
            String expected = "jar:" + FILE.toURI().toURL().toExternalForm() + "!/n1.jar!/n2/";
            assertEquals(expected, actual);
        }
    }
}
