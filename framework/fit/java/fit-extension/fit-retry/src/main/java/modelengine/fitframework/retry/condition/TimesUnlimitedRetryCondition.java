/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.condition;

import modelengine.fitframework.retry.Condition;

/**
 * 表示无限次重试的重试条件。
 *
 * @author 季聿阶
 * @since 2022-11-20
 */
public class TimesUnlimitedRetryCondition implements Condition {
    @Override
    public boolean matches(int attemptTimes, long executionTimeMillis, Throwable cause) {
        return true;
    }
}
