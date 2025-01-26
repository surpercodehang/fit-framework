/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.solo;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Solo;
import modelengine.fitframework.flowable.subscriber.RecordSubscriber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 表示 {@link PublisherSoloAdapter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-02-11
 */
@DisplayName("测试 PublisherSoloAdapter")
public class PublisherSoloAdapterTest {
    @Test
    @DisplayName("订阅另一个 Solo 时，消费数据的数量等于另一个 Solo 内数据的数量")
    void shouldReturnAnotherSoloSize() {
        Solo<Integer> solo = Solo.fromPublisher(Solo.just(1));
        AtomicInteger count = new AtomicInteger();
        solo.subscribe((subscription, i) -> count.incrementAndGet());
        assertThat(count.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("对于一个元素数量超过 1 的 Publisher 进行适配时，消费数据的数量被限定为 1")
    void shouldReturnOnlyElement() {
        RecordSubscriber<Integer> subscriber = new RecordSubscriber<>();
        Solo<Integer> solo = Solo.fromPublisher(Choir.just(1, 2, 3));
        solo.subscribe(subscriber);
        assertThat(subscriber.getElements()).contains(1);
    }
}
