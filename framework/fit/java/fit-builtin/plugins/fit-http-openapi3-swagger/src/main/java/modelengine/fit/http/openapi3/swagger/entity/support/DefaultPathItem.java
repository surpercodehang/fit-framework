/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.openapi3.swagger.entity.Operation;
import modelengine.fit.http.openapi3.swagger.entity.PathItem;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link PathItem} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-08-21
 */
public class DefaultPathItem implements PathItem {
    private final String path;
    private final Map<HttpRequestMethod, Operation> operations = new HashMap<>();

    public DefaultPathItem(String path) {
        this.path = notBlank(path, "The path of Path Item Object cannot be blank.");
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Map<HttpRequestMethod, Operation> getOperations() {
        return Collections.unmodifiableMap(this.operations);
    }

    @Override
    public void put(HttpRequestMethod method, Operation operation) {
        this.operations.put(method, operation);
    }

    @Override
    public Map<String, Object> toJson() {
        return this.operations.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> StringUtils.toLowerCase(entry.getKey().name()),
                        entry -> entry.getValue().toJson()));
    }
}
