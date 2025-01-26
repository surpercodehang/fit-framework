/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import modelengine.fit.http.openapi3.swagger.entity.MediaType;
import modelengine.fit.http.openapi3.swagger.entity.RequestBody;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link RequestBody} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-26
 */
public class DefaultRequestBody implements RequestBody {
    private final String description;
    private final Map<String, MediaType> content;
    private final boolean required;

    public DefaultRequestBody(String description, Map<String, MediaType> content, boolean required) {
        this.description = description;
        this.content = content;
        this.required = required;
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
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("required", this.required);
        if (StringUtils.isNotBlank(this.description)) {
            builder.put("description", this.description);
        }
        if (MapUtils.isNotEmpty(this.content)) {
            builder.put("content",
                    this.content.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toJson())));
        }
        return builder.build();
    }

    /**
     * 表示 {@link RequestBody.Builder} 的默认实现。
     */
    public static class Builder implements RequestBody.Builder {
        private String description;
        private Map<String, MediaType> content;
        private boolean required;

        @Override
        public RequestBody.Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public RequestBody.Builder content(Map<String, MediaType> content) {
            this.content = content;
            return this;
        }

        @Override
        public RequestBody.Builder isRequired(boolean required) {
            this.required = required;
            return this;
        }

        @Override
        public RequestBody build() {
            return new DefaultRequestBody(this.description, this.content, this.required);
        }
    }
}
