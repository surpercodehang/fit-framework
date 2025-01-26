/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.ThreadUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 表示 {@link Task} 的可重复调度的实现。
 *
 * @author 季聿阶
 * @since 2022-12-27
 */
public class ReschedulableTask implements Task, ScheduledFuture<Object> {
    private static final int TIME_ABNORMAL_SLEEP_TIME = 1000;

    private final ScheduledExecutorService threadPool;
    private final Task task;
    private final Instant startTime;
    private final ReschedulableExecution execution;
    private final Object lock = LockUtils.newSynchronizedLock();
    private volatile ScheduledFuture<Object> currentScheduledFuture;

    ReschedulableTask(ScheduledExecutorService threadPool, Task task, Instant startTime) {
        this.threadPool = notNull(threadPool, "The thread pool cannot be null.");
        this.task = notNull(task, "The task cannot be null.");
        this.startTime = notNull(startTime, "The start time cannot be null.");
        this.execution = new ReschedulableExecution();
    }

    private boolean isCurTimeInvalid(Instant curTime) {
        return curTime.isBefore(this.startTime) || this.execution.lastExecuteTime()
                .map(lastTime -> lastTime.isAfter(curTime))
                .orElse(false);
    }

    private Instant getCurTime() {
        Instant curTime = Instant.now();
        if (this.execution.status() == ExecutePolicy.ExecutionStatus.SCHEDULING) {
            // 当第一次进行调度的时候，忽略时间回拨问题。
            return curTime;
        }
        while (this.isCurTimeInvalid(curTime)) {
            ThreadUtils.sleep(TIME_ABNORMAL_SLEEP_TIME);
            curTime = Instant.now();
        }
        return curTime;
    }

    /**
     * 调度执行任务。
     *
     * @return 表示调度执行任务的未来结果的 {@link ScheduledFuture}{@code <?>}。
     */
    ScheduledFuture<?> schedule() {
        synchronized (this.lock) {
            Optional<Instant> optionalNextExecuteTime =
                    this.task.policy().nextExecuteTime(this.execution, this.startTime);
            if (!optionalNextExecuteTime.isPresent()) {
                return this;
            }
            Instant nextExecuteTime = optionalNextExecuteTime.get();
            Instant curTime = this.getCurTime();
            this.execution.updateScheduledTime(nextExecuteTime);
            long initialDelay = nextExecuteTime.toEpochMilli() - curTime.toEpochMilli();
            this.currentScheduledFuture = this.threadPool.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
            return this;
        }
    }

    @Override
    public Thread.UncaughtExceptionHandler uncaughtExceptionHandler() {
        return this.task.uncaughtExceptionHandler();
    }

    @Override
    public ExecutePolicy policy() {
        return this.task.policy();
    }

    @Override
    public Object call() throws Exception {
        synchronized (this.lock) {
            this.execution.updateExecuteTime(this.getCurTime());
        }
        Object actualResult = this.task.call();
        synchronized (this.lock) {
            this.execution.updateCompleteTime(this.getCurTime());
            notNull(this.currentScheduledFuture, "The current scheduled future cannot be null.");
            if (!this.currentScheduledFuture.isCancelled()) {
                this.schedule();
            }
        }
        return actualResult;
    }

    @Override
    public long getDelay(@Nonnull TimeUnit unit) {
        ScheduledFuture<?> scheduledFuture;
        synchronized (this.lock) {
            scheduledFuture = this.currentScheduledFuture;
        }
        return scheduledFuture == null ? 0 : scheduledFuture.getDelay(unit);
    }

    @Override
    public int compareTo(@Nonnull Delayed another) {
        if (another == this) {
            return 0;
        }
        long diff = this.getDelay(TimeUnit.MILLISECONDS) - another.getDelay(TimeUnit.MILLISECONDS);
        return diff == 0 ? 0 : diff < 0 ? -1 : 1;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this.lock) {
            return this.currentScheduledFuture != null && this.currentScheduledFuture.cancel(mayInterruptIfRunning);
        }
    }

    @Override
    public boolean isCancelled() {
        synchronized (this.lock) {
            return this.currentScheduledFuture != null && this.currentScheduledFuture.isCancelled();
        }
    }

    @Override
    public boolean isDone() {
        synchronized (this.lock) {
            return this.currentScheduledFuture != null && this.currentScheduledFuture.isDone();
        }
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        ScheduledFuture<?> scheduledFuture;
        synchronized (this.lock) {
            scheduledFuture = this.currentScheduledFuture;
        }
        return scheduledFuture == null ? null : scheduledFuture.get();
    }

    @Override
    public Object get(long timeout, @Nonnull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledFuture<?> scheduledFuture;
        synchronized (this.lock) {
            scheduledFuture = this.currentScheduledFuture;
        }
        return scheduledFuture == null ? null : scheduledFuture.get(timeout, unit);
    }
}
