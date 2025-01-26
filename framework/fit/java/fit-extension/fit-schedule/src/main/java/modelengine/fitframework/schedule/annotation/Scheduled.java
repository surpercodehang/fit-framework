/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 表示定时执行的任务。
 *
 * @author 季聿阶
 * @since 2023-01-18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Scheduled {
    /**
     * 获取定时调度的策略。
     *
     * @return 表示定时调度策略的 {@link Strategy}。
     */
    Strategy strategy();

    /**
     * 获取定时调度策略的值。
     *
     * @return 表示定时调度策略的值的 {@link String}。
     */
    String value();

    /**
     * 获取定时调度策略执行的时区。
     * <p>该字段仅在 {@link #strategy()} 为 {@link Strategy#CRON} 时生效。</p>
     *
     * @return 表示定时调度策略执行的时区的 {@link String}。
     */
    String zone() default "";

    /**
     * 获取定时调度策略执行的初始延迟。
     *
     * @return 表示定时调度策略执行的初始延迟的 {@code long}。
     */
    long initialDelay() default 0;

    /**
     * 获取定时调度策略执行相关时间的单位。
     *
     * @return 表示定时调度策略执行相关时间的单位的 {@link TimeUnit}。
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 表示定时调度的策略。
     */
    enum Strategy {
        /** 表示按照 CRON 表达式进行调度的策略。 */
        CRON,

        /** 表示按照固定延迟进行调度的策略。 */
        FIXED_DELAY,

        /** 表示按照固定频率进行调度的策略。 */
        FIXED_RATE,

        /** 表示一次性的调度策略。 */
        DISPOSABLE
    }
}
