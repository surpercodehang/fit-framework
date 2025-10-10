/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.tool.mcp.client.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fel.tool.mcp.client.McpClient;
import modelengine.fel.tool.mcp.entity.ClientSchema;
import modelengine.fel.tool.mcp.entity.Event;
import modelengine.fel.tool.mcp.entity.JsonRpc;
import modelengine.fel.tool.mcp.entity.Method;
import modelengine.fel.tool.mcp.entity.ServerSchema;
import modelengine.fel.tool.mcp.entity.Tool;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.TextEvent;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Subscription;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolExecutor;
import modelengine.fitframework.schedule.ThreadPoolScheduler;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.ThreadUtils;
import modelengine.fitframework.util.UuidUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a default implementation of the MCP client, responsible for interacting with the MCP server.
 * This class provides methods for initializing the client, retrieving tools, and calling tools.
 *
 * @author 季聿阶
 * @since 2025-05-21
 */
public class DefaultMcpClient implements McpClient {
    private static final Logger log = Logger.get(DefaultMcpClient.class);

    private final ObjectSerializer jsonSerializer;
    private final HttpClassicClient client;
    private final String baseUri;
    private final String sseEndpoint;
    private final String name;
    private final AtomicLong id = new AtomicLong(0);
    private final long pingInterval;

    private volatile String messageEndpoint;
    private volatile String sessionId;
    private volatile ServerSchema serverSchema;
    private volatile boolean initialized = false;
    private volatile boolean closed = false;
    private final Object initializedLock = LockUtils.newSynchronizedLock();
    private final Map<Long, Consumer<JsonRpc.Response<Long>>> responseConsumers = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> pendingRequests = new ConcurrentHashMap<>();
    private final Map<Long, Result> pendingResults = new ConcurrentHashMap<>();

    private volatile Subscription subscription;
    private volatile ThreadPoolScheduler pingScheduler;

    /**
     * Constructs a new instance of the DefaultMcpClient.
     *
     * @param jsonSerializer The serializer used for JSON serialization and deserialization.
     * @param client The HTTP client used for communication with the MCP server.
     * @param baseUri The base URI of the MCP server.
     * @param sseEndpoint The endpoint for the Server-Sent Events (SSE) connection.
     * @param pingInterval The interval for sending ping messages to the MCP server. Unit: milliseconds.
     */
    public DefaultMcpClient(ObjectSerializer jsonSerializer, HttpClassicClient client, String baseUri,
            String sseEndpoint, long pingInterval) {
        this.jsonSerializer = notNull(jsonSerializer, "The json serializer cannot be null.");
        this.client = notNull(client, "The http client cannot be null.");
        this.baseUri = notBlank(baseUri, "The MCP server base URI cannot be blank.");
        this.sseEndpoint = notBlank(sseEndpoint, "The MCP server SSE endpoint cannot be blank.");
        this.name = UuidUtils.randomUuidString();
        this.pingInterval = pingInterval > 0 ? pingInterval : 15_000;
    }

