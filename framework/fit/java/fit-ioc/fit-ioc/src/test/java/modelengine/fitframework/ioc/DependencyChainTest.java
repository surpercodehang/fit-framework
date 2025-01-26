/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Iterator;

@DisplayName("测试 DependencyChain 工具类")
class DependencyChainTest {
    @Test
    @DisplayName("空链中不包含任何元素")
    void should_contain_nothing_in_empty_chain() {
        DependencyChain chain = DependencyChain.empty();
        Iterator<BeanMetadata> iterator = chain.iterator();
        assertFalse(iterator.hasNext());
        assertEquals("non-dependency", chain.toString());
    }

    @Test
    @DisplayName("依赖链优先返回最新加的元素")
    void should_return_elements_nearly_preferred() {
        BeanMetadata metadata1 = Mockito.mock(BeanMetadata.class);
        BeanMetadata metadata2 = Mockito.mock(BeanMetadata.class);
        DependencyChain chain = DependencyChain.empty().next(metadata1).next(metadata2);
        Iterator<BeanMetadata> iterator = chain.iterator();
        assertTrue(iterator.hasNext());
        assertSame(metadata2, iterator.next());
        assertTrue(iterator.hasNext());
        assertSame(metadata1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    @DisplayName("依赖链的字符串表现形式可表达依赖关系")
    void should_return_dependency_chain_as_text() {
        BeanMetadata metadata1 = Mockito.mock(BeanMetadata.class);
        when(metadata1.name()).thenReturn("bean1");
        BeanMetadata metadata2 = Mockito.mock(BeanMetadata.class);
        when(metadata2.name()).thenReturn("bean2");
        DependencyChain chain = DependencyChain.empty().next(metadata1).next(metadata2);
        String text = chain.toString();
        assertEquals("bean1 -> bean2", text);
    }
}
