/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示重试退避策略的注解。
 *
 * @author 邬涨财
 * @since 2023-02-21
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Backoff {
    /**
     * 表示退避方法调用的最小延迟时间。
     *
     * @return 表示最小延迟时间的 {@code long}。
     */
    long minDelay() default 0;

    /**
     * 表示退避方法调用的最大延迟时间。
     *
     * @return 表示最大延迟时间的 {@code long}。
     */
    long maxDelay() default 0;

    /**
     * 表示退避方法的延迟时间的递增倍数。默认值为 0 ，表示忽略该参数。
     *
     * @return 表示延迟时间的递增倍数的 {@code double}。
     */
    double multiplier() default 0;
}
