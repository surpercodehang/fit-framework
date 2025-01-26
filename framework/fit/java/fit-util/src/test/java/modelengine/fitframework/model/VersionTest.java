/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link Version} 提供单元测试。
 *
 * @author 梁济时
 * @since 2021-10-11
 */
@DisplayName("测试 Version")
class VersionTest {
    @Nested
    class Construct {
        @Test
        void should_throws_when_major_is_negative() {
            assertThrows(IllegalArgumentException.class, () -> Version.create(-1, 0));
        }

        @Test
        void should_throws_when_minor_is_negative() {
            assertThrows(IllegalArgumentException.class, () -> Version.create(0, -1));
        }

        @Test
        void should_throws_when_revision_is_negative() {
            assertThrows(IllegalArgumentException.class, () -> Version.create(0, 0, -1));
        }

        @Test
        void should_throws_when_build_is_negative() {
            assertThrows(IllegalArgumentException.class, () -> Version.create(0, 0, 0, -1));
        }

        @Test
        void should_return_correct_value() {
            Version version = Version.create(1, 2, 3, 4);
            assertEquals(1, version.major());
            assertEquals(2, version.minor());
            assertEquals(3, version.revision());
            assertEquals(4, version.build());
        }

        @Test
        void should_return_correct_version() {
            Version version = Version.create(1, 2);
            assertEquals(1, version.major());
            assertEquals(2, version.minor());
            assertEquals(0, version.revision());
            assertEquals(0, version.build());
        }
    }

    @Nested
    @DisplayName("测试 parse(String value) 方法")
    class TestParse {
        @Test
        @DisplayName("当存在阶段时，解析正确")
        void shouldParseCorrectlyWhenExistStage() {
            Version version = Version.parse("1.0.0-SNAPSHOT");
            assertThat(version.stage()).isEqualTo("SNAPSHOT");
            assertThat(version.toString()).isEqualTo("1.0.0-SNAPSHOT");
        }
    }

    @Nested
    class ToString {
        @Test
        void should_return_string_to_minor() {
            Version version = Version.create(0, 0, 0, 0);
            assertEquals("0.0.0", version.toString());
        }

        @Test
        void should_return_string_to_revision() {
            Version version = Version.create(0, 0, 1);
            assertEquals("0.0.1", version.toString());
        }

        @Test
        void should_return_string_to_build() {
            Version version = Version.create(0, 0, 0, 1);
            assertEquals("0.0.0.1", version.toString());
        }
    }

    @Nested
    class Compare {
        @Test
        void should_return_zero_when_first_equals_second() {
            Version v1 = Version.create(1, 2, 3, 4);
            Version v2 = Version.create(1, 2, 3, 4);
            assertEquals(0, v1.compareTo(v2));
        }

        @Test
        void should_return_positive_when_first_greater_than_second() {
            Version v1 = Version.create(1, 12, 0, 0);
            Version v2 = Version.create(1, 2, 0, 0);
            assertTrue(v1.compareTo(v2) > 0);
        }
    }

    @Nested
    class Parse {
        @Test
        void should_return_null_when_parse_null() {
            assertNull(Version.parse(null));
        }

        @Test
        void should_return_null_when_parse_blank() {
            assertNull(Version.parse("  "));
        }

        @Test
        void should_throws_when_to_major() {
            assertThrows(IllegalArgumentException.class, () -> Version.parse("1"));
        }

        @Test
        void should_return_version_when_to_minor() {
            Version version = Version.parse("1.2");
            assertEquals(1, version.major());
            assertEquals(2, version.minor());
            assertEquals(0, version.revision());
            assertEquals(0, version.build());
        }

        @Test
        void should_return_version_when_to_revision() {
            Version version = Version.parse("1.2.3");
            assertEquals(1, version.major());
            assertEquals(2, version.minor());
            assertEquals(3, version.revision());
            assertEquals(0, version.build());
        }

        @Test
        void should_return_version_when_to_build() {
            Version version = Version.parse("1.2.3.4");
            assertEquals(1, version.major());
            assertEquals(2, version.minor());
            assertEquals(3, version.revision());
            assertEquals(4, version.build());
        }

        @Test
        void should_throws_when_part_not_positive_number() {
            assertThrows(IllegalArgumentException.class, () -> Version.parse("1.-1"));
        }
    }

    @Nested
    class Equals {
        @Test
        void should_return_true_when_compare_version_with_same_data() {
            Version v1 = Version.create(1, 2, 3, 4);
            Version v2 = Version.create(1, 2, 3, 4);
            assertEquals(v1, v2);
        }
    }

    @Nested
    class HashCode {
        @Test
        void should_return_same_hash_code_when_versions_with_same_data() {
            Version v1 = Version.create(4, 3, 2, 1);
            Version v2 = Version.create(4, 3, 2, 1);
            assertEquals(v1.hashCode(), v2.hashCode());
        }
    }
}
