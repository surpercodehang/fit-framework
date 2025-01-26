/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.async;

import static modelengine.fit.http.header.HttpHeaderKey.FIT_DATA_FORMAT;
import static modelengine.fit.http.header.HttpHeaderKey.FIT_TLV;
import static modelengine.fit.serialization.http.Constants.FIT_ASYNC_LONG_POLLING_DURATION_MILLIS;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.client.Request;
import modelengine.fit.client.Response;
import modelengine.fit.client.http.support.ConnectionBuilderFactory;
import modelengine.fit.client.http.support.HttpConnectionBuilder;
import modelengine.fit.client.http.util.HttpClientUtils;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.exception.AsyncTaskNotCompletedException;
import modelengine.fit.http.exception.AsyncTaskNotFoundException;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fit.http.protocol.Protocol;
import modelengine.fit.serialization.http.HttpUtils;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.tlv.TlvUtils;
import modelengine.fitframework.thread.DefaultThreadFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 通信服务器端抽象，封装了长轮询线程的实现细节。
 * <p>客户端对相同服务器端（相同的 workerId 和 workerInstanceId 的二元组）的调用都会使用同一服务器对象查询结果。</p>
 *
 * @author 王成
 * @author 季聿阶
 * @since 2023-11-17
 */
class AsyncTaskServer {
    private static final Logger log = Logger.get(AsyncTaskServer.class);
    private static final long TASK_WAITING_MILLIS = FIT_ASYNC_LONG_POLLING_DURATION_MILLIS / 2;

    private final BeanContainer container;

    /** 长轮询线程使用单线程池进行管理。 */
    private final ExecutorService asyncTaskExecutorService = Executors.newSingleThreadExecutor(new DefaultThreadFactory(
            "async-task-server",
            true,
            (thread, cause) -> {}));
    private final Map<String, SynchronousQueue<AsyncTaskResult>> pendingTasks = new ConcurrentHashMap<>();
    private final Map<String, AsyncTaskResult> completedTasks = new ConcurrentHashMap<>();

    /** 没有任务提交时，长轮询线程会在此信号量上挂起。提交任务时，每个任务会释放 1 信号量，用来解除长轮询线程的阻塞。 */
    private final Semaphore taskSemaphore = new Semaphore(0);
    private final String instanceId;

    /** 使用 volatile 标志位决定服务器是否应该清理并退出。 */
    private volatile boolean shouldStop = false;

    AsyncTaskServer(BeanContainer container, WorkerConfig workerConfig, HttpClassicClient client,
            Request longPollingRequest, String targetWorkerInstanceId) {
        this.container = notNull(container, "The bean container cannot be null.");
        notNull(workerConfig, "The worker config cannot be null.");
        notNull(client, "The http classic client cannot be null.");
        notNull(longPollingRequest, "The long pulling request cannot be null.");
        this.instanceId = notBlank(targetWorkerInstanceId, "The target worker instance id cannot be blank.");

        // 启动长轮询线程。
        this.asyncTaskExecutorService.submit(() -> this.longPolling(longPollingRequest, client, workerConfig));
    }

