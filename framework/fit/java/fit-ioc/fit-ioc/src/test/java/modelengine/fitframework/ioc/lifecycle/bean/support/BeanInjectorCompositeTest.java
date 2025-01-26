/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

@DisplayName("测试 BeanInjectorComposite 类")
class BeanInjectorCompositeTest {
    @Test
    @DisplayName("当待组合的Bean注入程序的列表为 null 时，抛出异常")
    void should_throw_when_combining_injectors_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new BeanInjectorComposite(null));
    }

    @Test
    @DisplayName("当待组合的Bean注入程序的列表为空时，抛出异常")
    void should_throw_when_combining_injectors_is_empty() {
        assertThrows(IllegalArgumentException.class, () -> new BeanInjectorComposite(Collections.emptyList()));
    }

    @Test
    @DisplayName("当待组合的Bean注入程序列表包含 null 时，抛出异常")
    void should_throw_when_combining_injectors_contains_null() {
        assertThrows(IllegalArgumentException.class, () -> new BeanInjectorComposite(Collections.singletonList(null)));
    }

    @Test
    @DisplayName("依次调用被组合的注入程序")
    void should_return_bean_injected_by_the_first_injector_when_it_return_non_null() {
        final Object bean1 = new byte[0];
        final Object bean2 = new byte[0];
        final Object bean3 = new byte[0];
        final Object bean4 = new byte[0];

        BeanInjector injector1 = mock(BeanInjector.class);
        BeanInjector injector2 = mock(BeanInjector.class);
        BeanInjector injector3 = mock(BeanInjector.class);

        BeanInjector composite = new BeanInjectorComposite(Arrays.asList(injector1, injector2, injector3));
        composite.inject(bean1);

        verify(injector1, times(1)).inject(bean1);
        verify(injector2, times(1)).inject(bean2);
        verify(injector3, times(1)).inject(bean3);
    }
}
