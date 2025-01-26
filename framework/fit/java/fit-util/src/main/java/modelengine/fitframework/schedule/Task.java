/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule;

import modelengine.fitframework.schedule.support.DefaultTask;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;

/**
 * 表示调度的任务。
 *
 * @author 季聿阶
 * @since 2022-11-16
 */
public interface Task extends Callable<Object> {
    /**
     * 获取执行任务过程中对于未捕获异常的处理器。
     *
     * @return 表示执行任务过程中未捕获异常的处理器的 {@link UncaughtExceptionHandler}。
     */
    UncaughtExceptionHandler uncaughtExceptionHandler();

    /**
     * 获取任务的执行策略。
     *
     * @return 表示任务执行策略的 {@link ExecutePolicy}。
     */
    ExecutePolicy policy();

    /**
     * 表示一次性任务。
     */
    interface DisposableTask extends Task {}

    /**
     * {@link Task} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置一个执行过程。
         *
         * @param runnable 表示执行过程的 {@link Runnable}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder runnable(Runnable runnable);

        /**
         * 向当前构建器中设置一个带返回值的执行过程。
         *
         * @param callable 表示带返回值的执行过程的 {@link Callable}{@code <?>}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder callable(Callable<?> callable);

        /**
         * 向当前构建器中设置未捕获异常的处理器。
         *
         * @param handler 表示未捕获异常的处理器的 {@link UncaughtExceptionHandler}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder uncaughtExceptionHandler(UncaughtExceptionHandler handler);

        /**
         * 向当前构建器中设置执行策略。
         *
         * @param policy 表示执行策略的 {@link ExecutePolicy}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder policy(ExecutePolicy policy);

        /**
         * 构建任务。
         *
         * @return 表示构建出来的任务的 {@link Task}。
         */
        Task build();

        /**
         * 构建一次性的任务。
         *
         * @return 表示构建出来的一次性任务的 {@link DisposableTask}。
         */
        DisposableTask buildDisposable();
    }

    /**
     * 获取 {@link Task} 的构建器。
     *
     * @return 表示 {@link Task} 的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null);
    }

    /**
     * 获取 {@link Task} 的构建器，同时将指定对象的值进行填充。
     *
     * @param task 表示指定对象的 {@link Task}。
     * @return 表示 {@link Task} 的构建器的 {@link Builder}。
     */
    static Builder builder(Task task) {
        return new DefaultTask.Builder(task);
    }
}
