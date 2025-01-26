/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.subscriber;

import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.inspection.Nonnull;

import java.util.List;

/**
 * 并发安全的测试用工具类，可以记录 {@link modelengine.fitframework.flowable.Subscriber}
 * 的各个方法被调用时的信息，并且能够灵活的进行元素请求和订阅取消。
 *
 * @param <T> 表示订阅者订阅的数据类型的 {@link T}。
 * @author 何天放
 * @since 2024-02-21
 */
public class ConcurrentRecordSubscriber<T> extends RecordSubscriber<T> {
    /**
     * 创建测试用工具类实例，在发生订阅时请求 {@code Long.MAX_VALUE} 个元素。
     */
    public ConcurrentRecordSubscriber() {
        super();
    }

    /**
     * 创建测试用工具类实例。
     *
     * @param onSubscribedRequest 表示发生订阅时请求元素数量的 {@code long}。
     */
    public ConcurrentRecordSubscriber(long onSubscribedRequest) {
        super(onSubscribedRequest);
    }

    /**
     * 创建测试用工具类实例。
     *
     * @param onSubscribedRequest 表示发生订阅时请求元素数量的 {@code long}。
     * @param consumeRequest 表示每次有元素被消费时请求数量的 {@code long}。
     */
    public ConcurrentRecordSubscriber(long onSubscribedRequest, long consumeRequest) {
        super(onSubscribedRequest, consumeRequest);
    }

    /**
     * 创建测试用工具类实例。
     *
     * @param onSubscribedRequest 表示发生订阅时请求元素数量的 {@code long}。
     * @param consumeRequest 表示每次有元素被消费时请求数量的 {@code long}。
     * @param cancelWhen 表示在元素数量达到指定值时取消订阅的 {@code long}。
     */
    public ConcurrentRecordSubscriber(long onSubscribedRequest, long consumeRequest, long cancelWhen) {
        super(onSubscribedRequest, consumeRequest, cancelWhen);
    }

    @Override
    public Subscription getSubscription() {
        synchronized (this) {
            return super.getSubscription();
        }
    }

    @Override
    public List<Record<Boolean>> getOnSubscribedRecords() {
        synchronized (this) {
            return super.getOnSubscribedRecords();
        }
    }

    @Override
    public List<Record<T>> getConsumeRecords() {
        synchronized (this) {
            return super.getConsumeRecords();
        }
    }

    @Override
    public List<Record<Boolean>> getCompleteRecords() {
        synchronized (this) {
            return super.getCompleteRecords();
        }
    }

    @Override
    public List<Record<Exception>> getFailRecords() {
        synchronized (this) {
            return super.getFailRecords();
        }
    }

    @Override
    public void onSubscribed(Subscription subscription) {
        synchronized (this) {
            super.onSubscribed(subscription);
        }
    }

    @Override
    public void consume(T data) {
        synchronized (this) {
            super.consume(data);
        }
    }

    @Override
    protected void complete(@Nonnull Subscription subscription) {
        synchronized (this) {
            super.complete(subscription);
        }
    }

    @Override
    public void fail(Exception cause) {
        synchronized (this) {
            super.fail(cause);
        }
    }

    @Override
    public List<T> getElements() {
        synchronized (this) {
            return super.getElements();
        }
    }

    @Override
    public boolean receivedCompleted() {
        synchronized (this) {
            return super.receivedCompleted();
        }
    }

    @Override
    public boolean receivedFailed() {
        synchronized (this) {
            return super.receivedFailed();
        }
    }
}
