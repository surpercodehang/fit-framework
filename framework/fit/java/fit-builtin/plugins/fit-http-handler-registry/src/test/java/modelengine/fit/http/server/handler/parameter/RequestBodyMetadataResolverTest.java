/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.value.PropertyValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 表示 {@link RequestBodyMetadataResolver} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-27
 */
@DisplayName("测试 RequestBodyMetadataResolver 类")
class RequestBodyMetadataResolverTest {
    private final AnnotationMetadataResolver annotationResolver = mock(AnnotationMetadataResolver.class);
    private final RequestBodyMetadataResolver bodyMetadataResolver =
            new RequestBodyMetadataResolver(this.annotationResolver);

    @Test
    @DisplayName("当提供参数时，返回参数中的内容体元信息")
    void givenParamThenReturnParameterBody() {
        final Parameter parameter =
                ReflectionUtils.getDeclaredMethod(HttpParamTest.class, "requestBody", String.class).getParameters()[0];
        final AnnotationMetadata annotations = mock(AnnotationMetadata.class);
        final RequestBody requestParam = parameter.getAnnotation(RequestBody.class);
        when(annotations.getAnnotation(RequestBody.class)).thenReturn(requestParam);
        final List<PropertyValueMetadata> propertyValueMetadata =
                this.bodyMetadataResolver.resolve(PropertyValue.createParameterValue(parameter), annotations);
        assertThat(propertyValueMetadata.size()).isEqualTo(1);
        assertThat(propertyValueMetadata.get(0)).returns(false, PropertyValueMetadata::isRequired);
    }

    @Test
    @DisplayName("获取需要解析的注解的类型")
    void shouldReturnAnnotation() {
        final Class<? extends Annotation> annotation = this.bodyMetadataResolver.getAnnotation();
        assertThat(annotation).isEqualTo(RequestBody.class);
    }
}

