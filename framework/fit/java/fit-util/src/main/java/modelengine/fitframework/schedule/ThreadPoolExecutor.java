/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule;

import modelengine.fitframework.schedule.Task.DisposableTask;
import modelengine.fitframework.schedule.support.DefaultThreadPoolExecutor;

import java.util.concurrent.Future;

/**
 * 表示线程池化的执行器。
 *
 * @author 季聿阶
 * @since 2022-11-15
 */
public interface ThreadPoolExecutor extends ThreadPool {
    /**
     * 执行一个一次性任务。
     *
     * @param task 表示一次性任务的 {@link DisposableTask}。
     * @throws IllegalArgumentException 当 {@code task} 为 {@code null} 时。
     */
    void execute(DisposableTask task);

    /**
     * 提交一个一次性任务。
     *
     * @param task 表示一次性任务的 {@link DisposableTask}。
     * @return 表示执行结果的 {@link Future}{@code <?>}。
     * @throws IllegalArgumentException 当 {@code task} 为 {@code null} 时。
     */
    Future<?> submit(DisposableTask task);

    /**
     * 表示线程池化的执行器的构建器。
     */
    interface Builder extends ThreadPool.Builder<Builder> {
        /**
         * 向当前构建器中设置统一的线程异常处理器。
         *
         * @param exceptionHandler 表示待设置的统一的线程异常处理器的 {@link Thread.UncaughtExceptionHandler}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder exceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler);

        /**
         * 构建一个线程池化的执行器。
         *
         * @return 表示构建出来的线程池化的执行器的 {@link ThreadPoolExecutor}。
         */
        ThreadPoolExecutor build();
    }

    /**
     * 创建一个自定义的线程池化的执行器的构建器。
     *
     * @return 表示创建出来的自定义的线程池化的执行器的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultThreadPoolExecutor.Builder();
    }
}
