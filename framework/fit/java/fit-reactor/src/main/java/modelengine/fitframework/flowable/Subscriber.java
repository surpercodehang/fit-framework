/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable;

import modelengine.fitframework.flowable.subscriber.EmptySubscriber;
import modelengine.fitframework.flowable.subscriber.FunctionalSubscriber;
import modelengine.fitframework.util.ObjectUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 表示订阅者，可以处理发布者所发布的数据，以及订阅开始、正常终结和异常终结信号。
 * <p>在对于发布者进行订阅之后，其 {@link #onSubscribed(Subscription)} 方法将被调用，并通过该方法向发布者提供一个
 * {@link Subscription}，作为订阅者其通过发布者所提供的 {@link Subscription} 请求自己所需要数量的元素，而在未对于其订阅的发布者通过
 * {@link Subscription#request(long)} 方法请求数据前将不会收到任何数据。</p>
 * <p>在进行订阅后，发布者为其提供以下保证：
 * <ul>
 *     <li>发布者所发布元素数量不大于其通过 {@link Subscription} 所请求的元素数量。</li>
 *     <li>发布者最多发布一个异常或数据结束事件。</li>
 * </ul>
 * </p>
 *
 * @param <T> 表示订阅的数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-07
 */
public interface Subscriber<T> {
    /**
     * 表示订阅关系发生时的事件。
     * <p>将在进行订阅即调用 {@link Publisher#subscribe(Subscriber)} 后被调用。</p>
     *
     * @param subscription 表示给定的订阅关系的 {@link Subscription}。
     */
    void onSubscribed(Subscription subscription);

    /**
     * 消费订阅的数据。
     * <p>{@link Publisher} 所发布的数据将由该方法进行消费，{@link Subscriber} 可在该方法中定义数据的消费逻辑。</p>
     *
     * @param data 表示待消费的数据的 {@link T}。
     */
    void consume(T data);

    /**
     * 表示正常终结信号。
     * <p>在 {@link Publisher} 发送正常终结信号后，该 {@link Subscriber} 的任何方法将不会再被 {@link Publisher} 调用。</p>
     */
    void complete();

    /**
     * 判断当前订阅者是否已经完成。
     * <p>订阅者完成的标志是，上游 {@link Publisher} 已经对当前订阅者调用了 {@link #complete()} 方法。</p>
     *
     * @return 如果当前订阅者已经完成，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isCompleted();

    /**
     * 表示异常终结信号。
     * <p>在 {@link Publisher} 发送异常终结信号后，该 {@link Subscriber} 的任何方法将不会再被 {@link Publisher} 调用。</p>
     *
     * @param cause 表示订阅中的错误信息的 {@link Exception}。
     */
    void fail(Exception cause);

    /**
     * 判断当前订阅者是否已经失败。
     * <p>订阅者失败的标志是，上游 {@link Publisher} 已经对当前订阅者调用了 {@link #fail(Exception)} 方法。</p>
     *
     * @return 如果当前订阅者已经失败，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isFailed();

    /**
     * 获取一个空的订阅者。
     *
     * @param <T> 表示订阅数据类型的 {@link T}。
     * @return 表示一个空的订阅者的 {@link EmptySubscriber}{@code <}{@link T}{@code >}。
     */
    static <T> EmptySubscriber<T> empty() {
        return ObjectUtils.cast(EmptySubscriber.INSTANCE);
    }

    /**
     * 通过指定的 Lambda 表达式，获取一个订阅者。
     *
     * @param onSubscribedAction 表示响应式流被订阅时的行为的 {@link Consumer}{@code <}{@link Subscription}{@code >}。
     * @param consumeAction 表示响应式流中的数据消费时的行为的
     * {@link BiConsumer}{@code <}{@link Subscription}{@code , }{@link T}{@code >}。
     * @param completeAction 表示响应式流正常终结时的行为的 {@link Consumer}{@code <}{@link Subscription}{@code >}。
     * @param failAction 表示响应式流异常终结时的行为的
     * {@link BiConsumer}{@code <}{@link Subscription}{@code , }{@link Exception}{@code >}。
     * @param <T> 表示订阅者所订阅数据的类型的 {@link T}。
     * @return 表示一个携带指定 Lambda 表达式的订阅者的 {@link FunctionalSubscriber}{@code <}{@link T}{@code >}。
     * @throws IllegalArgumentException 当 {@code onSubscribedAction}，{@code consumeAction}，{@code completeAction} 或
     * {@code failAction} 为 {@code null} 时。
     */
    static <T> FunctionalSubscriber<T> functional(Consumer<Subscription> onSubscribedAction,
            BiConsumer<Subscription, T> consumeAction, Consumer<Subscription> completeAction,
            BiConsumer<Subscription, Exception> failAction) {
        return new FunctionalSubscriber<>(onSubscribedAction, consumeAction, completeAction, failAction);
    }
}
