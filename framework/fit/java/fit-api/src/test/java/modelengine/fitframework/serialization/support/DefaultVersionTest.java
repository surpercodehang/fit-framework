/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.serialization.Version;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link DefaultVersion} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-16
 */
@DisplayName("测试 DefaultGenericableVersion 类以及相关类")
class DefaultVersionTest {
    private final DefaultVersion version = new DefaultVersion((byte) 20, (byte) 1, (byte) 1);

    @Test
    @DisplayName("提供 DefaultGenericableVersion 类，返回版本号")
    void givenDefaultGenericableVersionShouldReturnVersion() {
        String genericVersion = this.version.toString();
        assertThat(genericVersion).isEqualTo("20.1.1");
    }

    @Test
    @DisplayName("提供 GenericableVersion 接口 builder 方法，返回构造器")
    void givenGenericableVersionShouldReturnBuilder() {
        Version.Builder genericVersion = Version.builder("20.1.1");
        assertThat(genericVersion.build()).isEqualTo(this.version);
    }

    @Test
    @DisplayName("提供 GenericableVersion 接口 copy 方法，返回构造器")
    void givenGenericableVersionWhenCopyThenReturnBuilder() {
        Version build = this.version.copy().build();
        assertThat(build).isEqualTo(this.version);
    }

    @Test
    @DisplayName("提供 DefaultGenericableVersion 类，返回哈希值")
    void givenDefaultGenericableVersionShouldReturnHasCode() {
        int hashCode = this.version.hashCode();
        assertThat(hashCode).isGreaterThan(0);
    }

    @Test
    @DisplayName("提供 DefaultGenericableVersion 类 equals 方法与相同值的新对象比较时，true")
    void givenSaveDefaultGenericableVersionShouldReturnTrue() {
        DefaultVersion expected = new DefaultVersion((byte) 20, (byte) 1, (byte) 1);
        assertThat(this.version.equals(expected)).isTrue();
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    @DisplayName("提供 DefaultGenericableVersion 类 equals 方法与其他值的新对象比较时，false")
    void givenOtherDefaultGenericableVersionShouldReturnFalse() {
        assertThat(this.version.equals(this.getClass())).isFalse();
    }

    @Nested
    @DisplayName("测试类：Serializer")
    class TestSerializer {
        private final DefaultVersion.Serializer serializer = DefaultVersion.Serializer.INSTANCE;
        private final DefaultVersion version = DefaultVersionTest.this.version;

        @Test
        @DisplayName("提供 Serializer 类时，返回版本信息")
        void givenSerializerWhenSerializeThenReturnVersion() throws IOException {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                this.serializer.serialize(this.version, out);
                try (InputStream in = new ByteArrayInputStream(out.toByteArray())) {
                    Version expected = this.serializer.deserialize(in);
                    assertThat(this.version).isEqualTo(expected);
                }
            }
        }

        @Test
        @DisplayName("提供 Serializer 类调用父类方法时，返回版本信息")
        void givenSuperSerializerShouldReturnVersion() {
            byte[] bytes = this.serializer.serialize2Bytes(this.version);
            Version expected = this.serializer.deserialize(bytes);
            assertThat(this.version).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("测试类：Builder")
    class TestBuilder {
        private final DefaultVersion version = DefaultVersionTest.this.version;

        @Test
        @DisplayName("提供 GenericableVersion 类版本信息为空时，返回初始化版本信息")
        void givenInitGenericableVersionShouldReturnInitVersionInfo() {
            String actual = new DefaultVersion.Builder(StringUtils.EMPTY).build().toString();
            String expected = "0.0.0";
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("提供 Serializer 类时，返回版本信息")
        void givenBuilderWhenSerializeThenReturnVersion() {
            DefaultVersion.Builder expected = new DefaultVersion.Builder(this.version);
            DefaultVersion.Builder actual = new DefaultVersion.Builder("20.1.1");
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        }
    }
}
