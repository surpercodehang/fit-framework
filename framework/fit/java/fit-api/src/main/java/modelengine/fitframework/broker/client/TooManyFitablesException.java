/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.exception.FitException;

/**
 * 当可调用服务实现过多时发生的异常。
 *
 * @author 季聿阶
 * @since 2021-08-24
 */
@ErrorCode(TooManyFitablesException.CODE)
public class TooManyFitablesException extends FitException {
    /** 表示服务实现过多的异常码。 */
    public static final int CODE = 0x7F020000;

    /**
     * 通过异常信息来实例化 {@link TooManyFitablesException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public TooManyFitablesException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link TooManyFitablesException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public TooManyFitablesException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link TooManyFitablesException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public TooManyFitablesException(String message, Throwable cause) {
        super(message, cause);
    }
}
