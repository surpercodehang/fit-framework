/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("测试 DefaultBeanLifecycle 类")
class DefaultBeanLifecycleTest {
    private BeanMetadata metadata;
    private BeanCreator creator;
    private BeanInjector injector;
    private BeanInitializer initializer;
    private BeanDestroyer destroyer;

    @BeforeEach
    void setup() {
        this.metadata = mock(BeanMetadata.class);
        this.creator = mock(BeanCreator.class);
        this.injector = mock(BeanInjector.class);
        this.initializer = mock(BeanInitializer.class);
        this.destroyer = mock(BeanDestroyer.class);
    }

    private BeanLifecycle lifecycle() {
        return new DefaultBeanLifecycle(this.metadata,
                this.creator, null, this.injector, this.initializer, this.destroyer);
    }

    @Test
    @DisplayName("当没有 BeanCreator 时创建Bean方法返回 null")
    void should_return_null_when_create_without_creator() {
        this.creator = null;
        BeanLifecycle lifecycle = this.lifecycle();
        Object bean = lifecycle.create(new Object[0]);
        assertNull(bean);
    }

    @Test
    @DisplayName("当存在 BeanCreator 时，通过 BeanCreator 创建Bean")
    void should_invoke_creator_when_create_bean() {
        Object expected = mock(Object.class);
        Object[] arguments = new Object[0];
        when(this.creator.create(any())).thenReturn(expected);
        BeanLifecycle lifecycle = this.lifecycle();
        Object created = lifecycle.create(arguments);
        assertSame(expected, created);
        verify(this.creator, times(1)).create(arguments);
    }

    @Test
    @DisplayName("当没有 BeanInjector 时注入Bean方法返回被注入的Bean实例")
    void should_return_origin_bean_when_inject_without_injector() {
        this.injector = null;
        BeanLifecycle lifecycle = this.lifecycle();
        Object bean = mock(Object.class);
        assertDoesNotThrow(() -> lifecycle.inject(bean));
    }

    @Test
    @DisplayName("当存在 BeanInjector 时，通过 BeanInjector 注入Bean")
    void should_invoke_injector_when_inject_bean() {
        Object bean = mock(Object.class);
        BeanLifecycle lifecycle = this.lifecycle();
        lifecycle.inject(bean);
        verify(this.injector, times(1)).inject(bean);
    }

    @Test
    @DisplayName("当没有 BeanInitializer 时初始化Bean方法返回被初始化的Bean实例")
    void should_return_origin_bean_when_initialize_without_initializer() {
        this.initializer = null;
        BeanLifecycle lifecycle = this.lifecycle();
        Object bean = mock(Object.class);
        assertDoesNotThrow(() -> lifecycle.initialize(bean));
    }

    @Test
    @DisplayName("当存在 BeanInitializer 时，通过 BeanInitializer 初始化Bean")
    void should_invoke_initializer_when_initialize_bean() {
        Object bean = mock(Object.class);
        BeanLifecycle lifecycle = this.lifecycle();
        lifecycle.initialize(bean);
        verify(this.initializer, times(1)).initialize(bean);
    }

    @Test
    @DisplayName("当没有 BeanDestroyer 时销毁Bean不抛出异常")
    void should_not_throw_when_destroy_without_destroyer() {
        this.destroyer = null;
        BeanLifecycle lifecycle = this.lifecycle();
        Object bean = mock(Object.class);
        assertDoesNotThrow(() -> lifecycle.destroy(bean));
    }

    @Test
    @DisplayName("当存在 BeanDestroyer 时，通过 BeanDestroyer 销毁Bean")
    void should_invoke_destroyer_when_destroy_bean() {
        Object bean = mock(Object.class);
        BeanLifecycle lifecycle = this.lifecycle();
        lifecycle.destroy(bean);
        verify(this.destroyer, times(1)).destroy(bean);
    }

    @Test
    @DisplayName("当存在 BeanDestroyer 并抛出异常时，生命周期的Bean销毁方法不抛出异常")
    void should_not_throw_when_destroyer_throw_exception() {
        Object bean = mock(Object.class);
        doThrow(IllegalStateException.class).when(this.destroyer).destroy(any());
        BeanLifecycle lifecycle = this.lifecycle();
        assertDoesNotThrow(() -> lifecycle.destroy(bean));
        verify(this.destroyer, times(1)).destroy(bean);
    }
}
