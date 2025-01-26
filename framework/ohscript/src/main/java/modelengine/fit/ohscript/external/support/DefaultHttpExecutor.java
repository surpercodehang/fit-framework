/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.ohscript.external.HttpExecutor;
import modelengine.fit.ohscript.script.errors.ScriptExecutionException;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;
import modelengine.fit.ohscript.util.ValueUtils;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 {@link HttpExecutor} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-12-21
 */
public class DefaultHttpExecutor implements HttpExecutor {
    private static final String RESPONSE_HEADERS = "headers";

    private static final String RESPONSE_ENTITY = "body";

    private final HttpClassicClient httpClient;

    public DefaultHttpExecutor(HttpClassicClient httpClient) {
        this.httpClient = notNull(httpClient, "The http classic client cannot be null.");
    }

    @Override
    public Map<String, Object> execute(String method, String url, Map<String, ReturnValue> args) {
        HttpRequestMethod httpMethod = HttpRequestMethod.from(method);
        if (httpMethod == null) {
            throw new IllegalStateException(StringUtils.format("No http method. [httpMethod={0}]", method));
        }
        return this.doExecute(this.httpClient, httpMethod, url, args);
    }

    private Map<String, Object> doExecute(HttpClassicClient httpClient, HttpRequestMethod httpMethod, String url,
            Map<String, ReturnValue> args) {
        Map<String, Object> result = new HashMap<>();
        HttpClassicClientRequest request = httpClient.createRequest(httpMethod, url);
        this.setRequestHeaders(request, args);
        this.setRequestEntity(request, args);
        try (HttpClassicClientResponse<Object> response = request.exchange()) {
            this.setResponseHeaders(response, result);
            this.setResponseEntity(response, result);
        } catch (IOException e) {
            throw new ScriptExecutionException("Failed to execute by http.", e);
        }
        return result;
    }

    private void setRequestHeaders(HttpClassicClientRequest request, Map<String, ReturnValue> args) {
        if (!args.containsKey(REQUEST_HEADERS)) {
            return;
        }
        Map<String, ReturnValue> headerValues =
                getIfNull(cast(args.get(REQUEST_HEADERS).value()), Collections::emptyMap);
        for (Map.Entry<String, ReturnValue> entry : headerValues.entrySet()) {
            request.headers().add(ValueUtils.getActualKey(entry.getKey()), String.valueOf(entry.getValue().value()));
        }
    }

    private void setRequestEntity(HttpClassicClientRequest request, Map<String, ReturnValue> args) {
        if (!args.containsKey(REQUEST_ENTITY)) {
            return;
        }
        Map<String, ReturnValue> entityValues =
                getIfNull(cast(args.get(REQUEST_ENTITY).value()), Collections::emptyMap);
        if (!entityValues.containsKey(REQUEST_ENTITY_DATA)) {
            return;
        }
        String entityType = cast(entityValues.get(REQUEST_ENTITY_TYPE).value());
        Object entityData = entityValues.get(REQUEST_ENTITY_DATA).value();
        EntityType.from(entityType).ifPresent(type -> {
            if (type == EntityType.TEXT) {
                request.entity(Entity.createText(request, ObjectUtils.<String>cast(entityData)));
            } else if (type == EntityType.JSON) {
                request.entity(Entity.createObject(request, ValueUtils.fromOhScript(entityData)));
            } else if (type == EntityType.FORM) {
                request.entity(Entity.createMultiValue(request, this.createMultiValueMap(entityData)));
            } else {
                String message = StringUtils.format("Not supported entity type. [entityType={0}]", entityType);
                throw new ScriptExecutionException(message);
            }
        });
    }

    private MultiValueMap<String, String> createMultiValueMap(Object entityData) {
        Map<String, ReturnValue> form = cast(entityData);
        Map<String, List<String>> innerMap = form.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> ValueUtils.getActualKey(entry.getKey()),
                        entry -> Collections.singletonList(String.valueOf(entry.getValue().value()))));
        return MultiValueMap.create(innerMap);
    }

    private void setResponseHeaders(HttpClassicClientResponse<Object> response, Map<String, Object> result) {
        Map<String, List<String>> headers = new HashMap<>();
        result.put(RESPONSE_HEADERS, headers);
        for (String header : response.headers().names()) {
            headers.put(header, response.headers().all(header));
        }
    }

    private void setResponseEntity(HttpClassicClientResponse<Object> response, Map<String, Object> result) {
        response.entity().ifPresent(entity -> {
            if (entity instanceof ObjectEntity) {
                ObjectEntity<?> objectEntity = cast(entity);
                result.put(RESPONSE_ENTITY, objectEntity.object());
            } else if (entity instanceof TextEntity) {
                TextEntity textEntity = cast(entity);
                result.put(RESPONSE_ENTITY, textEntity.content());
            } else {
                String message =
                        StringUtils.format("Not supported content type. [contentType={0}]", entity.resolvedMimeType());
                throw new ScriptExecutionException(message);
            }
        });
    }

    private enum EntityType {
        TEXT,
        JSON,
        FORM;

        /**
         * 将指定消息体类型转换成枚举类型。
         *
         * @param type 表示指定消息体类型的 {@link String}。
         * @return 表示转换后的消息体枚举类型的 {@link Optional}{@code <}{@link EntityType}{@code >}。
         */
        public static Optional<EntityType> from(String type) {
            for (EntityType entityType : EntityType.values()) {
                if (StringUtils.equalsIgnoreCase(entityType.name(), type)) {
                    return Optional.of(entityType);
                }
            }
            return Optional.empty();
        }
    }
}
