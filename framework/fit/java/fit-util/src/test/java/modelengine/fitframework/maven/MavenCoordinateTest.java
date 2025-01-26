/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.maven;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.maven.support.DefaultMavenCoordinate;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * {@link MavenCoordinate} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2022-12-29
 */
@DisplayName("测试 MavenCoordinate 工具类")
class MavenCoordinateTest {
    private static final String GROUP_ID = "modelengine.fitframework";
    private static final String ARTIFACT_ID = "fit-util";
    private static final String VERSION = "3.0.0-SNAPSHOT";

    private final MavenCoordinate mavenCoordinate = new DefaultMavenCoordinate(GROUP_ID, ARTIFACT_ID, VERSION);

    @Nested
    @DisplayName("当提供一个 maven 坐标")
    class GivenMavenCoordinate {
        @Test
        @DisplayName("返回一个 groupId 值")
        void ThenGetGroupId() {
            String groupId = mavenCoordinate.groupId();
            assertThat(groupId).isEqualTo(GROUP_ID);
        }

        @Test
        @DisplayName("返回一个 artifactId 值")
        void ThenGetArtifactId() {
            String artifactId = mavenCoordinate.artifactId();
            assertThat(artifactId).isEqualTo(artifactId);
        }

        @Test
        @DisplayName("返回一个 version 值")
        void ThenGetVersion() {
            String version = mavenCoordinate.version();
            assertThat(version).isEqualTo(VERSION);
        }
    }

    @Test
    @DisplayName("当提供 groupId、artifactId、version 三个值， 返回一个 maven 坐标对象")
    void givenConditionThenNewObject() {
        MavenCoordinate coordinate = MavenCoordinate.create(GROUP_ID, ARTIFACT_ID, VERSION);
        assertThat(coordinate.artifactId()).isEqualTo(mavenCoordinate.artifactId());
        assertThat(coordinate.version()).isEqualTo(mavenCoordinate.version());
    }

    @Nested
    @DisplayName("当提供 groupId、artifactId、version 字符串时")
    class WhenParse {
        @Test
        @DisplayName("字符串格式正确，返回一个 maven 坐标对象")
        void formatCorrectThenParseOK() {
            String text = GROUP_ID + ":" + ARTIFACT_ID + ":" + VERSION;
            MavenCoordinate coordinate = MavenCoordinate.parse(text);
            assertThat(coordinate).isNotNull();
            assertThat(coordinate.version()).isEqualTo(mavenCoordinate.version());
        }

