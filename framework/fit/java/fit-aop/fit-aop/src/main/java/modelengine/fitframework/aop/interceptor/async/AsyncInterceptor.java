/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.async;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.aop.interceptor.support.AbstractMethodInterceptor;
import modelengine.fitframework.exception.ExceptionHandler;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LazyLoader;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * 表示异步执行的方法拦截器。
 *
 * @author 季聿阶
 * @since 2022-11-11
 */
public class AsyncInterceptor extends AbstractMethodInterceptor {
    private static final Logger log = Logger.get(AsyncInterceptor.class);

    private final LazyLoader<Executor> executorLoader;
    private final LazyLoader<ExceptionHandler> exceptionHandlerLoader;

    public AsyncInterceptor(Supplier<Executor> executorSupplier, Supplier<ExceptionHandler> exceptionHandlerSupplier) {
        this.executorLoader =
                new LazyLoader<>(notNull(executorSupplier, "The async executor supplier cannot be null."));
        this.exceptionHandlerLoader = new LazyLoader<>(exceptionHandlerSupplier);
    }

    @Nullable
    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable {
        this.executorLoader.get().execute(() -> this.asyncExecute(methodJoinPoint));
        return null;
    }

    private void asyncExecute(MethodJoinPoint methodJoinPoint) {
        try {
            methodJoinPoint.proceed();
        } catch (Throwable e) {
            ExceptionHandler exceptionHandler = this.exceptionHandlerLoader.get();
            if (exceptionHandler == null) {
                log.debug(e.getClass().getName(), e);
                log.error("Failed to execute asynchronously. [class={}, method={}]",
                        methodJoinPoint.getProxiedInvocation().getTarget() == null
                                ? null
                                : methodJoinPoint.getProxiedInvocation().getTarget().getClass().getName(),
                        methodJoinPoint.getProxiedInvocation().getMethod().getName());
            } else {
                exceptionHandler.handleException(e,
                        methodJoinPoint.getProxiedInvocation().getMethod(),
                        methodJoinPoint.getProxiedInvocation().getArguments());
            }
        }
    }
}
