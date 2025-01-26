/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.choir;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 表示 {@link IterableChoir} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-02-07
 */
@DisplayName("测试 IterableChoir")
public class IterableChoirTest {
    @Test
    @DisplayName("订阅一个有限的迭代器时，消费数据的数量等于迭代器内数据的数量")
    void shouldReturnIterableSize() {
        List<Integer> list = Arrays.asList(1, 2);
        Choir<Integer> choir = Choir.fromIterable(list);
        AtomicInteger count = new AtomicInteger();
        choir.subscribe((subscription, i) -> count.incrementAndGet());
        assertThat(count.get()).isEqualTo(list.size());
    }
}
