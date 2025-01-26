/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * {@link ClassBeanCreator} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-03
 */
@DisplayName("测试 ClassBeanCreator 类")
class ClassBeanCreatorTest {
    private BeanMetadata metadata;

    @SuppressWarnings("unused")
    static class Bean {
        private final int key;

        Bean(int key) {
            this.key = key;
        }
    }

    @BeforeEach
    void setup() {
        BeanResolver resolver = mock(BeanResolver.class);
        when(resolver.parameter(any(), any())).thenReturn(Optional.of(() -> 1));

        FitRuntime runtime = mock(FitRuntime.class);
        when(runtime.resolverOfBeans()).thenReturn(resolver);

        Plugin plugin = mock(Plugin.class);
        when(plugin.runtime()).thenReturn(runtime);

        BeanContainer container = mock(BeanContainer.class);
        when(container.plugin()).thenReturn(plugin);
        when(container.runtime()).thenCallRealMethod();

        this.metadata = mock(BeanMetadata.class);
        when(this.metadata.container()).thenReturn(container);
        when(this.metadata.runtime()).thenCallRealMethod();
    }

    @Test
    @DisplayName("当 BeanDefinition 为 null 时，抛出异常")
    void shouldThrowWhenBeanDefinitionIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ClassBeanCreator(null, Bean.class));
    }

    @Test
    @DisplayName("当 class 为 null 时，抛出异常")
    void shouldThrowWhenMethodIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ClassBeanCreator(this.metadata, null));
    }

    @Test
    @DisplayName("使用参数数量不匹配时，抛出异常")
    void shouldThrowWhenTheNumberOfArgumentsNotMatch() {
        final ClassBeanCreator beanCreator = new ClassBeanCreator(this.metadata, Bean.class);
        assertThatThrownBy(() -> beanCreator.create(new Object[] {1})).isInstanceOf(BeanCreationException.class);
    }

    @Test
    @DisplayName("使用正确的参数可成功实例化 Bean")
    void shouldReturnBeanCreatedByClass() {
        final ClassBeanCreator beanCreator = new ClassBeanCreator(this.metadata, Bean.class);
        final Object obj = beanCreator.create(new Object[0]);
        assertThat(obj).isInstanceOf(Bean.class).hasFieldOrPropertyWithValue("key", 1);
    }
}
