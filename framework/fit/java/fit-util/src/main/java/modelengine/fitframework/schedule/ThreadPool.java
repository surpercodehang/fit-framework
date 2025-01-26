/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

/**
 * 表示线程池。
 *
 * @author 季聿阶
 * @since 2022-12-26
 */
public interface ThreadPool {
    /**
     * 获取线程池的名字。
     *
     * @return 表示线程池名字的 {@link String}。
     */
    String name();

    /**
     * 关闭当前的线程池。
     *
     * @return 当线程池关闭正常，返回 {@code true}，当关闭超时，返回 {@code false}。
     * @throws InterruptedException 当关闭过程遇到打断事件时。
     */
    boolean shutdown() throws InterruptedException;

    /**
     * 表示线程池的构建器。
     *
     * @param <B> 表示真实的构建器类型的 {@link B}。
     */
    interface Builder<B extends Builder<B>> {
        /**
         * 向当前构建器中设置核心线程池大小。
         *
         * @param corePoolSize 表示待设置的核心线程池大小的 {@code int}。
         * @return 表示当前构建器的 {@link B}。
         */
        B corePoolSize(int corePoolSize);

        /**
         * 向当前构建器中设置最大线程池大小。
         *
         * @param maximumPoolSize 表示待设置的最大线程池大小的 {@code int}。
         * @return 表示当前构建器的 {@link B}。
         */
        B maximumPoolSize(int maximumPoolSize);

        /**
         * 向当前构建器中设置核心线程保活的最长时间。
         *
         * @param keepAliveTime 表示待设置的核心线程保活的最长时间的 {@code long}。
         * @param unit 表示待设置的时间单位的 {@link TimeUnit}。
         * @return 表示当前构建器的 {@link B}。
         */
        B keepAliveTime(long keepAliveTime, TimeUnit unit);

        /**
         * 向当前构建器中设置等待队列的大小。
         *
         * @param workQueueCapacity 表示待设置的等待队列的大小的 {@code int}。
         * @return 表示当前构建器的 {@link B}。
         */
        B workQueueCapacity(int workQueueCapacity);

        /**
         * 向当前构建器中设置线程池的名字。
         * <p>线程池的名字会被用于创建的每一个线程的名字的前缀。</p>
         *
         * @param threadPoolName 表示待设置的线程池名字的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        B threadPoolName(String threadPoolName);

        /**
         * 向当前构建器中设置线程是否为守护线程的标志。
         *
         * @param isDaemonThread 表示待设置的线程是否为守护线程标志的 {@code boolean}。
         * @return 表示当前构建器的 {@link B}。
         */
        B isDaemonThread(boolean isDaemonThread);

        /**
         * 向当前构建器中设置线程池满之后的拒绝处理器。
         *
         * @param rejectedExecutionHandler 表示待设置的线程池满之后的拒绝处理器的 {@link RejectedExecutionHandler}。
         * @return 表示当前构建器的 {@link B}。
         */
        B rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler);

        /**
         * 向当前构建器中设置线程池关闭时是否立即关闭的标志。
         *
         * @param isImmediateShutdown 表示待设置的线程池关闭时是否立即关闭的标志的 {@code boolean}。
         * @return 表示当前构建器的 {@link B}。
         */
        B isImmediateShutdown(boolean isImmediateShutdown);

        /**
         * 向当前构建器中设置线程池关闭时等待线程结束的最长时间。
         *
         * @param awaitTermination 表示待设置的线程池关闭时等待线程结束的最长时间的 {@code long}。
         * @param unit 表示待设置的时间单位的 {@link TimeUnit}。
         * @return 表示当前构建器的 {@link B}。
         */
        B awaitTermination(long awaitTermination, TimeUnit unit);
    }
}
