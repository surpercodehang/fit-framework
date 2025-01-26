/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.io.RandomAccessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@DisplayName("测试 RandomAccessorSubsection 类")
class RandomAccessorSubsectionTest {
    private RandomAccessor parent;

    @BeforeEach
    void setup() {
        this.parent = mock(RandomAccessor.class);
        when(parent.size()).thenReturn(100L);
    }

    @Nested
    @DisplayName("构造实例")
    class Construct {
        @Test
        @DisplayName("当父访问程序为 null 时抛出异常")
        void should_throw_when_parent_is_null() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new RandomAccessorSubsection(null, 0, 0));
            assertEquals("The parent accessor of subsection cannot be null.", exception.getMessage());
        }

        @Test
        @DisplayName("当片段访问程序的偏移量为负数时抛出异常")
        void should_throw_when_offset_is_negative() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new RandomAccessorSubsection(parent, -1, 0));
            assertEquals("The offset is out of bounds. [offset=-1, parent.size=100]", exception.getMessage());
        }

        @Test
        @DisplayName("当片段的偏移量超过父访问程序的大小时抛出异常")
        void should_throw_when_offset_is_greater_than_parent_size() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new RandomAccessorSubsection(parent, 101, 0));
            assertEquals("The offset is out of bounds. [offset=101, parent.size=100]", exception.getMessage());
        }

        @Test
        @DisplayName("当片段的长度为负数时抛出异常")
        void should_throw_when_length_is_negative() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new RandomAccessorSubsection(parent, 0, -1));
            assertEquals("The size is out of bounds. [size=-1, offset=0, parent.size=100]", exception.getMessage());
        }

        @Test
        @DisplayName("当片段的偏移量加长度超出父访问程序的总长度时抛出异常")
        void should_throw_when_sum_of_offset_and_length_is_greater_than_parent() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new RandomAccessorSubsection(parent, 0, 101));
            assertEquals("The size is out of bounds. [size=101, offset=0, parent.size=100]", exception.getMessage());
        }

        @Test
        @DisplayName("当偏移量为父访问程序的大小，且当前片段大小为 0 时，不抛出异常")
        void should_not_throw_when_offset_equals_with_parent_size_and_size_is_zero() {
            assertDoesNotThrow(() -> new RandomAccessorSubsection(parent, 100L, 0L));
        }
    }

    @Nested
    @DisplayName("读取数据")
    class Read {
        private RandomAccessorSubsection subsection;

        @BeforeEach
        void setup() {
            subsection = new RandomAccessorSubsection(parent, 10L, 20L);
        }

        @Test
        @DisplayName("当待读取的数据的偏移量为负数时抛出异常")
        void should_throw_when_offset_is_negative() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> subsection.read(-1, 10));
            assertEquals("The offset of data to read is out of bounds. [offset=-1, total=20]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当待读取的数据的偏移量超出数据总量时抛出异常")
        void should_throw_when_offset_is_greater_than_size() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> subsection.read(21, 0));
            assertEquals("The offset of data to read is out of bounds. [offset=21, total=20]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当待读取的数据的长度为负数时抛出异常")
        void should_throw_when_length_is_negative() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> subsection.read(0, -1));
            assertEquals("The length of data to read is out of bounds. [length=-1, offset=0, total=20]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当 offset 与 length 的和大于片段大小时抛出异常")
        void should_throw_when_sum_of_offset_and_length_is_greater_than_size() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> subsection.read(0, 21));
            assertEquals("The length of data to read is out of bounds. [length=21, offset=0, total=20]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当 offset 为片段大小，且 length 为 0 时不抛出异常")
        void should_not_throw_when_offset_equals_with_size_and_length_is_zero() {
            assertDoesNotThrow(() -> subsection.read(20, 0));
        }

        @Test
        @DisplayName("从父访问程序中读取数据")
        void should_read_data_from_parent() throws IOException {
            byte[] expected = new byte[] {0, 0, 0};
            when(parent.read(15, 3)).thenReturn(expected);
            byte[] actual = subsection.read(5, 3);
            assertSame(expected, actual);
        }
    }

    @Test
    @DisplayName("当父片段、偏移量和长度相同时，哈希值也相同")
    void should_return_same_hash_code_when_contains_same_data() {
        RandomAccessorSubsection sub1 = new RandomAccessorSubsection(parent, 10L, 10L);
        RandomAccessorSubsection sub2 = new RandomAccessorSubsection(parent, 10L, 10L);
        assertEquals(sub1.hashCode(), sub2.hashCode());
    }

    @Test
    @DisplayName("当比较同一个片段时，返回 true")
    void should_return_true_when_equals_with_itself() {
        RandomAccessorSubsection subsection = new RandomAccessorSubsection(parent, 10, 20);
        assertEquals(subsection, subsection);
    }

    @Test
    @DisplayName("当父片段、偏移量和长度相同时，哈希值也相同")
    void should_return_true_when_contains_same_data() {
        RandomAccessorSubsection sub1 = new RandomAccessorSubsection(parent, 10L, 10L);
        RandomAccessorSubsection sub2 = new RandomAccessorSubsection(parent, 10L, 10L);
        assertEquals(sub1, sub2);
    }

    @Test
    @DisplayName("当与 null 比较时，返回 false")
    void should_return_false_when_equals_with_null() {
        RandomAccessorSubsection subsection = new RandomAccessorSubsection(parent, 10, 20);
        assertNotEquals(subsection, null);
    }

    @Test
    @DisplayName("toString 方法应返回友好的信息")
    void should_return_friendly_message_from_to_string() {
        when(parent.toString()).thenReturn("[parent-accessor]");
        RandomAccessorSubsection subsection = new RandomAccessorSubsection(parent, 10, 20);
        String string = subsection.toString();
        assertEquals("[parent=[parent-accessor], offset=10, size=20]", string);
    }
}
