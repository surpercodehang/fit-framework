/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.subscriber;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Solo;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.inspection.Nonnull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 表示使用 Lambda 表达式进行订阅的 {@link Subscriber 订阅者}。
 *
 * @param <T> 表示订阅者订阅的数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-07
 */
public class FunctionalSubscriber<T> extends AbstractSubscriber<T> {
    /** 表示 {@link Choir} 在 {@link #onSubscribed(Subscription)} 方法被调用时的默认行为。 */
    public static final Consumer<Subscription> DEFAULT_ON_SUBSCRIBED_CHOIR_ACTION =
            subscription -> subscription.request(Long.MAX_VALUE);

    /** 表示 {@link Solo} 在 {@link #onSubscribed(Subscription)} 方法被调用时的默认行为。 */
    public static final Consumer<Subscription> DEFAULT_ON_SUBSCRIBED_SOLO_ACTION =
            subscription -> subscription.request(1);

    /** 表示订阅者在 {@link #consume(Object)} 方法被调用时的空行为。 */
    public static final BiConsumer<Subscription, Object> EMPTY_CONSUME_ACTION = (subscription, data) -> {};

    /** 表示订阅者在 {@link #complete()} 方法被调用时的空行为。 */
    public static final Consumer<Subscription> EMPTY_COMPLETE_ACTION = (subscription) -> {};

    /** 表示订阅者在 {@link #fail(Exception)} 方法被调用时的空行为。 */
    public static final BiConsumer<Subscription, Exception> EMPTY_FAIL_ACTION = (subscription, cause) -> {};

    private final Consumer<Subscription> onScribedAction;
    private final BiConsumer<Subscription, T> consumeAction;
    private final Consumer<Subscription> completeAction;
    private final BiConsumer<Subscription, Exception> failAction;

    public FunctionalSubscriber(Consumer<Subscription> onSubscribedAction, BiConsumer<Subscription, T> consumeAction,
            Consumer<Subscription> completeAction, BiConsumer<Subscription, Exception> failAction) {
        this.onScribedAction = notNull(onSubscribedAction, "The action on subscribed cannot be null.");
        this.consumeAction = notNull(consumeAction, "The action to consume data cannot be null.");
        this.completeAction = notNull(completeAction, "The action on complete cannot be null.");
        this.failAction = notNull(failAction, "The action on fail cannot be null.");
    }

    @Override
    protected void onSubscribed0(@Nonnull Subscription subscription) {
        this.onScribedAction.accept(subscription);
    }

    @Override
    protected void consume(@Nonnull Subscription subscription, T data) {
        this.consumeAction.accept(subscription, data);
    }

    @Override
    protected void complete(@Nonnull Subscription subscription) {
        this.completeAction.accept(subscription);
    }

    @Override
    protected void fail(@Nonnull Subscription subscription, Exception cause) {
        this.failAction.accept(subscription, cause);
    }
}
