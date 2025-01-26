/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.condition;

import modelengine.fitframework.retry.Condition;

/**
 * 表示限制重试次数的重试条件。
 *
 * @author 季聿阶
 * @since 2022-11-20
 */
public class TimesLimitedRetryCondition implements Condition {
    private final int maxAttemptTimes;

    public TimesLimitedRetryCondition(int maxAttemptTimes) {
        this.maxAttemptTimes = Math.max(maxAttemptTimes, 1);
    }

    @Override
    public boolean matches(int attemptTimes, long executionTimeMillis, Throwable cause) {
        return attemptTimes < this.maxAttemptTimes;
    }
}
