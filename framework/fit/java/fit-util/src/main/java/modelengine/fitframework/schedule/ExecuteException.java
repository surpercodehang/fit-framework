/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule;

/**
 * 表示执行过程中发生的异常。
 *
 * @author 季聿阶
 * @since 2022-12-26
 */
public class ExecuteException extends RuntimeException {
    public ExecuteException(Throwable cause) {
        super(cause);
    }
}
