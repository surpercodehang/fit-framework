/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.schedule.cron.CronField;
import modelengine.fitframework.util.TimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

/**
 * 表示每小时中的分钟的字段。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public class MinuteOfHourCronField extends AbstractCronField {
    private static final int MAX = 59;

    @Override
    public Optional<ZonedDateTime> findCurrentOrNextTime(@Nonnull ZonedDateTime dateTime) {
        int minuteValue = this.getBitSet().nextSetBit(dateTime.getMinute());
        if (minuteValue < 0) {
            return Optional.empty();
        }
        if (minuteValue == dateTime.getMinute()) {
            return Optional.of(dateTime);
        }
        ZonedDateTime target =
                dateTime.with(ChronoField.MINUTE_OF_HOUR, minuteValue).with(TimeUtils.firstTimeOfMinute());
        return Optional.of(target);
    }

    /**
     * 表示 {@link MinuteOfHourCronField} 的字段解析器。
     */
    public static class Parser extends AbstractBitCronFieldParser {
        @Override
        protected CronField initialCronField() {
            return new MinuteOfHourCronField();
        }

        @Override
        protected int getMaxValidValue() {
            return MAX;
        }
    }
}
