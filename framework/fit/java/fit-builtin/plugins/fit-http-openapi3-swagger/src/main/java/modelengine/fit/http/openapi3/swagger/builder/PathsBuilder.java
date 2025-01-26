/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.builder;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.openapi3.swagger.EntityBuilder;
import modelengine.fit.http.openapi3.swagger.entity.MediaType;
import modelengine.fit.http.openapi3.swagger.entity.Operation;
import modelengine.fit.http.openapi3.swagger.entity.Parameter;
import modelengine.fit.http.openapi3.swagger.entity.PathItem;
import modelengine.fit.http.openapi3.swagger.entity.Paths;
import modelengine.fit.http.openapi3.swagger.entity.RequestBody;
import modelengine.fit.http.openapi3.swagger.entity.Response;
import modelengine.fit.http.openapi3.swagger.entity.Responses;
import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fit.http.openapi3.swagger.entity.support.ObjectSchema;
import modelengine.fit.http.openapi3.swagger.util.SchemaTypeUtils;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.server.HttpDispatcher;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.ReflectibleMappingHandler;
import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fit.http.server.handler.Source;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.AnnotationUtils;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 {@link Paths} 的构建器。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class PathsBuilder extends AbstractBuilder implements EntityBuilder<Paths> {
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String TEXT_PLAIN = "text/plain";

    PathsBuilder(BeanContainer container) {
        super(container);
    }

    @Override
    public Paths build() {
        Paths paths = Paths.create();
        this.buildOperationList().forEach(operation -> {
            if (paths.contains(operation.path())) {
                paths.get(operation.path()).put(operation.method(), operation);
            } else {
                PathItem pathItem = PathItem.create(operation.path());
                pathItem.put(operation.method(), operation);
                paths.put(operation.path(), pathItem);
            }
        });
        return paths;
    }

    private List<Operation> buildOperationList() {
        return this.getHttpDispatcher()
                .map(HttpDispatcher::getHttpHandlersMapping)
                .map(this::buildOperationList)
                .orElseGet(Collections::emptyList);
    }

    private List<Operation> buildOperationList(Map<HttpRequestMethod, List<HttpHandler>> httpHandlers) {
        List<Operation> operations = new ArrayList<>();
        for (Map.Entry<HttpRequestMethod, List<HttpHandler>> entry : httpHandlers.entrySet()) {
            for (HttpHandler handler : entry.getValue()) {
                if (this.isHandlerIgnored(handler)) {
                    continue;
                }
                ReflectibleMappingHandler actualHandler = cast(handler);
                operations.add(Operation.custom()
                        .method(entry.getKey())
                        .path(actualHandler.pathPattern())
                        .tags(Collections.singleton(actualHandler.group()))
                        .summary(actualHandler.summary())
                        .description(actualHandler.description())
                        .parameters(this.parameters(actualHandler.propertyValueMetadata()))
                        .requestBody(this.requestBody(actualHandler.propertyValueMetadata()))
                        .responses(this.responses(actualHandler))
                        .build());
            }
        }
        return operations;
    }

    private List<Parameter> parameters(List<PropertyValueMetadata> metadataList) {
        return metadataList.stream()
                .filter(metadata -> !metadata.in().isInBody())
                .map(PathsBuilder::parameter)
                .collect(Collectors.toList());
    }

    private static Parameter parameter(PropertyValueMetadata metadata) {
        List<String> examples;
        if (StringUtils.isBlank(metadata.example())) {
            examples = Collections.emptyList();
        } else {
            examples = Collections.singletonList(metadata.example());
        }
        return Parameter.custom()
                .name(metadata.name())
                .in(StringUtils.toLowerCase(metadata.in().name()))
                .description(metadata.description())
                .isRequired(metadata.isRequired())
                .schema(Schema.create(metadata.name(), metadata.type(), metadata.description(), examples))
                .build();
    }

    private RequestBody requestBody(List<PropertyValueMetadata> metadataList) {
        List<PropertyValueMetadata> bodyList =
                metadataList.stream().filter(metadata -> metadata.in().isInBody()).collect(Collectors.toList());
        Optional<RequestBody> opRequestBody =
                this.lookupBodyWithoutName(bodyList).map(this::createRequestBodyFromBodyWithoutName);
        if (opRequestBody.isPresent()) {
            return opRequestBody.get();
        }
        List<PropertyValueMetadata> list = this.lookupBodyWithName(bodyList);
        MediaType mediaType = null;
        if (CollectionUtils.isNotEmpty(list)) {
            mediaType = this.createMediaTypeFromBodyWithName(list);
        } else {
            list = this.lookupForm(bodyList);
            if (CollectionUtils.isNotEmpty(list)) {
                mediaType = this.createMediaTypeFromForm(list);
            }
        }
        if (mediaType == null) {
            return null;
        }
        return RequestBody.custom()
                .content(MapBuilder.<String, MediaType>get().put(mediaType.name(), mediaType).build())
                .build();
    }

    private Responses responses(ReflectibleMappingHandler handler) {
        Responses responses = Responses.create();
        responses.put(String.valueOf(handler.statusCode()),
                Response.custom()
                        .description(handler.returnDescription())
                        .content(this.content(handler.method().getGenericReturnType(), handler.method()))
                        .build());
        return responses;
    }

    private Optional<PropertyValueMetadata> lookupBodyWithoutName(List<PropertyValueMetadata> metadataList) {
        for (PropertyValueMetadata metadata : metadataList) {
            if (metadata.in() == Source.BODY && StringUtils.isBlank(metadata.name())) {
                return Optional.of(metadata);
            }
        }
        return Optional.empty();
    }

    private List<PropertyValueMetadata> lookupBodyWithName(List<PropertyValueMetadata> metadataList) {
        return metadataList.stream()
                .filter(metadata -> metadata.in() == Source.BODY)
                .filter(metadata -> StringUtils.isNotBlank(metadata.name()))
                .collect(Collectors.toList());
    }

    private List<PropertyValueMetadata> lookupForm(List<PropertyValueMetadata> metadataList) {
        return metadataList.stream().filter(metadata -> metadata.in() == Source.FORM).collect(Collectors.toList());
    }

    private RequestBody createRequestBodyFromBodyWithoutName(PropertyValueMetadata metadata) {
        MediaType mediaType = MediaType.custom()
                .name(APPLICATION_JSON)
                .schema(Schema.create(metadata.type(),
                        metadata.description(),
                        Collections.singletonList(metadata.example())))
                .build();
        return RequestBody.custom()
                .description(metadata.description())
                .content(MapBuilder.<String, MediaType>get().put(mediaType.name(), mediaType).build())
                .isRequired(metadata.isRequired())
                .build();
    }

    private MediaType createMediaTypeFromBodyWithName(List<PropertyValueMetadata> metadataList) {
        return createMediaType(metadataList, APPLICATION_JSON);
    }

    private MediaType createMediaTypeFromForm(List<PropertyValueMetadata> metadataList) {
        return createMediaType(metadataList, APPLICATION_X_WWW_FORM_URLENCODED);
    }

    private static MediaType createMediaType(List<PropertyValueMetadata> metadataList, String mediaType) {
        ObjectSchema objectSchema =
                new ObjectSchema(StringUtils.EMPTY, Object.class, StringUtils.EMPTY, Collections.emptyList());
        for (PropertyValueMetadata metadata : metadataList) {
            Schema schema = Schema.create(metadata.type(),
                    metadata.description(),
                    Collections.singletonList(metadata.example()));
            objectSchema.addSchema(metadata.name(), schema, metadata.isRequired());
        }
        return MediaType.custom().name(mediaType).schema(objectSchema).build();
    }

    private MediaType createMediaTypeFromReturnType(Type type, String description, List<String> examples) {
        String mediaType;
        if (type == void.class || type == Void.class) {
            mediaType = TEXT_PLAIN;
        } else if (isApplicationJson(type)) {
            mediaType = APPLICATION_JSON;
        } else {
            mediaType = TEXT_PLAIN;
        }
        return MediaType.custom().name(mediaType).schema(Schema.create(type, description, examples)).build();
    }

    private Map<String, MediaType> content(Type type, AnnotatedElement element) {
        List<String> examples = Collections.emptyList();
        String description = StringUtils.EMPTY;
        Optional<Property> annotation = AnnotationUtils.getAnnotation(this.getContainer(), element, Property.class);
        if (annotation.isPresent()) {
            examples = Collections.singletonList(annotation.get().example());
            description = annotation.get().description();
        }
        MediaType mediaType = this.createMediaTypeFromReturnType(type, description, examples);
        return MapBuilder.<String, MediaType>get().put(mediaType.name(), mediaType).build();
    }

    private static boolean isApplicationJson(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = cast(type);
            return Map.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz) || clazz.isArray();
        }
        return SchemaTypeUtils.isObjectType(type);
    }
}
