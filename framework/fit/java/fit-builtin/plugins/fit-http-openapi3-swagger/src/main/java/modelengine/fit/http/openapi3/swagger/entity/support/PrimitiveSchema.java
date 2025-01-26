/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link Schema} 的基本类型实现。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public class PrimitiveSchema extends AbstractSchema {
    private final String type;
    private final String format;

    public PrimitiveSchema(String name, Type type, String description, List<String> examples) {
        super(name, type, description, examples);
        Class<?> clazz;
        try {
            clazz = TypeUtils.toClass(type);
        } catch (Exception e) {
            clazz = Object.class;
        }
        Schema.Info info = Schema.Info.from(clazz);
        this.type = info.type();
        this.format = info.format();
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", this.type);
        if (StringUtils.isNotBlank(this.format)) {
            builder.put("format", this.format);
        }
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        if (CollectionUtils.isNotEmpty(this.examples())) {
            builder.put("examples", this.examples());
        }
        return builder.build();
    }
}
