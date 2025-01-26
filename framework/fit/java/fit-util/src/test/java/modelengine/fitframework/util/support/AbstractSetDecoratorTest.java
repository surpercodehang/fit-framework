/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
class AbstractSetDecoratorTest {
    @Test
    void should_invoke_decorated_size_method() {
        Set decorated = mock(Set.class);
        when(decorated.size()).thenReturn(1);
        Set decorator = new AbstractSetDecorator(decorated) {};
        int ret = decorator.size();
        assertEquals(1, ret);
        verify(decorated, times(1)).size();
    }

    @Test
    void should_invoke_decorated_is_empty_method() {
        Set decorated = mock(Set.class);
        when(decorated.isEmpty()).thenReturn(true);
        Set decorator = new AbstractSetDecorator(decorated) {};
        boolean ret = decorator.isEmpty();
        assertTrue(ret);
        verify(decorated, times(1)).isEmpty();
    }

    @Test
    void should_invoke_decorated_is_contains_method() {
        Object object = new Object();
        Set decorated = mock(Set.class);
        when(decorated.contains(any())).thenReturn(true);
        Set decorator = new AbstractSetDecorator(decorated) {};
        boolean ret = decorator.contains(object);
        assertTrue(ret);
        verify(decorated, times(1)).contains(object);
    }

    @Test
    void should_invoke_decorated_iterator_method() {
        Iterator iterator = mock(Iterator.class);
        Set decorated = mock(Set.class);
        when(decorated.iterator()).thenReturn(iterator);
        Set decorator = new AbstractSetDecorator(decorated) {};
        Iterator ret = decorator.iterator();
        assertSame(iterator, ret);
        verify(decorated, times(1)).iterator();
    }

    @Test
    void should_invoke_decorated_to_array_method() {
        Object[] array = new Object[0];
        Set decorated = mock(Set.class);
        when(decorated.toArray()).thenReturn(array);
        Set decorator = new AbstractSetDecorator(decorated) {};
        Object[] ret = decorator.toArray();
        assertSame(array, ret);
        verify(decorated, times(1)).toArray();
    }

    @Test
    void should_invoke_decorated_to_array_with_input_array_method() {
        Object[] array = new Object[0];
        Set decorated = mock(Set.class);
        when(decorated.toArray(array)).thenReturn(array);
        Set decorator = new AbstractSetDecorator(decorated) {};
        Object[] ret = decorator.toArray(array);
        assertSame(array, ret);
        verify(decorated, times(1)).toArray(array);
    }

    @Test
    void should_invoke_decorated_add_method() {
        Object object = new Object();
        Set decorated = mock(Set.class);
        when(decorated.add(object)).thenReturn(true);
        Set decorator = new AbstractSetDecorator(decorated) {};
        boolean ret = decorator.add(object);
        assertTrue(ret);
        verify(decorated, times(1)).add(object);
    }

    @Test
    void should_invoke_decorated_remove_method() {
        Object object = new Object();
        Set decorated = mock(Set.class);
        when(decorated.remove(object)).thenReturn(true);
        Set decorator = new AbstractSetDecorator(decorated) {};
        boolean ret = decorator.remove(object);
        assertTrue(ret);
        verify(decorated, times(1)).remove(object);
    }

    @Test
    void should_invoke_decorated_contains_all_method() {
        Collection collection = mock(Collection.class);
        Set decorated = mock(Set.class);
        when(decorated.containsAll(collection)).thenReturn(true);
        Set decorator = new AbstractSetDecorator(decorated) {};
        boolean ret = decorator.containsAll(collection);
        assertTrue(ret);
        verify(decorated, times(1)).containsAll(collection);
    }

    @Test
    void should_invoke_decorated_add_all_method() {
        Collection collection = mock(Collection.class);
        Set decorated = mock(Set.class);
        when(decorated.addAll(collection)).thenReturn(true);
        Set decorator = new AbstractSetDecorator(decorated) {};
        boolean ret = decorator.addAll(collection);
        assertTrue(ret);
        verify(decorated, times(1)).addAll(collection);
    }

    @Test
    void should_invoke_decorated_retain_all_method() {
        Collection collection = mock(Collection.class);
        Set decorated = mock(Set.class);
        when(decorated.retainAll(collection)).thenReturn(true);
        Set decorator = new AbstractSetDecorator(decorated) {};
        boolean ret = decorator.retainAll(collection);
        assertTrue(ret);
        verify(decorated, times(1)).retainAll(collection);
    }

    @Test
    void should_invoke_decorated_remove_all_method() {
        Collection collection = mock(Collection.class);
        Set decorated = mock(Set.class);
        when(decorated.removeAll(collection)).thenReturn(true);
        Set decorator = new AbstractSetDecorator(decorated) {};
        boolean ret = decorator.removeAll(collection);
        assertTrue(ret);
        verify(decorated, times(1)).removeAll(collection);
    }

    @Test
    void should_invoke_decorated_clear_method() {
        Set decorated = mock(Set.class);
        Set decorator = new AbstractSetDecorator(decorated) {};
        decorator.clear();
        verify(decorated, times(1)).clear();
    }
}
