/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.publisher;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.FlowableException;
import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.flowable.operation.AbstractOperation;
import modelengine.fitframework.flowable.util.counter.Counter;
import modelengine.fitframework.flowable.util.worker.Worker;
import modelengine.fitframework.flowable.util.worker.WorkerObserver;

import java.util.function.Function;

/**
 * 表示 {@link Publisher} 的将每个数据通过指定的方式转换为一个响应式流并将各响应式流中的每个元素依次发送给下游的实现。
 *
 * @param <T> 表示发布者中数据转换前的类型的 {@link T}。
 * @param <R> 表示发布者中数据转换成的 {@link Publisher} 的元素类型的 {@link R}。
 * @author 何天放
 * @since 2024-02-20
 */
public class FlatMapPublisherDecorator<T, R> implements Publisher<R> {
    private final Publisher<T> decorated;
    private final Function<T, Publisher<R>> flatMapper;

    public FlatMapPublisherDecorator(Publisher<T> decorated, Function<T, Publisher<R>> flatMapper) {
        this.decorated = notNull(decorated, "The decorated flat map publisher cannot be null.");
        this.flatMapper = notNull(flatMapper, "The flat mapper cannot be null.");
    }

    @Override
    public void subscribe(Subscriber<R> subscriber) {
        this.decorated.subscribe(new FlatMapOperation<>(subscriber, this.flatMapper));
    }

    private static class FlatMapOperation<T, R> extends AbstractOperation<T, R> implements WorkerObserver<R> {
        private final Function<T, Publisher<R>> flatMapper;
        private Worker<R> worker = null;

        private final Counter requested = Counter.create();

        FlatMapOperation(Subscriber<R> subscriber, Function<T, Publisher<R>> flatMapper) {
            super(subscriber);
            this.flatMapper = flatMapper;
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            if (this.worker != null && !this.worker.isCompleted()) {
                throw new FlowableException("The data in the current publisher is not completely consumed.");
            }
            Publisher<R> publisher;
            try {
                publisher = this.flatMapper.apply(data);
            } catch (Exception cause) {
                this.getPreSubscription().cancel();
                this.getNextSubscriber().fail(cause);
                return;
            }
            this.worker = Worker.create(this, publisher, Long.MAX_VALUE);
            this.worker.run();
        }

        @Override
        protected void complete0(Subscription subscription) {
            if (this.worker != null && !this.worker.isCompleted()) {
                return;
            }
            super.complete0(subscription);
        }

        @Override
        protected void request0(long count) {
            this.requested.increase(count);
            if (this.worker == null || this.worker.isCompleted()) {
                this.getPreSubscription().request(1);
            } else {
                this.worker.request(count);
            }
        }

        @Override
        public void onWorkerSubscribed(Subscription subscription) {
            subscription.request(this.requested.getValue());
        }

        @Override
        public void onWorkerConsumed(R data, long id) {
            this.requested.decrease();
            this.getNextSubscriber().consume(data);
        }

        @Override
        public void onWorkerFailed(Exception cause) {
            this.getNextSubscriber().fail(cause);
        }

        @Override
        public void onWorkerCompleted() {
            if (this.isCompleted()) {
                this.getNextSubscriber().complete();
                return;
            }
            if (this.requested.getValue() > 0) {
                this.getPreSubscription().request(1);
            }
        }
    }
}
