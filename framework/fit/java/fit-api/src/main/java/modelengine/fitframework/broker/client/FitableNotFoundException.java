/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.exception.FitException;

/**
 * 当无法找到指定的泛服务实现时引发的异常。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-09-03
 */
@ErrorCode(FitableNotFoundException.CODE)
public class FitableNotFoundException extends FitException {
    /** 表示没有服务实现的异常码。 */
    public static final int CODE = 0x7F020001;

    /**
     * 通过异常信息来实例化 {@link FitableNotFoundException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FitableNotFoundException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link FitableNotFoundException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public FitableNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link FitableNotFoundException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public FitableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
