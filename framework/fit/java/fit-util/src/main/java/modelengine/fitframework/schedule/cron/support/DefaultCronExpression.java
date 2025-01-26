/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.schedule.cron.CronExpression;
import modelengine.fitframework.schedule.cron.CronField;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * 表示 {@link CronExpression} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public class DefaultCronExpression implements CronExpression {
    /** 表示用于搜索的未来年数，一般这个值不需要修改，但是如果存在业务需要，可以全局修改。 */
    private static int maxFutureYears = 10;

    private final CronField secondField;
    private final CronField minuteField;
    private final CronField hourField;
    private final CronField dayField;
    private final CronField monthField;

    DefaultCronExpression(CronField secondField, CronField minuteField, CronField hourField, CronField dayOfMonthField,
            CronField monthField, CronField dayOfWeekField) {
        this.secondField = notNull(secondField, "The second of minute field cannot be null.");
        this.minuteField = notNull(minuteField, "The minute of hour field cannot be null.");
        this.hourField = notNull(hourField, "The hour of day field cannot be null.");
        notNull(dayOfMonthField, "The day of month field cannot be null.");
        notNull(dayOfWeekField, "The day of week field cannot be null.");
        this.dayField = new DayCronFieldComposite(cast(dayOfMonthField), cast(dayOfWeekField));
        this.monthField = notNull(monthField, "The month of year field cannot be null.");
    }

    @Override
    public Optional<ZonedDateTime> findNextDateTime(ZonedDateTime dateTime) {
        NextDateTimeCalculator calculator = new NextDateTimeCalculator(Arrays.asList(this.monthField,
                this.dayField,
                this.hourField,
                this.minuteField,
                this.secondField), maxFutureYears, dateTime);
        return calculator.findNextDateTime();
    }

    /**
     * 设置可用于搜索的未来年数的最大值。
     *
     * @param value 表示可用于搜索的未来年数的最大值的 32 位整数。
     */
    public static void setMaxFutureYears(int value) {
        maxFutureYears = value;
    }
}
