/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolExecutor;
import modelengine.fitframework.util.ThreadUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表示 {@link DefaultThreadPoolScheduler} 的单元测试。
 *
 * @author 杭潇
 * @since 2022-12-28
 */
@DisplayName("测试 DefaultThreadPoolExecutor 类")
public class DefaultThreadPoolExecutorTest {
    private ThreadPoolExecutor threadPoolExecutor;
    private AtomicReference<String> message;
    private Task.DisposableTask disposableTask;

    private final String expected = "final";

    @BeforeEach
    void setup() {
        this.threadPoolExecutor = ThreadPoolExecutor.custom()
                .threadPoolName("threadPool-1")
                .awaitTermination(500L, TimeUnit.MILLISECONDS)
                .isImmediateShutdown(false)
                .corePoolSize(5)
                .maximumPoolSize(10)
                .keepAliveTime(1, TimeUnit.SECONDS)
                .workQueueCapacity(10)
                .isDaemonThread(false)
                .exceptionHandler((thread, throwable) -> {})
                .rejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy())
                .build();
        this.message = new AtomicReference<>("original");
        Callable<String> callable = () -> {
            this.message.set(this.expected);
            return this.message.get();
        };
        this.disposableTask = Task.builder().callable(callable).buildDisposable();
    }

    @Nested
    @DisplayName("线程池开启的情况下")
    class WhenOpenedThreadPool {
        private ThreadPoolExecutor executor() {
            return DefaultThreadPoolExecutorTest.this.threadPoolExecutor;
        }

        private String expected() {
            return DefaultThreadPoolExecutorTest.this.expected;
        }

        @DisplayName("调用执行方法，任务执行成功")
        @Test
        void givenAtomicParameterWhenInvokeExecuteThenUpdateSuccessfully() {
            this.executor().execute(DefaultThreadPoolExecutorTest.this.disposableTask);
            while (!Objects.equals(DefaultThreadPoolExecutorTest.this.message.get(),
                    DefaultThreadPoolExecutorTest.this.expected)) {
                ThreadUtils.sleep(0);
            }
            assertThat(DefaultThreadPoolExecutorTest.this.message.get()).isEqualTo(this.expected());
        }

        @Test
        @DisplayName("调用执行方法，执行 Runnable 任务成功")
        void givenAtomicParameterWhenExecuteWithRunnableThenUpdateSuccessfully() {
            Runnable runnable = () -> DefaultThreadPoolExecutorTest.this.message.set(this.expected());
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (thread, throwable) -> {};
            Task.DisposableTask runnableTask = Task.builder()
                    .runnable(runnable)
                    .uncaughtExceptionHandler(uncaughtExceptionHandler)
                    .policy(ExecutePolicy.fixedDelay(10L))
                    .buildDisposable();
            this.executor().execute(runnableTask);
            while (!Objects.equals(DefaultThreadPoolExecutorTest.this.message.get(), this.expected())) {
                ThreadUtils.sleep(0);
            }
            assertThat(DefaultThreadPoolExecutorTest.this.message.get()).isEqualTo(this.expected());
        }

        @DisplayName("调用提交方法，任务执行成功")
        @Test
        void givenAtomicParameterWhenInvokeSubmitThenUpdateSuccessfully()
                throws ExecutionException, InterruptedException {
            Future<?> submit = this.executor().submit(DefaultThreadPoolExecutorTest.this.disposableTask);
            assertThat(submit.get()).isEqualTo(this.expected());
        }
    }

    @Nested
    @DisplayName("线程池关闭的情况下")
    class WhenShutdownThreadPool {
        private ThreadPoolExecutor executor() {
            return DefaultThreadPoolExecutorTest.this.threadPoolExecutor;
        }

        private String expected() {
            return DefaultThreadPoolExecutorTest.this.expected;
        }

        @BeforeEach
        void shutdownThreadPool() throws InterruptedException {
            DefaultThreadPoolExecutorTest.this.threadPoolExecutor.shutdown();
        }

        @DisplayName("调用执行方法，任务执行失败并抛出异常")
        @Test
        void executeExecuteMethodThenFailedAndThrowsException() {
            RejectedExecutionException rejectedExecutionException = catchThrowableOfType(() -> this.executor()
                    .execute(DefaultThreadPoolExecutorTest.this.disposableTask), RejectedExecutionException.class);
            assertThat(rejectedExecutionException).isNotNull();
            assertThat(DefaultThreadPoolExecutorTest.this.message.get()).isNotEqualTo(this.expected());
        }

        @DisplayName("调用提交方法，任务执行失败并抛出异常")
        @Test
        void executeSubmitMethodThenFailedAndThrowsException() {
            RejectedExecutionException rejectedExecutionException = catchThrowableOfType(() -> this.executor()
                    .submit(DefaultThreadPoolExecutorTest.this.disposableTask), RejectedExecutionException.class);
            assertThat(rejectedExecutionException).isNotNull();
            assertThat(DefaultThreadPoolExecutorTest.this.message.get()).isNotEqualTo(this.expected());
        }
    }
}
