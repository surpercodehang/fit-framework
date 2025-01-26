/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreators;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * {@link BeanMethodBeanCreator} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-02
 */
@DisplayName("测试 BeanMethodBeanCreator 类")
class BeanMethodBeanCreatorTest {
    private BeanMetadata metadata;
    private Method method;

    @SuppressWarnings("unused")
    private String testMethod(String value) {
        return value;
    }

    @BeforeEach
    void setup() {
        this.method = ReflectionUtils.getDeclaredMethod(this.getClass(), "testMethod", String.class);

        BeanResolver resolver = mock(BeanResolver.class);
        FitRuntime runtime = mock(FitRuntime.class);
        when(runtime.resolverOfBeans()).thenReturn(resolver);

        Plugin plugin = mock(Plugin.class);
        when(plugin.runtime()).thenReturn(runtime);

        final BeanFactory beanFactory = mock(BeanFactory.class);
        when(beanFactory.get()).thenReturn(this);

        BeanContainer container = mock(BeanContainer.class);
        when(container.plugin()).thenReturn(plugin);
        when(container.runtime()).thenCallRealMethod();
        when(container.factory(anyString())).thenReturn(Optional.of(beanFactory));

        this.metadata = mock(BeanMetadata.class);
        when(this.metadata.container()).thenReturn(container);
        when(this.metadata.runtime()).thenCallRealMethod();
        when(this.metadata.name()).thenReturn("testMethod");
    }

    @Test
    @DisplayName("当 BeanDefinition 为 null 时，抛出异常")
    void shouldThrowWhenBeanDefinitionIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BeanMethodBeanCreator(null, this.method));
    }

    @Test
    @DisplayName("当 Method 为 null 时，抛出异常")
    void shouldThrowWhenMethodIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BeanMethodBeanCreator(this.metadata, null));
    }

    @Test
    @DisplayName("使用参数数量不匹配时，抛出异常")
    void shouldThrowWhenTheNumberOfArgumentsNotMatch() {
        final BeanCreator beanCreator = BeanCreators.byMethod(this.metadata, this.method);
        assertThatThrownBy(() -> beanCreator.create(new Object[] {1, "hello"})).isInstanceOf(
                BeanCreationException.class);
    }

    @Test
    @DisplayName("使用正确的参数可成功实例化Bean")
    void shouldReturnBeanCreatedByMethod() {
        final BeanMethodBeanCreator beanCreator = new BeanMethodBeanCreator(this.metadata, this.method);
        final Object obj = beanCreator.create(new Object[] {"hello"});
        assertThat(obj).isInstanceOf(String.class).isEqualTo("hello");
    }
}
