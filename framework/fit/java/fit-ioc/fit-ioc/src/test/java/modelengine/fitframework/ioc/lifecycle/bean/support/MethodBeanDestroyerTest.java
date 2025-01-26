/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

@DisplayName("测试 MethodBeanDestroyer 类")
class MethodBeanDestroyerTest {
    interface Bean {
        void destroy();

        void destroyWithArguments(Object argument);

        static void staticDestroy() {}
    }

    @Test
    @DisplayName("执行销毁方法对Bean进行销毁")
    void should_destroy_bean_with_method() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("destroy");
        Bean bean = mock(Bean.class);
        BeanDestroyer destroyer = BeanDestroyers.method(method);
        destroyer.destroy(bean);
        verify(bean, times(1)).destroy();
    }

    @Test
    @DisplayName("当销毁方法为 static 时抛出异常")
    void should_throw_when_destroy_method_is_static() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("staticDestroy");
        assertThrows(BeanDefinitionException.class, () -> new MethodBeanDestroyer(method));
    }

    @Test
    @DisplayName("当销毁方法存在入参时抛出异常")
    void should_throw_when_destroy_method_contains_parameters() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("destroyWithArguments", Object.class);
        assertThrows(BeanDefinitionException.class, () -> new MethodBeanDestroyer(method));
    }

    @Test
    @DisplayName("当方法抛出异常时，销毁过程不抛出异常")
    void should_not_throw_when_destroy_occurs_exception() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("destroy");
        MethodBeanDestroyer destroyer = new MethodBeanDestroyer(method);
        Bean bean = mock(Bean.class);
        doThrow(IllegalStateException.class).when(bean).destroy();
        assertDoesNotThrow(() -> destroyer.destroy(bean));
        verify(bean, times(1)).destroy();
    }
}
