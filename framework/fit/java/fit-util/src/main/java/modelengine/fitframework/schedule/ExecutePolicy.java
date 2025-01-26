/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.schedule.cron.CronExecutePolicy;
import modelengine.fitframework.schedule.support.DisposableExecutePolicy;
import modelengine.fitframework.schedule.support.FixedDelayExecutePolicy;
import modelengine.fitframework.schedule.support.FixedRateExecutePolicy;
import modelengine.fitframework.util.StringUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.TimeZone;

/**
 * 表示执行策略。
 *
 * @author 季聿阶
 * @since 2022-11-16
 */
public interface ExecutePolicy {
    /**
     * 根据当前执行情况，计算下一次执行的时间。
     *
     * @param execution 表示当前执行情况的 {@link Execution}。
     * @param startTime 表示任务可以执行的起始时间的 {@link Instant}。
     * @return 表示下一次执行时间的 {@link Optional}{@code <}{@link Instant}{@code >}，如果没有下一次执行，则返回
     * {@link Optional#empty()}。
     */
    Optional<Instant> nextExecuteTime(@Nonnull Execution execution, @Nonnull Instant startTime);

    /**
     * 创建一个一次性的执行策略。
     *
     * @return 表示一次性的执行策略的 {@link ExecutePolicy}。
     */
    static ExecutePolicy disposable() {
        return DisposableExecutePolicy.INSTANCE;
    }

    /**
     * 根据指定的执行周期创建一个固定频率的执行策略。
     *
     * @param periodMillis 表示执行周期的 {@code long}。
     * @return 表示固定频率的执行策略的 {@link ExecutePolicy}。
     */
    static ExecutePolicy fixedRate(long periodMillis) {
        return new FixedRateExecutePolicy(periodMillis);
    }

    /**
     * 根据指定的执行延迟时间创建一个固定延迟时间的执行策略。
     *
     * @param delayMillis 表示执行延迟时间的 {@code long}。
     * @return 表示固定延迟时间的执行策略的 {@link ExecutePolicy}。
     */
    static ExecutePolicy fixedDelay(long delayMillis) {
        return new FixedDelayExecutePolicy(delayMillis);
    }

    /**
     * 根据指定的 CRON 表达式创建一个 CRON 执行策略。
     *
     * @param expression 表示 CRON 表达式的 {@link String}。
     * @return 表示 CRON 执行策略的 {@link ExecutePolicy}。
     */
    static ExecutePolicy cron(String expression) {
        return cron(expression, null);
    }

    /**
     * 根据指定的 CRON 表达式和时区信息创建一个 CRON 执行策略。
     *
     * @param expression 表示 CRON 表达式的 {@link String}。
     * @param zone 表示时区信息的 {@link String}。当 {@code zone} 为 {@code null} 或空白字符串时，表示系统默认时区。
     * @return 表示 CRON 执行策略的 {@link ExecutePolicy}。
     */
    static ExecutePolicy cron(String expression, String zone) {
        if (StringUtils.isBlank(zone)) {
            return new CronExecutePolicy(expression);
        } else {
            return new CronExecutePolicy(expression, TimeZone.getTimeZone(zone));
        }
    }

    /**
     * 表示执行情况。
     */
    interface Execution {
        /**
         * 获取执行情况的状态。
         *
         * @return 表示执行情况的状态的 {@link ExecutionStatus}。
         */
        ExecutionStatus status();

        /**
         * 获取上一次调度的时间。
         *
         * @return 表示上一次调度时间的 {@link Optional}{@code <}{@link Instant}{@code >}。
         */
        Optional<Instant> lastScheduledTime();

        /**
         * 获取上一次开始执行的时间。
         *
         * @return 表示上一次开始执行时间的 {@link Optional}{@code <}{@link Instant}{@code >}。
         */
        Optional<Instant> lastExecuteTime();

        /**
         * 获取上一次执行完成的时间。
         *
         * @return 表示上一次执行完成时间的 {@link Optional}{@code <}{@link Instant}{@code >}。
         */
        Optional<Instant> lastCompleteTime();
    }

    /**
     * 表示执行状态。
     */
    enum ExecutionStatus {
        /** 表示正在调度，只有第一次执行的时候会进入这个状态，该状态表明任何时间都没有被设置。 */
        SCHEDULING,

        /** 表示调度完毕，该状态表明上一次调度时间已经设置完毕。 */
        SCHEDULED,

        /** 表示正在执行，该状态表明开始执行时间已经设置完毕。 */
        EXECUTING,

        /** 表示执行完毕，该状态表明执行完成时间已经设置完毕。 */
        EXECUTED
    }
}
