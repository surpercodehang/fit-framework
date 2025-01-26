/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.exception.FitException;

/**
 * 表示响应式编程框架的基础异常。
 *
 * @author 季聿阶
 * @since 2024-02-09
 */
@ErrorCode(FlowableException.CODE)
public class FlowableException extends FitException {
    /** 表示响应式编程框架的根异常码。 */
    public static final int CODE = 0x7F060000;

    /**
     * 通过异常信息来初始化响应式编程框架的基础异常。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FlowableException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来初始化响应式编程框架的基础异常。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public FlowableException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来初始化响应式编程框架的基础异常。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public FlowableException(String message, Throwable cause) {
        super(message, cause);
    }
}
