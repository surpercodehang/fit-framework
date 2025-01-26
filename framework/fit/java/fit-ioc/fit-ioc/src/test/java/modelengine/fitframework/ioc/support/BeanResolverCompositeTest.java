/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanDefinition;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link BeanResolverComposite} 的单元测试。
 *
 * @author 梁济时
 * @since 2022-05-09
 */
@DisplayName("测试 BeanResolverComposite 类")
class BeanResolverCompositeTest {
    @Test
    @DisplayName("当有解析程序认为构造方法为首选后，不再执行后续的解析程序")
    void should_return_true_when_is_preferred() {
        BeanResolver r1 = mock(BeanResolver.class);
        when(r1.preferred(any(), any())).thenReturn(false);
        BeanResolver r2 = mock(BeanResolver.class);
        when(r2.preferred(any(), any())).thenReturn(true);
        BeanResolver r3 = mock(BeanResolver.class);
        when(r3.preferred(any(), any())).thenReturn(false);

        BeanResolverComposite composite = new BeanResolverComposite();
        composite.addAll(Arrays.asList(r1, r2, r3));
        Constructor<?> constructor = Bean.class.getDeclaredConstructors()[0];
        boolean preferred = composite.preferred(mock(BeanMetadata.class), constructor);

        assertTrue(preferred);
        verify(r1, times(1)).preferred(any(), any());
        verify(r2, times(1)).preferred(any(), any());
        verify(r3, times(0)).preferred(any(), any());
    }

    @Test
    @DisplayName("当有解析程序解析到参数时，不再执行后续的解析程序")
    void should_return_first_resolved_parameter() throws NoSuchMethodException {
        ValueSupplier supplier = mock(ValueSupplier.class);
        BeanResolver r1 = mock(BeanResolver.class);
        when(r1.parameter(any(), any(Parameter.class))).thenReturn(Optional.empty());
        BeanResolver r2 = mock(BeanResolver.class);
        when(r2.parameter(any(), any(Parameter.class))).thenReturn(Optional.of(supplier));
        BeanResolver r3 = mock(BeanResolver.class);
        when(r3.parameter(any(), any(Parameter.class))).thenReturn(Optional.empty());

        BeanResolverComposite composite = new BeanResolverComposite();
        composite.addAll(Arrays.asList(r1, r2, r3));
        Parameter parameter = Bean.class.getDeclaredConstructor(Integer.class).getParameters()[0];
        Optional<ValueSupplier> actual = composite.parameter(mock(BeanMetadata.class), parameter);

        assertTrue(actual.isPresent());
        assertSame(supplier, actual.get());

        verify(r1, times(1)).parameter(any(), any());
        verify(r2, times(1)).parameter(any(), any());
        verify(r3, times(0)).parameter(any(), any());
    }

    @Test
    @DisplayName("当有解析程序解析到字段注入程序后，不再执行后续的解析程序")
    void should_return_first_resolved_field_injector() throws NoSuchFieldException {
        BeanInjector injector = mock(BeanInjector.class);
        BeanResolver r1 = mock(BeanResolver.class);
        when(r1.injector(any(), any(Field.class))).thenReturn(Optional.empty());
        BeanResolver r2 = mock(BeanResolver.class);
        when(r2.injector(any(), any(Field.class))).thenReturn(Optional.of(injector));
        BeanResolver r3 = mock(BeanResolver.class);
        when(r3.injector(any(), any(Field.class))).thenReturn(Optional.empty());

        BeanResolverComposite composite = new BeanResolverComposite();
        composite.addAll(Arrays.asList(r1, r2, r3));
        Field field = Bean.class.getDeclaredField("value");
        Optional<BeanInjector> actual = composite.injector(mock(BeanMetadata.class), field);

        assertTrue(actual.isPresent());
        assertSame(injector, actual.get());

        verify(r1, times(1)).injector(any(), any(Field.class));
        verify(r2, times(1)).injector(any(), any(Field.class));
        verify(r3, times(0)).injector(any(), any(Field.class));
    }

