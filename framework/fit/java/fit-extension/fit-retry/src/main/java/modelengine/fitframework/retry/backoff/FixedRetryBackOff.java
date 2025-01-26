/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.backoff;

import modelengine.fitframework.retry.RetryBackOff;

/**
 * 表示固定时间的退避策略。
 *
 * @param <T> 表示重试执行器的返回类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-11-20
 */
public class FixedRetryBackOff<T> implements RetryBackOff<T> {
    private final long backOffTimeMillis;

    public FixedRetryBackOff(long backOffTimeMillis) {
        this.backOffTimeMillis = Math.max(backOffTimeMillis, 0);
    }

    @Override
    public long sleepMillis(int retryTimes, Throwable cause) {
        return this.backOffTimeMillis;
    }
}
