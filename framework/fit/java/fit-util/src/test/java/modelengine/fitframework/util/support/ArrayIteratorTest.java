/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

/**
 * 为 {@link ArrayIterator} 提供单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class ArrayIteratorTest {
    /**
     * 目标方法：{@link ArrayIterator#next()}
     * <p>当未进行 {@link ArrayIterator#hasNext()} 判断直接获取下个元素，且无更多元素时应抛出异常。</p>
     */
    @Test
    public void should_throws_when_next_without_determination() {
        Integer[] items = new Integer[0];
        ArrayIterator<Integer> iterator = new ArrayIterator<>(items);
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void given_items_is_null_when_has_next_then_return_false() {
        ArrayIterator<Integer> iterator = new ArrayIterator<>(null);
        boolean actual = iterator.hasNext();
        Assertions.assertThat(actual).isFalse();
    }
}
