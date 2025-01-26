/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolScheduler;
import modelengine.fitframework.thread.DefaultThreadFactory;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 表示 {@link ThreadPoolScheduler} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-12-26
 */
public class DefaultThreadPoolScheduler extends AbstractThreadPool implements ThreadPoolScheduler {
    private final ScheduledExecutorService threadPool;

    private DefaultThreadPoolScheduler(String threadPoolName, ScheduledExecutorService threadPool,
            boolean isImmediateShutdown, long awaitTermination, TimeUnit awaitTerminationUnit) {
        super(threadPoolName, threadPool, isImmediateShutdown, awaitTermination, awaitTerminationUnit);
        this.threadPool = notNull(threadPool, "The thread pool cannot be null.");
    }

    @Override
    public ScheduledFuture<?> schedule(Task task, Instant startTime) {
        return new ReschedulableTask(this.threadPool, task, startTime).schedule();
    }

    /**
     * 表示 {@link ThreadPoolScheduler.Builder} 的默认实现。
     */
    public static class Builder extends AbstractThreadPool.Builder<ThreadPoolScheduler.Builder>
            implements ThreadPoolScheduler.Builder {
        /**
         * 构建一个线程池调度器服务。
         *
         * @return 表示构建出来的线程池调度器服务的 {@link ScheduledExecutorService}。
         * @throws IllegalArgumentException 当不满足创建 {@link ScheduledExecutorService} 的基础条件时。
         */
        private ScheduledExecutorService buildScheduledExecutorService() {
            greaterThanOrEquals(this.getCorePoolSize(),
                    0,
                    "The core pool size cannot be negative. [corePoolSize={0}]",
                    this.getCorePoolSize());
            return new ScheduledThreadPoolExecutor(this.getCorePoolSize(),
                    new DefaultThreadFactory(this.getThreadPoolName(), this.isDaemonThread(), null),
                    this.getRejectedExecutionHandler());
        }

        @Override
        public ThreadPoolScheduler build() {
            return new DefaultThreadPoolScheduler(this.getThreadPoolName(),
                    this.buildScheduledExecutorService(),
                    this.isImmediateShutdown(),
                    this.getAwaitTermination(),
                    this.getAwaitTerminationUnit());
        }
    }
}
