/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fit.http.server.handler.support.ParamValue;
import modelengine.fit.http.server.handler.support.QueryFetcher;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;

import java.lang.annotation.Annotation;

/**
 * 表示解析带有 {@link RequestQuery} 注解的参数的 {@link PropertyValueMapperResolver}。
 *
 * @author 邬涨财
 * @author 季聿阶
 * @since 2023-11-20
 */
public class RequestQueryMapperResolver extends AbstractRequestParamMapperResolver {
    /**
     * 通过注解解析器来实例化 {@link RequestQueryMapperResolver}。
     *
     * @param annotationResolver 表示注解解析器的 {@link AnnotationMetadataResolver}。
     * @throws IllegalArgumentException 当 {@code annotationResolver} 为 {@code null} 时。
     */
    public RequestQueryMapperResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestQuery.class;
    }

    @Override
    protected SourceFetcher createSourceFetcher(RequestParam requestParam) {
        return new QueryFetcher(ParamValue.custom()
                .name(requestParam.name())
                .required(requestParam.required())
                .defaultValue(requestParam.defaultValue())
                .in(requestParam.in())
                .build());
    }
}
