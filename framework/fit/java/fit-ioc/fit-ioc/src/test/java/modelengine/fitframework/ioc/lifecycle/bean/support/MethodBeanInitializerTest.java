/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

@DisplayName("测试 MethodBeanInitializer 类")
class MethodBeanInitializerTest {
    interface Bean {
        void initialize();
        void initializeWithArguments(Object argument);
        static void staticInitialize() {}
    }

    @Test
    @DisplayName("当初始化Bean时执行初始化方法")
    void should_invoke_initialize_method() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("initialize");
        BeanInitializer initializer = BeanInitializers.method(method);
        Bean bean = mock(Bean.class);
        initializer.initialize(bean);
        verify(bean, times(1)).initialize();
    }

    @Test
    @DisplayName("当初始化方法为 static 时抛出异常")
    void should_throw_when_initialize_with_static_method() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("staticInitialize");
        assertThrows(BeanDefinitionException.class, () -> new MethodBeanInitializer(method));
    }

    @Test
    @DisplayName("当初始化方法有参数时抛出异常")
    void should_throw_when_initialize_method_with_arguments() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("initializeWithArguments", Object.class);
        assertThrows(BeanDefinitionException.class, () -> new MethodBeanInitializer(method));
    }

    @Test
    @DisplayName("当初始化方法抛出异常时初始化Bean抛出异常")
    void should_throw_when_initialize_occurs_exception() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("initialize");
        MethodBeanInitializer initializer = new MethodBeanInitializer(method);
        Bean bean = mock(Bean.class);
        doThrow(IllegalStateException.class).when(bean).initialize();
        assertThrows(BeanCreationException.class, () -> initializer.initialize(bean));
    }
}
