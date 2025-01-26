/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import modelengine.fit.http.openapi3.swagger.entity.MediaType;
import modelengine.fit.http.openapi3.swagger.entity.Response;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link Response} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-27
 */
public class DefaultResponse implements Response {
    private final String description;
    private final Map<String, MediaType> content;

    public DefaultResponse(String description, Map<String, MediaType> content) {
        this.description = ObjectUtils.nullIf(description, StringUtils.EMPTY);
        this.content = content;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public Map<String, MediaType> content() {
        return this.content;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("description", this.description);
        if (MapUtils.isNotEmpty(this.content)) {
            builder.put("content",
                    this.content.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toJson())));
        }
        return builder.build();
    }

    /**
     * 表示 {@link Response.Builder} 的默认实现。
     */
    public static class Builder implements Response.Builder {
        private String description;
        private Map<String, MediaType> content;

        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Builder content(Map<String, MediaType> content) {
            this.content = content;
            return this;
        }

        @Override
        public Response build() {
            return new DefaultResponse(this.description, this.content);
        }
    }
}
