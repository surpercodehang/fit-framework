/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示 {@link ReschedulableExecution} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-01
 */
@DisplayName("测试 ReschedulableExecution 类")
public class ReschedulableExecutionTest {
    private final Instant now = Instant.now();
    private final ReschedulableExecution reschedulableExecution = new ReschedulableExecution();

    @Nested
    @DisplayName("给定并更新时间值")
    class GivenAndUpdateTime {
        private ReschedulableExecution getExecution() {
            return ReschedulableExecutionTest.this.reschedulableExecution;
        }

        @Test
        @DisplayName("返回上次任务调度时间值与给定值相等")
        void theLastScheduledTimeShouldBeEqualsToTheGivenTime() {
            this.getExecution().updateScheduledTime(ReschedulableExecutionTest.this.now);

            Optional<Instant> lastScheduledTime = this.getExecution().lastScheduledTime();
            assertThat(lastScheduledTime).isPresent();
            assertThat(lastScheduledTime.get()).isEqualTo(ReschedulableExecutionTest.this.now);
        }

        @Test
        @DisplayName("返回上次任务执行时间值与给定值相等")
        void theLastExecuteTimeShouldBeEqualsToTheGivenTime() {
            this.getExecution().updateScheduledTime(ReschedulableExecutionTest.this.now);
            this.getExecution().updateExecuteTime(ReschedulableExecutionTest.this.now.plusMillis(10L));

            Optional<Instant> lastExecuteTime = this.getExecution().lastExecuteTime();
            assertThat(lastExecuteTime).isPresent();
            assertThat(lastExecuteTime.get()).isEqualTo(ReschedulableExecutionTest.this.now.plusMillis(10L));
        }

        @Test
        @DisplayName("返回上次任务完成时间值与给定值相等")
        void theLastCompleteTimeShouldBeEqualsToTheGivenTime() {
            this.getExecution().updateScheduledTime(ReschedulableExecutionTest.this.now);
            this.getExecution().updateExecuteTime(ReschedulableExecutionTest.this.now);
            this.getExecution().updateCompleteTime(ReschedulableExecutionTest.this.now.plusMillis(100L));

            Optional<Instant> lastCompleteTime = this.getExecution().lastCompleteTime();
            assertThat(lastCompleteTime).isPresent();
            assertThat(lastCompleteTime.get()).isEqualTo(ReschedulableExecutionTest.this.now.plusMillis(100L));
        }
    }

    @Nested
    @DisplayName("状态值为 SCHEDULING")
    class StatusIsScheduling {
        private ReschedulableExecution getExecution() {
            return ReschedulableExecutionTest.this.reschedulableExecution;
        }

        @Test
        @DisplayName("更新任务执行时间，抛出异常")
        void updateExecuteTimeThrowException() {
            IllegalArgumentException illegalArgumentException = catchThrowableOfType(() -> this.getExecution()
                    .updateExecuteTime(ReschedulableExecutionTest.this.now), IllegalArgumentException.class);
            assertThat(illegalArgumentException).hasMessage(StringUtils.format(
                    "Failed to update execute time: The execution status is incorrect. [status={0}]",
                    ExecutePolicy.ExecutionStatus.SCHEDULING));
        }

        @Test
        @DisplayName("更新任务执行完成时间，抛出异常")
        void updateCompleteTimeThrowException() {
            IllegalArgumentException illegalArgumentException = catchThrowableOfType(() -> this.getExecution()
                    .updateCompleteTime(ReschedulableExecutionTest.this.now), IllegalArgumentException.class);
            assertThat(illegalArgumentException).hasMessage(StringUtils.format(
                    "Failed to update complete time: The execution status is incorrect. [status={0}]",
                    ExecutePolicy.ExecutionStatus.SCHEDULING));
        }
    }

    @Nested
    @DisplayName("状态值为 EXECUTING")
    class StatusIsExecuting {
        private ReschedulableExecution getExecution() {
            return ReschedulableExecutionTest.this.reschedulableExecution;
        }

        @BeforeEach
        void setup() {
            this.getExecution().updateScheduledTime(ReschedulableExecutionTest.this.now);
            this.getExecution().updateExecuteTime(ReschedulableExecutionTest.this.now);
        }

        @Test
        @DisplayName("更新任务调度时间，抛出异常")
        void updateScheduledTimeThrowException() {
            IllegalArgumentException illegalArgumentException = catchThrowableOfType(() -> this.getExecution()
                    .updateScheduledTime(ReschedulableExecutionTest.this.now), IllegalArgumentException.class);
            assertThat(illegalArgumentException).hasMessage(StringUtils.format(
                    "Failed to update scheduled time: The execution status is incorrect. [status={0}]",
                    ExecutePolicy.ExecutionStatus.EXECUTING));
        }
    }
}
