/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.schedule.ThreadPool;
import modelengine.fitframework.util.ObjectUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 表示 {@link ThreadPool} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2022-12-26
 */
public abstract class AbstractThreadPool implements ThreadPool {
    private final String name;
    private final ExecutorService threadPool;
    private final boolean isImmediateShutdown;
    private final long awaitTermination;
    private final TimeUnit awaitTerminationUnit;

    protected AbstractThreadPool(String name, ExecutorService threadPool, boolean isImmediateShutdown,
            long awaitTermination, TimeUnit awaitTerminationUnit) {
        this.name = notBlank(name, "The name cannot be blank.");
        this.threadPool = notNull(threadPool, "The thread pool cannot be null.");
        this.isImmediateShutdown = isImmediateShutdown;
        this.awaitTermination = awaitTermination;
        this.awaitTerminationUnit = ObjectUtils.nullIf(awaitTerminationUnit, TimeUnit.MILLISECONDS);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean shutdown() throws InterruptedException {
        if (this.isImmediateShutdown) {
            this.threadPool.shutdownNow()
                    .stream()
                    .filter(runnable -> runnable instanceof Future)
                    .map(Future.class::cast)
                    .forEach(future -> future.cancel(true));
        } else {
            this.threadPool.shutdown();
        }
        if (this.awaitTermination > 0) {
            return this.threadPool.awaitTermination(this.awaitTermination, this.awaitTerminationUnit);
        } else {
            return true;
        }
    }

    /**
     * 表示 {@link ThreadPool.Builder} 的默认实现。
     */
    public static class Builder<B extends ThreadPool.Builder<B>> implements ThreadPool.Builder<B> {
        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;
        private TimeUnit keepAliveTimeUnit;
        private int workQueueCapacity;
        private String threadPoolName;
        private boolean isDaemonThread;
        private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        private boolean isImmediateShutdown;
        private long awaitTermination;
        private TimeUnit awaitTerminationUnit;

        @Override
        public B corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this.self();
        }

        @Override
        public B maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this.self();
        }

        @Override
        public B keepAliveTime(long keepAliveTime, TimeUnit unit) {
            this.keepAliveTime = keepAliveTime;
            this.keepAliveTimeUnit = unit;
            return this.self();
        }

        @Override
        public B workQueueCapacity(int workQueueCapacity) {
            this.workQueueCapacity = workQueueCapacity;
            return this.self();
        }

        @Override
        public B threadPoolName(String threadPoolName) {
            this.threadPoolName = threadPoolName;
            return this.self();
        }

        @Override
        public B isDaemonThread(boolean isDaemonThread) {
            this.isDaemonThread = isDaemonThread;
            return this.self();
        }

        @Override
        public B rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this.self();
        }

        @Override
        public B isImmediateShutdown(boolean isImmediateShutdown) {
            this.isImmediateShutdown = isImmediateShutdown;
            return this.self();
        }

        @Override
        public B awaitTermination(long awaitTermination, TimeUnit unit) {
            this.awaitTermination = awaitTermination;
            this.awaitTerminationUnit = unit;
            return this.self();
        }

        private B self() {
            return ObjectUtils.cast(this);
        }

        /**
         * 获取核心线程池大小。
         *
         * @return 表示核心线程池大小的 {@code int}。
         */
        protected int getCorePoolSize() {
            return this.corePoolSize;
        }

        /**
         * 获取最大线程池大小。
         *
         * @return 表示最大线程池大小的 {@code int}。
         */
        protected int getMaximumPoolSize() {
            return this.maximumPoolSize;
        }

        /**
         * 获取核心线程保活的最长时间。
         *
         * @return 表示核心线程保活的最长时间的 {@code long}。
         */
        protected long getKeepAliveTime() {
            return this.keepAliveTime;
        }

        /**
         * 获取核心线程保活的最长时间的单位。
         *
         * @return 表示核心线程保活的最长时间的单位的 {@link TimeUnit}。
         */
        protected TimeUnit getKeepAliveTimeUnit() {
            return this.keepAliveTimeUnit;
        }

        /**
         * 获取等待队列的大小。
         *
         * @return 表示等待队列的大小的 {@code int}。
         */
        protected int getWorkQueueCapacity() {
            return this.workQueueCapacity;
        }

        /**
         * 获取线程池的名字。
         *
         * @return 表示线程池名字的 {@link String}。
         */
        protected String getThreadPoolName() {
            return this.threadPoolName;
        }

        /**
         * 获取线程是否为守护线程的标志。
         *
         * @return 表示线程是否为守护线程的标志的 {@code boolean}。
         */
        protected boolean isDaemonThread() {
            return this.isDaemonThread;
        }

        /**
         * 获取线程池满之后的拒绝处理器。
         *
         * @return 表示线程池满之后的拒绝处理器的 {@link RejectedExecutionHandler}。
         */
        protected RejectedExecutionHandler getRejectedExecutionHandler() {
            return this.rejectedExecutionHandler;
        }

        /**
         * 获取线程池关闭时是否立即关闭的标志。
         *
         * @return 表示线程池关闭时是否立即关闭的标志的 {@code boolean}。
         */
        protected boolean isImmediateShutdown() {
            return this.isImmediateShutdown;
        }

        /**
         * 获取线程池关闭时等待线程结束的最长时间。
         *
         * @return 表示线程池关闭时等待线程结束的最长时间的 {@code long}。
         */
        protected long getAwaitTermination() {
            return this.awaitTermination;
        }

        /**
         * 获取线程池关闭时等待线程结束的最长时间的单位。
         *
         * @return 表示线程池关闭时等待线程结束的最长时间的单位的 {@link TimeUnit}。
         */
        protected TimeUnit getAwaitTerminationUnit() {
            return this.awaitTerminationUnit;
        }

        /**
         * 创建工作的阻塞队列。
         *
         * @return 表示创建后的阻塞队列的 {@link BlockingQueue}{@code <}{@link Runnable}{@code >}。
         */
        protected BlockingQueue<Runnable> createWorkQueue() {
            if (this.workQueueCapacity <= 0) {
                return new SynchronousQueue<>();
            } else {
                return new LinkedBlockingQueue<>(this.workQueueCapacity);
            }
        }
    }
}
