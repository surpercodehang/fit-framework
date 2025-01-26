/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycleInterceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("测试 InterceptedBeanLifecycle 类")
class InterceptedBeanLifecycleTest {
    private BeanLifecycle intercepted;
    private BeanLifecycleInterceptor interceptor;
    private InterceptedBeanLifecycle lifecycle;

    @BeforeEach
    void setup() {
        this.intercepted = mock(BeanLifecycle.class);
        this.interceptor = mock(BeanLifecycleInterceptor.class);
        this.lifecycle = new InterceptedBeanLifecycle(this.intercepted, this.interceptor);
    }

    @Test
    @DisplayName("应返回被拦截的Bean定义")
    void should_return_the_definition_of_intercepted() {
        BeanMetadata definition = mock(BeanMetadata.class);
        when(this.intercepted.metadata()).thenReturn(definition);
        BeanMetadata interceptedDefinition = this.lifecycle.metadata();
        assertSame(definition, interceptedDefinition);
    }

    @Test
    @DisplayName("应返回拦截程序创建的Bean")
    void should_return_bean_created_by_interceptor() {
        Object[] arguments = new Object[0];
        Object bean = mock(Object.class);
        when(this.interceptor.create(this.intercepted, arguments)).thenReturn(bean);
        Object created = this.lifecycle.create(arguments);
        assertSame(bean, created);
        verify(this.intercepted, times(0)).create(any());
        verify(this.interceptor, times(1)).create(this.intercepted, arguments);
    }

    @Test
    @DisplayName("应通过拦截程序注入Bean")
    void should_return_injected_bean_by_interceptor() {
        Object bean = mock(Object.class);
        this.lifecycle.inject(bean);
        verify(this.intercepted, times(0)).inject(any());
        verify(this.interceptor, times(1)).inject(this.intercepted, bean);
    }

    @Test
    @DisplayName("应通过拦截程序初始化Bean")
    void should_return_initialized_bean_by_interceptor() {
        Object bean = mock(Object.class);
        this.lifecycle.initialize(bean);
        verify(this.intercepted, times(0)).initialize(any());
        verify(this.interceptor, times(1)).initialize(this.intercepted, bean);
    }

    @Test
    @DisplayName("应执行拦截程序的Bean销毁方法")
    void should_invoke_destroy_method_of_interceptor() {
        Object bean = mock(Object.class);
        this.lifecycle.destroy(bean);
        verify(this.intercepted, times(0)).destroy(any());
        verify(this.interceptor, times(1)).destroy(this.intercepted, bean);
    }
}