    @Test
    @DisplayName("当有解析程序解析到方法注入程序后，不再执行后续的解析程序")
    void should_return_first_resolved_method_injector() throws NoSuchMethodException {
        BeanInjector injector = mock(BeanInjector.class);
        BeanResolver r1 = mock(BeanResolver.class);
        when(r1.injector(any(), any(Method.class))).thenReturn(Optional.empty());
        BeanResolver r2 = mock(BeanResolver.class);
        when(r2.injector(any(), any(Method.class))).thenReturn(Optional.of(injector));
        BeanResolver r3 = mock(BeanResolver.class);
        when(r3.injector(any(), any(Method.class))).thenReturn(Optional.empty());

        BeanResolverComposite composite = new BeanResolverComposite();
        composite.addAll(Arrays.asList(r1, r2, r3));
        Method method = Bean.class.getDeclaredMethod("setValue", Integer.class);
        Optional<BeanInjector> actual = composite.injector(mock(BeanMetadata.class), method);

        assertTrue(actual.isPresent());
        assertSame(injector, actual.get());

        verify(r1, times(1)).injector(any(), any(Method.class));
        verify(r2, times(1)).injector(any(), any(Method.class));
        verify(r3, times(0)).injector(any(), any(Method.class));
    }

    @Test
    @DisplayName("当有解析程序解析到初始化程序后，不再执行后续的解析程序")
    void should_return_first_resolved_method_initializer() throws NoSuchMethodException {
        BeanInitializer initializer = mock(BeanInitializer.class);
        BeanResolver r1 = mock(BeanResolver.class);
        when(r1.initializer(any(), any())).thenReturn(Optional.empty());
        BeanResolver r2 = mock(BeanResolver.class);
        when(r2.initializer(any(), any())).thenReturn(Optional.of(initializer));
        BeanResolver r3 = mock(BeanResolver.class);
        when(r3.initializer(any(), any())).thenReturn(Optional.empty());

        BeanResolverComposite composite = new BeanResolverComposite();
        composite.addAll(Arrays.asList(r1, r2, r3));
        Method method = Bean.class.getDeclaredMethod("initialize");
        Optional<BeanInitializer> actual = composite.initializer(mock(BeanMetadata.class), method);

        assertTrue(actual.isPresent());
        assertSame(initializer, actual.get());

        verify(r1, times(1)).initializer(any(), any());
        verify(r2, times(1)).initializer(any(), any());
        verify(r3, times(0)).initializer(any(), any());
    }

    @Test
    @DisplayName("当有解析程序解析到销毁程序后，不再执行后续的解析程序")
    void should_return_first_resolved_method_destroyer() throws NoSuchMethodException {
        BeanDestroyer destroyer = mock(BeanDestroyer.class);
        BeanResolver r1 = mock(BeanResolver.class);
        when(r1.destroyer(any(), any())).thenReturn(Optional.empty());
        BeanResolver r2 = mock(BeanResolver.class);
        when(r2.destroyer(any(), any())).thenReturn(Optional.of(destroyer));
        BeanResolver r3 = mock(BeanResolver.class);
        when(r3.destroyer(any(), any())).thenReturn(Optional.empty());

        BeanResolverComposite composite = new BeanResolverComposite();
        composite.addAll(Arrays.asList(r1, r2, r3));
        Method method = Bean.class.getDeclaredMethod("destroy");
        Optional<BeanDestroyer> actual = composite.destroyer(mock(BeanMetadata.class), method);

        assertTrue(actual.isPresent());
        assertSame(destroyer, actual.get());

        verify(r1, times(1)).destroyer(any(), any());
        verify(r2, times(1)).destroyer(any(), any());
        verify(r3, times(0)).destroyer(any(), any());
    }

    @Test
    @DisplayName("当有解析程序解析到销毁程序后，不再执行后续的解析程序")
    void should_return_first_resolved_priority() {
        BeanResolver r1 = mock(BeanResolver.class);
        when(r1.priority(any())).thenReturn(Optional.empty());
        BeanResolver r2 = mock(BeanResolver.class);
        when(r2.priority(any())).thenReturn(Optional.of(100));
        BeanResolver r3 = mock(BeanResolver.class);
        when(r3.priority(any())).thenReturn(Optional.empty());

        BeanResolverComposite composite = new BeanResolverComposite();
        composite.addAll(Arrays.asList(r1, r2, r3));
        BeanMetadata metadata = mock(BeanMetadata.class);
        Optional<Integer> actual = composite.priority(metadata);

        assertTrue(actual.isPresent());
        assertEquals(100, actual.get());

        verify(r1, times(1)).priority(any());
        verify(r2, times(1)).priority(any());
        verify(r3, times(0)).priority(any());
    }

