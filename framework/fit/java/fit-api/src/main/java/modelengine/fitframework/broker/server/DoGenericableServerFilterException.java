/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server;

/**
 * 表示执行 {@link GenericableServerFilter#doFilter(String, Object[], GenericableServerFilterChain)} 过程中发生的异常。
 *
 * @author 李金绪
 * @since 2024-08-26
 */
public class DoGenericableServerFilterException extends RuntimeException {
    /**
     * 通过异常消息来实例化 {@link DoGenericableServerFilterException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public DoGenericableServerFilterException(String message) {
        this(message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link DoGenericableServerFilterException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public DoGenericableServerFilterException(String message, Throwable cause) {
        super(message, cause);
    }
}