/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.thread.DefaultThreadFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 表示 {@link ReschedulableTask} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-01
 */
@DisplayName("测试 ReschedulableTask 类")
public class ReschedulableTaskTest {
    private ReschedulableTask reschedulableTask;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (thread, throwable) -> {};
    private final ExecutePolicy executePolicy = ExecutePolicy.fixedDelay(10L);
    private ScheduledExecutorService scheduledThreadPoolExecutor;
    private final Instant now = Instant.now();
    private Task task;

    @BeforeEach
    void setup() {
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5,
                new DefaultThreadFactory("scheduledThreadPoolExecutor-1", false, this.uncaughtExceptionHandler),
                new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
        Callable<String> callable = () -> "finishCallable";
        this.task = Task.builder()
                .callable(callable)
                .policy(this.executePolicy)
                .uncaughtExceptionHandler(this.uncaughtExceptionHandler)
                .build();
        this.reschedulableTask = new ReschedulableTask(this.scheduledThreadPoolExecutor, this.task, this.now);
    }

    @Test
    @DisplayName("获取执行任务过程中未捕获异常的处理器与给定值相等")
    void theUncaughtExceptionHandlerShouldBeEqualsToTheGivenValue() {
        Thread.UncaughtExceptionHandler handler = this.reschedulableTask.uncaughtExceptionHandler();
        assertThat(handler).isEqualTo(this.uncaughtExceptionHandler);
    }

    @Test
    @DisplayName("获取任务的执行策略值与给定值相等")
    void theExecutePolicyShouldBeEqualsToTheGivenPolicy() {
        ExecutePolicy policy = this.reschedulableTask.policy();
        assertThat(policy).isEqualTo(this.executePolicy);
    }

    @Nested
    @DisplayName("测试获取延迟方法")
    class TestGetDelay {
        @Test
        @DisplayName("调用方法，获取的延迟值等于 0")
        void theReturnIsEqualsToZero() {
            long delay = ReschedulableTaskTest.this.reschedulableTask.getDelay(TimeUnit.MILLISECONDS);
            assertThat(delay).isEqualTo(0L);
        }

        @Nested
        @DisplayName("更新下次调度时间值")
        class UpdateCurrentScheduledFuture {
            @BeforeEach
            void setup() throws ExecutionException, InterruptedException {
                ScheduledFuture<?> schedule = ReschedulableTaskTest.this.reschedulableTask.schedule();
                schedule.get();
            }

            @Test
            @DisplayName("调用方法，获取的延迟值大于 0")
            void theReturnIsGreaterThanZero() {
                long delay = ReschedulableTaskTest.this.reschedulableTask.getDelay(TimeUnit.MILLISECONDS);
                assertThat(delay).isGreaterThan(0L);
            }
        }
    }

    @Nested
    @DisplayName("测试比较方法")
    class TestCompareTo {
        @Test
        @DisplayName("给定任务延迟大于当前任务延迟，返回 -1")
        void givenTaskDelayGreaterThanThisThenReturnNegativeNumber() throws ExecutionException, InterruptedException {
            ReschedulableTask testTaskDelay =
                    new ReschedulableTask(ReschedulableTaskTest.this.scheduledThreadPoolExecutor,
                            ReschedulableTaskTest.this.task,
                            ReschedulableTaskTest.this.now);
            ReschedulableTask givenTaskDelay =
                    new ReschedulableTask(ReschedulableTaskTest.this.scheduledThreadPoolExecutor,
                            ReschedulableTaskTest.this.task,
                            ReschedulableTaskTest.this.now);
            testTaskDelay.schedule().get();
            int compareTo = givenTaskDelay.compareTo(testTaskDelay);
            assertThat(compareTo).isEqualTo(-1);
        }

        @Test
        @DisplayName("给定任务延迟小于当前任务延迟，返回 1")
        void givenTaskDelayLessThanThisThenReturnPositiveNumber() throws ExecutionException, InterruptedException {
            ReschedulableTask testTaskDelay =
                    new ReschedulableTask(ReschedulableTaskTest.this.scheduledThreadPoolExecutor,
                            ReschedulableTaskTest.this.task,
                            ReschedulableTaskTest.this.now);
            ReschedulableTask givenTaskDelay =
                    new ReschedulableTask(ReschedulableTaskTest.this.scheduledThreadPoolExecutor,
                            ReschedulableTaskTest.this.task,
                            ReschedulableTaskTest.this.now);
            givenTaskDelay.schedule().get();
            int compareTo = givenTaskDelay.compareTo(testTaskDelay);
            assertThat(compareTo).isEqualTo(1);
        }

