/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.condition;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.retry.Condition;

/**
 * 表示任务执行的组合条件。
 *
 * @author 邬涨财
 * @since 2023-02-25
 */
public class ConditionComposite implements Condition {
    private final Condition condition1;
    private final Condition condition2;

    private ConditionComposite(Condition condition1, Condition condition2) {
        this.condition1 = Validation.notNull(condition1, "Condition1 can not be null.");
        this.condition2 = Validation.notNull(condition2, "Condition2 can not be null.");
    }

    /**
     * 将两个任务执行的条件合并成一个条件。
     *
     * @param condition1 表示待合并的第一个执行条件的 {@link Condition}。
     * @param condition2 表示待合并的第二个执行条件的 {@link Condition}。
     * @return 表示合并后的任务执行的条件的 {@link Condition}。
     */
    public static Condition combine(Condition condition1, Condition condition2) {
        if (condition1 == null) {
            return condition2;
        } else if (condition2 == null) {
            return condition1;
        } else {
            return new ConditionComposite(condition1, condition2);
        }
    }

    @Override
    public boolean matches(int attemptTimes, long executionTimeMillis, Throwable cause) {
        return this.condition1.matches(attemptTimes, executionTimeMillis, cause)
                && this.condition2.matches(attemptTimes, executionTimeMillis, cause);
    }
}