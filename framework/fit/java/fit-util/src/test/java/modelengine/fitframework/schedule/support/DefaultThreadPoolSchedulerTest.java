/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolScheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表示 {@link DefaultThreadPoolScheduler} 的单元测试。
 *
 * @author 杭潇
 * @since 2022-12-29
 */
@DisplayName("测试 DefaultThreadPoolScheduler 类")
public class DefaultThreadPoolSchedulerTest {
    private ThreadPoolScheduler threadPoolScheduler;
    private Task task;
    private AtomicReference<String> message;

    private final String expected = "final";

    @BeforeEach
    void setup() {
        this.threadPoolScheduler = ThreadPoolScheduler.custom()
                .threadPoolName("threadPool-1")
                .awaitTermination(500L, TimeUnit.MILLISECONDS)
                .isImmediateShutdown(true)
                .corePoolSize(5)
                .maximumPoolSize(10)
                .keepAliveTime(1, TimeUnit.SECONDS)
                .workQueueCapacity(10)
                .isDaemonThread(false)
                .rejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy())
                .build();
        this.message = new AtomicReference<>("original");
        Callable<String> callable = () -> {
            this.message.set(this.expected);
            return this.message.get();
        };
        this.task = Task.builder().callable(callable).build();
    }

    @Nested
    @DisplayName("线程池开启的状态下")
    class WhenOpenedThreadPool {
        private ThreadPoolScheduler scheduler() {
            return DefaultThreadPoolSchedulerTest.this.threadPoolScheduler;
        }

        @Test
        @DisplayName("调用无延时调度方法，任务执行成功")
        void invokeScheduleWithoutDelayThenUpdateSuccessfully() throws ExecutionException, InterruptedException {
            ScheduledFuture<?> schedule = this.scheduler().schedule(DefaultThreadPoolSchedulerTest.this.task);
            assertThat(schedule.get()).isEqualTo(DefaultThreadPoolSchedulerTest.this.expected);
        }

        @Nested
        @DisplayName("调用有延时的调度方法")
        class InvokeScheduleWithDelay {
            @Test
            @DisplayName("延时是正数，任务执行成功")
            void givenPositiveDelayThenUpdateSuccessfully() throws ExecutionException, InterruptedException {
                ScheduledFuture<?> schedule = DefaultThreadPoolSchedulerTest.this.threadPoolScheduler.schedule(
                        DefaultThreadPoolSchedulerTest.this.task,
                        100L);
                assertThat(schedule.get()).isEqualTo(DefaultThreadPoolSchedulerTest.this.expected);
            }

            @Test
            @DisplayName("延时是负数，任务执行成功")
            void givenNegativeDelayThenUpdateSuccessfully() throws ExecutionException, InterruptedException {
                ScheduledFuture<?> schedule = DefaultThreadPoolSchedulerTest.this.threadPoolScheduler.schedule(
                        DefaultThreadPoolSchedulerTest.this.task,
                        -100L);
                assertThat(schedule.get()).isEqualTo(DefaultThreadPoolSchedulerTest.this.expected);
            }
        }
    }

    @Nested
    @DisplayName("线程池关闭的的状态下")
    class WhenShutdownThreadPool {
        private String expected() {
            return DefaultThreadPoolSchedulerTest.this.expected;
        }

        @BeforeEach
        void shutdownThreadPool() throws InterruptedException {
            DefaultThreadPoolSchedulerTest.this.threadPoolScheduler.shutdown();
        }

        @Test
        @DisplayName("调用调度方法，任务执行失败并抛出异常")
        void executeScheduleMethodThenFailedAndThrowsException() {
            RejectedExecutionException rejectedExecutionException =
                    catchThrowableOfType(() -> DefaultThreadPoolSchedulerTest.this.threadPoolScheduler.schedule(
                            DefaultThreadPoolSchedulerTest.this.task), RejectedExecutionException.class);
            assertThat(rejectedExecutionException).isNotNull();
            assertThat(DefaultThreadPoolSchedulerTest.this.message.get()).isNotEqualTo(this.expected());
        }
    }
}
