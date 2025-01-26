/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry;

import modelengine.fitframework.retry.support.DefaultRetryExecutor;

import java.util.concurrent.Callable;

/**
 * 表示重试执行器。
 *
 * @param <T> 表示重试执行器的返回类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-11-17
 */
public interface RetryExecutor<T> {
    /**
     * 执行业务逻辑。
     * <p>当执行结束后，满足重试条件，则执行重试。</p>
     *
     * @return 表示业务返回值的 {@link T}。
     */
    T execute();

    /**
     * 获取 {@link RetryExecutor} 的构建器。
     *
     * @param <T> 表示重试执行器的返回类型的 {@link T}。
     * @return 表示 {@link RetryExecutor} 的构建器的 {@link Builder}。
     */
    static <T> Builder<T> builder() {
        return new DefaultRetryExecutor.Builder<>();
    }

    /**
     * 表示重试执行器的构建器。
     *
     * @param <T> 表示重试执行器的返回类型的 {@link T}。
     */
    interface Builder<T> {
        /**
         * 向当前构建器中设置业务执行逻辑。
         *
         * @param callable 表示业务执行逻辑的 {@link Callable}{@code <}{@link T}{@code >}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link T}{@code >}。
         */
        Builder<T> callable(Callable<T> callable);

        /**
         * 向当前构建器中设置业务恢复逻辑。
         *
         * @param recover 表示业务恢复逻辑的 {@link RecoverCallable}{@code <}{@link T}{@code >}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link T}{@code >}。
         */
        Builder<T> recover(RecoverCallable<T> recover);

        /**
         * 向当前构建器中设置执行重试的条件。
         *
         * @param condition 表示执行重试条件的 {@link Condition}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link T}{@code >}。
         */
        Builder<T> retryCondition(Condition condition);

        /**
         * 向当前构建器中设置执行重试的退避策略。
         *
         * @param backOff 表示执行重试的退避策略的 {@link RetryBackOff}{@code <}{@link T}{@code >}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link T}{@code >}。
         */
        Builder<T> backOff(RetryBackOff<T> backOff);

        /**
         * 向当前构建器中设置执行恢复的条件。
         *
         * @param condition 表示执行恢复条件的 {@link Condition}。
         * @return 表示当前构建器的 {@link Builder}{@code <}{@link T}{@code >}。
         */
        Builder<T> recoverCondition(Condition condition);

        /**
         * 构建一个重试执行器实例。
         *
         * @return 表示构建出来的重试执行器实例的 {@link RetryExecutor}{@code <}{@link T}{@code >}。
         * @throws IllegalArgumentException 当没有设置 {@code callable}、{@code condition} 或 {@code backOff} 时。
         */
        RetryExecutor<T> build();
    }
}
