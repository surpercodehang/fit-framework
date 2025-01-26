/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.exception;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.exception.FitException;

/**
 * 表示注册中心的异常。
 *
 * @author 李金绪
 * @since 2024-08-22
 */
@ErrorCode(AuthenticationException.CODE)
public class AuthenticationException extends FitException {
    /** 表示认证鉴权通信的异常码。 */
    public static final int CODE = 0x7F000601;

    /**
     * 通过异常信息来实例化 {@link AuthenticationException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link AuthenticationException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link AuthenticationException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
