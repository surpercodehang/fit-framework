/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.openapi3.swagger.entity.Tag;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link Tag} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class DefaultTag implements Tag {
    private final String name;
    private final String description;

    private DefaultTag(String name, String description) {
        this.name = notBlank(name, "The tag name cannot be blank.");
        this.description = description;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("name", this.name);
        if (StringUtils.isNotBlank(this.description)) {
            builder.put("description", this.description);
        }
        return builder.build();
    }

    /**
     * 表示 {@link Tag.Builder} 的默认实现。
     */
    public static class Builder implements Tag.Builder {
        private String name;
        private String description;

        @Override
        public Tag.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Tag.Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Tag build() {
            return new DefaultTag(this.name, this.description);
        }
    }
}
