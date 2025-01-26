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
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Optional;

/**
 * 表示每年中的月数的字段。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public class MonthOfYearCronField extends AbstractCronField {
    private static final int MIN = 1;
    private static final int MAX = 12;

    @Override
    public Optional<ZonedDateTime> findCurrentOrNextTime(@Nonnull ZonedDateTime dateTime) {
        int monthValue = this.getBitSet().nextSetBit(dateTime.getMonthValue());
        if (monthValue < 0) {
            return Optional.empty();
        }
        if (monthValue == dateTime.getMonthValue()) {
            return Optional.of(dateTime);
        }
        ZonedDateTime target = dateTime.with(ChronoField.MONTH_OF_YEAR, monthValue)
                .with(TemporalAdjusters.firstDayOfMonth())
                .with(TimeUtils.firstTimeOfDay());
        return Optional.of(target);
    }

    /**
     * 表示 {@link MonthOfYearCronField} 的字段解析器。
     */
    public static class Parser extends AbstractBitCronFieldParser {
        private static final String[] MONTHS = new String[] {
                "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
        };

        @Override
        protected CronField initialCronField() {
            return new MonthOfYearCronField();
        }

        @Override
        protected String convert(String fieldValue) {
            String result = fieldValue.toUpperCase(Locale.ROOT);
            for (int i = 0; i < MONTHS.length; i++) {
                result = result.replace(MONTHS[i], String.valueOf(i + 1));
            }
            return result;
        }

        @Override
        protected int getMinValidValue() {
            return MIN;
        }

        @Override
        protected int getMaxValidValue() {
            return MAX;
        }
    }
}
