/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.backoff;

import modelengine.fitframework.retry.RetryBackOff;

/**
 * 表示指数级的退避策略。
 *
 * @param <T> 表示重试执行器的返回类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-11-20
 */
public class ExponentialRetryBackOff<T> implements RetryBackOff<T> {
    private final long initialInterval;
    private final long maxInterval;
    private final double multiplier;

    public ExponentialRetryBackOff(long initialInterval, long maxInterval, double multiplier) {
        this.initialInterval = Math.max(initialInterval, 0);
        this.maxInterval = Math.max(maxInterval, initialInterval);
        this.multiplier = Math.max(multiplier, 1.0);
    }

    @Override
    public long sleepMillis(int retryTimes, Throwable cause) {
        if (retryTimes <= 1) {
            return this.initialInterval;
        }
        return Math.min((long) (this.initialInterval * Math.pow(this.multiplier, retryTimes - 1)), this.maxInterval);
    }
}