        @Test
        @DisplayName("字符串格式错误，抛出一个非法参数异常")
        void formatInCorrectThenThrowException() {
            String text = GROUP_ID + ";" + ARTIFACT_ID + ";" + VERSION;
            assertThatThrownBy(() -> MavenCoordinate.parse(text)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("当提供 Jar 包时")
    class WhenRead {
        private File tempDirectory;

        @AfterEach
        void teardown() {
            FileUtils.delete(tempDirectory);
        }

        @Test
        @DisplayName("Jar 包内包含 pom 文件，有父依赖，返回一个 maven 坐标")
        void givenJarIncludePomThenReturnMavenCoordinate() throws IOException {
            final File jarFile = this.getJar("META-INF/maven/pom.xml", "pom/pomIncludeParent.txt");
            Jar jar = Jar.from(jarFile);
            final MavenCoordinate coordinate = MavenCoordinateReader.read(jar);
            assertThat(coordinate).isNotNull();
            assertThat(coordinate.groupId()).isEqualTo(GROUP_ID);
            assertThat(coordinate.artifactId()).isEqualTo(ARTIFACT_ID);
            assertThat(coordinate.version()).isEqualTo(VERSION);
        }

        @Test
        @DisplayName("Jar 包内包含 pom 文件，没有父依赖，返回一个 maven 坐标")
        void givenJarIncludePomAndParentErrorThenReturnNull() throws IOException {
            final File jarFile = getJar("META-INF/maven/pom.xml", "pom/pomExcludeParent.txt");
            Jar jar = Jar.from(jarFile);
            final MavenCoordinate coordinate = MavenCoordinateReader.read(jar);
            assertThat(coordinate).isNotNull();
            assertThat(coordinate.groupId()).isEqualTo(GROUP_ID);
            assertThat(coordinate.artifactId()).isEqualTo(ARTIFACT_ID);
            assertThat(coordinate.version()).isEqualTo(VERSION);
        }

        @Test
        @DisplayName("Jar 包内不包含 pom 文件，返回 null")
        void givenJarExcludePomThenReturnNull() throws IOException {
            final File jarFile = this.getJar("META-INF/maven/excel.xml", "pom/pomIncludeParent.txt");
            Jar jar = Jar.from(jarFile);
            final MavenCoordinate coordinate = MavenCoordinateReader.read(jar);
            assertThat(coordinate).isNull();
        }

        @Test
        @DisplayName("Jar 包内包含 pom 文件，但文件分割符不是 '/'，返回 null")
        void givenJarIncludePomAndOtherSeparatorThenReturnNull() throws IOException {
            final File jarFile = getJar("META-INF/maven/jar\\pom.xml", "pom/pomIncludeParent.txt");
            Jar jar = Jar.from(jarFile);
            final MavenCoordinate coordinate = MavenCoordinateReader.read(jar);
            assertThat(coordinate).isNull();
        }

        /**
         * 表示生成临时 jar 文件。
         * <p> jar 临时文件结构：
         * META_INF
         * META_INF/maven
         * META_INF/maven/pom.xml </p>
         *
         * @param name 生成文件全名称。
         * @param location 读取 pom 文件的位置。
         * @return 返回 jar 文件。
         * @throws IOException 创建文件过程发生异常。
         */
        private File getJar(String name, String location) throws IOException {
            tempDirectory = Files.createTempDirectory("MavenCoordinateTest-").toFile().getCanonicalFile();
            File jarFile = new File(tempDirectory, "MavenCoordinateTest.jar");
            try (FileOutputStream out = new FileOutputStream(jarFile)) {
                out.write(this.zip(MapBuilder.<String, byte[]>get()
                        .put("META-INF", null)
                        .put("META-INF/maven", null)
                        .put(name, this.bytes(location))
                        .build()));
            }
            return jarFile;
        }

        private byte[] zip(Map<String, byte[]> entries) throws IOException {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                try (ZipOutputStream zip = new ZipOutputStream(out)) {
                    for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                        this.entry(zip, entry.getKey(), entry.getValue());
                    }
                }
                return out.toByteArray();
            }
        }

        private void entry(ZipOutputStream zip, String name, byte[] data) throws IOException {
            if (data == null) {
                ZipEntry entry = new ZipEntry(name + "/");
                zip.putNextEntry(entry);
            } else {
                ZipEntry entry = new ZipEntry(name);
                zip.putNextEntry(entry);
                zip.write(data);
            }
        }

        private byte[] bytes(String key) throws IOException {
            List<byte[]> buffers = new LinkedList<>();
            int total = 0;
            try (InputStream in = IoUtils.resource(IoUtils.class.getClassLoader(), key)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) >= 0) {
                    buffers.add(Arrays.copyOf(buffer, read));
                    total += read;
                }
            }
            byte[] bytes = new byte[total];
            int offset = 0;
            for (byte[] buffer : buffers) {
                System.arraycopy(buffer, 0, bytes, offset, buffer.length);
                offset += buffer.length;
            }
            return bytes;
        }
    }

    @Nested
    @DisplayName("测试方法：equals(Object obj)")
    class TestEquals {
        @Test
        @DisplayName("提供同类型 maven 坐标类，相同属性，返回 true")
        void givenSameTypeClassAndSameAttributeThenReturnTrue() {
            final DefaultMavenCoordinate mavenCoordinate1 = new DefaultMavenCoordinate("", ARTIFACT_ID, VERSION);
            final DefaultMavenCoordinate mavenCoordinate2 = new DefaultMavenCoordinate("", ARTIFACT_ID, VERSION);
            final boolean equals = mavenCoordinate1.equals(mavenCoordinate2);
            assertThat(equals).isTrue();
        }

        @Test
        @DisplayName("提供同类型 maven 坐标类，组织 id 属性不同，返回 false")
        void givenSameTypeClassAndDiffGroupIdThenReturnFalse() {
            final DefaultMavenCoordinate mavenCoordinate1 = new DefaultMavenCoordinate("", ARTIFACT_ID, VERSION);
            final boolean equals = mavenCoordinate1.equals(mavenCoordinate);
            assertThat(equals).isFalse();
        }

        @Test
        @DisplayName("提供同类型 maven 坐标类，模块 id 属性不同，返回 false")
        void givenSameTypeClassAndDiffArtifactIdThenReturnFalse() {
            final DefaultMavenCoordinate mavenCoordinate1 = new DefaultMavenCoordinate(GROUP_ID, "", VERSION);
            final boolean equals = mavenCoordinate1.equals(mavenCoordinate);
            assertThat(equals).isFalse();
        }

        @Test
        @DisplayName("提供同类型 maven 坐标类，版本 id 属性不同，返回 false")
        void givenSameTypeClassAndDiffVersionIdThenReturnFalse() {
            final DefaultMavenCoordinate mavenCoordinate1 = new DefaultMavenCoordinate(GROUP_ID, ARTIFACT_ID, "");
            final boolean equals = mavenCoordinate1.equals(mavenCoordinate);
            assertThat(equals).isFalse();
        }

        @Test
        @DisplayName("提供不同类型 class 类，返回 false")
        void givenDiffTypeClassThenReturnFalse() {
            final boolean equals = mavenCoordinate.equals(this);
            assertThat(equals).isFalse();
        }

        @Test
        @DisplayName("提供 null 时，返回 false")
        void givenNullThenReturnFalse() {
            final boolean equals = mavenCoordinate.equals(null);
            assertThat(equals).isFalse();
        }
    }

    @Nested
    @DisplayName("测试方法：hashCode()，toString()")
    class TestHashCodeToString {
        @Test
        @DisplayName("提供相同类型 maven 坐标类，相同属性时，哈希 code 相同")
        void testHashCode() {
            final DefaultMavenCoordinate mavenCoordinate1 = new DefaultMavenCoordinate(GROUP_ID, ARTIFACT_ID, VERSION);
            final int hashCode1 = mavenCoordinate1.hashCode();
            final int hashCode = mavenCoordinate.hashCode();
            assertThat(hashCode1).isEqualTo(hashCode);
        }

        @Test
        @DisplayName("提供 maven 坐标类，返回冒号连接的字符串")
        void testToString() {
            final String toString = mavenCoordinate.toString();
            assertThat(toString).isEqualTo("modelengine.fitframework:fit-util:3.0.0-SNAPSHOT");
        }
    }
}
