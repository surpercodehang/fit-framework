/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link Schema} 的数组实现。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public class ArraySchema extends AbstractSchema {
    private final Schema items;

    public ArraySchema(String name, Type type, String description, List<String> examples) {
        super(name, type, description, examples);
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = cast(type);
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.items = Schema.create(actualTypeArguments[0], description, examples);
        } else {
            Class<?> clazz = TypeUtils.toClass(type);
            this.items = Schema.create(clazz.getComponentType(), description, examples);
        }
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", "array");
        if (this.items != null) {
            builder.put("items", this.items.toJson());
        }
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        return builder.build();
    }
}
