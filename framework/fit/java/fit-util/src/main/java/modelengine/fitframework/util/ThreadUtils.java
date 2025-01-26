/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Validation;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * 为线程相关操作提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class ThreadUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ThreadUtils() {}

    /**
     * 等待执行线程结束。
     * <p>若当前线程被中断，则会通过 {@link Thread#interrupt()} 方法设置线程的中断标记。</p>
     *
     * @param thread 表示待等待的线程的 {@link Thread}。
     */
    public static void join(Thread thread) {
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 将当前线程休眠指定的毫秒数。
     * <p>休眠失败时，将通过 {@link Thread#interrupt()} 方法设置线程的中断标记。</p>
     *
     * @param milliseconds 表示待休眠的毫秒数的 {@code long}。
     */
    public static void sleep(long milliseconds) {
        Validation.greaterThanOrEquals(milliseconds, 0, "The sleep time cannot be negative.");
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 创建一个单线程的线程池。
     *
     * @param threadFactory 表示创建线程的线程工厂的 {@link ThreadFactory}。
     * @return 表示创建的单线程的线程池的 {@link ThreadPoolExecutor}。
     * @throws IllegalArgumentException 当 {@code threadFactory} 为 {@code null} 时。
     */
    public static ThreadPoolExecutor singleThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(1,
                1,
                0,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Validation.notNull(threadFactory, "The thread factory cannot be null."));
    }

    /**
     * 等待直到指定判定条件返回真值。
     *
     * @param test 表示待等待的判定条件的 {@link BooleanSupplier}。
     */
    public static void waitUntil(BooleanSupplier test) {
        while (!test.getAsBoolean()) {
            sleep(0L);
        }
    }
}
