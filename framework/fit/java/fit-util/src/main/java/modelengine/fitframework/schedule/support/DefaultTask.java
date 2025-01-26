/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.util.ObjectUtils;

import java.util.concurrent.Callable;

/**
 * 表示 {@link Task} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-16
 */
public class DefaultTask implements Task {
    private final Callable<?> callable;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private final ExecutePolicy policy;

    private DefaultTask(Callable<?> callable, Thread.UncaughtExceptionHandler uncaughtExceptionHandler,
            ExecutePolicy policy) {
        this.callable = Validation.notNull(callable, "The callable cannot be null.");
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.policy = ObjectUtils.getIfNull(policy, ExecutePolicy::disposable);
    }

    @Override
    public Object call() throws Exception {
        try {
            return this.callable.call();
        } catch (Exception e) {
            if (this.uncaughtExceptionHandler == null) {
                throw e;
            }
            this.uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
            return null;
        }
    }

    @Override
    public Thread.UncaughtExceptionHandler uncaughtExceptionHandler() {
        return this.uncaughtExceptionHandler;
    }

    @Override
    public ExecutePolicy policy() {
        return this.policy;
    }

    /**
     * 表示 {@link DisposableTask} 的默认实现，用于创建一个可以被丢弃的任务。
     */
    public static class DefaultDisposableTask extends DefaultTask implements DisposableTask {
        private DefaultDisposableTask(Callable<?> callable, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            super(callable, uncaughtExceptionHandler, ExecutePolicy.disposable());
        }
    }

    /**
     * 表示 {@link Task.Builder} 的默认实现。
     */
    public static class Builder implements Task.Builder {
        private Callable<?> callable;
        private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
        private ExecutePolicy policy;

        /**
         * 使用任务初始化 {@link Builder} 类的新实例。
         *
         * @param task 表示任务对象的 {@link Task}。
         */
        public Builder(Task task) {
            if (task != null) {
                this.callable = task;
                this.uncaughtExceptionHandler = task.uncaughtExceptionHandler();
                this.policy = task.policy();
            }
        }

        @Override
        public Task.Builder runnable(Runnable runnable) {
            if (runnable == null) {
                return this;
            }
            this.callable = () -> {
                runnable.run();
                return null;
            };
            return this;
        }

        @Override
        public Task.Builder callable(Callable<?> callable) {
            this.callable = callable;
            return this;
        }

        @Override
        public Task.Builder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
            this.uncaughtExceptionHandler = handler;
            return this;
        }

        @Override
        public Task.Builder policy(ExecutePolicy policy) {
            this.policy = policy;
            return this;
        }

        @Override
        public Task build() {
            return new DefaultTask(this.callable, this.uncaughtExceptionHandler, this.policy);
        }

        @Override
        public DisposableTask buildDisposable() {
            return new DefaultDisposableTask(this.callable, this.uncaughtExceptionHandler);
        }
    }
}
