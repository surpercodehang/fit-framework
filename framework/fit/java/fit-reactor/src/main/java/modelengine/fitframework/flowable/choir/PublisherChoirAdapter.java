/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.flowable.choir;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.inspection.Nonnull;

/**
 * 表示 {@link modelengine.fitframework.flowable.Choir} 的指定 {@link Publisher} 的适配。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-09
 */
public class PublisherChoirAdapter<T> extends AbstractChoir<T> {
    private final Publisher<T> publisher;

    /**
     * 使用指定的发布者初始化 {@link PublisherChoirAdapter} 的新实例。
     *
     * @param publisher 表示发布者的 {@link Publisher}{@code <}{@link T}{@code >}。
     * @throws IllegalArgumentException 当 {@code publisher} 为 {@code null} 时。
     */
    public PublisherChoirAdapter(Publisher<T> publisher) {
        this.publisher = notNull(publisher, "The publisher cannot be null.");
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        this.publisher.subscribe(subscriber);
    }
}
