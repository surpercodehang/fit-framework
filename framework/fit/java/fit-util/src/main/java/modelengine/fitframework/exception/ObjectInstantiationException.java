/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.exception;

/**
 * 当实例化对象失败时引发的异常。
 *
 * @author 梁济时
 * @since 2020-07-24
 */
public class ObjectInstantiationException extends RuntimeException {
    /**
     * 使用异常信息初始化 {@link ObjectInstantiationException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public ObjectInstantiationException(String message) {
        super(message);
    }

    /**
     * 使用引发异常的原因初始化 {@link ObjectInstantiationException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public ObjectInstantiationException(Throwable cause) {
        super(cause);
    }
}
