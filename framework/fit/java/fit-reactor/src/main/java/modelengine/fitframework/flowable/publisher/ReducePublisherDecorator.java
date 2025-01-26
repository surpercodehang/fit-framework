/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.publisher;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.flowable.operation.AbstractOperation;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

/**
 * 表示 {@link Publisher} 的携带数据合并的实现。
 *
 * @param <T> 表示发布者中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-11
 */
public class ReducePublisherDecorator<T> implements Publisher<T> {
    private final Publisher<T> decorated;
    private final BinaryOperator<T> reducer;

    public ReducePublisherDecorator(Publisher<T> decorated, BinaryOperator<T> reducer) {
        this.decorated = notNull(decorated, "The decorated reduce publisher cannot be null.");
        this.reducer = notNull(reducer, "The reducer cannot be null.");
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        this.decorated.subscribe(new ReduceOperation<>(this.reducer, subscriber));
    }

    private static class ReduceOperation<T> extends AbstractOperation<T, T> {
        private final BinaryOperator<T> reducer;
        private final AtomicBoolean requested = new AtomicBoolean();
        private final AtomicReference<T> result = new AtomicReference<>();

        /**
         * 使用数据聚合方法和下游的订阅者来初始化 {@link ReduceOperation}。
         *
         * @param reducer 表示数据聚合方法的 {@link BinaryOperator}{@code <}{@link T}{@code >}。
         * @param subscriber 表示下游的订阅者的 {@link Subscriber}{@code <}{@link T}{@code >}。
         */
        ReduceOperation(BinaryOperator<T> reducer, Subscriber<T> subscriber) {
            super(subscriber);
            this.reducer = reducer;
        }

        @Override
        protected void request0(long count) {
            if (this.requested.compareAndSet(false, true)) {
                this.getPreSubscription().request(1);
            }
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            if (this.result.get() != null) {
                try {
                    T reduced = this.reducer.apply(this.result.get(), data);
                    this.result.set(reduced);
                } catch (Exception cause) {
                    this.getPreSubscription().cancel();
                    this.getNextSubscriber().fail(cause);
                }
            } else {
                this.result.set(data);
            }
            this.getPreSubscription().request(1);
        }

        @Override
        protected void complete0(Subscription subscription) {
            this.getNextSubscriber().consume(this.result.get());
            super.complete0(subscription);
        }
    }
}
