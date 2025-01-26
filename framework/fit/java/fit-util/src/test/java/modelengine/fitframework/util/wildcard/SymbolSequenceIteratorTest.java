/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

@DisplayName("测试字符序的迭代器")
class SymbolSequenceIteratorTest {
    private SymbolSequence<Integer> sequence;

    @BeforeEach
    void setup() {
        this.sequence = ObjectUtils.cast(mock(SymbolSequence.class));
        when(this.sequence.length()).thenReturn(1);
        when(this.sequence.at(0)).thenReturn(100);
        when(this.sequence.iterator()).thenCallRealMethod();
        when(this.sequence.toString()).thenReturn("[100]");
    }

    @Test
    @DisplayName("返回的迭代器中包含正确的数据")
    void should_return_iterator_contains_correct_data() {
        Iterator<Integer> iterator = this.sequence.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(100, iterator.next());
        assertFalse(iterator.hasNext());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, iterator::next);
        assertEquals("No element at current position in symbol sequence. [sequence=[100], position=1]",
                exception.getMessage());
    }
}
