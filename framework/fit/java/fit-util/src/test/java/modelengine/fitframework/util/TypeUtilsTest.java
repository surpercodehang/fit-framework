/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.beans.Object4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link TypeUtils} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-01-04
 */
@DisplayName("测试 TypeUtils 工具类")
class TypeUtilsTest {
    @Nested
    @DisplayName("提供不同的 Type，转换为 class")
    class ToClass {
        @Test
        @DisplayName("提供 ParameterizedType 类型，正常转换")
        void givenParameterizedTypeThenReturnClass() {
            Type[] types = Integer.class.getGenericInterfaces();
            Class<?> clazz = TypeUtils.toClass(types[0]);
            assertThat(clazz).isEqualTo(Comparable.class);
        }

        @Test
        @DisplayName("提供 Class 类型，正常转换")
        void givenClassTypeThenReturnClass() {
            Type type = Integer.class.getGenericSuperclass();
            Class<?> clazz = TypeUtils.toClass(type);
            assertThat(clazz).isEqualTo(Number.class);
        }

        @Test
        @DisplayName("提供不支持类型，抛出异常")
        void givenUnSupportTypeThenThrowException() {
            Type type = (GenericArrayType) () -> null;
            assertThatThrownBy(() -> TypeUtils.toClass(type)).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("提供泛型上下限，返回通配符表达式类")
    class Wildcard {
        @Test
        @DisplayName("提供泛型上限和下限，抛出异常")
        void givenUpperAndLowerBoundThenThrowException() {
            Type upperBound = Object.class.getGenericSuperclass();
            Type lowerBound = Integer.class.getGenericSuperclass();
            assertThatThrownBy(() -> TypeUtils.wildcard(new Type[] {upperBound}, new Type[] {lowerBound})).isInstanceOf(
                    IllegalArgumentException.class);
        }

        @Test
        @DisplayName("提供泛型上限，返回通配符表达式类")
        void givenUpperBoundThenReturnWildcardType() {
            Type upperBound = Object.class.getGenericSuperclass();
            WildcardType wildcard = TypeUtils.wildcard(new Type[] {upperBound}, null);
            Type[] upperBounds = wildcard.getUpperBounds();
            assertThat(upperBounds.length).isEqualTo(1);
        }

        @Test
        @DisplayName("提供泛型下限，返回通配符表达式类")
        void givenLowerBoundThenReturnWildcardType() {
            Type lowerBound = Integer.class.getGenericSuperclass();
            WildcardType wildcard = TypeUtils.wildcard(null, new Type[] {lowerBound});
            Type[] lowerBounds = wildcard.getLowerBounds();
            assertThat(lowerBounds.length).isEqualTo(1);
        }

        @Test
        @DisplayName("测试 toString 方法")
        void testToString() {
            Type lowerBound = Integer.class.getGenericSuperclass();
            WildcardType wildcard = TypeUtils.wildcard(null, new Type[] {lowerBound});
            String toString = wildcard.toString();
            assertThat(toString).contains("super");
        }
    }

    @Nested
    @DisplayName("提供一个参数化泛型类")
    class GivenParameterized {
        private static final String JAVA_LANG_NUMBER = "java.util.List<java.lang.Number>";

        private ParameterizedType listType;
        private Type type;

        @BeforeEach
        void setUp() {
            this.type = Integer.class.getGenericSuperclass();
            this.listType = TypeUtils.parameterized(List.class, new Type[] {this.type});
        }

        @Nested
        @DisplayName("测试方法：parameterized(Class<?> rawClass, Type[] arguments, Type ownerType)")
        class TestParameterized {
            @Test
            @DisplayName("当提供 class 不包含泛型时，抛出异常")
            void givenRawClassExcludeTypeParametersThenThrowException() {
                assertThatThrownBy(() -> TypeUtils.parameterized(int.class, new Type[] {}, null)).isInstanceOf(
                        IllegalArgumentException.class);
            }

            @Test
            @DisplayName("当提供 class 的参数化类型数量与类型数组个数不相等时，抛出异常")
            void givenRawClassParametersLengthNotEqualsTypeArrayLengthThenThrowException() {
                assertThatThrownBy(() -> TypeUtils.parameterized(List.class, new Type[] {}, null)).isInstanceOf(
                        IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("测试方法：toString()")
        void testToString() {
            String toString = this.listType.toString();
            assertThat(toString).isEqualTo(JAVA_LANG_NUMBER);
        }

        @Test
        @DisplayName("测试方法：hashcode()")
        void testHashcode() {
            int hashCode = this.listType.hashCode();
            final ParameterizedType parameterizedType = TypeUtils.parameterized(List.class, new Type[] {this.type});
            final int code = parameterizedType.hashCode();
            assertThat(hashCode).isEqualTo(code);
        }

        @Nested
        @DisplayName("测试方法：equals(Object obj)")
        class TestEquals {
            @Test
            @DisplayName("当和自己比较相等时，返回 true")
            @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
            void givenSelfThenReturnTrue() {
                boolean equals = GivenParameterized.this.listType.equals(GivenParameterized.this.listType);
                assertThat(equals).isTrue();
            }

            @Test
            @DisplayName("当和同类型属性不同的对象比较相等时，返回 false")
            void givenSameTypeAndAttributeDiffThenReturnFalse() {
                ParameterizedType setType =
                        TypeUtils.parameterized(Set.class, new Type[] {GivenParameterized.this.type});
                boolean equals = GivenParameterized.this.listType.equals(setType);
                assertThat(equals).isFalse();
            }

            @Test
            @DisplayName("当和不同类型对象比较相等时，返回 false")
            @SuppressWarnings("EqualsBetweenInconvertibleTypes")
            void givenOtherTypeThenReturnFalse() {
                boolean equals = GivenParameterized.this.listType.equals("");
                assertThat(equals).isFalse();
            }
        }

        @Test
        @DisplayName("返回原始类型、拥有者类型、实际泛型参数")
        void givenParameterizedTypeThenReturnFields() {
            Type rawType = this.listType.getRawType();
            Type ownerType = this.listType.getOwnerType();
            Type[] arguments = this.listType.getActualTypeArguments();
            assertThat(rawType).isEqualTo(List.class);
            assertThat(ownerType).isNull();
            assertThat(arguments.length).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("测试方法：withDefault(Class<?> clazz)")
    class TestWithDefault {
        @Test
        @DisplayName("提供没有泛型的 class 类，返回该 class 类")
        void givenClassWithNoParameterizedTypeThenReturnRawType() {
            final Type withDefault = TypeUtils.withDefault(Integer.class);
            assertThat(withDefault).isEqualTo(Integer.class);
        }

        @Test
        @DisplayName("提供有泛型的 class 类，返回参数化类型")
        void givenClassWithParameterizedTypeThenReturnParameterizedType() {
            final Type type = TypeUtils.withDefault(List.class);
            assertThat(type.getTypeName()).isEqualTo("java.util.List<? extends java.lang.Object>");
        }
    }

    @Test
    @DisplayName("当给定类型变量时，返回其正确的定义位置")
    void shouldReturnTypeVariablesIndexes() {
        Map<Type, Integer> indexes = Stream.of(Object4.class.getDeclaredFields())
                .map(Field::getGenericType)
                .filter(type -> type instanceof TypeVariable)
                .collect(Collectors.toMap(Function.identity(),
                        variable -> TypeUtils.getTypeVariableIndex(ObjectUtils.cast(variable))));
        assertThat(indexes).hasSize(2);
    }
}
