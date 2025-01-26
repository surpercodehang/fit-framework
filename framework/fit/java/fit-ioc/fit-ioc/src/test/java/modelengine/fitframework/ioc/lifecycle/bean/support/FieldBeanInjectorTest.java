/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import modelengine.fitframework.ioc.BeanDefinitionException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

@DisplayName("测试 FieldBeanInjector 类")
class FieldBeanInjectorTest {
    @SuppressWarnings("unused")
    static class Bean {
        private static Object staticDependency;

        private final Object finalDependency = "final-dependency";

        private Object dependency;
    }

    @Test
    @DisplayName("使用字段注入程序对Bean的字段进行注入")
    void should_inject_field() throws NoSuchFieldException {
        Object expected = mock(Object.class);
        Field field = Bean.class.getDeclaredField("dependency");
        FieldBeanInjector injector = new FieldBeanInjector(field, () -> expected);
        Bean bean = new Bean();
        injector.inject(bean);
        assertSame(expected, bean.dependency);
    }

    @Test
    @DisplayName("当注入 final 字段时抛出异常")
    void should_throw_when_dependency_is_final() throws NoSuchFieldException {
        Field field = Bean.class.getDeclaredField("finalDependency");
        Object value = mock(Object.class);
        assertThrows(BeanDefinitionException.class, () -> new FieldBeanInjector(field, () -> value));
    }

    @Test
    @DisplayName("当注入 static 字段时抛出异常")
    void should_throw_when_dependency_is_static() throws NoSuchFieldException {
        Field field = Bean.class.getDeclaredField("staticDependency");
        Object value = mock(Object.class);
        assertThrows(BeanDefinitionException.class, () -> new FieldBeanInjector(field, () -> value));
    }
}
