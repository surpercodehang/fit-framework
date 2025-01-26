/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.TimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.BitSet;
import java.util.Optional;

/**
 * 表示日期字段的组合字段。
 *
 * @author 季聿阶
 * @since 2023-01-04
 */
public class DayCronFieldComposite extends AbstractCronField {
    private final DayOfMonthCronField dayOfMonthField;
    private final DayOfWeekCronField dayOfWeekField;

    public DayCronFieldComposite(DayOfMonthCronField dayOfMonthField, DayOfWeekCronField dayOfWeekField) {
        this.dayOfMonthField = notNull(dayOfMonthField, "The day of month field cannot be null.");
        this.dayOfWeekField = notNull(dayOfWeekField, "The day of week field cannot be null.");
    }

    @Override
    public Optional<ZonedDateTime> findCurrentOrNextTime(@Nonnull ZonedDateTime dateTime) {
        BitSet bs1 = this.dayOfMonthField.unionLastDays(dateTime);
        BitSet bs2 = this.dayOfWeekField.unionDaysOfWeek(dateTime);
        bs1.and(bs2);
        int dayValue = bs1.nextSetBit(dateTime.getDayOfMonth());
        if (dayValue < 0) {
            return Optional.empty();
        }
        if (dayValue == dateTime.getDayOfMonth()) {
            return Optional.of(dateTime);
        }
        ZonedDateTime target = dateTime.with(ChronoField.DAY_OF_MONTH, dayValue).with(TimeUtils.firstTimeOfDay());
        return Optional.of(target);
    }
}
