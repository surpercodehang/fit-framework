/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.async;

import modelengine.fitframework.aop.interceptor.AsyncConfigurer;
import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.async.support.DefaultAsyncMethodMatcherFactory;
import modelengine.fitframework.aop.interceptor.support.AbstractMethodInterceptorResolver;
import modelengine.fitframework.exception.ExceptionHandler;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.thread.DefaultThreadFactory;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 解析异步执行拦截事件提供者的解析器。
 *
 * @author 季聿阶
 * @since 2022-11-12
 */
public class AsyncInterceptorResolver extends AbstractMethodInterceptorResolver {
    private final Map<String, Map<String, Supplier<Executor>>> pluginExecutors = new HashMap<>();

    private final AsyncMethodMatcherFactory asyncMethodMatcherFactory = new DefaultAsyncMethodMatcherFactory();

    @Override
    public boolean eliminate(BeanMetadata metadata) {
        return false;
    }

    @Override
    public List<MethodInterceptor> resolve(BeanMetadata beanMetadata, Object bean) {
        List<MethodInterceptor> methodInterceptors = this.getMethodInterceptors(beanMetadata.container());
        return this.matchPointcuts(methodInterceptors, bean);
    }

    private List<MethodInterceptor> getMethodInterceptors(BeanContainer container) {
        List<MethodInterceptor> methodInterceptors = new ArrayList<>();
        Map<String, Supplier<Executor>> pluginExecutorSuppliers = this.getPluginExecutorSuppliers(container);
        Supplier<ExceptionHandler> exceptionHandler = this.createExceptionHandlerSupplier(container);
        for (Map.Entry<String, Supplier<Executor>> entry : pluginExecutorSuppliers.entrySet()) {
            MethodMatcher asyncMethodMatcher = this.asyncMethodMatcherFactory.create(entry.getKey());
            MethodInterceptor asyncInterceptor = new AsyncInterceptor(entry.getValue(), exceptionHandler);
            asyncInterceptor.getPointCut().matchers().add(asyncMethodMatcher);
            methodInterceptors.add(asyncInterceptor);
        }
        return methodInterceptors;
    }

    private Map<String, Supplier<Executor>> getPluginExecutorSuppliers(BeanContainer container) {
        if (this.pluginExecutors.containsKey(container.plugin().metadata().name())) {
            return this.pluginExecutors.get(container.plugin().metadata().name());
        }
        Map<String, Supplier<Executor>> executorSuppliers = container.factories(Executor.class)
                .stream()
                .collect(Collectors.toMap(factory -> factory.metadata().name(),
                        factory -> () -> ObjectUtils.cast(factory.get())));
        Supplier<Executor> defaultExecutorSupplier = this.getPluginDefaultExecutorSupplier(container);
        executorSuppliers.put(StringUtils.EMPTY, defaultExecutorSupplier);
        this.pluginExecutors.put(container.plugin().metadata().name(), executorSuppliers);
        return executorSuppliers;
    }

    private Supplier<Executor> getPluginDefaultExecutorSupplier(BeanContainer container) {
        Optional<BeanFactory> opConfigurer = container.factory(AsyncConfigurer.class);
        return opConfigurer.map(this::getDefaultExecutorSupplierFromConfigurer)
                .orElseGet(this::createDefaultExecutorSupplier);
    }

    private Supplier<Executor> getDefaultExecutorSupplierFromConfigurer(BeanFactory factory) {
        return () -> {
            AsyncConfigurer configurer = factory.get();
            return configurer.getExecutor() == null
                    ? this.createDefaultExecutorSupplier().get()
                    : configurer.getExecutor();
        };
    }

    private Supplier<Executor> createDefaultExecutorSupplier() {
        return () -> new ThreadPoolExecutor(10,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1),
                new DefaultThreadFactory("async", false, (thread, exception) -> {}));
    }

    private Supplier<ExceptionHandler> createExceptionHandlerSupplier(BeanContainer container) {
        Optional<BeanFactory> optional = container.factory(AsyncConfigurer.class);
        if (optional.isPresent()) {
            BeanFactory beanFactory = optional.get();
            return () -> {
                AsyncConfigurer asyncConfigurer = beanFactory.get();
                return asyncConfigurer.getUncaughtExceptionHandler();
            };
        }
        return () -> null;
    }
}
