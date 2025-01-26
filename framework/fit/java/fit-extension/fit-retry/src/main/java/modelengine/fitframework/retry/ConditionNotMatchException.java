/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry;

/**
 * 表示重试条件不满足的异常。
 *
 * @author 季聿阶
 * @since 2022-11-17
 */
public class ConditionNotMatchException extends RetryException {
    public ConditionNotMatchException(int attemptTimes, Throwable cause) {
        super(attemptTimes, cause);
    }
}
