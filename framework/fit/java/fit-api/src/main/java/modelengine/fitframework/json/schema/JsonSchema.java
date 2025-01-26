/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema;

import modelengine.fitframework.json.schema.support.ArraySchema;
import modelengine.fitframework.json.schema.support.BooleanSchema;
import modelengine.fitframework.json.schema.support.IntegerSchema;
import modelengine.fitframework.json.schema.support.JsonObjectSchema;
import modelengine.fitframework.json.schema.support.NumberSchema;
import modelengine.fitframework.json.schema.support.ObjectSchema;
import modelengine.fitframework.json.schema.support.StringSchema;
import modelengine.fitframework.json.schema.util.SchemaTypeUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 表示 Json 格式的规范。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
public interface JsonSchema {
    /**
     * 获取 Json 格式规范的类型。
     *
     * @return 表示 Json 格式规范类型的 {@link Type}。
     */
    Type type();

    /**
     * 获取 Json 格式规范的名字。
     *
     * @return 表示 Json 格式规范名字的 {@link String}。
     */
    String name();

    /**
     * 获取 Json 格式规范的描述。
     *
     * @return 表示 Json 格式规范的描述的 {@link String}。
     */
    String description();

    /**
     * 将 Json 格式规范转换成键值对形式。
     *
     * @return 表示转换后的键值对的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> toJsonObject();

    /**
     * 创建一个 Json 格式规范。
     *
     * @param type 表示类型的 {@link Type}。
     * @param referencedPrefix 表示如果存在引用，其引用前缀的 {@link String}。
     * @param referencedSchemas 表示引用数据结果的 {@link Map}{@code <}{@link Type}{@code , }{@link ObjectSchema}{@code >}。
     * @return 表示创建的格式规范的 {@link JsonSchema}。
     */
    static JsonSchema create(Type type, String referencedPrefix, Map<Type, ObjectSchema> referencedSchemas) {
        if (SchemaTypeUtils.isArrayType(type)) {
            return ArraySchema.create(type, referencedPrefix, referencedSchemas);
        }
        if (SchemaTypeUtils.isObjectType(type)) {
            return ObjectSchema.create(type, referencedPrefix, referencedSchemas);
        }
        return createPrimitive(type);
    }

    /**
     * 创建一个除对象和数组格式以外的 Json 格式规范。
     *
     * @param type 表示类型的 {@link Type}。
     * @return 表示创建的格式规范的 {@link JsonSchema}。
     */
    static JsonSchema createPrimitive(Type type) {
        Class<?> clazz = ReflectionUtils.ignorePrimitiveClass(TypeUtils.toClass(type));
        if (clazz == String.class || SchemaTypeUtils.isEnumType(type) || clazz == Character.class) {
            return new StringSchema(type);
        } else if (clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class
                || clazz == BigInteger.class) {
            return new IntegerSchema(type);
        } else if (clazz == Double.class || clazz == Float.class || clazz == BigDecimal.class) {
            return new NumberSchema(type);
        } else if (clazz == Boolean.class) {
            return new BooleanSchema(type);
        } else {
            throw new IllegalStateException(StringUtils.format(
                    "Unsupported Java type to initial json schema. [type={0}]",
                    type.getTypeName()));
        }
    }

    /**
     * 直接从一个 Json 对象中创建一个 Json 格式规范。
     *
     * @param name 表示规范名字的 {@link String}。
     * @param description 表示规范描述的 {@link String}。
     * @param jsonObject 表示 Json 对象的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示创建的格式规范的 {@link JsonSchema}。
     */
    static JsonSchema fromJsonObject(String name, String description, Map<String, Object> jsonObject) {
        return new JsonObjectSchema(name, description, jsonObject);
    }
}
