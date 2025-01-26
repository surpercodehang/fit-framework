/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

/**
 * 表示与事务相关的异常。
 *
 * @author 梁济时
 * @since 2022-08-24
 */
public class TransactionException extends RuntimeException {
    /**
     * 使用异常信息初始化 {@link TransactionException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public TransactionException(String message) {
        super(message);
    }

    /**
     * 使用引发异常的原因初始化 {@link TransactionException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public TransactionException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link TransactionException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
