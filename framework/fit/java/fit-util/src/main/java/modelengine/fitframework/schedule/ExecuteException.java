/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.schedule;

/**
 * 表示执行过程中发生的异常。
 *
 * @author 季聿阶
 * @since 2022-12-26
 */
public class ExecuteException extends RuntimeException {
    /**
     * 使用指定的原因来初始化 {@link ExecuteException} 的新实例。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ExecuteException(Throwable cause) {
        super(cause);
    }
}
