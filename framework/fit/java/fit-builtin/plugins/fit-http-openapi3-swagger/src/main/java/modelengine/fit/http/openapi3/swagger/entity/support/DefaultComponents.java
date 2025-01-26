/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import modelengine.fit.http.openapi3.swagger.entity.Components;
import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link Components} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public class DefaultComponents implements Components {
    private final Map<String, Schema> schemas;

    public DefaultComponents(Map<String, Schema> schemas) {
        this.schemas = schemas;
    }

    @Override
    public Map<String, Schema> schemas() {
        return this.schemas;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.get();
        if (this.schemas != null) {
            builder.put("schemas",
                    this.schemas.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toJson())));
        }
        return builder.build();
    }

    /**
     * 表示 {@link Components.Builder} 的默认实现。
     */
    public static class Builder implements Components.Builder {
        private Map<String, Schema> schemas;

        @Override
        public Components.Builder schemas(Map<String, Schema> schemas) {
            this.schemas = schemas;
            return this;
        }

        @Override
        public Components build() {
            return new DefaultComponents(this.schemas);
        }
    }
}
