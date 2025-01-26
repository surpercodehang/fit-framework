/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable;

/**
 * 表示发布者，负责产生数据流并发布给订阅者。
 * <p>发布者产生一系列数量不限的的元素，并按照其订阅者的要求进行发布，一个发布者可为多个订阅者提供服务，其订阅者通过调用其
 * {@link #subscribe(Subscriber)} 方法对其进行订阅。</p>
 *
 * @param <T> 表示发布的数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-07
 */
public interface Publisher<T> {
    /**
     * 向发布者订阅以启动数据发送。
     * <p>该方法可被多次执行，每次将为其订阅者产生一个新的 {@link Subscription}，在订阅过程中发生的异常将通过
     * {@link Subscriber#fail(Exception)} 进行传递。</p>
     *
     * @param subscriber 表示已订阅的 {@link Subscriber}{@code <}{@link T}{@code >}。
     */
    void subscribe(Subscriber<T> subscriber);
}
