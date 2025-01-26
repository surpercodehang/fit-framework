/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static java.lang.Thread.UncaughtExceptionHandler;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.schedule.ExecuteException;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolExecutor;
import modelengine.fitframework.thread.DefaultThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 表示 {@link ThreadPoolExecutor} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-12-26
 */
public class DefaultThreadPoolExecutor extends AbstractThreadPool implements ThreadPoolExecutor {
    private final ExecutorService threadPool;

    private DefaultThreadPoolExecutor(String threadPoolName, ExecutorService threadPool, boolean isImmediateShutdown,
            long awaitTermination, TimeUnit awaitTerminationUnit) {
        super(threadPoolName, threadPool, isImmediateShutdown, awaitTermination, awaitTerminationUnit);
        this.threadPool = Validation.notNull(threadPool, "The thread pool cannot be null.");
    }

    @Override
    public void execute(Task.DisposableTask task) {
        this.submit(task);
    }

    @Override
    public Future<?> submit(Task.DisposableTask task) {
        Validation.notNull(task, "The disposable task cannot be null.");
        return this.threadPool.submit(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                if (task.uncaughtExceptionHandler() != null) {
                    task.uncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    return null;
                } else {
                    throw new ExecuteException(e);
                }
            }
        });
    }

    /**
     * 表示 {@link ThreadPoolExecutor.Builder} 的默认实现。
     */
    public static class Builder extends AbstractThreadPool.Builder<ThreadPoolExecutor.Builder>
            implements ThreadPoolExecutor.Builder {
        private UncaughtExceptionHandler exceptionHandler;

        @Override
        public Builder exceptionHandler(UncaughtExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * 构建一个线程池执行器服务。
         *
         * @return 表示构建出来的线程池执行器服务的 {@link ExecutorService}。
         * @throws IllegalArgumentException 当不满足创建 {@link ExecutorService} 的基础条件时。
         */
        private ExecutorService buildThreadPoolExecutor() {
            Validation.greaterThanOrEquals(this.getCorePoolSize(),
                    0,
                    "The core pool size cannot be negative. [corePoolSize={0}]",
                    this.getCorePoolSize());
            Validation.greaterThan(this.getMaximumPoolSize(),
                    0,
                    "The maximum pool size must be positive. [maximumPoolSize={0}]",
                    this.getMaximumPoolSize());
            Validation.greaterThanOrEquals(this.getMaximumPoolSize(),
                    this.getCorePoolSize(),
                    "The maximum pool size cannot be less than the core pool size. "
                            + "[corePoolSize={0}, maximumPoolSize={1}]",
                    this.getCorePoolSize(),
                    this.getMaximumPoolSize());
            Validation.greaterThanOrEquals(this.getKeepAliveTime(),
                    0,
                    "The keep alive time cannot be negative. [keepAliveTime={0}]",
                    this.getKeepAliveTime());
            Validation.notNull(this.getKeepAliveTimeUnit(), "The keep alive time unit cannot be null.");
            return new java.util.concurrent.ThreadPoolExecutor(this.getCorePoolSize(),
                    this.getMaximumPoolSize(),
                    this.getKeepAliveTime(),
                    this.getKeepAliveTimeUnit(),
                    this.createWorkQueue(),
                    new DefaultThreadFactory(this.getThreadPoolName(), this.isDaemonThread(), this.exceptionHandler),
                    this.getRejectedExecutionHandler());
        }

        @Override
        public ThreadPoolExecutor build() {
            return new DefaultThreadPoolExecutor(this.getThreadPoolName(),
                    this.buildThreadPoolExecutor(),
                    this.isImmediateShutdown(),
                    this.getAwaitTermination(),
                    this.getAwaitTerminationUnit());
        }
    }
}
