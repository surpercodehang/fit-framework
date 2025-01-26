/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.exception.FitException;

/**
 * 当序列化或反序列化过程失败时引发的异常。
 *
 * @author 梁济时
 * @since 2020-11-05
 */
@ErrorCode(SerializationException.CODE)
public class SerializationException extends FitException {
    /** 表示序列化过程的异常码。 */
    public static final int CODE = 0x7F050000;

    /**
     * 通过异常信息来实例化 {@link SerializationException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link SerializationException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public SerializationException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link SerializationException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
