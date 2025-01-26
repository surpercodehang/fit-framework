/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema.support;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.json.schema.JsonSchema;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * 表示 {@link JsonSchema} 的对象实现。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
public class ObjectSchema extends AbstractJsonSchema {
    private final List<String> required = new ArrayList<>();
    private final Map<String, JsonSchema> properties = new LinkedHashMap<>();

    ObjectSchema(Type type) {
        super(type);
    }

    /**
     * 添加指定的格式规范。
     *
     * @param schema 表示待添加的格式规范的 {@link JsonSchema}。
     */
    public void addSchema(JsonSchema schema) {
        this.addSchema(schema, false);
    }

    /**
     * 添加指定的格式规范。
     *
     * @param schema 表示待添加的格式规范的 {@link JsonSchema}。
     * @param required 表示待添加的格式规范是否必须的标志的 {@code boolean}。
     */
    public void addSchema(JsonSchema schema, boolean required) {
        if (schema == null) {
            return;
        }
        this.properties.put(schema.name(), schema);
        if (required) {
            this.required.add(schema.name());
        }
    }

    @Override
    public Map<String, Object> toJsonObject() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", "object");
        if (CollectionUtils.isNotEmpty(this.required)) {
            builder.put("required", this.required);
        }
        if (MapUtils.isNotEmpty(this.properties)) {
            builder.put("properties",
                    this.properties.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toJsonObject())));
        }
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        return builder.build();
    }

    /**
     * 创建一个结构体的格式规范。
     *
     * @param type 表示结构体类型的 {@link Type}。
     * @param referencedPrefix 表示如果存在引用，其引用前缀的 {@link String}。
     * @param referencedSchemas 表示引用数据结果的 {@link Map}{@code <}{@link Type}{@code , }{@link ObjectSchema}{@code >}。
     * @return 表示创建的结构体的格式规范的 {@link ArraySchema}。
     */
    public static ObjectSchema create(Type type, String referencedPrefix, Map<Type, ObjectSchema> referencedSchemas) {
        ObjectSchema schema = new ObjectSchema(type);
        schema.setProperties(ObjectUtils.nullIf(referencedPrefix, StringUtils.EMPTY),
                ObjectUtils.getIfNull(referencedSchemas, Collections::emptyMap));
        return schema;
    }

    void setProperties(String referencedPrefix, Map<Type, ObjectSchema> referencedSchemas) {
        if (this.type() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = cast(this.type());
            Class<?> rawType = cast(parameterizedType.getRawType());
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.generateProperties(rawType, actualTypeArguments, referencedPrefix, referencedSchemas);
            return;
        }
        if (this.type() instanceof Class) {
            Class<?> clazz = (Class<?>) this.type();
            this.generateProperties(clazz, null, referencedPrefix, referencedSchemas);
        }
    }

    private void generateProperties(Class<?> clazz, Type[] actualTypeArguments, String referencedPrefix,
            Map<Type, ObjectSchema> referencedSchemas) {
        TypeVariable<?>[] typeVariables = clazz.getTypeParameters();
        for (Field field : ReflectionUtils.getDeclaredFields(clazz, true)) {
            Type actualFieldType;
            Type fieldType = field.getGenericType();
            if (fieldType instanceof TypeVariable && actualTypeArguments != null) {
                int index = findTypeVariableIndex(cast(fieldType),
                        typeVariables).orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "Could not find matching type variable. [field={0}]",
                        field.getName())));
                actualFieldType = actualTypeArguments[index];
            } else {
                actualFieldType = fieldType;
            }
            JsonSchema fieldSchema;
            if (MapUtils.isNotEmpty(referencedSchemas) && referencedSchemas.containsKey(actualFieldType)) {
                fieldSchema = new ReferenceSchema(referencedPrefix, referencedSchemas.get(actualFieldType));
            } else {
                fieldSchema = JsonSchema.create(actualFieldType,
                        nullIf(referencedPrefix, StringUtils.EMPTY),
                        referencedSchemas);
            }
            Property property = field.getDeclaredAnnotation(Property.class);
            String propertyDescription = StringUtils.EMPTY;
            String defaultValue = StringUtils.EMPTY;
            boolean isRequired = false;
            if (property != null) {
                propertyDescription = property.description();
                defaultValue = property.defaultValue();
                isRequired = property.required();
            }
            this.addSchema(new DecoratedSchema(field.getName(), propertyDescription, defaultValue, fieldSchema),
                    isRequired);
        }
    }

    private static OptionalInt findTypeVariableIndex(TypeVariable<?> typeVariable, TypeVariable<?>[] typeVariables) {
        for (int i = 0; i < typeVariables.length; i++) {
            if (Objects.equals(typeVariables[i], typeVariable)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }
}
