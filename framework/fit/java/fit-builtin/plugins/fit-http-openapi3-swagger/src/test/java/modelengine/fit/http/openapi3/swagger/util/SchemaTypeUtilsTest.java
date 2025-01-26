/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.util;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link SchemaTypeUtils} 的单元测试。
 *
 * @author 季聿阶
 * @author 王成
 * @since 2023-08-26
 */
@DisplayName("测试 SchemaTypeUtils")
public class SchemaTypeUtilsTest {
    @Nested
    @DisplayName("测试方法：getObjectTypes(Type type)")
    class TestGetObjectType {
        @ParameterizedTest
        @CsvSource({"java.lang.String", "[Ljava.lang.Long;"})
        @DisplayName("当类型为 JDK 的标准类型时，返回空集合")
        void shouldReturnEmptyWhenClassIsJdkClass(String className) throws ClassNotFoundException {
            Class<?> clazz = Class.forName(className);
            Set<Type> actual = SchemaTypeUtils.getObjectTypes(clazz);
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("当类型为 int 时，返回空集合")
        void shouldReturnEmptyWhenClassIsInt() {
            Set<Type> actual = SchemaTypeUtils.getObjectTypes(int.class);
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("当类型为 long[] 时，返回空集合")
        void shouldReturnEmptyWhenClassIsLongArray() {
            Set<Type> actual = SchemaTypeUtils.getObjectTypes(long[].class);
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("当类型为自定义类型时，返回包含该类型的集合")
        void shouldReturnClassWhenClassIsCustom() {
            Set<Type> actual = SchemaTypeUtils.getObjectTypes(Sample1.class);
            assertThat(actual).contains(Sample1.class);
        }

        @Test
        @DisplayName("当类型为 List 泛型类型时，返回包含该类型的集合")
        void shouldReturnClassWhenClassIsListType() {
            ParameterizedType type = TypeUtils.parameterized(List.class, new Type[] {Sample1.class});
            Set<Type> actual = SchemaTypeUtils.getObjectTypes(type);
            assertThat(actual).contains(Sample1.class);
        }

        @Test
        @DisplayName("当类型为自定义泛型类型时，返回包含该类型的集合")
        void shouldReturnClassWhenClassIsParameterizedType() {
            ParameterizedType type = TypeUtils.parameterized(Sample3.class, new Type[] {Sample1.class, Sample2.class});
            Set<Type> actual = SchemaTypeUtils.getObjectTypes(type);
            assertThat(actual).contains(type, Sample1.class);
        }

        @Test
        @DisplayName("当类型为枚举类型时，返回包含该类型的集合")
        void shouldReturnClassWhenClassIsEnum() {
            Set<Type> actual = SchemaTypeUtils.getObjectTypes(SampleEnum.class);
            assertThat(actual).contains(SampleEnum.class);
        }

        @Test
        @DisplayName("当类型为自定义类型，且其字段中存在该类型本身，返回包含该类型的集合")
        void shouldReturnClassWhenClassIsCustomWithSameTypeField() {
            Set<Type> actual = SchemaTypeUtils.getObjectTypes(Sample4.class);
            assertThat(actual).contains(Sample4.class);
        }
    }

    @Nested
    @DisplayName("测试方法：isObjectType(Type type)")
    class TestIsObjectType {
        @Test
        @DisplayName("当类型为 JDK 库类型时，返回 false")
        void shouldReturnFalseWhenClassIsJdkClass() {
            assertThat(SchemaTypeUtils.isObjectType(String.class)).isFalse();
        }

        @Test
        @DisplayName("当类型为 int 时，返回 false")
        void shouldReturnFalseWhenClassIsInt() {
            assertThat(SchemaTypeUtils.isObjectType(int.class)).isFalse();
        }

        @Test
        @DisplayName("当类型为自定义类型时，返回 true")
        void shouldReturnTrueWhenClassIsCustom() {
            assertThat(SchemaTypeUtils.isObjectType(Sample1.class)).isTrue();
        }

        @Test
        @DisplayName("当类型为 List 泛型类型时，返回 false")
        void shouldReturnTrueWhenClassIsListType() {
            ParameterizedType type = TypeUtils.parameterized(List.class, new Type[] {Sample1.class});
            assertThat(SchemaTypeUtils.isObjectType(type)).isFalse();
        }

        @Test
        @DisplayName("当类型为自定义泛型类型时，返回 true")
        void shouldReturnTrueWhenClassIsParameterizedType() {
            ParameterizedType type = TypeUtils.parameterized(Sample3.class, new Type[] {Sample1.class, Sample2.class});
            assertThat(SchemaTypeUtils.isObjectType(type)).isTrue();
        }
    }

    @Nested
    @DisplayName("测试方法：isArrayType(Type type)")
    class TestIsArrayType {
        @Test
        @DisplayName("当类型为 JDK 库的数组类型时，返回 true")
        void shouldReturnTrueWhenClassIsJdkArrayClass() {
            assertThat(SchemaTypeUtils.isArrayType(String[].class)).isTrue();
        }

        @Test
        @DisplayName("当类型为 List 类型时，返回 true")
        void shouldReturnTrueWhenClassIsList() {
            assertThat(SchemaTypeUtils.isArrayType(List.class)).isTrue();
        }

        @Test
        @DisplayName("当类型为 List 的泛型类型时，返回 true")
        void shouldReturnTrueWhenClassIsGenericList() {
            ParameterizedType type = TypeUtils.parameterized(List.class, new Type[] {String.class});
            assertThat(SchemaTypeUtils.isArrayType(type)).isTrue();
        }
    }

    @Nested
    @DisplayName("测试方法：isEnumType(Type type)")
    class TestIsEnumType {
        @Test
        @DisplayName("当类型为 JDK 库的枚举类型时，返回 true")
        void shouldReturnTrueWhenClassIsJdkEnumClass() {
            assertThat(SchemaTypeUtils.isEnumType(SampleEnum.class)).isTrue();
        }
    }

    @Nested
    @DisplayName("测试方法：getTypeName(Type type)")
    class TestGetTypeName {
        @Test
        @DisplayName("当类型为基本类型时，返回类型的名字")
        void shouldReturnTypeNameWhenTypeIsPrimitive() {
            String actual = SchemaTypeUtils.getTypeName(int.class);
            assertThat(actual).isEqualTo("int");
        }

        @Test
        @DisplayName("当类型为基本数组类型时，返回类型的名字")
        void shouldReturnTypeNameWhenTypeIsPrimitiveArray() {
            String actual = SchemaTypeUtils.getTypeName(String[].class);
            assertThat(actual).isEqualTo("StringArray");
        }

        @Test
        @DisplayName("当类型为泛型类型时，返回类型的名字")
        void shouldReturnTypeNameWhenTypeIsGeneric() {
            ParameterizedType type = TypeUtils.parameterized(Sample3.class, new Type[] {Sample1.class, Sample2.class});
            String actual = SchemaTypeUtils.getTypeName(type);
            assertThat(actual).isEqualTo("modelengine.fit.http.openapi3.swagger.util.SchemaTypeUtilsTest$Sample3_of_"
                    + "modelengine.fit.http.openapi3.swagger.util.SchemaTypeUtilsTest$Sample1_and_"
                    + "modelengine.fit.http.openapi3.swagger.util.SchemaTypeUtilsTest$Sample2");
        }
    }

    private static class Sample1 {}

    private static class Sample2 {}

    @SuppressWarnings("unused")
    private static class Sample3<Sample1, Sample2> {}

    private static class Sample4 {
        private List<Sample4> samples;
        private Sample4 nextSample;
        private Map<String, Sample4> sampleMap;
    }

    private enum SampleEnum {
        ITEM1(1, "item1Value"),
        ITEM2(2, "item2Value");

        private final int param1;
        private final String param2;

        SampleEnum(int param1, String param2) {
            this.param1 = param1;
            this.param2 = param2;
        }
    }
}
