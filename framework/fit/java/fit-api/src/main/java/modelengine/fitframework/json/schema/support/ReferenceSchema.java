/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.json.schema.JsonSchema;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link JsonSchema} 的引用实现。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
public class ReferenceSchema extends AbstractJsonSchema {
    private final String reference;
    private final JsonSchema schema;

    ReferenceSchema(String reference, JsonSchema schema) {
        super(schema);
        this.reference = nullIf(reference, StringUtils.EMPTY);
        this.schema = schema;
    }

    @Override
    public Map<String, Object> toJsonObject() {
        return MapBuilder.<String, Object>get()
                .put("type", "object")
                .put("#ref", this.reference + this.schema.name())
                .build();
    }
}
