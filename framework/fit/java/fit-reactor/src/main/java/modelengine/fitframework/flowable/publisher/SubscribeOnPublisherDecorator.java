/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.publisher;

import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.flowable.operation.AbstractOperation;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolExecutor;
import modelengine.fitframework.util.LockUtils;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表示 {@link Publisher} 的切换订阅流程执行器的实现。
 *
 * @param <T> 表示发布者中数据类型的 {@link T}。
 * @author 何天放
 * @since 2024-10-31
 */
public class SubscribeOnPublisherDecorator<T> implements Publisher<T> {
    private final Publisher<T> decorated;
    private final ThreadPoolExecutor executor;
    private final boolean requestOnSeparateThread;

    public SubscribeOnPublisherDecorator(Publisher<T> decorated, ThreadPoolExecutor executor,
            boolean requestOnSeparateThread) {
        this.decorated = decorated;
        this.executor = executor;
        this.requestOnSeparateThread = requestOnSeparateThread;
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        SubscribeOnOperation<T> operation =
                new SubscribeOnOperation<>(this.decorated, subscriber, this.executor, this.requestOnSeparateThread);
        subscriber.onSubscribed(operation);
        operation.subscribePrevious();
    }

    private static class SubscribeOnOperation<T> extends AbstractOperation<T, T> {
        private final Publisher<T> previousPublisher;
        private final ThreadPoolExecutor executor;
        private final boolean requestOnSeparateThread;
        private final AtomicReference<Subscription> subscription = new AtomicReference<>();
        private final AtomicLong requested = new AtomicLong();
        private final Object lock = LockUtils.newSynchronizedLock();

        SubscribeOnOperation(Publisher<T> publisher, Subscriber<T> subscriber, ThreadPoolExecutor executor,
                boolean requestOnSeparateThread) {
            super(subscriber);
            this.previousPublisher = publisher;
            this.executor = executor;
            this.requestOnSeparateThread = requestOnSeparateThread;
        }

        @Override
        protected void request0(long count) {
            synchronized (this.lock) {
                if (this.subscription.get() == null) {
                    this.requested.addAndGet(count);
                    return;
                }
            }
            this.requestUpstream(count);
        }

        @Override
        protected void onSubscribed0(Subscription subscription) {
            long count;
            synchronized (this.lock) {
                if (this.subscription.compareAndSet(null, subscription)) {
                    count = this.requested.getAndSet(0L);
                } else {
                    return;
                }
            }
            if (count == 0) {
                return;
            }
            this.requestUpstream(count);
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            this.getNextSubscriber().consume(data);
        }

        /**
         * 发起对于前序数据发布者的订阅。
         */
        public void subscribePrevious() {
            this.executor.execute(Task.builder()
                    .runnable(() -> this.previousPublisher.subscribe(this))
                    .buildDisposable());
        }

        private void requestUpstream(long count) {
            if (!this.requestOnSeparateThread) {
                this.subscription.get().request(count);
                return;
            }
            this.executor.execute(Task.builder()
                    .runnable(() -> this.subscription.get().request(count))
                    .buildDisposable());
        }
    }
}
