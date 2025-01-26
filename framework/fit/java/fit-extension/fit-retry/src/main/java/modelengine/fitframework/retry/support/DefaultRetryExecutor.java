/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.retry.Condition;
import modelengine.fitframework.retry.ConditionNotMatchException;
import modelengine.fitframework.retry.RecoverCallable;
import modelengine.fitframework.retry.RetryBackOff;
import modelengine.fitframework.retry.RetryException;
import modelengine.fitframework.retry.RetryExecutor;
import modelengine.fitframework.util.ThreadUtils;

import java.util.concurrent.Callable;

/**
 * 表示 {@link RetryExecutor} 的默认实现。
 *
 * @param <T> 表示重试执行器的返回类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-11-17
 */
public class DefaultRetryExecutor<T> implements RetryExecutor<T> {
    private final Callable<T> callable;
    private final RecoverCallable<T> recover;
    private final Condition retryCondition;
    private final RetryBackOff<T> backOff;
    private final Condition recoverCondition;

    private DefaultRetryExecutor(Callable<T> callable, RecoverCallable<T> recover, Condition retryCondition,
            RetryBackOff<T> backOff, Condition recoverCondition) {
        this.callable = notNull(callable, "The callable in retry executor cannot be null.");
        this.recover = recover;
        this.retryCondition = notNull(retryCondition, "The condition in retry executor cannot be null.");
        this.backOff = notNull(backOff, "The back off in retry executor cannot be null.");
        this.recoverCondition = recoverCondition;
    }

    @Override
    public T execute() {
        int attemptTimes = 0;
        long executionStartTimeMillis = 0L;
        long executionMillis;
        boolean shouldRetry;
        do {
            try {
                attemptTimes++;
                executionStartTimeMillis = System.currentTimeMillis();
                return this.callable.call();
            } catch (RetryException e) {
                throw e;
            } catch (Throwable cause) {
                executionMillis = System.currentTimeMillis() - executionStartTimeMillis;
                shouldRetry = this.retryCondition.matches(attemptTimes, executionMillis, cause);
                if (shouldRetry) {
                    long sleepMillis = this.backOff.sleepMillis(attemptTimes, cause);
                    this.sleep(sleepMillis);
                    continue;
                }
                if (this.isRecover(attemptTimes, executionMillis, cause)) {
                    return this.recover(attemptTimes, cause);
                }
                throw new ConditionNotMatchException(attemptTimes, cause);
            }
        } while (true);
    }

    private boolean isRecover(int attemptTimes, long executionMillis, Throwable cause) {
        if (this.recoverCondition == null || this.recover == null) {
            return false;
        }
        return this.recoverCondition.matches(attemptTimes, executionMillis, cause);
    }

    private T recover(int attemptTimes, Throwable cause) {
        try {
            return this.recover.call(cause);
        } catch (Exception e) {
            throw new RetryException(attemptTimes, e);
        }
    }

    private void sleep(long sleepMillis) {
        if (sleepMillis >= 0) {
            ThreadUtils.sleep(sleepMillis);
        }
    }

    /**
     * 表示 {@link modelengine.fitframework.retry.RetryExecutor.Builder} 的默认实现。
     *
     * @param <T> 表示重试执行的返回值类型的 {@link T}。
     */
    public static class Builder<T> implements RetryExecutor.Builder<T> {
        private Callable<T> callable;
        private RecoverCallable<T> recover;
        private Condition retryCondition;
        private RetryBackOff<T> backOff;
        private Condition recoverCondition;

        @Override
        public RetryExecutor.Builder<T> callable(Callable<T> callable) {
            this.callable = callable;
            return this;
        }

        @Override
        public RetryExecutor.Builder<T> recover(RecoverCallable<T> recover) {
            this.recover = recover;
            return this;
        }

        @Override
        public RetryExecutor.Builder<T> retryCondition(Condition condition) {
            this.retryCondition = condition;
            return this;
        }

        @Override
        public RetryExecutor.Builder<T> backOff(RetryBackOff<T> backOff) {
            this.backOff = backOff;
            return this;
        }

        @Override
        public RetryExecutor.Builder<T> recoverCondition(Condition condition) {
            this.recoverCondition = condition;
            return this;
        }

        @Override
        public RetryExecutor<T> build() {
            return new DefaultRetryExecutor<>(this.callable,
                    this.recover,
                    this.retryCondition,
                    this.backOff,
                    this.recoverCondition);
        }
    }
}
