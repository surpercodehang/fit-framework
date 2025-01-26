/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.ArraySchema;
import modelengine.fit.http.openapi3.swagger.entity.support.EnumSchema;
import modelengine.fit.http.openapi3.swagger.entity.support.ObjectSchema;
import modelengine.fit.http.openapi3.swagger.entity.support.PrimitiveSchema;
import modelengine.fit.http.openapi3.swagger.entity.support.ReferenceSchema;
import modelengine.fit.http.openapi3.swagger.util.SchemaTypeUtils;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#schema-object">OpenAPI
 * 3.1.0</a> 文档中的格式样例信息。
 *
 * @author 季聿阶
 * @since 2023-08-22
 */
public interface Schema extends Serializable {
    /**
     * 获取格式样例的名字。
     *
     * @return 表示格式样例名字的 {@link String}。
     */
    String name();

    /**
     * 获取格式样例的 Java 类型。
     *
     * @return 表示格式样例的 Java 类型的 {@link Type}。
     */
    Type type();

    /**
     * 获取格式样例的说明信息。
     *
     * @return 表示格式样例的说明信息的 {@link String}。
     */
    String description();

    /**
     * 获取格式样例的样例列表。
     *
     * @return 表示格式样例的样例列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> examples();

    /**
     * 通过类型、说明信息和样例列表来创建一个格式样例。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @param description 表示说明信息的 {@link String}。
     * @param examples 表示样例列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示创建出来的格式样例的 {@link Schema}。
     */
    static Schema create(Type type, String description, List<String> examples) {
        return create(SchemaTypeUtils.getTypeName(type), type, description, examples);
    }

    /**
     * 通过名字、类型、说明信息和样例列表来创建一个格式样例。
     *
     * @param name 表示指定名字的 {@link String}。
     * @param type 表示指定类型的 {@link Type}。
     * @param description 表示说明信息的 {@link String}。
     * @param examples 表示样例列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示创建出来的格式样例的 {@link Schema}。
     */
    static Schema create(String name, Type type, String description, List<String> examples) {
        if (SchemaTypeUtils.isArrayType(type)) {
            return new ArraySchema(name, type, description, examples);
        } else if (SchemaTypeUtils.isObjectType(type)) {
            return new ReferenceSchema(name, type, description, examples);
        } else if (SchemaTypeUtils.isEnumType(type)) {
            return new EnumSchema(name, type, description, examples);
        } else {
            return new PrimitiveSchema(name, type, description, examples);
        }
    }

    /**
     * 通过类型、说明信息和样例列表来创建一个对象的格式样例。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @param description 表示说明信息的 {@link String}。
     * @param examples 表示样例列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示创建出来的对象格式样例的 {@link Schema}。
     */
    static Schema fromObject(Type type, String description, List<String> examples) {
        return ObjectSchema.create(type, description, examples);
    }

    /**
     * 通过类型、说明信息和样例列表来创建一个数组的格式样例。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @param description 表示说明信息的 {@link String}。
     * @param examples 表示样例列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示创建出来的数组格式样例的 {@link Schema}。
     */
    static Schema fromArray(Type type, String description, List<String> examples) {
        return new ArraySchema(SchemaTypeUtils.getTypeName(type), type, description, examples);
    }

    /**
     * 通过类型、说明信息和样例列表来创建一个枚举的格式样例。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @param description 表示说明信息的 {@link String}。
     * @param examples 表示样例列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示创建出来的枚举格式样例的 {@link Schema}。
     */
    static Schema fromEnum(Type type, String description, List<String> examples) {
        return new EnumSchema(SchemaTypeUtils.getTypeName(type), type, description, examples);
    }

    /**
     * 表示格式样例的详细信息。
     */
    enum Info {
        STRING(String.class, "string", ""),
        BYTE(Byte.class, "string", "byte"),
        /** OpenAPI 规范中不支持 int16 的类型。 */
        SHORT(Short.class, "integer", "int32"),
        INTEGER(Integer.class, "integer", "int32"),
        LONG(Long.class, "integer", "int64"),
        FLOAT(Float.class, "number", "float"),
        DOUBLE(Double.class, "number", "double"),
        CHARACTER(Character.class, "string", ""),
        BOOLEAN(Boolean.class, "boolean", ""),
        DATE(Date.class, "string", "date"),
        DATETIME(LocalDateTime.class, "string", "date-time"),
        ENUM(Enum.class, "string", ""),
        ARRAY(List.class, "array", ""),
        OBJECT(Map.class, "object", "");

        private final Class<?> clazz;
        private final String type;
        private final String format;

        Info(Class<?> clazz, String type, String format) {
            this.clazz = clazz;
            this.type = type;
            this.format = format;
        }

        /**
         * 获取 OpenAPI 规范支持的类型。
         *
         * @return 表示 OpenAPI 规范支持的类型的 {@link String}。
         */
        public String type() {
            return this.type;
        }

        /**
         * 获取 OpenAPI 规范支持的格式。
         *
         * @return 表示 OpenAPI 规范支持的格式的 {@link String}。
         */
        public String format() {
            return this.format;
        }

        /**
         * 将指定的 Java 类型转换成 OpenAPI 支持的格式样例。
         *
         * @param clazz 表示指定的 Java 类型的 {@link Class}{@code <?>}。
         * @return 表示转换后的格式样例详细信息的 {@link Info}。
         */
        public static Info from(Class<?> clazz) {
            Class<?> actual = ReflectionUtils.ignorePrimitiveClass(clazz);
            for (Info info : Info.values()) {
                if (info.clazz.isAssignableFrom(actual)) {
                    return info;
                }
            }
            return OBJECT;
        }
    }
}
