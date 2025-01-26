/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.schedule.ExecutePolicy;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示固定延迟时间的执行策略。
 *
 * @author 季聿阶
 * @since 2022-11-15
 */
public class FixedDelayExecutePolicy extends AbstractExecutePolicy {
    private final long delayMillis;

    public FixedDelayExecutePolicy(long delayMillis) {
        this.delayMillis =
                Validation.greaterThan(delayMillis, 0,
                        "The delay millis must be positive. [delayMillis={0}]", delayMillis);
    }

    @Override
    public Optional<Instant> nextExecuteTime(@Nonnull ExecutePolicy.Execution execution, @Nonnull Instant startTime) {
        this.validateExecutionStatus(execution.status());
        if (execution.status() == ExecutePolicy.ExecutionStatus.SCHEDULING) {
            return Optional.of(startTime);
        } else {
            Optional<Instant> lastCompleteTime = execution.lastCompleteTime();
            Validation.isTrue(lastCompleteTime.isPresent(), "The last complete time must be present.");
            Validation.isFalse(lastCompleteTime.get().isBefore(startTime),
                    "The last complete time cannot before the start time. [lastCompleteTime={0}, startTime={1}]",
                    lastCompleteTime.get(),
                    startTime);
            return Optional.of(lastCompleteTime.get().plusMillis(this.delayMillis));
        }
    }
}
