/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestCookie;
import modelengine.fit.http.annotation.RequestForm;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMetadataResolver;
import modelengine.fit.http.server.handler.Source;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fit.http.server.handler.support.CookieFetcher;
import modelengine.fit.http.server.handler.support.FormUrlEncodedEntityFetcher;
import modelengine.fit.http.server.handler.support.HeaderFetcher;
import modelengine.fit.http.server.handler.support.MultiSourcesPropertyValueMapper;
import modelengine.fit.http.server.handler.support.ObjectEntityFetcher;
import modelengine.fit.http.server.handler.support.ParamValue;
import modelengine.fit.http.server.handler.support.PathVariableFetcher;
import modelengine.fit.http.server.handler.support.QueryFetcher;
import modelengine.fit.http.server.handler.support.SourceFetcherInfo;
import modelengine.fit.http.server.handler.support.TypeTransformationPropertyValueMapper;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.json.schema.util.SchemaTypeUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 表示解析带有 {@link RequestBean} 注解的参数的 {@link PropertyValueMetadataResolver}。
 *
 * @author 邬涨财
 * @since 2023-11-14
 */
public class RequestBeanMapperResolver extends AbstractPropertyValueMapperResolver {
    /**
     * <p>考虑到 PathVariable 等请求映射参数注解带有 RequestParam 注解，所以在初始化 IS_ARRAY_MAPPING 时，需要将 RequestParam
     * 注解的 put 操作放置在最后；否则其他参数注解可能不会正确的从 IS_ARRAY_MAPPING 取出。</p>
     */
    private static final Map<Class<? extends Annotation>, Function<PropertyValue, Boolean>> IS_ARRAY_MAPPING =
            MapBuilder.<Class<? extends Annotation>, Function<PropertyValue, Boolean>>get(LinkedHashMap::new)
                    .put(PathVariable.class, (propertyValue -> false))
                    .put(RequestBody.class, (propertyValue -> false))
                    .put(RequestCookie.class, (propertyValue -> false))
                    .put(RequestForm.class, (propertyValue -> List.class.isAssignableFrom(propertyValue.getType())))
                    .put(RequestQuery.class, (propertyValue -> List.class.isAssignableFrom(propertyValue.getType())))
                    .put(RequestParam.class, (propertyValue -> List.class.isAssignableFrom(propertyValue.getType())))
                    .build();
    private static final Map<Source, Function<ParamValue, SourceFetcher>> SOURCE_FETCHER_MAPPING =
            MapBuilder.<Source, Function<ParamValue, SourceFetcher>>get()
                    .put(Source.QUERY, QueryFetcher::new)
                    .put(Source.HEADER, HeaderFetcher::new)
                    .put(Source.COOKIE, CookieFetcher::new)
                    .put(Source.PATH, PathVariableFetcher::new)
                    .put(Source.BODY, ObjectEntityFetcher::new)
                    .put(Source.FORM, FormUrlEncodedEntityFetcher::new)
                    .build();
    private static final String DESTINATION_NAME_SEPARATOR = ".";

    private final AnnotationMetadataResolver annotationResolver;

    /**
     * 通过注解解析器来实例化 {@link RequestBeanMapperResolver}。
     *
     * @param annotationResolver 表示注解解析器的 {@link AnnotationMetadataResolver}。
     * @throws IllegalArgumentException 当 {@code annotationResolver} 为 {@code null} 时。
     */
    public RequestBeanMapperResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
        this.annotationResolver = notNull(annotationResolver, "The annotation metadata resolver cannot be null.");
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestBean.class;
    }

    @Override
    protected Optional<PropertyValueMapper> resolve(PropertyValue propertyValue,
            AnnotationMetadata annotationMetadata) {
        List<SourceFetcherInfo> propertyValueMappers = this.getSourceFetcherInfos(propertyValue, StringUtils.EMPTY);
        return Optional.of(new TypeTransformationPropertyValueMapper(new MultiSourcesPropertyValueMapper(
                propertyValueMappers), propertyValue.getType()));
    }

    private List<SourceFetcherInfo> getSourceFetcherInfos(PropertyValue propertyValue, String destinationName) {
        List<SourceFetcherInfo> sourceFetcherInfos = new ArrayList<>();
        for (Field field : ReflectionUtils.getDeclaredFields(propertyValue.getType(), true)) {
            PropertyValue fieldPropertyValue = PropertyValue.createFieldValue(field);
            String fieldPath = destinationName + DESTINATION_NAME_SEPARATOR + fieldPropertyValue.getName();
            Optional<AnnotatedElement> element = fieldPropertyValue.getElement();
            if (!element.isPresent()) {
                continue;
            }
            AnnotationMetadata annotationMetadata = this.annotationResolver.resolve(element.get());
            if (SchemaTypeUtils.isObjectType(field.getType())) {
                sourceFetcherInfos.addAll(this.getSourceFetcherInfos(fieldPropertyValue, fieldPath));
                continue;
            }
            SourceFetcher sourceFetcher;
            boolean isArrayType;
            if (annotationMetadata.isAnnotationNotPresent(RequestParam.class)) {
                sourceFetcher = SOURCE_FETCHER_MAPPING.get(Source.QUERY)
                        .apply(ParamValue.custom().name(field.getName()).build());
                isArrayType = SchemaTypeUtils.isArrayType(field.getType());
            } else {
                RequestParam annotation = annotationMetadata.getAnnotation(RequestParam.class);
                sourceFetcher = this.getSourceFetcher(annotation);
                isArrayType = this.isArray(fieldPropertyValue, annotationMetadata);
            }
            sourceFetcherInfos.add(new SourceFetcherInfo(sourceFetcher,
                    fieldPath.substring(DESTINATION_NAME_SEPARATOR.length()),
                    isArrayType));
        }
        return sourceFetcherInfos;
    }

    private Boolean isArray(PropertyValue propertyValue, AnnotationMetadata annotationMetadata) {
        return IS_ARRAY_MAPPING.entrySet()
                .stream()
                .filter(entry -> annotationMetadata.isAnnotationPresent(entry.getKey()))
                .findFirst()
                .map(entry -> entry.getValue().apply(propertyValue))
                .orElseThrow(() -> new IllegalStateException("Failed to judge whether property value is array."));
    }

    private SourceFetcher getSourceFetcher(RequestParam requestParam) {
        Function<ParamValue, SourceFetcher> function = SOURCE_FETCHER_MAPPING.get(requestParam.in());
        return function.apply(ParamValue.custom()
                .name(requestParam.name())
                .in(requestParam.in())
                .defaultValue(requestParam.defaultValue())
                .required(requestParam.required())
                .build());
    }
}
