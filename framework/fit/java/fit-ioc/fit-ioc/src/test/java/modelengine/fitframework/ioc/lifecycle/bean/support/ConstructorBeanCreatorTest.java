/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.DependencyResolver;
import modelengine.fitframework.ioc.DependencyResolvingResult;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Optional;

@DisplayName("测试 ConstructorBeanCreator 类")
class ConstructorBeanCreatorTest {
    private BeanMetadata metadata;
    private Constructor<?> constructor;
    private DependencyResolver dependencyResolver;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        BeanResolver resolver = mock(BeanResolver.class);
        when(resolver.parameter(any(), any())).thenReturn(Optional.empty());
        when(resolver.parameter(any(),
                argThat(parameter -> parameter.getType().equals(int.class)))).thenReturn(Optional.of(() -> 1));

        this.dependencyResolver = mock(DependencyResolver.class);
        AnnotationMetadataResolver annotationResolver = mock(AnnotationMetadataResolver.class);

        FitRuntime runtime = mock(FitRuntime.class);
        when(runtime.resolverOfBeans()).thenReturn(resolver);
        when(runtime.resolverOfDependencies()).thenReturn(this.dependencyResolver);
        when(runtime.resolverOfAnnotations()).thenReturn(annotationResolver);

        Plugin plugin = mock(Plugin.class);
        when(plugin.runtime()).thenReturn(runtime);

        BeanContainer container = mock(BeanContainer.class);
        when(container.plugin()).thenReturn(plugin);
        when(container.runtime()).thenCallRealMethod();

        this.metadata = mock(BeanMetadata.class);
        when(this.metadata.container()).thenReturn(container);
        when(this.metadata.runtime()).thenCallRealMethod();

        this.constructor = Bean.class.getDeclaredConstructor(int.class, String.class);
    }

    @Test
    @DisplayName("当BeanDefinition为 null 时抛出异常")
    void should_throw_when_bean_definition_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new ConstructorBeanCreator(null, this.constructor));
    }

    @Test
    @DisplayName("当Bean构造方法为 null 时抛出异常")
    void should_throw_when_constructor_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new ConstructorBeanCreator(this.metadata, null));
    }

    @Test
    @DisplayName("使用正确的参数可成功实例化Bean")
    void should_return_bean_created_by_constructor() {
        BeanCreator creator = new ConstructorBeanCreator(this.metadata, this.constructor);
        Object result = creator.create(new Object[] {"hello"});
        assertTrue(result instanceof Bean);
        Bean bean = (Bean) result;
        assertEquals(1, bean.key);
        assertEquals("hello", bean.value);
    }

    @Test
    @DisplayName("使用参数数量不匹配时直接返回 null")
    void should_return_null_when_the_number_of_arguments_not_match() {
        when(this.dependencyResolver.resolve(any(),
                any(),
                any(),
                any())).thenReturn(DependencyResolvingResult.failure());
        BeanCreator creator = new ConstructorBeanCreator(this.metadata, this.constructor);
        assertThrows(BeanCreationException.class, () -> creator.create(new Object[0]));
    }

    @Test
    @DisplayName("使用参数类型不匹配时直接返回 null")
    void should_return_null_when_the_type_of_arguments_not_match() {
        when(this.dependencyResolver.resolve(any(),
                any(),
                any(),
                any())).thenReturn(DependencyResolvingResult.failure());
        BeanCreator creator = new ConstructorBeanCreator(this.metadata, this.constructor);
        assertThrows(BeanCreationException.class, () -> creator.create(new Object[] {true}));
    }

    static class Bean {
        private final int key;
        private final String value;

        Bean(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
