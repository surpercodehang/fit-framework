/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fit.http.openapi3.swagger.util.SchemaTypeUtils;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 {@link Schema} 的对象实现。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public class ObjectSchema extends AbstractSchema {
    private final List<String> required = new ArrayList<>();
    private final Map<String, Schema> properties = new HashMap<>();

    public ObjectSchema(String name, Type type, String description, List<String> examples) {
        super(name, type, description, examples);
    }

    /**
     * 添加指定的格式样例。
     *
     * @param name 表示待添加的格式样例的字段名的 {@link String}。
     * @param schema 表示待添加的格式样例的 {@link Schema}。
     */
    public void addSchema(String name, Schema schema) {
        this.addSchema(name, schema, false);
    }

    /**
     * 添加指定的格式样例。
     *
     * @param name 表示待添加的格式样例的字段名的 {@link String}。
     * @param schema 表示待添加的格式样例的 {@link Schema}。
     * @param required 表示待添加的格式样例是否必须的标志的 {@code boolean}。
     */
    public void addSchema(String name, Schema schema, boolean required) {
        if (schema == null) {
            return;
        }
        this.properties.put(name, schema);
        if (required) {
            this.required.add(name);
        }
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", "object");
        if (CollectionUtils.isNotEmpty(this.required)) {
            builder.put("required", this.required);
        }
        if (MapUtils.isNotEmpty(this.properties)) {
            builder.put("properties",
                    this.properties.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toJson())));
        }
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        return builder.build();
    }

    /**
     * 通过名字、类型和样例列表来创建一个对象的格式样例。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @param description 表示说明信息的 {@link String}。
     * @param examples 表示样例列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示创建出来的对象格式样例的 {@link ObjectSchema}。
     */
    public static ObjectSchema create(Type type, String description, List<String> examples) {
        ObjectSchema objectSchema = new ObjectSchema(SchemaTypeUtils.getTypeName(type), type, description, examples);
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = cast(type);
            Class<?> rawType = cast(parameterizedType.getRawType());
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            generateProperties(objectSchema, rawType, actualTypeArguments);
        } else if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            generateProperties(objectSchema, clazz, null);
        }
        return objectSchema;
    }

    private static void generateProperties(ObjectSchema objectSchema, Class<?> clazz, Type[] actualTypeArguments) {
        TypeVariable<?>[] typeVariables = clazz.getTypeParameters();
        for (Field field : ReflectionUtils.getDeclaredFields(clazz, true)) {
            Property property = field.getDeclaredAnnotation(Property.class);
            List<String> fieldExamples = new ArrayList<>();
            String propertyDescription = StringUtils.EMPTY;
            if (property != null) {
                fieldExamples.add(property.example());
                propertyDescription = property.description();
            }
            Type fieldType = field.getGenericType();
            if (fieldType instanceof TypeVariable && actualTypeArguments != null) {
                int index = findTypeVariableIndex(cast(fieldType),
                        typeVariables).orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "Could not find matching type variable. [field={0}]",
                        field.getName())));
                Type actualTypeArgument = actualTypeArguments[index];
                Schema schema = Schema.create(actualTypeArgument, propertyDescription, fieldExamples);
                objectSchema.addSchema(field.getName(), schema);
            } else {
                Schema schema = Schema.create(fieldType, propertyDescription, fieldExamples);
                objectSchema.addSchema(field.getName(), schema);
            }
        }
    }

    private static Optional<Integer> findTypeVariableIndex(TypeVariable<?> typeVariable,
            TypeVariable<?>[] typeVariables) {
        for (int i = 0; i < typeVariables.length; i++) {
            if (Objects.equals(typeVariables[i], typeVariable)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
