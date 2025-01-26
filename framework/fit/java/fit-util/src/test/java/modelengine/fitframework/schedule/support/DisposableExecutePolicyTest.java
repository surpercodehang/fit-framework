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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示 {@link DisposableExecutePolicy} 的单元测试。
 *
 * @author 杭潇
 * @since 2022-12-28
 */
@DisplayName("测试 DisposableExecutePolicy 类")
public class DisposableExecutePolicyTest {
    @Nested
    @DisplayName("调用下次执行时间方法")
    class InvokeTheNextExecuteTimeMethod {
        @DisplayName("返回时间值与开始执行时间值相同")
        @Test
        void theNextExecuteTimeShouldBeEqualsToTheStartTime() {
            Instant startTime = Instant.now();
            ExecutePolicy.Execution execution = mock(ExecutePolicy.Execution.class);
            when(execution.status()).thenReturn(ExecutePolicy.ExecutionStatus.SCHEDULING);

            Optional<Instant> instant = DisposableExecutePolicy.INSTANCE.nextExecuteTime(execution, startTime);
            assertThat(instant.isPresent()).isTrue();
            assertThat(instant.get()).isEqualTo(startTime);
        }

        @DisplayName("再次调用，返回时间值为空")
        @Test
        void rescheduleThenReturnValueIsEmpty() {
            ExecutePolicy.Execution execution = mock(ExecutePolicy.Execution.class);
            when(execution.status()).thenReturn(ExecutePolicy.ExecutionStatus.EXECUTED);

            Optional<Instant> instant =
                    DisposableExecutePolicy.INSTANCE.nextExecuteTime(execution, Instant.now());
            assertThat(instant.isPresent()).isFalse();
        }
    }
}
