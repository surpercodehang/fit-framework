/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.schedule.ExecutePolicy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示 {@link FixedRateExecutePolicy} 的单元测试。
 *
 * @author 杭潇
 * @since 2022-12-05
 */
@DisplayName("测试 FixedRateExecutePolicy 类")
class FixedRateExecutePolicyTest {
    @DisplayName("状态值为 SCHEDULING 时，下次执行时间方法返回时间值与开始执行时间值相同")
    @Test
    void givenStatusIsSchedulingThenReturnValueIsTheSameAsStartTime() {
        long period = 500L;
        Instant startTime = Instant.now();

        ExecutePolicy executePolicy = ExecutePolicy.fixedRate(period);
        ExecutePolicy.Execution execution = mock(ExecutePolicy.Execution.class);
        when(execution.status()).thenReturn(ExecutePolicy.ExecutionStatus.SCHEDULING);

        Optional<Instant> instant = executePolicy.nextExecuteTime(execution, startTime);
        assertThat(instant.isPresent()).isTrue();
        assertThat(instant.get()).isEqualTo(startTime);
    }

    @DisplayName("状态值为 EXECUTED 时，下次执行时间方法返回时间值与上次任务开始时间值有关")
    @Test
    void givenStatusIsExecuteThenReturnValueIsDependOnLastExecuteTime() {
        long period = 500L;
        Instant executeTime = Instant.now();
        ExecutePolicy.Execution execution = mock(ExecutePolicy.Execution.class);
        when(execution.status()).thenReturn(ExecutePolicy.ExecutionStatus.EXECUTED);
        when(execution.lastExecuteTime()).thenReturn(Optional.of(executeTime));

        ExecutePolicy executePolicy = ExecutePolicy.fixedRate(period);
        Optional<Instant> optionalInstant = executePolicy.nextExecuteTime(execution, executeTime.minusSeconds(1));

        assertThat(optionalInstant.isPresent()).isTrue();
        assertThat(optionalInstant.get().getEpochSecond()).isEqualTo(Instant.ofEpochMilli(
                executeTime.toEpochMilli() + period).getEpochSecond());
    }
}
