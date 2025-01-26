/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * {@link IteratorComposite} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-01-28
 */
@DisplayName("测试 IteratorComposite 工具类")
class IteratorCompositeTest {
    private final List<String> numberList = Arrays.asList("1", "2", "3");
    private final List<String> letterList = Arrays.asList("a", "b", "c");

    @Nested
    @DisplayName("测试方法：hasNext()，next()")
    class TestHasNextAndNext {
        @Test
        @DisplayName("当提供有元素的迭代组合器，调用 next 方法时，获取到元素")
        void givenIteratorThenReturnIterateElement() {
            Iterator<String> iterator = getIteratorComposite();
            final List<String> combinedList = new ArrayList<>();
            while (iterator.hasNext()) {
                final String next = iterator.next();
                combinedList.add(next);
            }
            assertThat(combinedList).contains("1", "2", "3", "a", "b", "c");
        }

        @Test
        @DisplayName("当提供空的迭代组合器，调用 next 方法时，抛出未找到元素异常")
        void givenEmptyIteratorThenReturnThrowException() {
            final List<Iterator<String>> iteratorList = new ArrayList<>();
            Iterator<String> iterator = new IteratorComposite<>(iteratorList);
            assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
        }
    }

    private Iterator<String> getIteratorComposite() {
        final List<Iterator<String>> list = new ArrayList<>();
        list.add(numberList.iterator());
        list.add(letterList.iterator());
        return new IteratorComposite<>(list);
    }

    @Nested
    @DisplayName("测试方法：combine")
    class TestCombine {
        @Test
        @DisplayName("当提供多个迭代器时，返回组合后的 1 个迭代器")
        void givenIteratorListThenReturnCombinedIterator() {
            final Iterator<String> combinedList =
                    IteratorComposite.combine(numberList.iterator(), letterList.iterator());
            assertThat(combinedList).isNotNull().toIterable().hasSize(6).containsSequence("1", "2", "3", "a", "b", "c");
        }

        @Test
        @DisplayName("当提供含多个迭代器的集合时，返回组合后的 1 个迭代器")
        void givenCollectionListThenReturnCombinedIterator() {
            final List<Iterator<String>> list = new ArrayList<>();
            list.add(numberList.iterator());
            list.add(letterList.iterator());
            final Iterator<String> combinedList = IteratorComposite.combine(list);
            assertThat(combinedList).isNotNull().toIterable().hasSize(6).containsSequence("1", "2", "3", "a", "b", "c");
        }

        @Test
        @DisplayName("当提供 1 个空的集合时，返回 1 个空的迭代器")
        void givenCollectionListThenReturnCombinedIterator1() {
            final List<Iterator<String>> emptyList = new ArrayList<>();
            final Iterator<String> combinedList = IteratorComposite.combine(emptyList);
            assertThat(combinedList).isNotNull().toIterable().hasSize(0);
        }
    }
}
