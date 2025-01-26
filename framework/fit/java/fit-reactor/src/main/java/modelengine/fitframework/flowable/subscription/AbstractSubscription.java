/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.subscription;

import static modelengine.fitframework.inspection.Validation.greaterThan;

import modelengine.fitframework.flowable.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 表示 {@link Subscription} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2024-02-11
 */
public abstract class AbstractSubscription implements Subscription {
    private final AtomicBoolean cancelled = new AtomicBoolean();

    @Override
    public void request(long count) {
        greaterThan(count, 0, "The number of elements to request must be positive. [count={0}]", count);
        if (this.cancelled.get()) {
            return;
        }
        this.request0(count);
    }

    /**
     * 请求指定数量的数据。
     * <p>当前请求没有被取消。</p>
     *
     * @param count 表示请求的数据的数量的 {@code long}。
     */
    protected abstract void request0(long count);

    @Override
    public void cancel() {
        if (!this.cancelled.compareAndSet(false, true)) {
            return;
        }
        this.cancel0();
    }

    /**
     * 取消当前的订阅关系。
     * <p>默认为空。</p>
     */
    protected void cancel0() {}

    @Override
    public boolean isCancelled() {
        return this.cancelled.get();
    }
}
