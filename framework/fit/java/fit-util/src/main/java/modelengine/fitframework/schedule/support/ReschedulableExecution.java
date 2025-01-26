/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.schedule.ExecutePolicy;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示 {@link ExecutePolicy.Execution} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-16
 */
public class ReschedulableExecution implements ExecutePolicy.Execution {
    private volatile ExecutePolicy.ExecutionStatus status = ExecutePolicy.ExecutionStatus.SCHEDULING;
    private volatile Instant lastScheduledTime;
    private volatile Instant lastExecuteTime;
    private volatile Instant lastCompleteTime;

    /**
     * 修改调度时间。
     *
     * @param lastScheduledTime 表示调度时间的 {@link Instant}。
     * @throws IllegalArgumentException 当当前执行状态不为 {@link ExecutePolicy.ExecutionStatus#SCHEDULING} 或
     * {@link ExecutePolicy.ExecutionStatus#EXECUTED} 时。
     * @throws IllegalArgumentException 当 {@code lastScheduledTime} 为 {@code null} 时。
     */
    void updateScheduledTime(Instant lastScheduledTime) {
        Validation.isTrue(this.status == ExecutePolicy.ExecutionStatus.SCHEDULING
                        || this.status == ExecutePolicy.ExecutionStatus.EXECUTED,
                "Failed to update scheduled time: The execution status is incorrect. [status={0}]",
                this.status);
        this.lastScheduledTime = Validation.notNull(lastScheduledTime, "The last scheduled time cannot be null.");
        this.status = ExecutePolicy.ExecutionStatus.SCHEDULED;
    }

    /**
     * 修改开始执行时间。
     *
     * @param lastExecuteTime 表示开始执行时间的 {@link Instant}。
     * @throws IllegalArgumentException 当当前执行状态不为 {@link ExecutePolicy.ExecutionStatus#SCHEDULED} 时。
     * @throws IllegalArgumentException 当 {@code lastExecuteTime} 为 {@code null} 时。
     */
    void updateExecuteTime(Instant lastExecuteTime) {
        Validation.isTrue(this.status == ExecutePolicy.ExecutionStatus.SCHEDULED,
                "Failed to update execute time: The execution status is incorrect. [status={0}]",
                this.status);
        this.lastExecuteTime = Validation.notNull(lastExecuteTime, "The last execute time cannot be null.");
        this.status = ExecutePolicy.ExecutionStatus.EXECUTING;
    }

    /**
     * 修改执行完毕时间。
     *
     * @param lastCompleteTime 表示执行完毕时间的 {@link Instant}。
     * @throws IllegalArgumentException 当当前执行状态不为 {@link ExecutePolicy.ExecutionStatus#EXECUTING} 时。
     * @throws IllegalArgumentException 当 {@code lastCompleteTime} 为 {@code null} 时。
     */
    void updateCompleteTime(Instant lastCompleteTime) {
        Validation.isTrue(this.status == ExecutePolicy.ExecutionStatus.EXECUTING,
                "Failed to update complete time: The execution status is incorrect. [status={0}]",
                this.status);
        this.lastCompleteTime = Validation.notNull(lastCompleteTime, "The last complete time cannot be null.");
        this.status = ExecutePolicy.ExecutionStatus.EXECUTED;
    }

    @Override
    public ExecutePolicy.ExecutionStatus status() {
        return this.status;
    }

    @Override
    public Optional<Instant> lastScheduledTime() {
        return Optional.ofNullable(this.lastScheduledTime);
    }

    @Override
    public Optional<Instant> lastExecuteTime() {
        return Optional.ofNullable(this.lastExecuteTime);
    }

    @Override
    public Optional<Instant> lastCompleteTime() {
        return Optional.ofNullable(this.lastCompleteTime);
    }
}
