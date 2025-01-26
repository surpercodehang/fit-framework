/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.operation;

import static modelengine.fitframework.inspection.Validation.greaterThan;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.flowable.subscriber.AbstractSubscriber;
import modelengine.fitframework.inspection.Nonnull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 表示同时是订阅者和订阅关系的抽象父类。
 *
 * @param <T> 表示当前类作为订阅者时，所订阅的数据类型的 {@link T}。
 * @param <R> 表示当前类作为订阅关系时，下游订阅者所订阅的数据类型的 {@link R}。
 * @author 季聿阶
 * @since 2024-02-09
 */
public abstract class AbstractOperation<T, R> extends AbstractSubscriber<T> implements Subscription {
    private final Subscriber<R> nextSubscriber;
    private final AtomicBoolean cancelled = new AtomicBoolean();

    /**
     * 使用下游的订阅者来初始化 {@link AbstractOperation}。
     *
     * @param nextSubscriber 表示下游的订阅者的 {@link Subscriber}{@code <}{@link R}{@code >}。
     */
    protected AbstractOperation(Subscriber<R> nextSubscriber) {
        this.nextSubscriber = notNull(nextSubscriber, "The next subscriber cannot be null.");
    }

    /**
     * 获取下游的订阅者。
     *
     * @return 表示下游的订阅者的 {@link Subscriber}{@code <}{@link R}{@code >}。
     */
    @Nonnull
    protected Subscriber<R> getNextSubscriber() {
        return this.nextSubscriber;
    }

    @Override
    protected void onSubscribed0(@Nonnull Subscription subscription) {
        this.nextSubscriber.onSubscribed(this);
    }

    @Override
    public void request(long count) {
        greaterThan(count, 0, "The number of elements to request must be positive. [count={0}]", count);
        if (this.cancelled.get()) {
            return;
        }
        this.request0(count);
    }

    /**
     * 请求指定数量的数据。
     * <p>当前请求没有被取消。</p>
     *
     * @param count 表示请求的数据的数量的 {@code long}。
     */
    protected void request0(long count) {
        this.getPreSubscription().request(count);
    }

    @Override
    public void cancel() {
        if (!this.cancelled.compareAndSet(false, true)) {
            return;
        }
        this.cancel0();
    }

    /**
     * 取消当前的订阅关系。
     * <p>默认调用上游订阅关系进行取消。</p>
     */
    protected void cancel0() {
        this.getPreSubscription().cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled.get();
    }

    @Override
    protected void consume(@Nonnull Subscription subscription, T data) {
        if (this.isTerminated()) {
            return;
        }
        this.consume0(subscription, data);
    }

    /**
     * 消费订阅的数据。
     * <p>当前待消费的数据处于可消费的状态。</p>
     *
     * @param subscription 表示上游的订阅关系的 {@link Subscription}。
     * @param data 表示待消费的数据的 {@link T}。
     */
    protected abstract void consume0(Subscription subscription, T data);

    @Override
    protected void complete(@Nonnull Subscription subscription) {
        if (this.cancelled.get()) {
            return;
        }
        this.complete0(subscription);
    }

    /**
     * 消费正常终结信号。
     * <p>当前待消费的正常终结信号处于可消费的状态。</p>
     *
     * @param subscription 表示上游的订阅关系的 {@link Subscription}。
     */
    protected void complete0(Subscription subscription) {
        this.getNextSubscriber().complete();
    }

    @Override
    protected void fail(@Nonnull Subscription subscription, Exception cause) {
        if (this.cancelled.get()) {
            return;
        }
        this.fail0(subscription, cause);
    }

    /**
     * 消费异常终结信号。
     * <p>当前待消费的异常终结信号处于可消费的状态。</p>
     *
     * @param subscription 表示上游的订阅关系的 {@link Subscription}。
     * @param cause 表示待消费的异常 {@link Exception}
     */
    protected void fail0(Subscription subscription, Exception cause) {
        this.getNextSubscriber().fail(cause);
    }

    private boolean isTerminated() {
        return this.cancelled.get() || this.isCompleted() || this.isFailed();
    }
}