    @Override
    public void initialize() {
        if (this.closed) {
            throw new IllegalStateException("The MCP client is closed.");
        }
        HttpClassicClientRequest request =
                this.client.createRequest(HttpRequestMethod.GET, this.baseUri + this.sseEndpoint);
        Choir<TextEvent> messages = this.client.exchangeStream(request, TextEvent.class);
        ThreadPoolExecutor threadPool = ThreadPoolExecutor.custom()
                .threadPoolName("mcp-client-" + this.name)
                .awaitTermination(3, TimeUnit.SECONDS)
                .isImmediateShutdown(true)
                .corePoolSize(1)
                .maximumPoolSize(1)
                .keepAliveTime(60, TimeUnit.SECONDS)
                .workQueueCapacity(Integer.MAX_VALUE)
                .isDaemonThread(true)
                .exceptionHandler((thread, cause) -> log.warn("Exception in MCP client pool.", cause))
                .rejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy())
                .build();
        messages.subscribeOn(threadPool).subscribe(subscription -> {
                    log.info("Prepare to create SSE channel.");
                    this.subscription = subscription;
                    subscription.request(Long.MAX_VALUE);
                },
                (subscription, textEvent) -> this.consumeTextEvent(textEvent),
                subscription -> log.info("SSE channel is completed."),
                (subscription, cause) -> log.error("SSE channel is failed.", cause));
        if (!this.waitInitialized()) {
            throw new IllegalStateException("Failed to initialize.");
        }
    }

    private void consumeTextEvent(TextEvent textEvent) {
        log.info("Receive message from MCP server. [id={}, event={}, message={}]",
                textEvent.id(),
                textEvent.event(),
                textEvent.data());
        if (StringUtils.isBlank(textEvent.event()) || StringUtils.isBlank((String) textEvent.data())) {
            return;
        }
        if (Objects.equals(textEvent.event(), Event.ENDPOINT.code())) {
            this.initializeMcpServer(textEvent);
            return;
        }
        Map<String, Object> jsonRpc = this.jsonSerializer.deserialize((String) textEvent.data(), Object.class);
        Object messageId = jsonRpc.get("id");
        if (messageId == null) {
            // Notification message, ignore.
            return;
        }
        long actualId = Long.parseLong(messageId.toString());
        Consumer<JsonRpc.Response<Long>> consumer = this.responseConsumers.remove(actualId);
        if (consumer == null) {
            log.info("No consumer registered. [id={}]", actualId);
            return;
        }
        Object error = jsonRpc.get("error");
        JsonRpc.Response<Long> response;
        if (error == null) {
            response = JsonRpc.createResponse(actualId, jsonRpc.get("result"));
        } else {
            response = JsonRpc.createResponseWithError(actualId, error);
        }
        consumer.accept(response);
    }

    private void pingServer() {
        if (this.isNotInitialized()) {
            log.info("MCP client is not initialized and {} method will be delayed.", Method.PING.code());
            return;
        }
        this.post2McpServer(Method.PING, null, null);
    }

    private void initializeMcpServer(TextEvent textEvent) {
        this.messageEndpoint = textEvent.data().toString();
        ClientSchema schema = new ClientSchema("2024-11-05",
                new ClientSchema.Capabilities(),
                new ClientSchema.Info("FIT MCP Client", "3.5.4-SNAPSHOT"));
        this.post2McpServer(Method.INITIALIZE, schema, (request, currentId) -> {
            this.sessionId = request.queries()
                    .first("session_id")
                    .orElseThrow(() -> new IllegalStateException("The session_id cannot be empty."));
            this.responseConsumers.put(currentId, this::initializedMcpServer);
        });
    }

    private void initializedMcpServer(JsonRpc.Response<Long> response) {
        if (response.error() != null) {
            log.error("Abort send {} method to MCP server. [sessionId={}, error={}]",
                    Method.NOTIFICATION_INITIALIZED.code(),
                    this.sessionId,
                    response.error());
            throw new IllegalStateException(response.error().toString());
        }
        this.recordServerSchema(response);
        HttpClassicClientRequest request =
                this.client.createRequest(HttpRequestMethod.POST, this.baseUri + this.messageEndpoint);
        JsonRpc.Notification notification = JsonRpc.createNotification(Method.NOTIFICATION_INITIALIZED.code());
        request.entity(Entity.createObject(request, notification));
        log.info("Send {} method to MCP server. [sessionId={}, notification={}]",
                Method.NOTIFICATION_INITIALIZED.code(),
                this.sessionId,
                notification);
        try (HttpClassicClientResponse<Object> exchange = request.exchange(Object.class)) {
            if (exchange.statusCode() >= 200 && exchange.statusCode() < 300) {
                log.info("Send {} method to MCP server successfully. [sessionId={}, statusCode={}]",
                        Method.NOTIFICATION_INITIALIZED.code(),
                        this.sessionId,
                        exchange.statusCode());
            } else {
                log.error("Failed to {} MCP server. [sessionId={}, statusCode={}]",
                        Method.NOTIFICATION_INITIALIZED.code(),
                        this.sessionId,
                        exchange.statusCode());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        synchronized (this.initializedLock) {
            this.initialized = true;
            this.initializedLock.notifyAll();
        }
        this.pingScheduler = ThreadPoolScheduler.custom()
                .threadPoolName("mcp-client-ping-" + this.name)
                .awaitTermination(3, TimeUnit.SECONDS)
                .isImmediateShutdown(true)
                .corePoolSize(1)
                .maximumPoolSize(1)
                .keepAliveTime(60, TimeUnit.SECONDS)
                .workQueueCapacity(Integer.MAX_VALUE)
                .isDaemonThread(true)
                .build();
        this.pingScheduler.schedule(Task.builder()
                .runnable(this::pingServer)
                .policy(ExecutePolicy.fixedDelay(this.pingInterval))
                .build(), this.pingInterval);
    }

    private void recordServerSchema(JsonRpc.Response<Long> response) {
        Map<String, Object> mapResult = cast(response.result());
        this.serverSchema = ServerSchema.create(mapResult);
        log.info("MCP server has initialized. [server={}]", this.serverSchema);
    }

    @Override
    public List<Tool> getTools() {
        if (this.closed) {
            throw new IllegalStateException("The MCP client is closed.");
        }
        if (this.isNotInitialized()) {
            throw new IllegalStateException("MCP client is not initialized. Please wait a moment.");
        }
        long requestId = this.post2McpServer(Method.TOOLS_LIST, null, (request, currentId) -> {
            this.responseConsumers.put(currentId, this::getTools0);
            this.pendingRequests.put(currentId, true);
        });
        while (this.pendingRequests.get(requestId)) {
            ThreadUtils.sleep(100);
        }
        Result result = this.pendingResults.remove(requestId);
        this.pendingRequests.remove(requestId);
        if (result.isSuccess()) {
            return ObjectUtils.cast(result.getContent());
        } else {
            throw new IllegalStateException(result.getError());
        }
    }

    private void getTools0(JsonRpc.Response<Long> response) {
        if (response.error() != null) {
            String error = StringUtils.format("Failed to get tools list from MCP server. [sessionId={0}, response={1}]",
                    this.sessionId,
                    response);
            this.pendingResults.put(response.id(), Result.error(error));
            this.pendingRequests.put(response.id(), false);
            return;
        }
        Map<String, Object> result = cast(response.result());
        List<Map<String, Object>> rawTools = cast(result.get("tools"));
        List<Tool> tools = new ArrayList<>(rawTools.stream()
                .map(rawTool -> ObjectUtils.<Tool>toCustomObject(rawTool, Tool.class))
                .toList());
        this.pendingResults.put(response.id(), Result.success(tools));
        this.pendingRequests.put(response.id(), false);
    }

    @Override
    public Object callTool(String name, Map<String, Object> arguments) {
        if (this.closed) {
            throw new IllegalStateException("The MCP client is closed.");
        }
        if (this.isNotInitialized()) {
            throw new IllegalStateException("MCP client is not initialized. Please wait a moment.");
        }
        long requestId = this.post2McpServer(Method.TOOLS_CALL,
                MapBuilder.<String, Object>get().put("name", name).put("arguments", arguments).build(),
                (request, currentId) -> {
                    this.responseConsumers.put(currentId, this::callTools0);
                    this.pendingRequests.put(currentId, true);
                });
        while (this.pendingRequests.get(requestId)) {
            ThreadUtils.sleep(100);
        }
        Result result = this.pendingResults.remove(requestId);
        this.pendingRequests.remove(requestId);
        if (result.isSuccess()) {
            return result.getContent();
        } else {
            throw new IllegalStateException(result.getError());
        }
    }

    private void callTools0(JsonRpc.Response<Long> response) {
        if (response.error() != null) {
            String error = StringUtils.format("Failed to call tool from MCP server. [sessionId={0}, response={1}]",
                    this.sessionId,
                    response);
            this.pendingResults.put(response.id(), Result.error(error));
            this.pendingRequests.put(response.id(), false);
            return;
        }
        Map<String, Object> result = cast(response.result());
        boolean isError = cast(result.get("isError"));
        if (isError) {
            String error = StringUtils.format("Failed to call tool from MCP server. [sessionId={0}, result={1}]",
                    this.sessionId,
                    result);
            this.pendingResults.put(response.id(), Result.error(error));
            this.pendingRequests.put(response.id(), false);
            return;
        }
        List<Map<String, Object>> rawContents = cast(result.get("content"));
        if (CollectionUtils.isEmpty(rawContents)) {
            String error = StringUtils.format(
                    "Failed to call tool from MCP server: no result returned. [sessionId={0}, result={1}]",
                    this.sessionId,
                    result);
            this.pendingResults.put(response.id(), Result.error(error));
            this.pendingRequests.put(response.id(), false);
            return;
        }
        Map<String, Object> rawContent = rawContents.get(0);
        this.pendingResults.put(response.id(), Result.success(rawContent.get("text")));
        this.pendingRequests.put(response.id(), false);
    }

    private long post2McpServer(Method method, Object requestParams,
            BiConsumer<HttpClassicClientRequest, Long> requestConsumer) {
        HttpClassicClientRequest request =
                this.client.createRequest(HttpRequestMethod.POST, this.baseUri + this.messageEndpoint);
        long currentId = this.getNextId();
        if (requestConsumer != null) {
            requestConsumer.accept(request, currentId);
        }
        JsonRpc.Request<Long> rpcRequest = JsonRpc.createRequest(currentId, method.code(), requestParams);
        request.entity(Entity.createObject(request, rpcRequest));
        log.info("Send {} method to MCP server. [sessionId={}, request={}]", method.code(), this.sessionId, rpcRequest);
        try (HttpClassicClientResponse<Object> exchange = request.exchange(Object.class)) {
            if (exchange.statusCode() >= 200 && exchange.statusCode() < 300) {
                log.info("Send {} method to MCP server successfully. [sessionId={}, statusCode={}]",
                        method.code(),
                        this.sessionId,
                        exchange.statusCode());
            } else {
                log.error("Failed to {} MCP server. [sessionId={}, statusCode={}]",
                        method.code(),
                        this.sessionId,
                        exchange.statusCode());
            }
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to {0} MCP server. [sessionId={1}]",
                    method.code(),
                    this.sessionId), e);
        }
        return currentId;
    }

    private long getNextId() {
        long tmpId = this.id.getAndIncrement();
        if (tmpId < 0) {
            this.id.set(0);
            return 0;
        }
        return tmpId;
    }

    private boolean isNotInitialized() {
        return !this.initialized;
    }

    private boolean waitInitialized() {
        if (this.initialized) {
            return true;
        }
        synchronized (this.initializedLock) {
            if (this.initialized) {
                return true;
            }
            try {
                this.initializedLock.wait(60_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Failed to initialize.", e);
            }
        }
        return this.initialized;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        if (this.subscription != null) {
            this.subscription.cancel();
        }
        try {
            if (this.pingScheduler != null) {
                this.pingScheduler.shutdown();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
        log.info("Close MCP client. [name={}, sessionId={}]", this.name, this.sessionId);
    }
}
