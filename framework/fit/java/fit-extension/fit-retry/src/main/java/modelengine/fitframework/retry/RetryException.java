/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry;

/**
 * 表示重试的异常。
 *
 * @author 季聿阶
 * @since 2022-11-17
 */
public class RetryException extends RuntimeException {
    private final int attemptTimes;

    /**
     * 创建一个 {@link RetryException} 实例。
     *
     * @param attemptTimes 表示重试次数的 {@code int}。
     * @param cause 表示导致重试的原因的 {@link Throwable}。
     */
    public RetryException(int attemptTimes, Throwable cause) {
        super(cause);
        this.attemptTimes = attemptTimes;
    }

    public int getAttemptTimes() {
        return this.attemptTimes;
    }
}
