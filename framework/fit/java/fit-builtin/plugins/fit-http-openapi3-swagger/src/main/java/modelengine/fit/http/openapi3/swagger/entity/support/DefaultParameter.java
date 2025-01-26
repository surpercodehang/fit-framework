/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.openapi3.swagger.entity.Parameter;
import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link Parameter} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public class DefaultParameter implements Parameter {
    private final String name;
    private final String in;
    private final String description;
    private final boolean required;
    private final boolean deprecated;
    private final Schema schema;

    private DefaultParameter(String name, String in, String description, boolean required, boolean deprecated,
            Schema schema) {
        this.name = notBlank(name, "The parameter name cannot be blank.");
        this.in = notBlank(in, "The parameter position cannot be blank.");
        this.description = description;
        this.required = required;
        this.deprecated = deprecated;
        this.schema = schema;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String in() {
        return this.in;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public boolean isDeprecated() {
        return this.deprecated;
    }

    @Override
    public Schema schema() {
        return this.schema;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get()
                .put("name", this.name)
                .put("in", this.in)
                .put("required", this.required)
                .put("deprecated", this.deprecated);
        if (StringUtils.isNotBlank(this.description)) {
            builder.put("description", this.description);
        }
        if (this.schema != null) {
            builder.put("schema", this.schema.toJson());
        }
        return builder.build();
    }

    /**
     * 表示 {@link Parameter.Builder} 的默认实现。
     */
    public static class Builder implements Parameter.Builder {
        private String name;
        private String in;
        private String description;
        private boolean required;
        private boolean deprecated;
        private Schema schema;

        @Override
        public Parameter.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Parameter.Builder in(String in) {
            this.in = in;
            return this;
        }

        @Override
        public Parameter.Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Parameter.Builder isRequired(boolean required) {
            this.required = required;
            return this;
        }

        @Override
        public Parameter.Builder isDeprecated(boolean deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        @Override
        public Parameter.Builder schema(Schema schema) {
            this.schema = schema;
            return this;
        }

        @Override
        public Parameter build() {
            return new DefaultParameter(this.name,
                    this.in,
                    this.description,
                    this.required,
                    this.deprecated,
                    this.schema);
        }
    }
}
