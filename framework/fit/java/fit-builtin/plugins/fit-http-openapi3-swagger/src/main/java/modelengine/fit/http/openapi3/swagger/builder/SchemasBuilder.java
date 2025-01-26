/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.builder;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.openapi3.swagger.EntityBuilder;
import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fit.http.openapi3.swagger.util.SchemaTypeUtils;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.server.HttpDispatcher;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.ReflectibleMappingHandler;
import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link Map}{@code <}{@link String}{@code , }{@link Schema}{@code >} 的构建器。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class SchemasBuilder extends AbstractBuilder implements EntityBuilder<Map<String, Schema>> {
    SchemasBuilder(BeanContainer container) {
        super(container);
    }

    @Override
    public Map<String, Schema> build() {
        return this.getHttpDispatcher()
                .map(HttpDispatcher::getHttpHandlersMapping)
                .map(this::buildSchemaList)
                .orElse(null);
    }

    private Map<String, Schema> buildSchemaList(Map<HttpRequestMethod, List<HttpHandler>> httpHandlers) {
        List<Schema> schemaList = this.buildSchemas(this.getSchemaTypes(httpHandlers));
        Map<String, Schema> schemas = new HashMap<>();
        for (Schema schema : schemaList) {
            schemas.put(schema.name(), schema);
        }
        return schemas;
    }

    private Set<Type> getSchemaTypes(Map<HttpRequestMethod, List<HttpHandler>> httpHandlers) {
        Set<Type> schemaTypes = new HashSet<>();
        for (Map.Entry<HttpRequestMethod, List<HttpHandler>> entry : httpHandlers.entrySet()) {
            for (HttpHandler handler : entry.getValue()) {
                if (this.isHandlerIgnored(handler)) {
                    continue;
                }
                ReflectibleMappingHandler actualHandler = cast(handler);
                schemaTypes.addAll(getSchemaTypes(actualHandler));
            }
        }
        return schemaTypes;
    }

    private static Set<Type> getSchemaTypes(ReflectibleMappingHandler handler) {
        Set<Type> schemaTypes = handler.propertyValueMetadata()
                .stream()
                .map(PropertyValueMetadata::type)
                .map(SchemaTypeUtils::getObjectTypes)
                .reduce(new HashSet<>(), (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                });
        if (SchemaTypeUtils.isObjectType(handler.method().getGenericReturnType())) {
            schemaTypes.addAll(SchemaTypeUtils.getObjectTypes(handler.method().getGenericReturnType()));
        }
        if (SchemaTypeUtils.isArrayType(handler.method().getGenericReturnType())) {
            schemaTypes.addAll(SchemaTypeUtils.getObjectTypes(handler.method().getGenericReturnType()));
        }
        return schemaTypes;
    }

    private List<Schema> buildSchemas(Set<Type> schemaTypes) {
        return schemaTypes.stream().map(type -> {
            if (SchemaTypeUtils.isObjectType(type)) {
                return Schema.fromObject(type, StringUtils.EMPTY, Collections.emptyList());
            } else if (SchemaTypeUtils.isArrayType(type)) {
                return Schema.fromArray(type, StringUtils.EMPTY, Collections.emptyList());
            } else if (SchemaTypeUtils.isEnumType(type)) {
                return Schema.fromEnum(type, StringUtils.EMPTY, Collections.emptyList());
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
