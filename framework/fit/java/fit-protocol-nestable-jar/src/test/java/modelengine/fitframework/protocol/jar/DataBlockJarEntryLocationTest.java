/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 为 {@link JarEntryLocation} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-01-29
 */
@DisplayName("测试 DefaultJarEntryLocation 类")
public class DataBlockJarEntryLocationTest {
    @Nested
    @DisplayName("实例化 DefaultJarEntryLocation 类对象")
    class InitializeDataBlockJarEntryLocation {
        private JarLocation jar;
        private JarEntryLocation jarEntryLocation;
        private File file;
        private List<String> nests;
        private final String entryName = "FIT-entry";

        @BeforeEach
        void setup() throws IOException {
            this.file = Files.createTempFile("JarInJar-", ".jar").toFile();

            try (JarBuilder builder = JarBuilder.of(this.file)) {
                builder.store("FIT-INF/lib/nested.jar", "FIT-INF-INFO".getBytes(StandardCharsets.UTF_8));
            }
            this.nests = new LinkedList<>();
            this.nests.add("FIT-TEST");
            this.jar = JarLocation.custom().file(this.file).nests(this.nests).build();
            this.jarEntryLocation = JarEntryLocation.custom().jar(this.jar).entry(this.entryName).build();
        }

        @AfterEach
        void teardown() throws IOException {
            Files.deleteIfExists(this.file.toPath());
        }

        @Test
        @DisplayName("实例化时，给定空的条目值，抛出异常")
        void givenEmptyEntryWhenInitializeThenThrowException() {
            IllegalArgumentException illegalArgumentException = catchThrowableOfType(() -> JarEntryLocation.custom()
                    .jar(JarLocation.custom().file(this.file).nests(this.nests).build())
                    .entry("")
                    .build(), IllegalArgumentException.class);
            assertThat(illegalArgumentException).isNotNull();
        }

        @Test
        @DisplayName("获取 jar 与给定值相等")
        void theJarShouldBeEqualsToTheGivenJar() {
            JarLocation jarLocation = this.jarEntryLocation.jar();
            assertThat(jarLocation.toString()).endsWith(file.getName() + "!/FIT-TEST!/");
        }

        @Test
        @DisplayName("获取条目信息与给定条目值相等")
        void theEntryShouldBeEqualsToTheGivenEntry() {
            String entry = this.jarEntryLocation.entry();
            assertThat(entry).isEqualTo(this.entryName);
        }

        @Test
        @DisplayName("获取 Url 信息于给定 Url 值相等")
        void theUrlShouldBeEqualsToTheGivenUrl() throws MalformedURLException {
            URL url = this.jarEntryLocation.toUrl();
            assertThat(url.toString()).endsWith(file.getName() + "!/FIT-TEST!/FIT-entry");
        }

        @Nested
        @DisplayName("测试 equals 方法")
        class TestEquals {
            @SuppressWarnings("EqualsWithItself")
            @Test
            @DisplayName("当目标值相同时，返回 true")
            void givenSameValueThenReturnTrue() {
                boolean equals = jarEntryLocation.equals(jarEntryLocation);
                assertThat(equals).isTrue();
            }

            @Test
            @DisplayName("当目标类型不同时，返回 false")
            void givenDifferentClassThenReturnFalse() {
                Object parameter = JarEntryLocation.custom()
                        .jar(JarLocation.custom().file(file).nests(nests).build())
                        .entry("entry")
                        .build();
                boolean equals = jarEntryLocation.equals(parameter);
                assertThat(equals).isFalse();
            }

            @Nested
            @DisplayName("目标类型相同")
            class GivenTheSameClass {
                @Test
                @DisplayName("给定条目值不同时，返回 false")
                void givenDifferentEntryThenReturnFalse() {
                    JarEntryLocation parameter = JarEntryLocation.custom().jar(jar).entry("entry").build();
                    boolean equals = jarEntryLocation.equals(parameter);
                    assertThat(equals).isFalse();
                }

                @Test
                @DisplayName("给定默认 Jar 位置信息值不同时，返回 false")
                void givenDifferentDefaultJarLocationThenReturnFalse() {
                    JarEntryLocation parameter = JarEntryLocation.custom()
                            .jar(JarLocation.custom().file(file).build())
                            .entry(entryName)
                            .build();
                    boolean equals = jarEntryLocation.equals(parameter);
                    assertThat(equals).isFalse();
                }

