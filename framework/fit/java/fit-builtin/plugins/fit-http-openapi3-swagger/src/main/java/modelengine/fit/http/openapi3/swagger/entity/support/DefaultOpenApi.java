/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.openapi3.swagger.entity.Components;
import modelengine.fit.http.openapi3.swagger.entity.Info;
import modelengine.fit.http.openapi3.swagger.entity.OpenApi;
import modelengine.fit.http.openapi3.swagger.entity.Paths;
import modelengine.fit.http.openapi3.swagger.entity.Tag;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link OpenApi} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public class DefaultOpenApi implements OpenApi {
    private final String openApi;
    private final Info info;
    private final Paths paths;
    private final List<Tag> tags;
    private final Components components;

    public DefaultOpenApi(String openApi, Info info, Paths paths, List<Tag> tags, Components components) {
        this.openApi = notBlank(openApi, "The openapi cannot be blank.");
        this.info = notNull(info, "The info cannot be null.");
        this.paths = paths;
        this.tags = tags;
        this.components = components;
    }

    @Override
    public String openapi() {
        return this.openApi;
    }

    @Override
    public Info info() {
        return this.info;
    }

    @Override
    public Paths paths() {
        return this.paths;
    }

    @Override
    public List<Tag> tags() {
        return this.tags;
    }

    @Override
    public Components components() {
        return this.components;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder =
                MapBuilder.<String, Object>get().put("openapi", this.openApi).put("info", this.info.toJson());
        if (this.paths != null) {
            builder.put("paths", this.paths.toJson());
        }
        if (CollectionUtils.isNotEmpty(this.tags)) {
            builder.put("tags", this.tags.stream().map(Tag::toJson).collect(Collectors.toList()));
        }
        if (this.components != null) {
            builder.put("components", this.components.toJson());
        }
        return builder.build();
    }

    /**
     * 表示 {@link OpenApi.Builder} 的默认实现。
     */
    public static class Builder implements OpenApi.Builder {
        private String openApi;
        private Info info;
        private Paths paths;
        private List<Tag> tags;
        private Components components;

        @Override
        public OpenApi.Builder openapi(String openApi) {
            this.openApi = openApi;
            return this;
        }

        @Override
        public OpenApi.Builder info(Info info) {
            this.info = info;
            return this;
        }

        @Override
        public OpenApi.Builder paths(Paths paths) {
            this.paths = paths;
            return this;
        }

        @Override
        public OpenApi.Builder tags(List<Tag> tags) {
            this.tags = tags;
            return this;
        }

        @Override
        public OpenApi.Builder components(Components components) {
            this.components = components;
            return this;
        }

        @Override
        public OpenApi build() {
            return new DefaultOpenApi(this.openApi, this.info, this.paths, this.tags, this.components);
        }
    }
}
