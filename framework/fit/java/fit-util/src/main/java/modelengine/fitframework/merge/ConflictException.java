/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge;

/**
 * 表示冲突的异常。
 *
 * @author 季聿阶
 * @since 2022-07-30
 */
public class ConflictException extends RuntimeException {
    /**
     * 通过异常信息来实例化 {@link ConflictException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link ConflictException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
