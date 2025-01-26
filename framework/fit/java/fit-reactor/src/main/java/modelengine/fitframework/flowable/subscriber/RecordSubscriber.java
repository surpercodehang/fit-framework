/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.subscriber;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.FlowableException;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.inspection.Nonnull;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试用工具类，可以记录 {@link modelengine.fitframework.flowable.Subscriber}
 * 的各个方法被调用时的信息，并且能够灵活的进行元素请求和订阅取消。
 *
 * @param <T> 表示订阅者订阅的数据类型的 {@link T}。
 * @author 何天放
 * @since 2024-02-20
 */
public class RecordSubscriber<T> extends EmptySubscriber<T> {
    private final List<Record<T>> consumeRecords = new ArrayList<>();
    private final List<Record<Boolean>> onSubscribedRecords = new ArrayList<>();
    private final List<Record<Exception>> failRecords = new ArrayList<>();
    private final List<Record<Boolean>> completeRecords = new ArrayList<>();
    private final long onSubscribedRequest;
    private final long consumeRequest;
    private final long cancelWhen;

    private Subscription subscription;
    private long index = 0L;

    /**
     * 创建测试用工具类实例，在发生订阅时请求 {@code Long.MAX_VALUE} 个元素。
     */
    public RecordSubscriber() {
        this(Long.MAX_VALUE, 0, -1);
    }

    /**
     * 创建测试用工具类实例。
     *
     * @param onSubscribedRequest 表示发生订阅时请求元素数量的 {@code long}。
     */
    public RecordSubscriber(long onSubscribedRequest) {
        this(onSubscribedRequest, 0, -1);
    }

    /**
     * 创建测试用工具类实例。
     *
     * @param onSubscribedRequest 表示发生订阅时请求元素数量的 {@code long}。
     * @param consumeRequest 表示每次有元素被消费时请求数量的 {@code long}。
     */
    public RecordSubscriber(long onSubscribedRequest, long consumeRequest) {
        this(onSubscribedRequest, consumeRequest, -1);
    }

    /**
     * 创建测试用工具类实例。
     *
     * @param onSubscribedRequest 表示发生订阅时请求元素数量的 {@code long}。
     * @param consumeRequest 表示每次有元素被消费时请求数量的 {@code long}。
     * @param cancelWhen 表示在元素数量达到指定值时取消订阅的 {@code long}。
     */
    public RecordSubscriber(long onSubscribedRequest, long consumeRequest, long cancelWhen) {
        this.onSubscribedRequest = onSubscribedRequest;
        this.consumeRequest = consumeRequest;
        this.cancelWhen = cancelWhen;
    }

    /**
     * 获取订阅关系。
     *
     * @return 表示所获取订阅关系的 {@link Subscription}。
     */
    public Subscription getSubscription() {
        return this.subscription;
    }

    /**
     * 获取所有订阅记录。
     *
     * @return 表示订阅记录的 {@code List<}{@link Record}{@code <Boolean>>}。
     */
    public List<Record<Boolean>> getOnSubscribedRecords() {
        return this.onSubscribedRecords;
    }

    /**
     * 获取所有元素消费记录。
     *
     * @return 表示元素消费记录的 {@code List<}{@link Record}{@code <}{@link T}{@code >>}。
     */
    public List<Record<T>> getConsumeRecords() {
        return this.consumeRecords;
    }

    /**
     * 获取所有正常终结信号记录。
     *
     * @return 表示正常终结信号记录的 {@code List<}{@link Record}{@code <Boolean>>}。
     */
    public List<Record<Boolean>> getCompleteRecords() {
        return this.completeRecords;
    }

    /**
     * 获取所有异常终结信号记录。
     *
     * @return 表示异常终结信号记录的 {@code List<}{@link Record}{@code <Exception>>}。
     */
    public List<Record<Exception>> getFailRecords() {
        return this.failRecords;
    }

    @Override
    public void onSubscribed(Subscription subscription) {
        notNull(subscription, () -> new FlowableException("The subscriber cannot be subscribed twice."));
        this.subscription = subscription;
        this.onSubscribedRecords.add(new Record<>(false, this.index));
        this.index++;
        if (this.onSubscribedRequest > 0) {
            this.subscription.request(this.onSubscribedRequest);
        }
    }

    @Override
    public void consume(T data) {
        this.consumeRecords.add(new Record<>(data, this.index));
        this.index++;
        if (this.cancelWhen >= 0 && this.consumeRecords.size() >= this.cancelWhen) {
            this.subscription.cancel();
            return;
        }
        if (this.consumeRequest > 0) {
            this.subscription.request(this.consumeRequest);
        }
    }

    @Override
    protected void complete(@Nonnull Subscription subscription) {
        this.completeRecords.add(new Record<>(false, this.index));
        this.index++;
    }

    @Override
    public void fail(Exception cause) {
        this.failRecords.add(new Record<>(cause, this.index));
        this.index++;
    }

    /**
     * 获取所有被消费的元素。
     *
     * @return 表示被消费元素列表的 {@code List<T>}
     */
    public List<T> getElements() {
        List<T> result = new ArrayList<>();
        for (Record<T> record : this.consumeRecords) {
            result.add(record.data);
        }
        return result;
    }

    /**
     * 获取是否收到正常终结信号。
     *
     * @return 表示是否收到正常终结信号的 {@code boolean}。
     * @throws FlowableException 当收到不止一个正常终结信号时。
     */
    public boolean receivedCompleted() {
        if (this.completeRecords.size() > 1) {
            throw new FlowableException("Completed called more than once.");
        }
        return this.completeRecords.size() == 1;
    }

    /**
     * 获取是否收到异常终结信号。
     *
     * @return 表示是否收到异常终结信号的 {@code boolean}。
     * @throws FlowableException 当收到不止一个异常终结信号时。
     */
    public boolean receivedFailed() {
        if (this.failRecords.size() > 1) {
            throw new FlowableException("Failed called more than once.");
        }
        return this.failRecords.size() == 1;
    }

    /**
     * 表示单条记录。
     *
     * @param <T> 表示元素类型的 {@code T}
     */
    public static class Record<T> {
        private final long index;
        private final T data;

        protected Record(T data, long index) {
            this.data = data;
            this.index = index;
        }

        /**
         * 获取当前记录的序号。
         *
         * @return 表示记录序号的 {@code long}。
         */
        public long getIndex() {
            return this.index;
        }

        /**
         * 获取当前记录的内容。
         *
         * @return 表示记录内容的 {@code T}。
         */
        public T getData() {
            return this.data;
        }
    }
}
