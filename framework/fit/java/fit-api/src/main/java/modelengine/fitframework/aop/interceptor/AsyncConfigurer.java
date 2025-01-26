/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor;

import modelengine.fitframework.exception.ExceptionHandler;
import modelengine.fitframework.inspection.Nullable;

import java.util.concurrent.Executor;

/**
 * 表示异步执行的配置器。
 *
 * @author 季聿阶
 * @since 2022-11-11
 */
public interface AsyncConfigurer {
    /**
     * 获取默认异步执行的线程池。
     *
     * @return 表示默认异步执行的线程池的 {@link Executor}。
     */
    @Nullable
    default Executor getExecutor() {
        return null;
    }

    /**
     * 获取默认异步执行线程池的异常处理器。
     *
     * @return 表示默认异步执行线程池的异常处理器的 {@link ExceptionHandler}。
     */
    @Nullable
    default ExceptionHandler getUncaughtExceptionHandler() {
        return null;
    }
}
