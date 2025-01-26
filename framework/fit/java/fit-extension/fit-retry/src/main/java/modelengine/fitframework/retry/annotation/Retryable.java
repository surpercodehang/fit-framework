/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.annotation;

import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示重试策略的注解。
 *
 * @author 邬涨财
 * @since 2023-02-21
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryable {
    /**
     * 表示重试退避方法的方法名。
     *
     * @return 表示重试退避方法的方法名的 {@link String}。
     */
    String recover() default "";

    /**
     * 表示需要重试的异常类型。
     *
     * @return 表示需要重试的异常类型的 {@link Class}{@code <? extends }{@link Throwable}{@code >[]}。
     */
    @Forward(property = "exceptions")
    Class<? extends Throwable>[] value() default {};

    /**
     * 表示需要重试的异常类型。与 {@link #value()} 作用相同。
     *
     * @return 表示需要重试的异常类型的 {@link Class}{@code <? extends }{@link Throwable}{@code >[]}。
     */
    Class<? extends Throwable>[] exceptions() default {};

    /**
     * 表示最大尝试次数。
     *
     * @return 表示最大尝试次数的 {@code int}。
     */
    int maxAttempts() default 3;

    /**
     * 表示重试的退避策略。
     *
     * @return 表示重试的退避策略的 {@link Backoff}。
     */
    Backoff backoff() default @Backoff();
}
