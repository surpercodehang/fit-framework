/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.openapi3.swagger.entity.MediaType;
import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 表示 {@link MediaType} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-26
 */
public class DefaultMediaType implements MediaType {
    private final String name;
    private final Schema schema;

    public DefaultMediaType(String name, Schema schema) {
        this.name = notBlank(name, "The media type name cannot be blank.");
        this.schema = schema;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Schema schema() {
        return this.schema;
    }

    @Override
    public Map<String, Object> toJson() {
        return MapBuilder.<String, Object>get().put("schema", this.schema.toJson()).build();
    }

    /**
     * 表示 {@link MediaType.Builder} 的默认实现。
     */
    public static class Builder implements MediaType.Builder {
        private String name;
        private Schema schema;

        @Override
        public MediaType.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public MediaType.Builder schema(Schema schema) {
            this.schema = schema;
            return this;
        }

        @Override
        public MediaType build() {
            return new DefaultMediaType(this.name, this.schema);
        }
    }
}
