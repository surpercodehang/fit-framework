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
 * 构建器构建出的对象的单元测试。
 *
 * @author 季聿阶
 * @since 2022-06-23
 */
@DisplayName("测试构建器构建出的对象")
public class JavaBeanTest {
    @Nested
    @DisplayName("测试构建对象的 Object 类方法")
    class TestObjectMethods {
        @Nested
        @DisplayName("测试 equals(Object obj) 方法")
        class TestEquals {
            @Test
            @DisplayName("当使用相同属性生成的两个对象进行比较时，结果为 true")
            void given2BuiltObjectsWithTheSameFieldsThenEqualsTrue() {
                String host = "localhost";
                int port = 8080;
                Address address1 = Address.builder().host(host).port(port).build();
                Address address2 = Address.builder().host(host).port(port).build();
                assertThat(address1).isEqualTo(address2);
            }

            @Test
            @DisplayName("当生成对象自己和自己比较时，结果为 true")
            void givenBuiltObjectAndItselfThenEqualsTrue() {
                String host = "localhost";
                int port = 8080;
                Address address = Address.builder().host(host).port(port).build();
                assertThat(address).isEqualTo(address);
            }

            @Test
            @DisplayName("当生成对象和其他对象比较时，结果为 false")
            void givenBuiltObjectAndOthersThenEqualsFalse() {
                String host = "localhost";
                int port = 8080;
                Object address = Address.builder().host(host).port(port).build();
                assertThat(address).isNotEqualTo("other");
            }
        }

        @Test
        @DisplayName("当调用生成对象的 hashCode() 方法时，等于其各个属性的 hashCode()")
        void givenBuiltObjectHashCodeThenEqualsItsFieldsHashCode() {
            String host = "localhost";
            int port = 8080;
            Address address = Address.builder().host(host).port(port).build();
            ObjectProxy proxy = ObjectUtils.cast(address);
            assertThat(address.hashCode()).isEqualTo(proxy.$toMap().hashCode());
        }

        @Test
        @DisplayName("当调用生成对象的 toString() 方法时，展示其所有属性的内容")
        void givenBuiltObjectToStringThenGetAllItsFields() {
            String host = "localhost";
            int port = 8080;
            Address address = Address.builder().host(host).port(port).build();
            ObjectProxy proxy = ObjectUtils.cast(address);
            assertThat(address.toString()).isEqualTo(Address.class.getName() + proxy.$toMap().toString());
        }
    }
}
