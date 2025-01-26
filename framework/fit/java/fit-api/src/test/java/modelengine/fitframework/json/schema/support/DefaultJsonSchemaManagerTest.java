/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.json.schema.JsonSchema;
import modelengine.fitframework.json.schema.JsonSchemaManager;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link DefaultJsonSchemaManager} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
@DisplayName("测试 DefaultJsonSchemaManager")
public class DefaultJsonSchemaManagerTest {
    private JsonSchemaManager manager;

    @BeforeEach
    void setup() {
        this.manager = new DefaultJsonSchemaManager();
    }

    @AfterEach
    void teardown() {
        this.manager = null;
    }

    @Nested
    @DisplayName("Array 类型可以正确解析 JsonSchema")
    class TestArray {
        @Test
        @DisplayName("数组类型正确")
        void shouldReturnCorrectSchemaGivenArray() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(String[].class);
            assertThat(actual).isInstanceOf(ArraySchema.class).returns("StringArray", JsonSchema::name);
        }

        @Test
        @DisplayName("List 类型正确")
        void shouldReturnCorrectSchemaGivenList() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(List.class);
            assertThat(actual).isInstanceOf(ArraySchema.class).returns("List", JsonSchema::name);
        }
    }

    @Nested
    @DisplayName("Object 类型可以正确解析 JsonSchema")
    class TestObject {
        @Test
        @DisplayName("Map 类型正确")
        void shouldReturnCorrectSchemaGivenMap() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Map.class);
            assertThat(actual).isInstanceOf(ObjectSchema.class).returns("Map", JsonSchema::name);
        }

        @Test
        @DisplayName("自定义结构体类型正确")
        void shouldReturnCorrectSchemaGivenCustomObject() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(TestObjectClass.class);
            assertThat(actual).isInstanceOf(ObjectSchema.class).returns("TestObjectClass", JsonSchema::name);
        }
    }

    @Nested
    @DisplayName("String 类型可以正确解析 JsonSchema")
    class TestString {
        @Test
        @DisplayName("String 类型正确")
        void shouldReturnCorrectSchemaGivenString() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(String.class);
            assertThat(actual).isInstanceOf(StringSchema.class).returns("String", JsonSchema::name);
        }

        @Test
        @DisplayName("枚举类型正确")
        void shouldReturnCorrectSchemaGivenEnum() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(TestEnumClass.class);
            assertThat(actual).isInstanceOf(StringSchema.class).returns("TestEnumClass", JsonSchema::name);
            Map<String, Object> jsonObject = actual.toJsonObject();
            assertThat(jsonObject).containsEntry("enum", Arrays.asList("E1", "E2"));
        }

        @Test
        @DisplayName("char 类型正确")
        void shouldReturnCorrectSchemaGivenChar() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(char.class);
            assertThat(actual).isInstanceOf(StringSchema.class).returns("char", JsonSchema::name);
        }

        @Test
        @DisplayName("Character 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedCharacter() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Character.class);
            assertThat(actual).isInstanceOf(StringSchema.class).returns("Character", JsonSchema::name);
        }
    }

    @Nested
    @DisplayName("Integer 类型可以正确解析 JsonSchema")
    class TestInteger {
        @Test
        @DisplayName("byte 类型正确")
        void shouldReturnCorrectSchemaGivenByte() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(byte.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("byte", JsonSchema::name);
        }

        @Test
        @DisplayName("Byte 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedByte() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Byte.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("Byte", JsonSchema::name);
        }

        @Test
        @DisplayName("short 类型正确")
        void shouldReturnCorrectSchemaGivenShort() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(short.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("short", JsonSchema::name);
        }

        @Test
        @DisplayName("Short 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedShort() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Short.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("Short", JsonSchema::name);
        }

        @Test
        @DisplayName("int 类型正确")
        void shouldReturnCorrectSchemaGivenInteger() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(int.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("int", JsonSchema::name);
        }

        @Test
        @DisplayName("Integer 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedInteger() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Integer.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("Integer", JsonSchema::name);
        }

        @Test
        @DisplayName("long 类型正确")
        void shouldReturnCorrectSchemaGivenLong() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(long.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("long", JsonSchema::name);
        }

        @Test
        @DisplayName("Long 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedLong() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Long.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("Long", JsonSchema::name);
        }

        @Test
        @DisplayName("BigInteger 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedBigInteger() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(BigInteger.class);
            assertThat(actual).isInstanceOf(IntegerSchema.class).returns("BigInteger", JsonSchema::name);
        }
    }

    @Nested
    @DisplayName("Number 类型可以正确解析 JsonSchema")
    class TestNumber {
        @Test
        @DisplayName("float 类型正确")
        void shouldReturnCorrectSchemaGivenFloat() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(float.class);
            assertThat(actual).isInstanceOf(NumberSchema.class).returns("float", JsonSchema::name);
        }

        @Test
        @DisplayName("Float 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedFloat() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Float.class);
            assertThat(actual).isInstanceOf(NumberSchema.class).returns("Float", JsonSchema::name);
        }

        @Test
        @DisplayName("double 类型正确")
        void shouldReturnCorrectSchemaGivenDouble() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(double.class);
            assertThat(actual).isInstanceOf(NumberSchema.class).returns("double", JsonSchema::name);
        }

        @Test
        @DisplayName("Double 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedDouble() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Double.class);
            assertThat(actual).isInstanceOf(NumberSchema.class).returns("Double", JsonSchema::name);
        }

        @Test
        @DisplayName("BigDecimal 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedBigDecimal() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(BigDecimal.class);
            assertThat(actual).isInstanceOf(NumberSchema.class).returns("BigDecimal", JsonSchema::name);
        }
    }

    @Nested
    @DisplayName("Boolean 类型可以正确解析 JsonSchema")
    class TestBoolean {
        @Test
        @DisplayName("boolean 类型正确")
        void shouldReturnCorrectSchemaGivenBoolean() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(boolean.class);
            assertThat(actual).isInstanceOf(BooleanSchema.class).returns("boolean", JsonSchema::name);
        }

        @Test
        @DisplayName("Boolean 类型正确")
        void shouldReturnCorrectSchemaGivenBoxedBoolean() {
            JsonSchema actual = DefaultJsonSchemaManagerTest.this.manager.createSchema(Boolean.class);
            assertThat(actual).isInstanceOf(BooleanSchema.class).returns("Boolean", JsonSchema::name);
        }
    }

    @Nested
    @DisplayName("方法参数可以正确解析")
    class TestMethod {
        @Test
        @DisplayName("当方法参数都是基本类型时，解析正确")
        void shouldReturnCorrectSchemaGivenPrimitive() throws NoSuchMethodException {
            Method m1 = TestObjectClass.class.getDeclaredMethod("m1", String.class, int.class);
            JsonSchema schema = DefaultJsonSchemaManagerTest.this.manager.createSchema(m1);
            assertThat(schema).isInstanceOf(ObjectSchema.class);
            Map<String, Object> map = schema.toJsonObject();
            assertThat(map).containsEntry("type", "object").containsEntry("required", Collections.singletonList("p2"));
            assertThat(map.get("properties")).isInstanceOf(Map.class);
            Map<String, Object> properties = ObjectUtils.cast(map.get("properties"));
            assertThat(properties).containsEntry("p1", MapBuilder.get().put("type", "string").build())
                    .containsEntry("p2",
                            MapBuilder.get()
                                    .put("type", "integer")
                                    .put("description", "param2")
                                    .put("default", "1")
                                    .build());
        }

        @Test
        @DisplayName("当方法参数存在自定义结构体类型时，解析正确")
        void shouldReturnCorrectSchemaGivenCustomObject() throws NoSuchMethodException {
            Method m2 = TestObjectClass.class.getDeclaredMethod("m2", User.class, User.class);
            JsonSchema schema = DefaultJsonSchemaManagerTest.this.manager.createSchema(m2);
            assertThat(schema).isInstanceOf(ObjectSchema.class);
            Map<String, Object> map = schema.toJsonObject();
            assertThat(map).containsEntry("type", "object");
            assertThat(map.get("properties")).isInstanceOf(Map.class);
            Map<String, Object> properties = ObjectUtils.cast(map.get("properties"));
            assertThat(properties).isNotEmpty();
            Map<String, Object> p1 = ObjectUtils.cast(properties.get("p1"));
            assertThat(p1).containsEntry("type", "object");
            Map<String, Object> p1Properties = ObjectUtils.cast(p1.get("properties"));
            assertThat(p1Properties).containsEntry("name", MapBuilder.get().put("type", "string").build())
                    .containsEntry("age", MapBuilder.get().put("type", "integer").build());
            Map<String, Object> p2 = ObjectUtils.cast(properties.get("p1"));
            assertThat(p2).containsEntry("type", "object");
            Map<String, Object> p2Properties = ObjectUtils.cast(p2.get("properties"));
            assertThat(p2Properties).containsEntry("name", MapBuilder.get().put("type", "string").build())
                    .containsEntry("age", MapBuilder.get().put("type", "integer").build());
        }
    }

    enum TestEnumClass {
        E1,
        E2;
    }

    static class TestObjectClass {
        private String f1;
        private int f2;

        void m1(String p1, @Property(description = "param2", defaultValue = "1", required = true) int p2) {}

        void m2(User p1, User p2) {}
    }

    static class User {
        private String name;
        private int age;
    }
}