        @Test
        @DisplayName("给定任务延迟等于当前任务延迟，返回 0")
        void givenTaskDelayEqualsToThisThenReturnZero() {
            ReschedulableTask testTaskDelay =
                    new ReschedulableTask(ReschedulableTaskTest.this.scheduledThreadPoolExecutor,
                            ReschedulableTaskTest.this.task,
                            ReschedulableTaskTest.this.now);
            ReschedulableTask givenTaskDelay =
                    new ReschedulableTask(ReschedulableTaskTest.this.scheduledThreadPoolExecutor,
                            ReschedulableTaskTest.this.task,
                            ReschedulableTaskTest.this.now);
            int compareTo = givenTaskDelay.compareTo(testTaskDelay);
            assertThat(compareTo).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("更新当前计划下次执行的时间值")
    class UpdateCurrentScheduledFuture {
        @BeforeEach
        void setup() throws ExecutionException, InterruptedException {
            ScheduledFuture<?> schedule = ReschedulableTaskTest.this.reschedulableTask.schedule();
            schedule.get();
        }

        @Nested
        @DisplayName("测试 cancel 方法")
        class TaskCancel {
            @Test
            @DisplayName("取消任务，给定中断运行值为 true，返回 false")
            void givenInterruptTrueWhenTaskCanceledThenReturnFalse() {
                ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                boolean cancel = ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                assertThat(cancel).isFalse();
            }

            @Test
            @DisplayName("不取消任务，给定中断运行值为 true，返回 true")
            void givenInterruptTrueWhenTaskNotCanceledThenReturnTrue() {
                boolean cancel = ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                assertThat(cancel).isTrue();
            }
        }

        @Nested
        @DisplayName("测试 isCanceled 方法")
        class TaskIsCanceled {
            @Test
            @DisplayName("取消任务，判断任务是否被取消，返回 true")
            void whenTaskCanceledThenReturnTrue() {
                ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                boolean cancelled = ReschedulableTaskTest.this.reschedulableTask.isCancelled();
                assertThat(cancelled).isTrue();
            }

            @Test
            @DisplayName("不取消任务，判断任务是否被取消，返回 false")
            void whenTaskNotCanceledShouldReturnFalse() {
                boolean cancelled = ReschedulableTaskTest.this.reschedulableTask.isCancelled();
                assertThat(cancelled).isFalse();
            }
        }

        @Nested
        @DisplayName("测试 isDone 方法")
        class TaskIsDone {
            @Test
            @DisplayName("取消任务，判断任务是否结束，返回 true")
            void whenTaskCanceledThenReturnTrue() {
                ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                boolean cancelled = ReschedulableTaskTest.this.reschedulableTask.isDone();
                assertThat(cancelled).isTrue();
            }

            @Test
            @DisplayName("不取消任务，判断任务是否结束，返回 false")
            void whenTaskNotCanceledShouldReturnFalse() {
                boolean cancelled = ReschedulableTaskTest.this.reschedulableTask.isDone();
                assertThat(cancelled).isFalse();
            }
        }

        @Test
        @DisplayName("调用 get() 方法，返回值不为 null")
        void invokeGetMethodThenReturnNotNull() throws ExecutionException, InterruptedException {
            Object waitInfo = ReschedulableTaskTest.this.reschedulableTask.get();
            assertThat(waitInfo).isNotNull();
        }

        @Nested
        @DisplayName("测试 get(long, TimeUnit) 方法")
        class TestGetMethod {
            @Test
            @DisplayName("给定等待时间，返回值不为 null")
            void givenWaitTimeThenReturnNotNull() throws ExecutionException, InterruptedException, TimeoutException {
                Object waitInfo = ReschedulableTaskTest.this.reschedulableTask.get(1L, TimeUnit.SECONDS);
                assertThat(waitInfo).isNotNull();
            }
        }

        @Nested
        @DisplayName("测试 call() 方法")
        class TestCallMethod {
            @Test
            @DisplayName("取消任务，执行方法，任务执行成功")
            void whenTaskCanceledThenTaskExecutedSuccessfully() throws Exception {
                ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                Object call = ReschedulableTaskTest.this.reschedulableTask.call();
                assertThat(call).isEqualTo("finishCallable");
            }
        }
    }

    @Nested
    @DisplayName("未更新当前计划下次执行的时间值")
    class NotUpdateCurrentScheduledFuture {
        @Nested
        @DisplayName("测试 cancel 方法")
        class TestCancel {
            @Test
            @DisplayName("给定中断运行值为 true，返回 false")
            void givenInterruptTrueThenReturnFalse() {
                boolean cancel = ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                assertThat(cancel).isFalse();
            }
        }

        @Nested
        @DisplayName("测试 isCancel 方法")
        class TaskIsCanceled {
            @Test
            @DisplayName("取消任务，判断任务是否被取消，返回 false")
            void whenTaskCanceledThenReturnFalse() {
                ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                boolean cancelled = ReschedulableTaskTest.this.reschedulableTask.isCancelled();
                assertThat(cancelled).isFalse();
            }
        }

        @Nested
        @DisplayName("测试 isDone 方法")
        class TaskIsDone {
            @Test
            @DisplayName("取消任务，判断任务是否结束，返回 false")
            void whenTaskCanceledThenReturnFalse() {
                ReschedulableTaskTest.this.reschedulableTask.cancel(true);
                boolean cancelled = ReschedulableTaskTest.this.reschedulableTask.isDone();
                assertThat(cancelled).isFalse();
            }
        }

        @Test
        @DisplayName("调用 get() 方法，返回值为 null")
        void invokeGetMethodThenReturnNull() throws ExecutionException, InterruptedException {
            Object waitInfo = ReschedulableTaskTest.this.reschedulableTask.get();
            assertThat(waitInfo).isEqualTo(null);
        }

        @Nested
        @DisplayName("测试 get(long, TimeUnit) 方法")
        class TestGetMethod {
            @Test
            @DisplayName("给定等待时间，返回 null")
            void givenWaitTimeThenReturnNull() throws ExecutionException, InterruptedException, TimeoutException {
                Object waitInfo = ReschedulableTaskTest.this.reschedulableTask.get(10L, TimeUnit.MILLISECONDS);
                assertThat(waitInfo).isEqualTo(null);
            }
        }
    }
}
