/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.choir;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.subscriber.RecordSubscriber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 表示 {@link FlexibleEmitterChoir} 的单元测试。
 *
 * @author 何天放
 * @since 2024-05-22
 */
public class FlexibleEmitterChoirTest {
    @Test
    @DisplayName("当订阅者发起订阅时，结果符合预期")
    void subscribeOperationShouldCalledOnceWhenSubscribe() {
        Emitter<Integer> emitter = Emitter.create();
        AtomicBoolean subscribed = new AtomicBoolean();
        new FlexibleEmitterChoir<>(() -> emitter, null, observer -> {
            subscribed.set(true);
        }, null, null).subscribe();
        assertThat(subscribed.get()).isTrue();
    }

    @Test
    @DisplayName("当订阅者多次进行元素请求时，结果符合预期")
    void requestOperationShouldCalledTwiceWhenRequestTwice() {
        Subscriber<Integer> subscriber = new RecordSubscriber<>(2, 1);
        Emitter<Integer> emitter = Emitter.create();
        List<Long> requestRecords = new ArrayList<>();
        FlexibleEmitterChoir<Integer> choir =
                new FlexibleEmitterChoir<>(() -> emitter, null, null, requestRecords::add, null);
        choir.subscribe(subscriber);
        choir.notifyOnSubscribed();
        emitter.emit(0);
        assertThat(requestRecords).hasSize(2).contains(2L, 1L);
    }

    @Test
    @DisplayName("当订阅者取消订阅关系时，结果符合预期")
    void cancelOperationShouldCalledWhenCancel() {
        Subscriber<Integer> subscriber = new RecordSubscriber<>(1, 0, 1);
        Emitter<Integer> emitter = Emitter.create();
        AtomicBoolean cancelled = new AtomicBoolean();
        FlexibleEmitterChoir<Integer> choir = new FlexibleEmitterChoir<>(() -> emitter, null, null, null, () -> {
            cancelled.set(true);
        });
        choir.subscribe(subscriber);
        choir.notifyOnSubscribed();
        emitter.emit(0);
        assertThat(cancelled.get()).isTrue();
    }
}
