/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule;

import modelengine.fitframework.schedule.support.DefaultThreadPoolScheduler;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

/**
 * 表示线程池化的调度器。
 *
 * @author 季聿阶
 * @since 2022-11-15
 */
public interface ThreadPoolScheduler extends ThreadPool {
    /**
     * 调度执行指定的任务。
     *
     * @param task 表示指定任务的 {@link Task}。
     * @return 表示调度结果的 {@link ScheduledFuture}{@code <?>}。
     * @throws IllegalArgumentException 当 {@code task} 为 {@code null} 时。
     */
    default ScheduledFuture<?> schedule(Task task) {
        return this.schedule(task, Instant.now());
    }

    /**
     * 调度执行指定的任务。
     *
     * @param task 表示指定任务的 {@link Task}。
     * @param delayMillis 表示第一次可以执行任务的起始时间距当前时刻的间隔的 {@code long}，单位为毫秒。
     * @return 表示调度结果的 {@link ScheduledFuture}{@code <?>}。
     * @throws IllegalArgumentException 当 {@code task} 为 {@code null} 时。
     */
    default ScheduledFuture<?> schedule(Task task, long delayMillis) {
        long actualDelayMillis = delayMillis < 0 ? 0 : delayMillis;
        Instant actualStartTime = Instant.now().plusMillis(actualDelayMillis);
        return this.schedule(task, actualStartTime);
    }

    /**
     * 调度执行指定的任务。
     *
     * @param task 表示指定任务的 {@link Task}。
     * @param startTime 表示第一次可以执行任务的起始时间的 {@link Instant}。
     * @return 表示调度结果的 {@link ScheduledFuture}{@code <?>}。
     * @throws IllegalArgumentException 当 {@code task} 为 {@code null} 时。
     */
    ScheduledFuture<?> schedule(Task task, Instant startTime);

    /**
     * 表示线程池化的调度器的构建器。
     */
    interface Builder extends ThreadPool.Builder<Builder> {
        /**
         * 构建一个线程池化的调度器。
         *
         * @return 表示构建出来的线程池化的调度器的 {@link ThreadPoolScheduler}。
         */
        ThreadPoolScheduler build();
    }

    /**
     * 创建一个自定义的线程池化的调度器的构建器。
     *
     * @return 表示创建出来的自定义的线程池化的调度器的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultThreadPoolScheduler.Builder();
    }
}
