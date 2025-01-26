/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema.support;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.json.schema.JsonSchema;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表示 {@link JsonSchema} 的数组实现。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
public class ArraySchema extends AbstractJsonSchema {
    private final AtomicReference<JsonSchema> items = new AtomicReference<>();

    ArraySchema(Type type) {
        super(type);
    }

    @Override
    public Map<String, Object> toJsonObject() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", "array");
        if (this.items.get() != null) {
            builder.put("items", this.items.get().toJsonObject());
        }
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        return builder.build();
    }

    /**
     * 创建一个数组的格式规范。
     *
     * @param type 表示数组类型的 {@link Type}。
     * @param referencedPrefix 表示如果存在引用，其引用前缀的 {@link String}。
     * @param referencedSchemas 表示引用数据结果的 {@link Map}{@code <}{@link Type}{@code , }{@link ObjectSchema}{@code >}。
     * @return 表示创建的数组的格式规范的 {@link ArraySchema}。
     */
    public static ArraySchema create(Type type, String referencedPrefix, Map<Type, ObjectSchema> referencedSchemas) {
        ArraySchema schema = new ArraySchema(type);
        schema.setItems(ObjectUtils.nullIf(referencedPrefix, StringUtils.EMPTY),
                ObjectUtils.getIfNull(referencedSchemas, Collections::emptyMap));
        return schema;
    }

    void setItems(String referencedPrefix, Map<Type, ObjectSchema> referencedSchemas) {
        if (this.items.get() != null) {
            return;
        }
        Type itemsType;
        if (this.type() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = cast(this.type());
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            // 泛型时，List<T> 的泛型参数必然只有 1 个。
            itemsType = actualTypeArguments[0];
        } else {
            Class<?> clazz = TypeUtils.toClass(this.type());
            // 数组时，数组主参数必然不为 null。
            itemsType = clazz.getComponentType();
        }
        itemsType = ObjectUtils.nullIf(itemsType, Map.class);
        JsonSchema itemsSchema;
        if (MapUtils.isNotEmpty(referencedSchemas) && referencedSchemas.containsKey(itemsType)) {
            itemsSchema = new ReferenceSchema(referencedPrefix, referencedSchemas.get(itemsType));
        } else {
            itemsSchema = JsonSchema.create(itemsType, nullIf(referencedPrefix, StringUtils.EMPTY), referencedSchemas);
        }
        this.items.compareAndSet(null, itemsSchema);
    }
}
