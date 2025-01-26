/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable;

/**
 * 表示 {@link Publisher} 和 {@link Subscriber} 之间的订阅关系。
 * <p>{@link Subscription} 管理着 {@link Publisher} 和 {@link Subscriber} 之间的数据流，同时，提供了 {@link Subscriber} 向
 * {@link Publisher} 控制数据的相关方法。</p>
 *
 * @author 季聿阶
 * @since 2024-02-07
 */
public interface Subscription {
    /**
     * 请求指定数量的数据。
     * <p>
     * <ul>
     *     <li>{@link Subscriber} 通过 {@link Publisher} 提供的本接口中的本方法请求指定数量的数据。</li>
     *     <li>在进行请求之前，{@link Publisher} 将不会发送任何数据给 {@link Subscriber}。</li>
     *     <li>{@link Publisher} 所发送数据数量将不会超过请求的数量，当 {@link Publisher}
     *     所拥有的元素数量小于请求数量时，将不会有额外数据发送。</li>
     *     <li>{@link Subscriber} 可以随时通过该接口请求数据，并且每次请求的数量将会得到累加，但最多不超过 {@link Long#MAX_VALUE}
     *     个（请求 {@link Long#MAX_VALUE} 个元素视为请求无限数量的元素）。</li>
     * </p>
     *
     * @param count 表示请求的数据的数量的 {@code long}。
     */
    void request(long count);

    /**
     * 取消当前的订阅关系。
     * <p>取消订阅关系后，{@link Publisher} 将不会再向 {@link Subscriber} 发送数据或信号，但由于 {@link Publisher}
     * 可能无法接受到取消操作，因此仍有可能存在数据或信号被发送。</p>
     */
    void cancel();

    /**
     * 判断当前订阅关系是否已经取消。
     *
     * @return 如果当前订阅关系已经取消，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isCancelled();
}
