/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 方法拦截器
 *
 * @since 1.0
 */
public class MethodInterceptor {
    private final TriFunction<Callable<?>, Method, Object[], Object> interceptor;

    /**
     * 构造方法
     *
     * @param interceptor 方法拦截器，用于处理方法调用前后的逻辑
     */
    public MethodInterceptor(TriFunction<Callable<?>, Method, Object[], Object> interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * 构造方法
     */
    public MethodInterceptor() {
        this(null);
    }

    /**
     * 拦截方法调用
     *
     * @param zuper 原始方法调用
     * @param method 被调用的方法
     * @param args 方法参数
     * @return 方法调用结果
     * @throws Exception 方法调用可能抛出的异常
     */
    @RuntimeType
    public Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @AllArguments Object[] args)
            throws Exception {
        if (interceptor == null) {
            return zuper.call();
        } else {
            return interceptor.apply(zuper, method, args);
        }
    }

    /**
     * 拦截方法调用，不包含原始方法调用
     *
     * @param method 被调用的方法
     * @param args 方法参数
     * @return 方法调用结果
     * @throws Exception 方法调用可能抛出的异常
     */
    @RuntimeType
    public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {
        return interceptor.apply(null, method, args);
    }
}
