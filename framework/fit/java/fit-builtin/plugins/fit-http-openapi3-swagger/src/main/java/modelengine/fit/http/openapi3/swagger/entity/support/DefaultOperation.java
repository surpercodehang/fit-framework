/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.openapi3.swagger.entity.Operation;
import modelengine.fit.http.openapi3.swagger.entity.Parameter;
import modelengine.fit.http.openapi3.swagger.entity.RequestBody;
import modelengine.fit.http.openapi3.swagger.entity.Responses;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link Operation} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public class DefaultOperation implements Operation {
    private final String path;
    private final HttpRequestMethod method;
    private final Set<String> tags;
    private final String summary;
    private final String description;
    private final String operationId;
    private final List<Parameter> parameters;
    private final RequestBody requestBody;
    private final Responses responses;

    public DefaultOperation(String path, HttpRequestMethod method, Set<String> tags, String summary, String description,
            String operationId, List<Parameter> parameters, RequestBody requestBody, Responses responses) {
        this.path = notBlank(path, "The path cannot be blank.");
        this.method = notNull(method, "The method cannot be null.");
        this.tags = tags;
        this.summary = summary;
        this.description = description;
        this.operationId = operationId;
        this.parameters = parameters;
        this.requestBody = requestBody;
        this.responses = responses;
    }

    @Override
    public String path() {
        return this.path;
    }

    @Override
    public HttpRequestMethod method() {
        return this.method;
    }

    @Override
    public Set<String> tags() {
        return this.tags;
    }

    @Override
    public String summary() {
        return this.summary;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String operationId() {
        return this.operationId;
    }

    @Override
    public List<Parameter> parameters() {
        return this.parameters;
    }

    @Override
    public RequestBody requestBody() {
        return this.requestBody;
    }

    @Override
    public Responses responses() {
        return this.responses;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder =
                MapBuilder.<String, Object>get().put("operationId", this.method + " " + this.path);
        if (CollectionUtils.isNotEmpty(this.tags)) {
            builder.put("tags", this.tags);
        }
        if (StringUtils.isNotBlank(this.summary)) {
            builder.put("summary", this.summary);
        }
        if (StringUtils.isNotBlank(this.description)) {
            builder.put("description", this.description);
        }
        if (CollectionUtils.isNotEmpty(this.parameters)) {
            builder.put("parameters", this.parameters.stream().map(Parameter::toJson).collect(Collectors.toList()));
        }
        if (this.requestBody != null) {
            builder.put("requestBody", this.requestBody.toJson());
        }
        if (this.responses != null) {
            builder.put("responses", this.responses.toJson());
        }
        return builder.build();
    }

    /**
     * 表示 {@link Operation.Builder} 的默认实现。
     */
    public static class Builder implements Operation.Builder {
        private String path;
        private HttpRequestMethod method;
        private Set<String> tags;
        private String summary;
        private String description;
        private String operationId;
        private List<Parameter> parameters;
        private RequestBody requestBody;
        private Responses responses;

        @Override
        public Operation.Builder path(String path) {
            this.path = path;
            return this;
        }

        @Override
        public Operation.Builder method(HttpRequestMethod method) {
            this.method = method;
            return this;
        }

        @Override
        public Operation.Builder tags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        @Override
        public Operation.Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        @Override
        public Operation.Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Operation.Builder operationId(String operationId) {
            this.operationId = operationId;
            return this;
        }

        @Override
        public Operation.Builder parameters(List<Parameter> parameters) {
            this.parameters = parameters;
            return this;
        }

        @Override
        public Operation.Builder requestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        @Override
        public Operation.Builder responses(Responses responses) {
            this.responses = responses;
            return this;
        }

        @Override
        public Operation build() {
            return new DefaultOperation(this.path,
                    this.method,
                    this.tags,
                    this.summary,
                    this.description,
                    this.operationId,
                    this.parameters,
                    this.requestBody,
                    this.responses);
        }
    }
}
