/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron;

import modelengine.fitframework.schedule.cron.support.DefaultCronExpression;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * 表示 CRON 的表达式。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public interface CronExpression {
    /**
     * 根据指定的时间，获取在其之后的满足当前 CRON 表达式的下一个时间点。
     *
     * @param dateTime 表示指定时间的 {@link ZonedDateTime}。
     * @return 当找到下一个满足条件的时间点时，返回 {@link Optional}{@code <}{@link ZonedDateTime}{@code >}，否则，返回
     * {@link Optional#empty()}。
     */
    Optional<ZonedDateTime> findNextDateTime(ZonedDateTime dateTime);

    /**
     * 设置最大允许搜索的未来的年数，默认为未来 10 年。
     *
     * @param year 表示待设置的最大允许搜索的未来的年数的 {@code int}。
     */
    static void setMaxFutureYears(int year) {
        DefaultCronExpression.setMaxFutureYears(year);
    }
}
