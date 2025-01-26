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

import java.util.concurrent.atomic.AtomicLong;

/**
 * 表示 {@link Publisher} 的计算数据数量并将数量转换为一个响应式流的实现。
 *
 * @param <T> 表示发布者中数据类型的 {@link T}。
 * @author 黄夏露
 * @since 2024-03-04
 */
public class CountPublisherDecorator<T> implements Publisher<Long> {
    private final Publisher<T> decorated;

    public CountPublisherDecorator(Publisher<T> decorated) {
        this.decorated = notNull(decorated, "The decorated count publisher cannot be null.");
    }

    @Override
    public void subscribe(Subscriber<Long> subscriber) {
        this.decorated.subscribe(new CountOperation<>(subscriber));
    }

    private static class CountOperation<T> extends AbstractOperation<T, Long> {
        private final AtomicLong count;

        CountOperation(Subscriber<Long> subscriber) {
            super(subscriber);
            this.count = new AtomicLong();
        }

        @Override
        protected void request0(long count) {
            super.request0(Long.MAX_VALUE);
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            this.count.getAndIncrement();
        }

        @Override
        protected void complete0(Subscription subscription) {
            this.getNextSubscriber().consume(this.count.get());
            super.complete0(subscription);
        }
    }
}
