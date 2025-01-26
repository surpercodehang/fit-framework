/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.exception.FitException;

/**
 * 当无法找到指定的本地调用器时引发的异常。
 *
 * @author 季聿阶
 * @since 2024-03-30
 */
@ErrorCode(ServerLocalExecutorNotFoundException.CODE)
public class ServerLocalExecutorNotFoundException extends FitException {
    /** 表示没有服务实现的异常码。 */
    public static final int CODE = 0x7F010003;

    /**
     * 通过异常信息来实例化 {@link ServerLocalExecutorNotFoundException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public ServerLocalExecutorNotFoundException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link ServerLocalExecutorNotFoundException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ServerLocalExecutorNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link ServerLocalExecutorNotFoundException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ServerLocalExecutorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
