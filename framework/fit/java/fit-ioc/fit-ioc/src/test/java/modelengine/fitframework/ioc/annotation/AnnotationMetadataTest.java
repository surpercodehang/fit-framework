/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import modelengine.fitframework.ioc.annotation.circular.Circular;
import modelengine.fitframework.ioc.annotation.circular.CircularAnnotationConsumer;
import modelengine.fitframework.ioc.annotation.convert.IntValue;
import modelengine.fitframework.ioc.annotation.convert.ValueConvertedAnnotationConsumer;
import modelengine.fitframework.ioc.annotation.repeatable.RepeatableAnnotationConsumer;
import modelengine.fitframework.ioc.annotation.repeatable.Value;
import modelengine.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.traditional.ThirdLevel;
import modelengine.fitframework.ioc.annotation.traditional.TraditionalAnnotationConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DisplayName("测试注解转发")
class AnnotationMetadataTest {
    private AnnotationMetadataResolver resolver;

    @BeforeEach
    void setup() {
        this.resolver = new DefaultAnnotationMetadataResolver();
    }

    @Test
    @DisplayName("传统方式注解的解析验证")
    void shouldResolveTraditionalAnnotation() {
        AnnotationMetadata annotations = this.resolver.resolve(TraditionalAnnotationConsumer.class);
        ThirdLevel annotation = annotations.getAnnotation(ThirdLevel.class);
        assertNotNull(annotation);
        assertEquals(TraditionalAnnotationConsumer.VALUE, annotation.key());
    }

    @Test
    @DisplayName("可重复注解的解析验证")
    void shouldResolveRepeatableAnnotation() {
        AnnotationMetadata annotations = this.resolver.resolve(RepeatableAnnotationConsumer.class);
        Value[] values = annotations.getAnnotationsByType(Value.class);
        assertNotNull(values);
        assertEquals(4, values.length);
        List<String> names = Stream.of(values).map(Value::value).sorted().collect(Collectors.toList());
        assertIterableEquals(Arrays.asList("", "A1", "A2", "RepeatableAnnotationConsumer"), names);
    }

    @Test
    @DisplayName("循环转发解析验证")
    void shouldResolveCircularAnnotation() {
        AnnotationMetadata annotations = this.resolver.resolve(CircularAnnotationConsumer.class);
        Circular circular = annotations.getAnnotation(Circular.class);
        assertNotNull(circular);
        assertEquals(CircularAnnotationConsumer.VALUE, circular.key1());
        assertEquals(CircularAnnotationConsumer.VALUE, circular.key2());
    }

    @Test
    @DisplayName("注解值转换验证")
    void shouldConvertValueOfAnnotationProperty() {
        AnnotationMetadata annotations = this.resolver.resolve(ValueConvertedAnnotationConsumer.class);
        IntValue intValue = annotations.getAnnotation(IntValue.class);
        assertNotNull(intValue);
        assertEquals(100, intValue.value());
    }
}
