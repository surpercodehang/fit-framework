/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.value.PropertyValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * 表示 {@link RequestBodyMapperResolver} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-27
 */
@DisplayName("测试 RequestBodyMapperResolver 类")
class RequestBodyMapperResolverTest {
    private final AnnotationMetadataResolver annotationResolver = mock(AnnotationMetadataResolver.class);
    private final RequestBodyMapperResolver bodyMapperResolver = new RequestBodyMapperResolver(this.annotationResolver);

    @Test
    @DisplayName("当提供参数时，返回参数中的内容体映射器")
    void givenParamThenReturnParameterMapper() {
        final Parameter parameter =
                ReflectionUtils.getDeclaredMethod(HttpParamTest.class, "requestBody", String.class).getParameters()[0];
        final AnnotationMetadata annotations = mock(AnnotationMetadata.class);
        final RequestParam requestParam = Mockito.mock(RequestParam.class);
        when(annotations.getAnnotation(any())).thenReturn(requestParam);
        when(requestParam.required()).thenReturn(false);
        when(requestParam.defaultValue()).thenReturn(null);
        final Optional<PropertyValueMapper> parameterMapper =
                this.bodyMapperResolver.resolve(PropertyValue.createParameterValue(parameter), annotations);
        assertThat(parameterMapper).isPresent();
    }

    @Test
    @DisplayName("获取需要解析的注解的类型")
    void shouldReturnAnnotation() {
        final Class<? extends Annotation> annotation = this.bodyMapperResolver.getAnnotation();
        assertThat(annotation).isEqualTo(RequestBody.class);
    }
}
