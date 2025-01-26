/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.thread;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link ThreadFactory} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-07-25
 */
public class DefaultThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber;
    private final String namePrefix;
    private final boolean isDaemon;
    private final UncaughtExceptionHandler exceptionHandler;

    /**
     * 通过线程名前缀、是否为守护线程和线程发生异常的处理器来实例化 {@link DefaultThreadFactory}。
     *
     * @param namePrefix 表示线程名前缀的 {@link String}。
     * @param isDaemon 表示是否为守护线程的 {@code boolean}。
     * @param exceptionHandler 表示线程发生异常的处理器的 {@link UncaughtExceptionHandler}。
     * @throws IllegalArgumentException 当 {@code namePrefix} 为 {@code null} 或空白字符串时。
     */
    public DefaultThreadFactory(String namePrefix, boolean isDaemon, UncaughtExceptionHandler exceptionHandler) {
        this.threadNumber = new AtomicInteger(0);
        this.isDaemon = isDaemon;
        this.namePrefix = Validation.notBlank(namePrefix, "The thread name prefix cannot be blank.");
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        Thread thread = new Thread(runnable, this.namePrefix + "-thread-" + this.threadNumber.getAndIncrement());
        thread.setDaemon(this.isDaemon);
        thread.setUncaughtExceptionHandler(this.exceptionHandler);
        return thread;
    }
}

