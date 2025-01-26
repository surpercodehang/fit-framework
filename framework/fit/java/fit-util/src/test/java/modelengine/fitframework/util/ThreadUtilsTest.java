/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import modelengine.fitframework.thread.DefaultThreadFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * {@link ThreadUtils} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-06
 */
public class ThreadUtilsTest {
    @Nested
    @DisplayName("Test method: join(Thread thread)")
    class TestJoin {
        @Nested
        @DisplayName("Test expected scenario")
        class TestExpectedScenario {
            @Test
            @DisplayName("Given two threads to add list then their sequence is stable")
            void givenNewThreadAndMainThreadToAddListThenTheirSequenceIsStable() {
                List<Integer> list = new ArrayList<>();
                Thread t1 = new Thread(() -> this.addList(list, 0));
                t1.setName("ThreadUtils-thread-1");
                t1.setUncaughtExceptionHandler((thread, ex) -> {});
                t1.start();
                assertThatNoException().isThrownBy(() -> ThreadUtils.join(t1));
                this.addList(list, 1);
                assertThat(list).containsSequence(0, 1);
            }

            @Test
            @DisplayName("Given thread is null then invoked without exception")
            void givenThreadNullThenInvokedWithoutException() {
                assertThatNoException().isThrownBy(() -> ThreadUtils.join(null));
            }

            private void addList(List<Integer> list, int integer) {
                list.add(integer);
            }
        }

        @Nested
        @DisplayName("Test exception scenario")
        class TestExceptionScenario {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Given join with InterruptedException then interrupted flag is correct")
            void givenThreadJoinWithExceptionThenInterruptedFlagIsCorrect() throws InterruptedException {
                Thread t1 = mock(Thread.class);
                doThrow(new InterruptedException()).when(t1).join();
                assertThat(Thread.currentThread().isInterrupted()).isFalse();
                assertThatNoException().isThrownBy(() -> ThreadUtils.join(t1));
                assertThat(Thread.currentThread().isInterrupted()).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("Test method: sleep(long milliseconds)")
    class TestSleep {
        @Nested
        @DisplayName("Test expected scenario")
        class TestExpectedScenario {
            @Test
            @DisplayName("Given sleep 1 millis then time gap is greater or equal to 1")
            void givenThreadSleep1MillisThenTimeGapIsGreaterOrEqualTo1() {
                long start = System.currentTimeMillis();
                ThreadUtils.sleep(1);
                long stop = System.currentTimeMillis();
                assertThat(stop - start).isGreaterThanOrEqualTo(1);
            }
        }

        @Nested
        @DisplayName("Test exception scenario")
        class TestExceptionScenario {
            @SuppressWarnings("ConstantConditions")
            @Test
            @DisplayName("Given main thread run with InterruptedException then interrupted flag is correct")
            void givenMainThreadRunWithInterruptedExceptionThenInterruptedFlagIsCorrect() {
                InterruptThread t1 = new InterruptThread(Thread.currentThread());
                t1.setName("ThreadUtils-thread-2");
                t1.start();
                assertThat(Thread.currentThread().isInterrupted()).isFalse();
                assertThatNoException().isThrownBy(() -> ThreadUtils.sleep(1000));
                assertThat(Thread.currentThread().isInterrupted()).isTrue();
            }

            final class InterruptThread extends Thread {
                private final Thread targetThread;

                InterruptThread(Thread thread) {
                    super.setName("InterruptThread");
                    this.targetThread = thread;
                }

                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                        this.targetThread.interrupt();
                    } catch (InterruptedException ignored) {
                        // ignore
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("Test method: singleThreadPool")
    class TestSingleThreadPool {
        @Test
        @DisplayName("create 1 thread，execute normal")
        void createOneThreadThenExecuteNormal() {
            final ThreadPoolExecutor executor =
                    ThreadUtils.singleThreadPool(new DefaultThreadFactory("default", false, (thread, ex) -> {}));
            executor.execute(() -> {});
            assertThat(executor.getPoolSize()).isEqualTo(1);
        }

        @Test
        @DisplayName("create more than 1 thread，execute throw exception")
        void createMoreThanOneThenThrowException() {
            final ThreadPoolExecutor executor =
                    ThreadUtils.singleThreadPool(new DefaultThreadFactory("default", false, (thread, ex) -> {}));
            executor.execute(() -> {});
            try {
                executor.execute(() -> {});
            } catch (RejectedExecutionException exception) {
                assertThat(exception).isInstanceOf(RejectedExecutionException.class);
            }
        }
    }
}
