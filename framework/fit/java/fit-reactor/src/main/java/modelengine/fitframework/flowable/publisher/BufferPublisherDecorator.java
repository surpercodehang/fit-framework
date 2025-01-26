/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.publisher;

import static modelengine.fitframework.inspection.Validation.greaterThan;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.flowable.operation.AbstractOperation;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LockUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link Publisher} 的将数据按指定大小进行分组的实现。
 *
 * @param <T> 表示发布者中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-14
 */
public class BufferPublisherDecorator<T> implements Publisher<List<T>> {
    private final Publisher<T> decorated;
    private final int bufferSize;

    public BufferPublisherDecorator(Publisher<T> decorated, int bufferSize) {
        this.decorated = notNull(decorated, "The decorated buffer publisher cannot be null.");
        this.bufferSize = greaterThan(bufferSize, 0, "The buffer size must be positive. [bufferSize={0}]", bufferSize);
    }

    @Override
    public void subscribe(Subscriber<List<T>> subscriber) {
        this.decorated.subscribe(new BufferOperator<>(this.bufferSize, subscriber));
    }

    private static class BufferOperator<T> extends AbstractOperation<T, List<T>> {
        private final int bufferSize;
        private final List<T> buffer = new ArrayList<>();
        private final Object lock = LockUtils.newSynchronizedLock();

        /**
         * 使用缓冲区大小和下游的订阅者来初始化 {@link BufferOperator}。
         *
         * @param bufferSize 表示缓冲区大小的 {@code int}。
         * @param subscriber 表示下游的订阅者的 {@link Subscriber}{@code <}{@link List}{@code <}{@link T}{@code >}{@code >}。
         */
        protected BufferOperator(int bufferSize, Subscriber<List<T>> subscriber) {
            super(subscriber);
            this.bufferSize = bufferSize;
        }

        @Override
        protected void request0(long count) {
            try {
                super.request0(Math.multiplyExact(count, this.bufferSize));
            } catch (ArithmeticException e) {
                super.request0(Long.MAX_VALUE);
            }
        }

        @Override
        protected void cancel0() {
            this.consumeRemained();
            super.cancel0();
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            List<T> newBuffer;
            synchronized (this.lock) {
                this.buffer.add(data);
                if (this.buffer.size() < this.bufferSize) {
                    return;
                }
                newBuffer = new ArrayList<>(this.buffer);
                this.buffer.clear();
            }
            this.getNextSubscriber().consume(newBuffer);
        }

        @Override
        protected void complete0(Subscription subscription) {
            this.consumeRemained();
            super.complete0(subscription);
        }

        @Override
        protected void fail0(Subscription subscription, Exception cause) {
            this.consumeRemained();
            super.fail0(subscription, cause);
        }

        private void consumeRemained() {
            List<T> newBuffer;
            synchronized (this.lock) {
                if (this.buffer.isEmpty()) {
                    newBuffer = new ArrayList<>();
                } else {
                    newBuffer = new ArrayList<>(this.buffer);
                    this.buffer.clear();
                }
            }
            if (CollectionUtils.isNotEmpty(newBuffer)) {
                this.getNextSubscriber().consume(newBuffer);
            }
        }
    }
}
