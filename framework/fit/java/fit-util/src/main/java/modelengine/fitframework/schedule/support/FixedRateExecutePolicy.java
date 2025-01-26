/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static modelengine.fitframework.inspection.Validation.greaterThan;
import static modelengine.fitframework.inspection.Validation.isFalse;
import static modelengine.fitframework.inspection.Validation.isTrue;

import modelengine.fitframework.inspection.Nonnull;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示固定频率的执行策略。
 *
 * @author 季聿阶
 * @since 2022-11-15
 */
public class FixedRateExecutePolicy extends AbstractExecutePolicy {
    private final long periodMillis;

    public FixedRateExecutePolicy(long periodMillis) {
        this.periodMillis =
                greaterThan(periodMillis, 0, "The period millis must be positive. [period={0}]", periodMillis);
    }

    @Override
    public Optional<Instant> nextExecuteTime(@Nonnull Execution execution, @Nonnull Instant startTime) {
        this.validateExecutionStatus(execution.status());
        if (execution.status() == ExecutionStatus.SCHEDULING) {
            return Optional.of(startTime);
        } else {
            Optional<Instant> lastExecuteTime = execution.lastExecuteTime();
            isTrue(lastExecuteTime.isPresent(), "The last execute time must be present.");
            isFalse(lastExecuteTime.get().isBefore(startTime),
                    "The last execute time cannot before the start time. [lastExecuteTime={0}, startTime={1}]",
                    lastExecuteTime.get(),
                    startTime);
            return Optional.of(lastExecuteTime.get().plusMillis(this.periodMillis));
        }
    }
}
