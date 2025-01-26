/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.util.worker;

import modelengine.fitframework.flowable.Subscription;

/**
 * 表示 {@link Worker} 接收到待消费元素、异常终结信号或正常终结信号时的观察者。
 *
 * @param <T> 表示数据类型的 {@link T}。
 * @author 何天放
 * @since 2024-02-20
 */
public interface WorkerObserver<T> {
    /**
     * 表示 {@link Worker} 在订阅关系发生时调用的接口。
     *
     * @param subscription 表示订阅关系 {@link Subscription}。
     */
    void onWorkerSubscribed(Subscription subscription);

    /**
     * 表示 {@link Worker} 接收到应消费数据时回传应消费数据的接口。
     *
     * @param data 表示应消费的数据 {@link T}。
     * @param id 表示 Worker 标识的 {@code long}。
     */
    void onWorkerConsumed(T data, long id);

    /**
     * 表示 {@link Worker} 接收到异常终结信号时回传异常终结信号的接口。
     *
     * @param cause 表示应消费异常的 {@code cause}。
     */
    void onWorkerFailed(Exception cause);

    /**
     * 表示 {@link Worker} 接收到正常终结信号时回传正常终结信号的接口。
     */
    void onWorkerCompleted();
}
