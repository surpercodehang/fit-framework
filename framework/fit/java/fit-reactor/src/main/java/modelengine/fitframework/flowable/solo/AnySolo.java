/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.solo;

import modelengine.fitframework.flowable.Solo;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.subscription.AbstractSubscription;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.LockUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 表示 {@link Solo} 的单数据实现。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-11
 */
public class AnySolo<T> extends AbstractSolo<T> {
    private final T data;

    public AnySolo(T data) {
        this.data = data;
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        subscriber.onSubscribed(new AnySubscription<>(subscriber, this.data));
    }

    private static class AnySubscription<T> extends AbstractSubscription {
        private final Subscriber<T> subscriber;
        private final T data;

        private final AtomicLong counter = new AtomicLong();
        private final AtomicBoolean consumed = new AtomicBoolean();
        private final Object lock = LockUtils.newSynchronizedLock();

        AnySubscription(Subscriber<T> subscriber, T data) {
            this.subscriber = subscriber;
            this.data = data;
        }

        @Override
        protected void request0(long count) {
            if (this.consumed.get()) {
                return;
            }
            synchronized (this.lock) {
                long pre = this.counter.getAndAdd(count);
                this.onCounterValueChanged(pre);
            }
        }

        private void onCounterValueChanged(long pre) {
            if (pre > 0) {
                return;
            }
            if (this.isCancelled() || !this.consumed.compareAndSet(false, true)) {
                return;
            }
            this.subscriber.consume(this.data);
            if (!this.isCancelled()) {
                this.subscriber.complete();
            }
        }
    }
}
