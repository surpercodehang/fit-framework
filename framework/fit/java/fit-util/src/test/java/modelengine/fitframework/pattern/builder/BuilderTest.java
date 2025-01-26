/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.builder;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 生成出来的构建器的单元测试。
 *
 * @author 季聿阶
 * @since 2022-06-23
 */
@DisplayName("测试构建器")
public class BuilderTest {
    @Nested
    @DisplayName("测试构建器的 Object 类方法")
    class TestObjectMethods {
        @Nested
        @DisplayName("测试 equals(Object obj) 方法")
        class TestEquals {
            @Test
            @DisplayName("当使用相同属性生成的两个构建器进行比较时，结果为 true")
            void given2BuildersWithTheSameFieldsThenEqualsTrue() {
                String host = "localhost";
                int port = 8080;
                Address.Builder builder1 = Address.builder().host(host).port(port);
                Address.Builder builder2 = Address.builder().host(host).port(port);
                assertThat(builder1).isEqualTo(builder2);
            }

            @Test
            @DisplayName("当生成构建器自己和自己比较时，结果为 true")
            void givenBuilderAndItselfThenEqualsTrue() {
                String host = "localhost";
                int port = 8080;
                Address.Builder builder = Address.builder().host(host).port(port);
                assertThat(builder).isEqualTo(builder);
            }

            @Test
            @DisplayName("当生成构建器和其他对象比较时，结果为 false")
            void givenBuilderAndOthersThenEqualsFalse() {
                String host = "localhost";
                int port = 8080;
                Object builder = Address.builder().host(host).port(port);
                assertThat(builder).isNotEqualTo("other");
            }
        }

        @Test
        @DisplayName("当调用生成构建器的 hashCode() 方法时，等于其各个属性的 hashCode()")
        void givenBuilderHashCodeThenEqualsItsFieldsHashCode() {
            String host = "localhost";
            int port = 8080;
            Address.Builder builder = Address.builder().host(host).port(port);
            ObjectProxy proxy = ObjectUtils.cast(builder);
            assertThat(builder.hashCode()).isEqualTo(proxy.$toMap().hashCode());
        }

        @Test
        @DisplayName("当调用生成构建器的 toString() 方法时，展示其所有属性的内容")
        void givenBuilderToStringThenGetAllItsFields() {
            String host = "localhost";
            int port = 8080;
            Address.Builder builder = Address.builder().host(host).port(port);
            ObjectProxy proxy = ObjectUtils.cast(builder);
            assertThat(builder.toString()).isEqualTo(Address.Builder.class.getName() + proxy.$toMap().toString());
        }
    }
}
