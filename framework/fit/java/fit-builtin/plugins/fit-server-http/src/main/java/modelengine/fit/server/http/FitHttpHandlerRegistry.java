/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http;

import static modelengine.fit.serialization.http.Constants.FIT_ASYNC_TASK_PATH_PATTERN;
import static modelengine.fit.serialization.http.Constants.FIT_PATH_PATTERN;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.broker.LocalGenericableRepository;
import modelengine.fitframework.broker.server.Dispatcher;
import modelengine.fitframework.conf.runtime.WorkerConfig;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.StringUtils;

/**
 * FIT 通信方式的处理器的注册器。
 *
 * @author 季聿阶
 * @since 2022-09-14
 */
@Component
public class FitHttpHandlerRegistry {
    private final BeanContainer container;
    private final LocalGenericableRepository repository;
    private final String contextPath;

    /**
     * 创建 FIT 通信的处理器的注册器对象。
     *
     * @param httpServer 表示 Http 服务器的 {@link HttpClassicServer}。
     * @param dispatcher 表示 Http 请求转发器的 {@link Dispatcher}。
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param repository 表示本地服务仓的 {@link LocalGenericableRepository}。
     * @param contextPath 表示 Http 请求前缀的 {@link String}。
     * @param worker 表示本地进程配置的 {@link WorkerConfig}。
     */
    public FitHttpHandlerRegistry(HttpClassicServer httpServer, Dispatcher dispatcher, BeanContainer container,
            LocalGenericableRepository repository, @Value("${server.http.context-path}") String contextPath,
            WorkerConfig worker) {
        notNull(httpServer, "The http server cannot be null.");
        notNull(dispatcher, "The receiver cannot be null.");
        this.container = notNull(container, "The bean container cannot be null.");
        this.repository = notNull(repository, "The local genericable repository cannot be null.");
        this.contextPath = StringUtils.isBlank(contextPath) ? StringUtils.EMPTY : contextPath;
        if (StringUtils.isNotBlank(this.contextPath)) {
            HttpHandler handler =
                    this.createHttpHandler(httpServer, dispatcher, this.contextPath + FIT_PATH_PATTERN, worker);
            httpServer.httpDispatcher().register(HttpRequestMethod.POST.name(), handler);
            HttpHandler asyncTaskHandler =
                    this.createAsyncHttpHandler(httpServer, worker, this.contextPath + FIT_ASYNC_TASK_PATH_PATTERN);
            httpServer.httpDispatcher().register(HttpRequestMethod.GET.name(), asyncTaskHandler);
        }
        HttpHandler handler = this.createHttpHandler(httpServer, dispatcher, FIT_PATH_PATTERN, worker);
        httpServer.httpDispatcher().register(HttpRequestMethod.POST.name(), handler);
        HttpHandler asyncTaskHandler = this.createAsyncHttpHandler(httpServer, worker, FIT_ASYNC_TASK_PATH_PATTERN);
        httpServer.httpDispatcher().register(HttpRequestMethod.GET.name(), asyncTaskHandler);
    }

    private HttpHandler createHttpHandler(HttpClassicServer httpServer, Dispatcher dispatcher, String pathPattern,
            WorkerConfig worker) {
        return new FitHttpHandler(this.container,
                dispatcher,
                this.repository,
                worker,
                HttpHandler.StaticInfo.builder().pathPattern(pathPattern).build(),
                HttpHandler.ExecutionInfo.builder().httpServer(httpServer).build());
    }

    private HttpHandler createAsyncHttpHandler(HttpClassicServer httpServer, WorkerConfig worker, String pathPattern) {
        return new FitHttpAsyncTaskHandler(this.container,
                worker,
                HttpHandler.StaticInfo.builder().pathPattern(pathPattern).build(),
                HttpHandler.ExecutionInfo.builder().httpServer(httpServer).build());
    }

    String getContextPath() {
        return this.contextPath;
    }
}