                @Test
                @DisplayName("给定条目值与默认 Jar 位置信息值都相同时，返回 true")
                void givenSameEntryAndDefaultJarLocationThenReturnTrue() {
                    JarEntryLocation parameter = jarEntryLocation.copy().build();
                    boolean equals = jarEntryLocation.equals(parameter);
                    assertThat(equals).isTrue();
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("当目标值为 null 时，返回 false")
            void givenNullValueThenReturnFalse() {
                boolean equals = jarEntryLocation.equals(null);
                assertThat(equals).isFalse();
            }
        }

        @Test
        @DisplayName("以当前记录作为 JAR 使用时的位置，返回与期望值相等")
        void theAsJarThenReturnShouldEqualsToExceptionResult() throws MalformedURLException {
            JarLocation jarLocation = this.jarEntryLocation.asJar();
            assertThat(jarLocation.toString()).isEqualTo(this.jar.toUrl() + "FIT-entry!/");
        }

        @Test
        @DisplayName("获取哈希值不等于 0")
        void theHashCodeNotEqualsToZero() {
            int hashCode = this.jarEntryLocation.hashCode();
            assertThat(hashCode).isNotEqualTo(0);
        }

        @Nested
        @DisplayName("解析 JAR 中记录的位置信息")
        class ParseJarLocationInfo {
            @Test
            @DisplayName("给定不存在的 Url 字符串值，抛出异常")
            void givenNotExistUrlValueThenThrowException() {
                String url = "notExistUrl";
                IllegalArgumentException exception =
                        assertThrows(IllegalArgumentException.class, () -> JarEntryLocation.parse(url));
                String error = String.format(Locale.ROOT, "Unsupported protocol of JAR or entry url. [url=%s]", url);
                assertEquals(error, exception.getMessage());
            }

            @Test
            @DisplayName("给定有效的 Url 值，解析返回值存在")
            void givenValidUrlValueThenParseValueExist() {
                JarEntryLocation parse = JarEntryLocation.parse(jarEntryLocation.toString());
                assertThat(parse.toString()).isEqualTo(jarEntryLocation.toString());
            }
        }

        @Nested
        @DisplayName("测试 Builder 类")
        class BuilderTest {
            @Nested
            @DisplayName("初始化 Builder 类的新实例")
            class InitializeBuilderInstance {
                @Test
                @DisplayName("当拷贝原始位置时，返回新的实例，包含与原始值相同的信息")
                void shouldReturnNewInstanceWithSameDataWithCopy() {
                    JarEntryLocation another = jarEntryLocation.copy().build();
                    assertThat(another).isNotNull();
                    assertThat(another).isEqualTo(jarEntryLocation);
                }

                @Test
                @DisplayName("当未指定所属 JAR 时，抛出异常")
                void shouldThrowWhenJarIsNotSpecified() {
                    assertThatThrownBy(() -> JarEntryLocation.custom()
                            .entry(jarEntryLocation.entry())
                            .build()).isInstanceOf(IllegalArgumentException.class)
                            .hasMessage("The owning JAR of an entry cannot be null.");
                }

                @Test
                @DisplayName("当未指定记录名称时，抛出异常")
                void shouldThrowWhenEntryIsNotSpecified() {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> JarEntryLocation.custom().jar(jarEntryLocation.jar()).build());
                    String error = "The name of entry in JAR cannot be null or an empty string.";
                    assertEquals(error, exception.getMessage());
                }
            }

            @Test
            @DisplayName("给定 JarLocation 值与 entry 值，构建 Builder 的实例的参数值与给定值相同")
            void givenJarLocationAndEntryValueThenReturnNewBuilderInstance() {
                JarLocation location = jarEntryLocation.jar();
                String builderEntry = "Builder-Entry";
                JarEntryLocation.Builder builder = JarEntryLocation.custom();
                builder.entry(builderEntry);
                builder.jar(location);
                JarEntryLocation build = builder.build();
                assertThat(build.entry()).isEqualTo(builderEntry);
                assertThat(build.jar()).isEqualTo(location);
            }
        }

        @Nested
        @DisplayName("测试 JarLocation")
        class TestJarLocation {
            @Test
            @DisplayName("给定空的初始值，构建新的实例抛出异常")
            void givenEmptyValueWhenBuildNewInstanceThenThrowException() {
                JarLocation.Builder custom = JarLocation.custom();
                IllegalArgumentException illegalArgumentException =
                        AssertionsForClassTypes.catchThrowableOfType(custom::build, IllegalArgumentException.class);
                assertThat(illegalArgumentException).hasMessage("The file of a JAR location cannot be null.");
            }

            @Test
            @DisplayName("给定有效初始值，构建新的实例成功")
            void givenValidInitialValueThenBuildNewInstanceSuccessfully() {
                JarLocation.Builder copy = jar.copy();
                assertDoesNotThrow(copy::build);
            }
        }
    }
}
