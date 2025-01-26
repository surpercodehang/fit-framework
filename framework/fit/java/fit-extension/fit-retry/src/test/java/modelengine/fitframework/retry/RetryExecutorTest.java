/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.retry.backoff.ExponentialRetryBackOff;
import modelengine.fitframework.retry.backoff.FixedRetryBackOff;
import modelengine.fitframework.retry.condition.ExceptionCondition;
import modelengine.fitframework.retry.condition.TimesLimitedRetryCondition;
import modelengine.fitframework.retry.condition.TimesUnlimitedRetryCondition;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * {@link RetryExecutor} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2022-12-30
 */
@DisplayName("测试 RetryExecutor 工具类")
class RetryExecutorTest {
    private final int ATTEMPT_TIMES = 2;
    private final int BACK_OFF_TIMES_MILLS = 1;
    private final RetryExecutor.Builder<Object> builder = RetryExecutor.builder();
    private final Condition retryCondition = new TimesLimitedRetryCondition(this.ATTEMPT_TIMES);
    private final RetryBackOff<Object> retryBackOff = new FixedRetryBackOff<>(this.BACK_OFF_TIMES_MILLS);
    private final String LOWER_ABC = "abc";
    private final String UPPER_ABC = "ABC";

    @Nested
    @DisplayName("当提供一个构建器")
    class RetryExecutorBuilder {
        @Test
        @DisplayName("业务逻辑正常执行，没有重试")
        void setUpServiceLogic() {
            Callable<String> callable = RetryExecutorTest.this.LOWER_ABC::toUpperCase;
            Object result = RetryExecutorTest.this.builder.callable(ObjectUtils.cast(callable))
                    .retryCondition(RetryExecutorTest.this.retryCondition)
                    .backOff(RetryExecutorTest.this.retryBackOff)
                    .build()
                    .execute();
            assertThat(result).isEqualTo(RetryExecutorTest.this.UPPER_ABC);
        }

        @Test
        @DisplayName("业务逻辑执行出错，设置恢复逻辑，执行恢复逻辑")
        void setUpDowngradeLogic() {
            // 该业务逻辑一定会抛出异常
            Callable<String> callable = () -> {
                String upperCase = RetryExecutorTest.this.LOWER_ABC.toUpperCase(Locale.ROOT);
                if (RetryExecutorTest.this.UPPER_ABC.equals(upperCase)) {
                    throw new IllegalArgumentException();
                }
                return upperCase;
            };
            Condition recoverCondition =
                    new ExceptionCondition(Collections.singletonList(IllegalArgumentException.class));
            RecoverCallable<String> downgrade = (exception) -> RetryExecutorTest.this.LOWER_ABC;
            Object result = RetryExecutorTest.this.builder.callable(ObjectUtils.cast(callable))
                    .recover(ObjectUtils.cast(downgrade))
                    .retryCondition(RetryExecutorTest.this.retryCondition)
                    .backOff(RetryExecutorTest.this.retryBackOff)
                    .recoverCondition(recoverCondition)
                    .build()
                    .execute();
            assertThat(result).isEqualTo(RetryExecutorTest.this.LOWER_ABC);
        }

        @Nested
        @DisplayName("设置不同尝试条件并执行")
        class DiffRetryCondition {
            @Test
            @DisplayName("不超过设置最大尝试次数，某一次尝试成功，则结束")
            void retryLessEqualLimitedTimes() {
                final int[] count = {0};
                Object result = RetryExecutorBuilder.this.commonExecute(RetryExecutorTest.this.retryCondition,
                        RetryExecutorTest.this.retryBackOff,
                        count,
                        RetryExecutorTest.this.ATTEMPT_TIMES);
                assertThat(result).isEqualTo(RetryExecutorTest.this.UPPER_ABC);
                assertThat(count[0]).isEqualTo(RetryExecutorTest.this.ATTEMPT_TIMES);
            }

            @Test
            @DisplayName("超过设置最大尝试次数，抛出条件不满足异常")
            void retryGreaterLimitedTimes() {
                final int[] count = {0};
                assertThatThrownBy(() -> RetryExecutorBuilder.this.commonExecute(RetryExecutorTest.this.retryCondition,
                        RetryExecutorTest.this.retryBackOff,
                        count,
                        RetryExecutorTest.this.ATTEMPT_TIMES + 1)).isInstanceOf(ConditionNotMatchException.class);
            }

            @Test
            @DisplayName("不限制尝试次数，某一次尝试成功，则结束")
            void retryUnLimitedTimes() {
                final int[] count = {0};
                Condition unlimitedRetryCondition = new TimesUnlimitedRetryCondition();
                Object result = RetryExecutorBuilder.this.commonExecute(unlimitedRetryCondition,
                        RetryExecutorTest.this.retryBackOff,
                        count,
                        RetryExecutorTest.this.ATTEMPT_TIMES);
                assertThat(result).isEqualTo(RetryExecutorTest.this.UPPER_ABC);
                assertThat(count[0]).isEqualTo(RetryExecutorTest.this.ATTEMPT_TIMES);
            }
        }

        @Nested
        @DisplayName("设置不同退避策略并执行")
        class DiffBackOff {
            @Test
            @DisplayName("固定时间的退避策略")
            void retryFixedBackOff() {
                final int[] count = {0};
                RetryBackOff<Object> backOff = new FixedRetryBackOff<>(RetryExecutorTest.this.BACK_OFF_TIMES_MILLS);
                Object result = RetryExecutorBuilder.this.commonExecute(RetryExecutorTest.this.retryCondition,
                        backOff,
                        count,
                        RetryExecutorTest.this.ATTEMPT_TIMES);
                assertThat(result).isEqualTo(RetryExecutorTest.this.UPPER_ABC);
                assertThat(count[0]).isEqualTo(RetryExecutorTest.this.ATTEMPT_TIMES);
            }

            @Test
            @DisplayName("指数级的退避策略")
            void retryExponentialBackOff() {
                final int[] count = {0};
                RetryBackOff<Object> backOff =
                        new ExponentialRetryBackOff<>(RetryExecutorTest.this.BACK_OFF_TIMES_MILLS,
                                RetryExecutorTest.this.BACK_OFF_TIMES_MILLS * 10,
                                2.0);
                Object result = RetryExecutorBuilder.this.commonExecute(RetryExecutorTest.this.retryCondition,
                        backOff,
                        count,
                        RetryExecutorTest.this.ATTEMPT_TIMES);
                assertThat(result).isEqualTo(RetryExecutorTest.this.UPPER_ABC);
                assertThat(count[0]).isEqualTo(RetryExecutorTest.this.ATTEMPT_TIMES);
            }
        }

        private Object commonExecute(Condition condition, RetryBackOff<Object> backOff, int[] count, int attemptTimes) {
            Callable<String> callable = () -> {
                String upperCase = RetryExecutorTest.this.LOWER_ABC.toUpperCase(Locale.ROOT);
                count[0]++;
                if (count[0] < attemptTimes) {
                    throw new IllegalArgumentException();
                }
                return upperCase;
            };
            return RetryExecutorTest.this.builder.callable(ObjectUtils.cast(callable))
                    .retryCondition(condition)
                    .backOff(backOff)
                    .build()
                    .execute();
        }
    }
}
