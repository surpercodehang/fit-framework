/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry;

/**
 * 表示重试的退避策略。
 *
 * @param <T> 表示重试执行器的返回类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-11-17
 */
@FunctionalInterface
public interface RetryBackOff<T> {
    /**
     * 获取当业务抛出异常时，重试执行的退避时间。
     *
     * @param retryTimes 表示当前重试的次数的 {@code int}。第一次重试时该值为 {@code 1}。
     * @param cause 表示当前业务执行抛出异常的 {@link Throwable}。
     * @return 表示业务抛出异常时，重试执行的退避时间的 {@code long}，单位为毫秒。
     */
    long sleepMillis(int retryTimes, Throwable cause);
}
