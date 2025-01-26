/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.model.Version;
import modelengine.fitframework.model.support.DefaultVersion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultVersion} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-06
 */
public class DefaultVersionTest {
    @Nested
    @DisplayName("Test method: equals(Object obj)")
    class TestEquals {
        private final Version version = new DefaultVersion(1, 2, 3, 4, "");

        @Nested
        @DisplayName("Given default version itself")
        class GivenDefaultVersionItself {
            @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
            @Test
            @DisplayName("Return true")
            void returnTrue() {
                boolean actual = TestEquals.this.version.equals(TestEquals.this.version);
                assertThat(actual).isTrue();
            }
        }

        @Nested
        @DisplayName("Given another default version")
        class GivenAnotherDefaultVersion {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Given obj is null then return false")
            void givenAnotherNullThenReturnFalse() {
                boolean actual = TestEquals.this.version.equals(null);
                assertThat(actual).isFalse();
            }

            @SuppressWarnings("EqualsBetweenInconvertibleTypes")
            @Test
            @DisplayName("Given obj is Integer then return false")
            void givenAnotherIntegerThenReturnFalse() {
                boolean actual = TestEquals.this.version.equals(1);
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("Given obj is DefaultVersion with different major then return false")
            void givenAnotherWithDifferentMajorThenReturnFalse() {
                boolean actual = TestEquals.this.version.equals(new DefaultVersion(0, 2, 3, 4, ""));
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("Given obj is DefaultVersion with different minor then return false")
            void givenAnotherWithDifferentMinorThenReturnFalse() {
                boolean actual = TestEquals.this.version.equals(new DefaultVersion(1, 0, 3, 4, ""));
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("Given obj is DefaultVersion with different revision then return false")
            void givenAnotherWithDifferentRevisionThenReturnFalse() {
                boolean actual = TestEquals.this.version.equals(new DefaultVersion(1, 2, 0, 4, ""));
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("Given obj is DefaultVersion with different build then return false")
            void givenAnotherWithDifferentBuildThenReturnFalse() {
                boolean actual = TestEquals.this.version.equals(new DefaultVersion(1, 2, 3, 0, ""));
                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("Given obj is DefaultVersion with the same attribute then return true")
            void givenAnotherWithTheSameAttributeThenReturnTrue() {
                boolean actual = TestEquals.this.version.equals(new DefaultVersion(1, 2, 3, 4, ""));
                assertThat(actual).isTrue();
            }
        }
    }
}