    @Test
    @DisplayName("提供 BeanResolverComposite 类 removeAll 方法时，返回空")
    void givenBeanResolverCompositeWhenRemoveAllThenReturnEmpty() {
        BeanResolverComposite composite = new BeanResolverComposite();
        BeanResolver beanResolver = mock(BeanResolver.class);
        List<BeanResolver> resolvers = Collections.singletonList(beanResolver);
        composite.addAll(resolvers);
        composite.removeAll(resolvers);
        assertThat(composite.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("提供 BeanResolverComposite 类 add 方法添加 null 时，返回空")
    void givenBeanResolverCompositeWhenAddNullThenReturnEmpty() {
        BeanResolverComposite composite = new BeanResolverComposite();
        composite.add(null);
        assertThat(composite.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("提供 BeanResolverComposite 类 add 方法添加 BeanResolverComposite 类型时，返回正常信息")
    void givenBeanResolverCompositeWhenAddBeanResolverCompositeThenReturnBean() {
        BeanResolverComposite composite = new BeanResolverComposite();
        BeanResolver beanResolver = mock(BeanResolver.class);
        List<BeanResolver> resolvers = Collections.singletonList(beanResolver);
        composite.addAll(resolvers);
        BeanResolverComposite actual = new BeanResolverComposite();
        actual.add(composite);
        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("提供 BeanResolverComposite 类 remove 方法删除 null 时，返回空")
    void givenBeanResolverCompositeWhenRemoveNullThenReturnEmpty() {
        BeanResolverComposite composite = new BeanResolverComposite();
        composite.remove(null);
        assertThat(composite.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("提供 BeanResolverComposite 类 remove 方法删除 BeanResolverComposite 类型时，返回正常信息")
    void givenBeanResolverCompositeWhenRemoveBeanResolverCompositeThenReturnBean() {
        BeanResolverComposite composite = new BeanResolverComposite();
        BeanResolver beanResolver = mock(BeanResolver.class);
        List<BeanResolver> resolvers = Collections.singletonList(beanResolver);
        composite.addAll(resolvers);
        BeanResolverComposite actual = new BeanResolverComposite();
        actual.remove(composite);
        assertThat(actual.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("提供 BeanResolverComposite 类 empty 方法时，返回 true")
    void givenBeanResolverCompositeShouldReturnTrue() {
        BeanResolverComposite composite = new BeanResolverComposite();
        assertThat(composite.empty()).isTrue();
    }

    @Test
    @DisplayName("提供 BeanResolverComposite 类 bean 方法时，返回正常信息")
    void givenBeanResolverCompositeShouldReturnValue() throws NoSuchMethodException {
        BeanResolverComposite composite = new BeanResolverComposite();
        BeanResolver beanResolver = mock(BeanResolver.class);
        List<BeanResolver> resolvers = Collections.singletonList(beanResolver);
        composite.addAll(resolvers);
        DefaultBeanContainer container = Mockito.mock(DefaultBeanContainer.class);
        BeanDefinition definition = BeanDefinition.custom()
                .name("test")
                .type(Bean.class)
                .aliases(null)
                .stereotype("stereotype")
                .annotations(null)
                .preferred(false)
                .lazy(false)
                .dependencies(null)
                .applicable(null)
                .build();
        Optional<BeanDefinition> definitionOptional = Optional.ofNullable(definition);
        when(beanResolver.bean(container, Bean.class)).thenReturn(definitionOptional);
        Optional<BeanDefinition> bean = composite.bean(container, Bean.class);
        assertThat(bean).isPresent()
                .get()
                .extracting(BeanDefinition::applicable)
                .isEqualTo(BeanApplicableScope.INSENSITIVE);

        Method method = Bean.class.getDeclaredMethod("initialize");
        when(beanResolver.bean(container, method)).thenReturn(definitionOptional);
        Optional<BeanDefinition> methodBean = composite.bean(container, method);
        assertThat(methodBean).isPresent()
                .get()
                .extracting(BeanDefinition::applicable)
                .isEqualTo(BeanApplicableScope.INSENSITIVE);
    }

    static class Bean {
        private Integer value;
        private final int flags = 0;

        Bean(Integer value) {
            this.value = value;
        }

        void setValue(Integer value) {
            this.value = value;
        }

        void initialize() {}

        void destroy() {}
    }
}
