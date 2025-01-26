/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * {@link LockUtils} 的单元测试。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class LockUtilsTest {
    @Nested
    @DisplayName("Test new lock")
    class TestNewLock {
        @Nested
        @DisplayName("Test method: newReentrantLock()")
        class TestNewReentrantLock {
            @Test
            @DisplayName("Return new lock")
            void returnNewLock() {
                Lock actual = LockUtils.newReentrantLock();
                assertThat(actual).isNotNull();
            }
        }

        @Nested
        @DisplayName("Test method: newReentrantReadWriteLock()")
        class TestNewReentrantReadWriteLock {
            @Test
            @DisplayName("Return new lock")
            void returnNewLock() {
                ReadWriteLock actual = LockUtils.newReentrantReadWriteLock();
                assertThat(actual).isNotNull();
            }
        }

        @Nested
        @DisplayName("Test method: newSynchronizedLock()")
        class TestNewSynchronizedLock {
            @Test
            @DisplayName("Return new lock")
            void returnNewLock() {
                Object actual = LockUtils.newSynchronizedLock();
                assertThat(actual).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Test synchronize")
    class TestSynchronize {
        @Nested
        @DisplayName("Test method: synchronize(Lock lock, BooleanSupplier supplier)")
        class TestSynchronizeBooleanSupplier {
            @Nested
            @DisplayName("Given lock")
            class GivenLock {
                @Test
                @DisplayName("Given 2 thread invoked at the same time then return ordered result")
                void given2ThreadsInvokedAtTheSameTimeThenReturnOrderedResult() {
                    Lock lock = LockUtils.newReentrantLock();
                    List<Integer> actual = new ArrayList<>();
                    List<Integer> expected = Arrays.asList(0, 1, 0, 1);
                    Thread t1 = new Thread(new TestBooleanSupplier(actual, lock));
                    Thread t2 = new Thread(new TestBooleanSupplier(actual, lock));
                    t1.setName("LockUtilsTest-Thread-1");
                    t2.setName("LockUtilsTest-Thread-2");
                    t1.setUncaughtExceptionHandler((thread, ex) -> {});
                    t2.setUncaughtExceptionHandler((thread, ex) -> {});
                    t1.start();
                    t2.start();
                    ThreadUtils.join(t1);
                    ThreadUtils.join(t2);
                    assertThat(actual).containsSequence(expected);
                }

                @Test
                @DisplayName("Given runnable is null then throw IllegalArgumentException")
                void givenRunnableIsNullThenThrowException() {
                    Lock lock = LockUtils.newReentrantLock();
                    IllegalArgumentException exception = catchThrowableOfType(() -> LockUtils.synchronize(lock,
                            ObjectUtils.<BooleanSupplier>cast(null)), IllegalArgumentException.class);
                    assertThat(exception).isNotNull().hasMessage("The supplier to get value cannot be null.");
                }

                @Test
                @DisplayName("Given runnable throw Exception then throw its original Exception")
                void givenRunnableThrowExceptionThenThrowException() {
                    Lock lock = LockUtils.newReentrantLock();
                    IllegalStateException exception =
                            catchThrowableOfType(() -> LockUtils.synchronize(lock, (BooleanSupplier) () -> {
                                throw new IllegalStateException();
                            }), IllegalStateException.class);
                    assertThat(exception).isNotNull();
                }
            }

            @Nested
            @DisplayName("Given no lock")
            class GivenNoLock {
                @Test
                @DisplayName("Given 2 thread invoked at the same time then return disordered result")
                void given2ThreadsInvokedAtTheSameTimeThenReturnDisorderedResult() {
                    List<Integer> actual = new ArrayList<>();
                    List<Integer> expected = Arrays.asList(0, 0, 1, 1);
                    Thread t1 = new Thread(new TestBooleanSupplier(actual, null));
                    Thread t2 = new Thread(new TestBooleanSupplier(actual, null));
                    t1.setName("LockUtilsTest-Thread-1");
                    t2.setName("LockUtilsTest-Thread-2");
                    t1.setUncaughtExceptionHandler((thread, ex) -> {});
                    t2.setUncaughtExceptionHandler((thread, ex) -> {});
                    t1.start();
                    t2.start();
                    ThreadUtils.join(t1);
                    ThreadUtils.join(t2);
                    assertThat(actual).containsSequence(expected);
                }
            }

            private class TestBooleanSupplier extends TestAction implements Runnable {
                private final Lock lock;

                TestBooleanSupplier(List<Integer> list, Lock lock) {
                    super(list, lock);
                    this.lock = lock;
                }

                @Override
                public void run() {
                    LockUtils.synchronize(this.lock, () -> {
                        this.recordSurroundSleep();
                        return true;
                    });
                }
            }
        }

        @Nested
        @DisplayName("Test method: synchronize(Lock lock, IntSupplier supplier)")
        class TestSynchronizeIntSupplier {
            @Nested
            @DisplayName("Given lock")
            class GivenLock {
                @Test
                @DisplayName("Given 2 thread invoked at the same time then return ordered result")
                void given2ThreadsInvokedAtTheSameTimeThenReturnOrderedResult() {
                    Lock lock = LockUtils.newReentrantLock();
                    List<Integer> actual = new ArrayList<>();
                    List<Integer> expected = Arrays.asList(0, 1, 0, 1);
                    Thread t1 = new Thread(new TestIntSupplier(actual, lock));
                    Thread t2 = new Thread(new TestIntSupplier(actual, lock));
                    t1.setName("LockUtilsTest-Thread-1");
                    t2.setName("LockUtilsTest-Thread-2");
                    t1.setUncaughtExceptionHandler((thread, ex) -> {});
                    t2.setUncaughtExceptionHandler((thread, ex) -> {});
                    t1.start();
                    t2.start();
                    ThreadUtils.join(t1);
                    ThreadUtils.join(t2);
                    assertThat(actual).containsSequence(expected);
                }

                @Test
                @DisplayName("Given runnable is null then throw IllegalArgumentException")
                void givenRunnableIsNullThenThrowException() {
                    Lock lock = LockUtils.newReentrantLock();
                    IllegalArgumentException exception =
                            catchThrowableOfType(() -> LockUtils.synchronize(lock, ObjectUtils.<IntSupplier>cast(null)),
                                    IllegalArgumentException.class);
                    assertThat(exception).isNotNull().hasMessage("The supplier to get value cannot be null.");
                }

                @Test
                @DisplayName("Given runnable throw Exception then throw its original Exception")
                void givenRunnableThrowExceptionThenThrowException() {
                    Lock lock = LockUtils.newReentrantLock();
                    IllegalStateException exception =
                            catchThrowableOfType(() -> LockUtils.synchronize(lock, (IntSupplier) () -> {
                                throw new IllegalStateException();
                            }), IllegalStateException.class);
                    assertThat(exception).isNotNull();
                }
            }

            @Nested
            @DisplayName("Given no lock")
            class GivenNoLock {
                @Test
                @DisplayName("Given 2 thread invoked at the same time then return disordered result")
                void given2ThreadsInvokedAtTheSameTimeThenReturnDisorderedResult() {
                    List<Integer> actual = new ArrayList<>();
                    List<Integer> expected = Arrays.asList(0, 0, 1, 1);
                    Thread t1 = new Thread(new TestIntSupplier(actual, null));
                    Thread t2 = new Thread(new TestIntSupplier(actual, null));
                    t1.setName("LockUtilsTest-Thread-1");
                    t2.setName("LockUtilsTest-Thread-2");
                    t1.setUncaughtExceptionHandler((thread, ex) -> {});
                    t2.setUncaughtExceptionHandler((thread, ex) -> {});
                    t1.start();
                    t2.start();
                    ThreadUtils.join(t1);
                    ThreadUtils.join(t2);
                    assertThat(actual).containsSequence(expected);
                }
            }

            private class TestIntSupplier extends TestAction implements Runnable {
                private final Lock lock;

                TestIntSupplier(List<Integer> list, Lock lock) {
                    super(list, lock);
                    this.lock = lock;
                }

                @Override
                public void run() {
                    LockUtils.synchronize(this.lock, () -> {
                        this.recordSurroundSleep();
                        return 0;
                    });
                }
            }
        }

        @Nested
        @DisplayName("Test method: synchronize(Lock lock, Runnable action)")
        class TestSynchronizeRunnable {
            @Nested
            @DisplayName("Given lock")
            class GivenLock {
                @Test
                @DisplayName("Given 2 thread invoked at the same time then return ordered result")
                void given2ThreadsInvokedAtTheSameTimeThenReturnOrderedResult() {
                    Lock lock = LockUtils.newReentrantLock();
                    List<Integer> actual = new ArrayList<>();
                    List<Integer> expected = Arrays.asList(0, 1, 0, 1);
                    Thread t1 = new Thread(new TestRunnable(actual, lock));
                    Thread t2 = new Thread(new TestRunnable(actual, lock));
                    t1.setName("LockUtilsTest-Thread-1");
                    t2.setName("LockUtilsTest-Thread-2");
                    t1.setUncaughtExceptionHandler((thread, ex) -> {});
                    t2.setUncaughtExceptionHandler((thread, ex) -> {});
                    t1.start();
                    t2.start();
                    ThreadUtils.join(t1);
                    ThreadUtils.join(t2);
                    assertThat(actual).containsSequence(expected);
                }

                @Test
                @DisplayName("Given runnable is null then throw IllegalArgumentException")
                void givenRunnableIsNullThenThrowException() {
                    Lock lock = LockUtils.newReentrantLock();
                    IllegalArgumentException exception =
                            catchThrowableOfType(() -> LockUtils.synchronize(lock, ObjectUtils.<Runnable>cast(null)),
                                    IllegalArgumentException.class);
                    assertThat(exception).isNotNull().hasMessage("The action to perform synchronously cannot be null.");
                }
            }

            @Nested
            @DisplayName("Given no lock")
            class GivenNoLock {
                @Test
                @DisplayName("Given 2 thread invoked at the same time then return disordered result")
                void given2ThreadsInvokedAtTheSameTimeThenReturnDisorderedResult() {
                    List<Integer> actual = new ArrayList<>();
                    List<Integer> expected = Arrays.asList(0, 0, 1, 1);
                    Thread t1 = new Thread(new TestRunnable(actual, null));
                    Thread t2 = new Thread(new TestRunnable(actual, null));
                    t1.setName("LockUtilsTest-Thread-1");
                    t2.setName("LockUtilsTest-Thread-2");
                    t1.setUncaughtExceptionHandler((thread, ex) -> {});
                    t2.setUncaughtExceptionHandler((thread, ex) -> {});
                    t1.start();
                    t2.start();
                    ThreadUtils.join(t1);
                    ThreadUtils.join(t2);
                    assertThat(actual).containsSequence(expected);
                }
            }

            private class TestRunnable extends TestAction implements Runnable {
                private final Lock lock;

                TestRunnable(List<Integer> list, Lock lock) {
                    super(list, lock);
                    this.lock = lock;
                }

                @Override
                public void run() {
                    LockUtils.synchronize(this.lock, this::recordSurroundSleep);
                }
            }
        }

        @Nested
        @DisplayName("Test method: synchronize(Lock lock, Supplier<T> supplier)")
        class TestSynchronizeSupplier {
            @Nested
            @DisplayName("Given lock")
            class GivenLock {
                @Test
                @DisplayName("Given 2 thread invoked at the same time then return ordered result")
                void given2ThreadsInvokedAtTheSameTimeThenReturnOrderedResult() {
                    Lock lock = LockUtils.newReentrantLock();
                    List<Integer> actual = new ArrayList<>();
                    List<Integer> expected = Arrays.asList(0, 1, 0, 1);
                    Thread t1 = new Thread(new TestSupplier(actual, lock));
                    Thread t2 = new Thread(new TestSupplier(actual, lock));
                    t1.setName("LockUtilsTest-Thread-1");
                    t2.setName("LockUtilsTest-Thread-2");
                    t1.start();
                    t2.start();
                    t1.setUncaughtExceptionHandler((thread, ex) -> {});
                    t2.setUncaughtExceptionHandler((thread, ex) -> {});
                    ThreadUtils.join(t1);
                    ThreadUtils.join(t2);
                    assertThat(actual).containsSequence(expected);
                }

                @Test
                @DisplayName("Given runnable is null then throw IllegalArgumentException")
                void givenRunnableIsNullThenThrowException() {
                    Lock lock = LockUtils.newReentrantLock();
                    IllegalArgumentException exception =
                            catchThrowableOfType(() -> LockUtils.synchronize(lock, (Supplier<Integer>) null),
                                    IllegalArgumentException.class);
                    assertThat(exception).isNotNull().hasMessage("The supplier to get value cannot be null.");
                }

                @Test
                @DisplayName("Given runnable throw Exception then throw its original Exception")
                void givenRunnableThrowExceptionThenThrowException() {
                    Lock lock = LockUtils.newReentrantLock();
                    IllegalStateException exception =
                            catchThrowableOfType(() -> LockUtils.synchronize(lock, (Supplier<Object>) () -> {
                                throw new IllegalStateException();
                            }), IllegalStateException.class);
                    assertThat(exception).isNotNull();
                }
            }

            @Nested
            @DisplayName("Given no lock")
            class GivenNoLock {
                @Test
                @DisplayName("Given 2 thread invoked at the same time then return disordered result")
                void given2ThreadsInvokedAtTheSameTimeThenReturnDisorderedResult() {
                    List<Integer> actual = new ArrayList<>();
                    List<Integer> expected = Arrays.asList(0, 0, 1, 1);
                    Thread t1 = new Thread(new TestSupplier(actual, null));
                    Thread t2 = new Thread(new TestSupplier(actual, null));
                    t1.setName("LockUtilsTest-Thread-1");
                    t2.setName("LockUtilsTest-Thread-2");
                    t1.setUncaughtExceptionHandler((thread, ex) -> {});
                    t2.setUncaughtExceptionHandler((thread, ex) -> {});
                    t1.start();
                    t2.start();
                    ThreadUtils.join(t1);
                    ThreadUtils.join(t2);
                    assertThat(actual).containsSequence(expected);
                }
            }

            private class TestSupplier extends TestAction implements Runnable {
                private final Lock lock;

                TestSupplier(List<Integer> list, Lock lock) {
                    super(list, lock);
                    this.lock = lock;
                }

                @Override
                public void run() {
                    LockUtils.synchronize(this.lock, () -> {
                        this.recordSurroundSleep();
                        return ObjectUtils.<Integer>cast(0);
                    });
                }
            }
        }

        private class TestAction {
            private final List<Integer> list;
            private final Lock lock;

            TestAction(List<Integer> list, Lock lock) {
                this.list = list;
                this.lock = lock;
            }

            void recordSurroundSleep() {
                synchronized (this.list) {
                    this.list.notifyAll();
                    this.list.add(0);
                    if (this.lock == null) {
                        try {
                            this.list.wait();
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
                synchronized (this.list) {
                    this.list.notifyAll();
                    this.list.add(1);
                    try {
                        this.list.wait(10);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }
}