    private void longPolling(Request request, HttpClassicClient client, WorkerConfig workerConfig) {
        while (true) {
            // 任务数量为空且服务器未被停止时，长轮询线程挂起等待。
            while (this.pendingTasks.isEmpty() && !this.shouldStop) {
                try {
                    // 长轮询线程每隔长轮询一半的时间会解除阻塞并检查服务器状态，服务器退出时会设置退出标志位，线程池会中断长轮询线程。
                    this.taskSemaphore.tryAcquire(TASK_WAITING_MILLIS, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignored) {
                    // 循环条件检查，此处可直接忽略异常。
                }
            }

            // 检查退出标志位。
            if (this.shouldStop) {
                log.warn("Async task server was forced to stop. [id={}]", this.instanceId);
                this.cleanUp();
                return;
            }

            // 开始长轮询
            if (this.startLongPolling(request, client, workerConfig)) {
                return;
            }
        }
    }

    private boolean startLongPolling(Request request, HttpClassicClient client, WorkerConfig workerConfig) {
        try (HttpClassicClientResponse<Object> clientResponse = client.exchange(this.buildRequest(client,
                request,
                workerConfig))) {
            int responseCode = HttpClientUtils.getResponseCode(request, clientResponse);
            if (responseCode == AsyncTaskNotFoundException.CODE) {
                // 如果返回值为任务未找到 (AsyncTaskNotFoundException)，则说明服务器端已无任务，客户端现有的任务已丢失。
                // 此时应该停止长轮询、清理任务并退出服务器。
                this.cleanUp();
                return true;
            } else if (responseCode == AsyncTaskNotCompletedException.CODE) {
                // 如果返回值为任务未完成，继续长轮询。
            } else {
                // 如果返回值为 OK 或者其他情况，则将结果传递给客户端请求线程。
                Response response = HttpClientUtils.getResponse(this.container, request, clientResponse);
                AsyncTaskResult result = new AsyncTaskResult(response);
                String asyncTaskId = HttpUtils.getAsyncTaskId(response.metadata().tagValues());
                SynchronousQueue<AsyncTaskResult> queue = this.pendingTasks.get(asyncTaskId);
                if (queue == null || !queue.offer(result)) {
                    // queue 不存在或者投送失败时，把任务存入暂存区。offer() 方法不阻塞。
                    this.completedTasks.put(asyncTaskId, result);
                }
                // 无论是否有线程阻塞在执行结果上，都清除已完成的任务。
                this.pendingTasks.remove(asyncTaskId);
            }
        } catch (Exception e) {
            // 捕获全部异常。任何异常抛出，都会触发长轮询线程退出并清理所有已有任务。
            log.warn("Async task server will stop due to exception. [id={}]", this.instanceId, e);
            this.cleanUp();
            return true;
        }
        return false;
    }

    /**
     * 阻塞调用。获取异步执行结果。
     *
     * @param taskId 表示任务 ID 的 {@code String}
     * @return 表示异步任务执行结果的 {@link AsyncTaskResult}。
     */
    AsyncTaskResult get(String taskId) {
        // 如果暂存组有已完成的匹配任务，直接从暂存区获取
        if (this.completedTasks.containsKey(taskId)) {
            return this.completedTasks.remove(taskId);
        }
        if (this.shouldStop) {
            return AsyncTaskResult.getEmptyResult();
        }
        SynchronousQueue<AsyncTaskResult> queue = new SynchronousQueue<>();
        this.pendingTasks.put(taskId, queue);
        // 解除长轮询线程的阻塞
        this.taskSemaphore.release();

        // 等待任务完成
        while (this.pendingTasks.containsKey(taskId) && !this.shouldStop) {
            try {
                // 兜底处理，当高并发时，可能存在前序检查已完成任务列表之后，又向其中设置的情况，因此，需要周期性的进行检查。
                AsyncTaskResult asyncResult = queue.poll(5000, TimeUnit.MILLISECONDS);
                if (asyncResult != null) {
                    return asyncResult;
                }
                if (this.completedTasks.containsKey(taskId)) {
                    return this.completedTasks.remove(taskId);
                }
            } catch (InterruptedException ignored) {
                // 线程被中断时，手动移除当前任务并返回空结果
                this.pendingTasks.remove(taskId);
                return AsyncTaskResult.getEmptyResult();
            }
        }
        // 在等待任务队列被删除指定任务后，异步结果可能存在已完成的队列中。
        if (this.completedTasks.containsKey(taskId)) {
            return this.completedTasks.remove(taskId);
        }
        // 任务被移除，应该返回空结果
        return AsyncTaskResult.getEmptyResult();
    }

    /**
     * 设置退出标志位并关闭线程池
     */
    void close() {
        this.shouldStop = true;
        this.asyncTaskExecutorService.shutdownNow();
    }

    private void cleanUp() {
        this.shouldStop = true;
        // 加锁并清理所有现有任务
        for (SynchronousQueue<AsyncTaskResult> result : this.pendingTasks.values()) {
            result.offer(AsyncTaskResult.getEmptyResult());
        }
        this.pendingTasks.clear();
    }

    /**
     * 进程实例唯一标识不一致或者本服务器已退出时，应该被替换。
     *
     * @param serverInstanceId 表示请求实例唯一标识的 {@link String}。
     * @return 服务器是否应该被替换的 {@code boolean}。
     */
    boolean shouldBeReplaced(String serverInstanceId) {
        return !this.instanceId.equals(serverInstanceId) || this.shouldStop;
    }

    private HttpClassicClientRequest buildRequest(HttpClassicClient client, Request request,
            WorkerConfig workerConfig) {
        HttpConnectionBuilder builder =
                cast(ConnectionBuilderFactory.getConnectionBuilder(Protocol.from(request.protocol())));
        String url = builder.buildLongPollingUrl(request);
        HttpClassicClientRequest clientRequest = client.createRequest(HttpRequestMethod.GET, url);
        TagLengthValues tagLengthValues = request.metadata().tagValues();
        TlvUtils.setWorkerId(tagLengthValues, workerConfig.id());
        TlvUtils.setWorkerInstanceId(tagLengthValues, workerConfig.instanceId());
        clientRequest.headers()
                .add(FIT_DATA_FORMAT.value(), String.valueOf(request.metadata().dataFormatByte()))
                .add(FIT_TLV.value(), HttpUtils.encode(tagLengthValues.serialize()))
                .add(MessageHeaderNames.ACCEPT, MimeType.APPLICATION_OCTET_STREAM.value());
        return clientRequest;
    }
}
