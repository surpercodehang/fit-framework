/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client;

import modelengine.fitframework.exception.DegradableException;
import modelengine.fitframework.exception.ErrorCode;

/**
 * 当找不到服务地址时发生的异常。
 *
 * @author 季聿阶
 * @since 2021-08-24
 */
@ErrorCode(TargetNotFoundException.CODE)
public class TargetNotFoundException extends DegradableException {
    /** 表示没有服务地址的异常码。 */
    public static final int CODE = 0x7F030000;

    /**
     * 通过异常信息来实例化 {@link TargetNotFoundException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public TargetNotFoundException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link TargetNotFoundException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public TargetNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link TargetNotFoundException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public TargetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
