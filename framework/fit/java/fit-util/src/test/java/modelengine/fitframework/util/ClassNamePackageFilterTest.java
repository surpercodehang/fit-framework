/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link ClassNamePackageFilter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-03
 */
public class ClassNamePackageFilterTest {
    @Nested
    @DisplayName("Test method: test(String className)")
    class TestTest {
        @Nested
        @DisplayName("Given class name 'modelengine.demo'")
        class GivenClassName {
            private final String className = "modelengine.demo";

            @Test
            @DisplayName("Given no packages then return true")
            void givenNoPackagesThenReturnTrue() {
                Predicate<String> predicate = ClassNamePackageFilter.create(null);
                boolean actual = predicate.test(this.className);
                assertThat(actual).isTrue();
            }

            @Test
            @DisplayName("Given packages are ['modelengine', 'modelengine.demo'] then return true")
            void given2ValidPackagesThenReturnTrue() {
                Predicate<String> predicate = ClassNamePackageFilter.create(
                        Stream.of("modelengine", "modelengine.demo").collect(Collectors.toList()));
                boolean actual = predicate.test(this.className);
                assertThat(actual).isTrue();
            }

            @Test
            @DisplayName("Given packages are ['modelengine.another'] then return false")
            void givenInvalidPackagesThenReturnFalse() {
                Predicate<String> predicate = ClassNamePackageFilter.create(
                        Stream.of("modelengine.another").collect(Collectors.toList()));
                boolean actual = predicate.test(this.className);
                assertThat(actual).isFalse();
            }
        }
    }
}

