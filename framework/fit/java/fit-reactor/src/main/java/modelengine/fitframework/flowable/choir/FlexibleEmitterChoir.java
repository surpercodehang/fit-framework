/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.choir;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.subscription.AbstractSubscription;
import modelengine.fitframework.flowable.util.OnSubscribedObserver;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ObjectUtils;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 表示 {@link Choir} 的可配置订阅、元素请求、取消行为的数据发送器实现。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 何天放
 * @since 2024-05-22
 */
public class FlexibleEmitterChoir<T> extends AbstractChoir<T> implements OnSubscribedObserver {
    private static final Runnable EMPTY_RUNNABLE = () -> {};
    private static final Consumer<OnSubscribedObserver> EMPTY_ON_SUBSCRIBED_OBSERVER_CONSUMER = count -> {};
    private static final Consumer<Long> EMPTY_LONG_CONSUMER = count -> {};
    private static final Consumer<Emitter<?>> EMPTY_EMITTER_CONSUMER = emitter -> {};

    private final Supplier<Emitter<T>> emitterSupplier;
    private final Consumer<Emitter<T>> emitterConsumer;
    private final Consumer<OnSubscribedObserver> subscribeHandler;
    private final Consumer<Long> requestHandler;
    private final Runnable cancelHandler;
    private Subscriber<T> subscriber;
    private FlexibleEmitterChoirSubscription<T> subscription;

    /**
     * 构建一个可配置订阅、元素请求、取消行为并且订阅关系发生通知时机可控的 {@link Choir} 数据发送器实现。
     *
     * @param emitterSupplier 表示数据发送器供应方式的 {@link Supplier}{@code <}{@link T}{@code >}。
     * @param emitterConsumer 表示数据发送器消费方式的 {@link Consumer}{@code <}{@link T}{@code >}。
     * @param subscribeHandler 表示订阅行为的 {@link Runnable}。
     * @param requestHandler 表示元素请求行为的 {@link Consumer}{@code <}{@link Long}{@code >}。
     * @param cancelHandler 表示取消行为的 {@link Runnable}。
     */
    public FlexibleEmitterChoir(Supplier<Emitter<T>> emitterSupplier, Consumer<Emitter<T>> emitterConsumer,
            Consumer<OnSubscribedObserver> subscribeHandler, Consumer<Long> requestHandler, Runnable cancelHandler) {
        this.emitterSupplier = notNull(emitterSupplier, "The emitter supplier cannot be null.");
        this.emitterConsumer = emitterConsumer == null ? ObjectUtils.cast(EMPTY_EMITTER_CONSUMER) : emitterConsumer;
        this.subscribeHandler =
                subscribeHandler == null ? ObjectUtils.cast(EMPTY_ON_SUBSCRIBED_OBSERVER_CONSUMER) : subscribeHandler;
        this.requestHandler = requestHandler == null ? EMPTY_LONG_CONSUMER : requestHandler;
        this.cancelHandler = cancelHandler == null ? EMPTY_RUNNABLE : cancelHandler;
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        this.subscriber = subscriber;
        this.subscription = new FlexibleEmitterChoirSubscription<>(subscriber, this.requestHandler, this.cancelHandler);
        this.subscribeHandler.accept(this);
    }

    @Override
    public void notifyOnSubscribed() {
        Emitter<T> emitter = notNull(this.emitterSupplier.get(), "The result of emitter supplier cannot be null.");
        emitter.observe(this.subscription);
        this.subscriber.onSubscribed(this.subscription);
        try {
            this.emitterConsumer.accept(emitter);
        } catch (Exception ex) {
            emitter.fail(ex);
        }
    }

    private static class FlexibleEmitterChoirSubscription<T> extends AbstractSubscription
            implements Emitter.Observer<T> {
        private final Subscriber<T> subscriber;
        private final AtomicLong requested = new AtomicLong();
        private final Consumer<Long> requestHandler;
        private final Runnable cancelHandler;

        public FlexibleEmitterChoirSubscription(Subscriber<T> subscriber, Consumer<Long> requestHandler,
                Runnable cancelHandler) {
            this.subscriber = notNull(subscriber, "The subscriber cannot be null.");
            this.requestHandler = notNull(requestHandler, "The request handler cannot be null.");
            this.cancelHandler = notNull(cancelHandler, "The cancel handler cannot be null.");
        }

        @Override
        protected void request0(long count) {
            this.requested.addAndGet(count);
            this.requestHandler.accept(count);
        }

        @Override
        protected void cancel0() {
            this.cancelHandler.run();
        }

        @Override
        public void onEmittedData(T data) {
            if (this.isCancelled()) {
                return;
            }
            if (requested.getAndDecrement() > 0) {
                this.subscriber.consume(data);
            } else {
                this.requested.getAndIncrement();
            }
        }

        @Override
        public void onCompleted() {
            if (this.isCancelled()) {
                return;
            }
            this.subscriber.complete();
        }

        @Override
        public void onFailed(Exception cause) {
            if (this.isCancelled()) {
                return;
            }
            this.subscriber.fail(cause);
        }
    }
}
