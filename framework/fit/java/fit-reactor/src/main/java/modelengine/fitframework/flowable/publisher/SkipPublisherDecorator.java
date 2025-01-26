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
import modelengine.fitframework.flowable.util.counter.Counter;
import modelengine.fitframework.inspection.Nonnull;

/**
 * 表示 {@link Publisher} 的从开始跳过指定个数元素的过滤的实现。
 *
 * @param <T> 表示发布者中待过滤数据的类型的 {@link T}。
 * @author 鲁为
 * @since 2024-03-07
 */
public class SkipPublisherDecorator<T> implements Publisher<T> {
    private final Publisher<T> decorated;
    private final int count;

    public SkipPublisherDecorator(Publisher<T> decorated, int count) {
        this.decorated = notNull(decorated, "The decorated count publisher cannot be null.");
        this.count = greaterThan(count, 0, "The count to skip must be positive. [count={0}]", count);
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        this.decorated.subscribe(new SkipOperation<>(subscriber, this.count));
    }

    private static class SkipOperation<T> extends AbstractOperation<T, T> {
        private final Counter skipConsumeCount;

        SkipOperation(Subscriber<T> subscriber, int count) {
            super(subscriber);
            this.skipConsumeCount = Counter.create(count);
        }

        @Override
        protected void onSubscribed0(@Nonnull Subscription subscription) {
            long skipCount = this.skipConsumeCount.getValue();
            super.onSubscribed0(subscription);
            if (skipCount > 0) {
                super.request0(skipCount);
            }
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            if (hasSkipped() || !skip()) {
                this.getNextSubscriber().consume(data);
            }
        }

        private boolean hasSkipped() {
            return this.skipConsumeCount.getValue() == 0;
        }

        private boolean skip() {
            return this.skipConsumeCount.decrease() == 1;
        }
    }
}
