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

import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * 表示 {@link BeanInitializerComposite} 的单元测试。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
@DisplayName("测试 BeanInitializerComposite 类")
class BeanInitializerCompositeTest {
    @Test
    @DisplayName("当待组合的Bean初始化程序的列表为 null 时，抛出异常")
    void should_throw_when_combining_initializers_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new BeanInitializerComposite(null));
    }

    @Test
    @DisplayName("当待组合的Bean初始化程序的列表为空时，抛出异常")
    void should_throw_when_combining_initializers_is_empty() {
        assertThrows(IllegalArgumentException.class, () -> new BeanInitializerComposite(Collections.emptyList()));
    }

    @Test
    @DisplayName("当待组合的Bean初始化程序列表包含 null 时，抛出异常")
    void should_throw_when_combining_initializers_contains_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new BeanInitializerComposite(Collections.singletonList(null)));
    }

    @Test
    @DisplayName("依次调用被组合的Bean初始化程序")
    void should_return_bean_initialized_by_the_first_initializer_when_it_return_non_null() {
        final Object bean1 = new byte[0];
        final Object bean2 = new byte[0];
        final Object bean3 = new byte[0];
        final Object bean4 = new byte[0];

        BeanInitializer initializer1 = mock(BeanInitializer.class);
        BeanInitializer initializer2 = mock(BeanInitializer.class);
        BeanInitializer initializer3 = mock(BeanInitializer.class);

        BeanInitializer composite =
                new BeanInitializerComposite(Arrays.asList(initializer1, initializer2, initializer3));
        composite.initialize(bean1);

        verify(initializer1, times(1)).initialize(bean1);
        verify(initializer2, times(1)).initialize(bean2);
        verify(initializer3, times(1)).initialize(bean3);
    }
}
