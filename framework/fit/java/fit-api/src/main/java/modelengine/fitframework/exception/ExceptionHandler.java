/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.exception;

import java.lang.reflect.Method;

/**
 * 表示异常处理器。
 *
 * @author 季聿阶
 * @since 2022-11-11
 */
@FunctionalInterface
public interface ExceptionHandler {
    /**
     * 处理发生的异常。
     *
     * @param cause 表示发生异常的原因的 {@link Throwable}。
     * @param method 表示发生异常的方法的 {@link Method}。
     * @param params 表示发生异常的参数列表的 {@link Object}{@code []}。
     */
    void handleException(Throwable cause, Method method, Object... params);
}
