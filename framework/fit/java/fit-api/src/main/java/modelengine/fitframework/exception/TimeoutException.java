/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.exception;

/**
 * 表示远程调用发生在客户端的超时异常。
 *
 * @author 季聿阶
 * @since 2024-11-25
 */
@ErrorCode(TimeoutException.CODE)
public class TimeoutException extends ClientException {
    /** 表示异步任务未执行完成的异常码。 */
    public static final int CODE = 0x7F040001;

    /**
     * 通过异常信息来实例化 {@link TimeoutException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public TimeoutException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link TimeoutException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public TimeoutException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link TimeoutException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
