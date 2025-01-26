/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule;

import modelengine.fitframework.util.LazyLoader;

import java.util.concurrent.TimeUnit;

/**
 * 表示 {@link ThreadPoolExecutor} 各种实现的集合。
 *
 * @author 何天放
 * @since 2024-11-09
 */
public class ThreadPoolExecutors {
    private static final int DEFAULT_PARALLEL_SCHEDULER_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final long DEFAULT_KEEPALIVE_SECONDS = 60L;
    private static final String SINGLE_NAME = "single";
    private static final String PARALLEL_NAME = "parallel";
    private static final LazyLoader<ThreadPoolExecutor> SINGLE_LOADER =
            new LazyLoader<>(() -> newSingle(SINGLE_NAME, true));
    private static final LazyLoader<ThreadPoolExecutor> PARALLEL_LOADER =
            new LazyLoader<>(() -> newParallel(PARALLEL_NAME, DEFAULT_PARALLEL_SCHEDULER_POOL_SIZE, true));

    /**
     * 提供具有单一线程的执行器。
     *
     * @return 表示单一线程执行器的 {@link ThreadPoolExecutor}。
     */
    public static ThreadPoolExecutor single() {
        return SINGLE_LOADER.get();
    }

    /**
     * 提供具有多个线程的执行器。
     * <p>具有多个线程的执行器中的线程数量为当前的可用核心数。</p>
     *
     * @return 表示多个线程执行器的 {@link ThreadPoolExecutor}。
     */
    public static ThreadPoolExecutor parallel() {
        return PARALLEL_LOADER.get();
    }

    /**
     * 创建具有单一线程的执行器。
     *
     * @param threadPoolName 表示线程池名称的 {@link String}。
     * @return 表示所创建单一线程执行器的 {@link ThreadPoolExecutor}。
     */
    public static ThreadPoolExecutor newSingle(String threadPoolName) {
        return newSingle(threadPoolName, false);
    }

    /**
     * 创建具有单一线程的执行器。
     *
     * @param threadPoolName 表示线程池名称的 {@link String}。
     * @param daemon 表示线程池中线程是否为守护线程的 {@code boolean}。
     * @return 表示所创建单一线程执行器的 {@link ThreadPoolExecutor}。
     */
    public static ThreadPoolExecutor newSingle(String threadPoolName, boolean daemon) {
        return ThreadPoolExecutor.custom()
                .threadPoolName(threadPoolName)
                .isDaemonThread(daemon)
                .corePoolSize(1)
                .maximumPoolSize(1)
                .keepAliveTime(DEFAULT_KEEPALIVE_SECONDS, TimeUnit.MILLISECONDS)
                .workQueueCapacity(Integer.MAX_VALUE)
                .build();
    }

    /**
     * 创建具有多个线程的执行器。
     * <p>具有多个线程的执行器中的线程数量为当前的可用核心数。</p>
     *
     * @param threadPoolName 表示线程池名称的 {@link String}。
     * @return 表示所创建的多个线程执行器的 {@link ThreadPoolExecutor}。
     */
    public static ThreadPoolExecutor newParallel(String threadPoolName) {
        return newParallel(threadPoolName, DEFAULT_PARALLEL_SCHEDULER_POOL_SIZE);
    }

    /**
     * 创建具有多个线程的执行器。
     *
     * @param threadPoolName 表示线程池名称的 {@link String}。
     * @param parallelism 表示线程数量的 {@code int}。
     * @return 表示所创建的多个线程执行器的 {@link ThreadPoolExecutor}。
     */
    public static ThreadPoolExecutor newParallel(String threadPoolName, int parallelism) {
        return newParallel(threadPoolName, parallelism, false);
    }

    /**
     * 创建具有多个线程的执行器。
     *
     * @param threadPoolName 表示线程池名称的 {@link String}。
     * @param parallelism 表示线程数量的 {@code int}。
     * @param daemon 表示线程池中线程是否为守护线程的 {@code boolean}。
     * @return 表示所创建的多个线程执行器的 {@link ThreadPoolExecutor}。
     */
    public static ThreadPoolExecutor newParallel(String threadPoolName, int parallelism, boolean daemon) {
        return ThreadPoolExecutor.custom()
                .threadPoolName(threadPoolName)
                .isDaemonThread(daemon)
                .corePoolSize(parallelism)
                .maximumPoolSize(parallelism)
                .keepAliveTime(DEFAULT_KEEPALIVE_SECONDS, TimeUnit.MILLISECONDS)
                .workQueueCapacity(Integer.MAX_VALUE)
                .build();
    }
}
