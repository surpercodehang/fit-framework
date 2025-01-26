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

import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

@DisplayName("测试 BeanDestroyerComposite 类")
class BeanDestroyerCompositeTest {
    @Test
    @DisplayName("当待组合的Bean销毁程序的列表为 null 时，抛出异常")
    void should_throw_when_combining_destroyers_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new BeanDestroyerComposite(null));
    }

    @Test
    @DisplayName("当待组合的Bean销毁程序的列表为空时，抛出异常")
    void should_throw_when_combining_destroyers_is_empty() {
        assertThrows(IllegalArgumentException.class, () -> new BeanDestroyerComposite(Collections.emptyList()));
    }

    @Test
    @DisplayName("当待组合的Bean销毁程序列表包含 null 时，抛出异常")
    void should_throw_when_combining_destroyers_contains_null() {
        assertThrows(IllegalArgumentException.class, () -> new BeanDestroyerComposite(Collections.singletonList(null)));
    }

    @Test
    @DisplayName("依次调用被组合的Bean销毁程序")
    void should_return_bean_destroyed_by_the_first_destroyer_when_it_return_non_null() {
        final Object bean = new byte[0];

        BeanDestroyer destroyer1 = mock(BeanDestroyer.class);
        BeanDestroyer destroyer2 = mock(BeanDestroyer.class);
        BeanDestroyer destroyer3 = mock(BeanDestroyer.class);

        BeanDestroyer composite = new BeanDestroyerComposite(Arrays.asList(destroyer1, destroyer2, destroyer3));
        composite.destroy(bean);

        verify(destroyer1, times(1)).destroy(bean);
        verify(destroyer2, times(1)).destroy(bean);
        verify(destroyer3, times(1)).destroy(bean);
    }
}
