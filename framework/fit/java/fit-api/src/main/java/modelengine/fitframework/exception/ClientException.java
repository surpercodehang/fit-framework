/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.exception;

/**
 * 表示远程调用发生在客户端的异常。
 *
 * @author 季聿阶
 * @since 2023-06-17
 */
@ErrorCode(ClientException.CODE)
public class ClientException extends FitException {
    /** 表示异步任务未执行完成的异常码。 */
    public static final int CODE = 0x7F040000;

    /**
     * 通过异常信息来实例化 {@link ClientException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public ClientException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link ClientException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ClientException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link ClientException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
