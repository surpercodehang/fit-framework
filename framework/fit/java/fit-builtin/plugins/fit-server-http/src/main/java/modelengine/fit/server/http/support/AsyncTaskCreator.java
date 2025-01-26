/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http.support;

import static modelengine.fit.serialization.http.Constants.FIT_ASYNC_LONG_POLLING_DURATION_MILLIS;

import modelengine.fit.http.exception.AsyncTaskExecutionException;
import modelengine.fit.http.exception.AsyncTaskNotFoundException;
import modelengine.fit.serialization.http.HttpUtils;
import modelengine.fitframework.broker.server.Response;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 表示 FIT 异步任务提交者。
 *
 * @author 王成
 * @since 2023-11-17
 */
public class AsyncTaskCreator {
    private static final Logger log = Logger.get(AsyncTaskCreator.class);

    private final String workerInstanceId;
    private final Map<String, CompletableFuture<Response>> store;
    private final Semaphore taskSemaphore;

    public AsyncTaskCreator(String workerInstanceId) {
        this.workerInstanceId = workerInstanceId;
        this.store = new ConcurrentHashMap<>();
        this.taskSemaphore = new Semaphore(0);
    }

    /**
     * 判断请求者的进程实例唯一标识是否与之前的提交记录一致。
     *
     * @param workerInstanceId 表示请求实例唯一标识的 {@link String}。
     * @return 表示请求者的实例唯一标识是否与之前的提交记录一致的 {@code boolean}。
     */
    boolean isNotSameInstance(String workerInstanceId) {
        return !StringUtils.equals(this.getInstanceId(), workerInstanceId);
    }

    /**
     * 获取进程实例的唯一标识。
     *
     * @return 表示进程实例唯一标识的 {@code String}。
     */
    String getInstanceId() {
        return this.workerInstanceId;
    }

    /**
     * 将任务查询句柄加入对应请求者实例的记录。
     *
     * @param taskId 表示异步任务唯一标识的 {@link String}。
     * @param responseFuture 表示异步任务查询句柄的 {@link CompletableFuture}{@code <}{@link Response}{@code >}。
     */
    void addTask(String taskId, CompletableFuture<Response> responseFuture) {
        // 异步任务唯一标识重复时，输出告警日志并覆盖已有任务。
        if (this.store.containsKey(taskId)) {
            log.warn("Duplicated task id found. [workerInstanceId={}, taskId={}]", this.workerInstanceId, taskId);
        }
        this.taskSemaphore.release();
        this.store.put(taskId, responseFuture);
    }

    /**
     * 任务完成时释放信号量，解除长轮询线程的阻塞。
     */
    void notifyCompletion() {
        this.taskSemaphore.release();
    }

    /**
     * 长轮询异步任务。
     *
     * @return 表示长轮询的结果的 {@link Optional}{@code <}{@link Response}{@code >}。
     * @throws AsyncTaskNotFoundException 当指定的异步任务无法找到时。
     * @throws AsyncTaskExecutionException 当指定的异步任务执行过程中发生异常时。
     */
    Optional<Response> longPolling() {
        long durationMillis = FIT_ASYNC_LONG_POLLING_DURATION_MILLIS;
        long curMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - curMillis <= durationMillis) {
            Collection<CompletableFuture<Response>> allTasks = this.store.values();
            if (allTasks.isEmpty()) {
                throw new AsyncTaskNotFoundException(StringUtils.format(
                        "Async task not found for current worker. [workerInstanceId={0}]",
                        this.workerInstanceId));
            }
            try {
                // taskSemaphore 会在新任务抵达或者已有任务完成时被释放，解除阻塞。
                this.taskSemaphore.tryAcquire(durationMillis, TimeUnit.MILLISECONDS);
                // 由于数组的协变性，toArray 后会失去类型信息，但强制类型转换是必定成功的。
                // 使用非阻塞查询 API。
                Response response = ObjectUtils.cast(CompletableFuture.anyOf(allTasks.toArray(new CompletableFuture[0]))
                        .getNow(null));
                if (response != null) {
                    // 清除已经结束的任务结果。
                    this.store.remove(HttpUtils.getAsyncTaskId(response.metadata().tagValues()));
                    return Optional.of(response);
                }
            } catch (InterruptedException e) {
                return Optional.empty();
            } catch (CompletionException e) {
                log.warn("Async task execution failure. [cause={}]", e.getMessage());
                throw new AsyncTaskExecutionException(e.getCause());
            }
        }
        // 本次长轮询结束但没有任务完成，返回空结果。
        return Optional.empty();
    }
}
