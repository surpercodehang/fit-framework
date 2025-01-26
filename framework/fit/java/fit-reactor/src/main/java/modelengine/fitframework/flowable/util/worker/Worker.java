/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.util.worker;

import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Subscriber;

/**
 * 表示辅助消费响应式流中元素的工具。
 *
 * @param <T> 表示响应式流中元素类型的 {@link T}。
 * @author 何天放
 * @since 2024-02-20
 */
public interface Worker<T> extends Subscriber<T> {
    /**
     * 启动 {@link Worker}，开始元素消费。
     */
    void run();

    /**
     * 向响应式流中请求一定数量的元素。
     *
     * @param count 表示所请求元素数量的 {@code long}。
     */
    void request(long count);

    /**
     * 取消对于该响应式流的订阅。
     */
    void cancel();

    /**
     * 创建具有默认实现的 {@link Worker}。
     *
     * @param observer 表示用于处理回调的 {@link WorkerObserver}。
     * @param publisher 表示需要被辅助消费的响应式流的 {@link Publisher}。
     * @param <T> 表示响应式流中元素类型的 {@link T}。
     * @return 表示所创建的用于辅助消费的 {@link Worker}。
     */
    static <T> Worker<T> create(WorkerObserver<T> observer, Publisher<T> publisher) {
        return create(observer, publisher, 0);
    }

    /**
     * 创建具有默认实现的 {@link Worker}。
     *
     * @param observer 表示用于处理回调的 {@link WorkerObserver}。
     * @param publisher 表示需要被辅助消费的响应式流的 {@link Publisher}。
     * @param id 表示用于区分不同 {@link Worker} 的 {@code long}。
     * @param <T> 表示响应式流中元素类型的 {@link T}。
     * @return 表示所创建的用于辅助消费的 {@link Worker}。
     */
    static <T> Worker<T> create(WorkerObserver<T> observer, Publisher<T> publisher, long id) {
        return new DefaultWorker<>(observer, publisher, id);
    }
}
